package com.tkym.labs.jvml;

import static com.tkym.labs.jvml.JavaLaunchFactory.OS.ELSE;
import static com.tkym.labs.jvml.JavaLaunchFactory.OS.LINUX;
import static com.tkym.labs.jvml.JavaLaunchFactory.OS.MAC;
import static com.tkym.labs.jvml.JavaLaunchFactory.OS.WIN;


public class JavaLaunchFactory{
	private static final JavaLaunchFactory singleton = new JavaLaunchFactory();
	enum OS {
		WIN("Windows"),MAC("Mac"),LINUX("Linux"),ELSE(null);
		String index;
		OS(String index){
			this.index = index;
		}
	}
	private String osname;
	private String lib;
	private String _S;
	private String classpath;
	private OS os;
	private JavaLaunchFactory(){
		osname = System.getProperty("os.name");
		lib = System.getProperty("java.home");
		_S = System.getProperty("file.separator");
		classpath = System.getProperty("java.class.path");
		if (osname.startsWith(WIN.index)) os = WIN;
		else if (osname.startsWith(LINUX.index)) os = LINUX;
		else if (osname.startsWith(MAC.index)) os = MAC;
		else os = ELSE;
		System.out.println(os);
	}
	OS getOS(){
		return os;
	}
	public static final JavaLaunchFactory getInstance(){
		return singleton;
	}
	public JavaLaunch java(){
		return createLaunch().
				cp(classpath).
				d("file.encoding",System.getProperty("file.encoding"));
	}
	
	private JavaLaunch createLaunch(){
		if (os == WIN) return createAsWin();
		else if (os == MAC) return createAsMac();
		else
			throw new UnsupportedOperationException(
					"unsupport os. os name is"+osname);
	}
	
	private JavaLaunch createAsMac(){
		return new JavaLaunch(lib+_S+"bin"+_S+"java");
	}
	private JavaLaunch createAsWin(){
		return new JavaLaunch(lib+_S+"bin"+_S+"javaw.exe");
	}
	int getSuccessReturnCode(){
//		if (os == MAC) return 1;
		return 0;
	}
}