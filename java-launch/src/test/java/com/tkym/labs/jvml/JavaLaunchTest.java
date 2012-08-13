package com.tkym.labs.jvml;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

public class JavaLaunchTest {
	@Test
	public void testAPIJavaw() throws InterruptedException, ExecutionException, JavaLaunchException{
		debugProp("file.encoding");
		System.out.println("---------");
		int ret = JavaLaunchFactory.getInstance().java()
				.main(DebugMain.class).asFuture().get();
		assertThat(ret, is(0));
	}
	
	private static void debugProp(String key){
		System.out.println(key+"="+System.getProperties().get(key));
	}
	
	@Test
	public void testAPIJavawCase002() throws InterruptedException, ExecutionException, JavaLaunchException{
		JavaLaunchFactory.getInstance().java().main(DebugMain.class).exec(new JavaLaunchErrorHandle() {
			@Override
			public void onFail(int error){
				fail();
			} 
		});
	}
	
	@Test
	public void testSameEncoding() throws InterruptedException, ExecutionException, JavaLaunchException{
		int ret = JavaLaunchFactory.getInstance().java().main(IsSameEncode.class, System.getProperty("file.encoding")).asFuture().get();
		assertThat(ret, is(0));
	}
	
	
	@Test
	public void testJavaLaunch() throws JavaLaunchException, InterruptedException, ExecutionException{
		JavaLaunch javavm = JavaLaunchFactory.getInstance().java();
		int ret = javavm.
			xmx(512,"m").
			xms(256,"m").
			d("file.encoding","UTF-8").
			main(Booting.class,"arg1").asFuture().get();
			
		System.out.println(ret);
	}
	
	static class IsSameEncode{
		public static void main(String[] args) {
			String encoding = System.getProperty("file.encoding");
			if (args.length == 1) 
				if (args[0].equals(encoding))
					System.exit(0);
			throw new Error();
		}
	}
	static class Booting{
		public static void main(String[] args) {
			System.out.println("ok");
		}
	}
	static class DebugMain {
		public static void main(String[] args) {
			debugProp("file.encoding");
			System.out.println(DebugMain.class.getName()+"System.out:こんにちわ。Java");
			System.err.println(DebugMain.class.getName()+"System.err:こんにちわ。Java");
		}
	}
}