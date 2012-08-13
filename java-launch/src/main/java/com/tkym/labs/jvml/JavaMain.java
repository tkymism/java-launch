package com.tkym.labs.jvml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;


public class JavaMain<T> {
	private final Class<T> main;
	private final Future<Integer> future;
	JavaMain(final Class<T> main, final Process process){
		this.main = main;
		final Thread syserrThread = new Thread(
				createObserveTask(process.getErrorStream(), System.err), 
				main.getName()+"."+"err");
		final Thread sysoutThread = new Thread(
				createObserveTask(process.getInputStream(), System.out), 
				main.getName()+"."+"out");
		ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, main.getName());
			}
		});
		this.future = executorService.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				syserrThread.start();
				sysoutThread.start();
				int ret = process.waitFor();
				syserrThread.join();
				sysoutThread.join();
				return ret;
			}
		});
	}
	public Class<T> getMain() {
		return main;
	}
	public Future<Integer> asFuture(){
		return this.future;
	}
	public void exec(JavaLaunchErrorHandle onFail){
		try {
			int ret = asFuture().get();
			if (ret != JavaLaunchFactory.getInstance().getSuccessReturnCode())
				if (onFail != null)
					onFail.onFail(ret);
		} catch (InterruptedException e) {
			throw new JavaLaunchException(e);
		} catch (ExecutionException e) {
			throw new JavaLaunchException(e);
		} 
	}
	static Runnable createObserveTask(final InputStream is, final PrintStream ps){
		return new Runnable() {
			@Override
			public void run() {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				while(true){
					try {
						String line = reader.readLine();
						if(line == null) break;
						ps.println(line);
					} catch (IOException e) {
						try {
							reader.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
	}
}