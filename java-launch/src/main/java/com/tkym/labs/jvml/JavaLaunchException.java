package com.tkym.labs.jvml;

@SuppressWarnings("serial")
public class JavaLaunchException extends RuntimeException{
	JavaLaunchException(Throwable t){
		super(t);
	}
}