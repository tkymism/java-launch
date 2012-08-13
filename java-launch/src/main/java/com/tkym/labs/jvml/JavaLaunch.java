package com.tkym.labs.jvml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JavaLaunch{
	private final String java;
	private Map<String, String> environment = 
			new HashMap<String, String>();
	private Map<String, String> properties =
			new HashMap<String, String>();
	private String classpath = null;
	private String xmx = null;
	private String xms = null;
	JavaLaunch(String java){
		this.java = java;
	}
	public JavaLaunch cp(String classpath){
		this.classpath = classpath;
		return this;
	}
	public JavaLaunch xmx(int size, String unit){
		this.xmx = size + unit;
		return this;
	}
	public JavaLaunch xms(int size, String unit){
		this.xms = size + unit;
		return this;
	}
	public JavaLaunch env(String key, String value){
		environment.put(key, value);
		return this;
	}
	public JavaLaunch d(String key, String value){
		this.properties.put(key, value);
		return this;
	}
	public JavaLaunch d(Properties prop){
		for (Object key : prop.keySet())
			this.properties.put((String)key, (String) prop.getProperty((String)key));
		return this;
	}
	static String encloseDoubleQuotes(String source){
		if (!source.contains(" "))
			return source;
		if(source.startsWith("\"") && source.endsWith("\""))
			return source;
		else
			return "\"" + source + "\"";
	}
	public <T> JavaMain<T> main(Class<T> main, String... args) throws JavaLaunchException{
		ProcessBuilder builder = new ProcessBuilder(buildCommand(main, args));
		builder.environment().putAll(environment);
		try {
			return new JavaMain<T>(main, builder.start());
		} catch (IOException e) {
			throw new JavaLaunchException(e);
		}
	}
	
	<T> String command(Class<T> main, String... args){
		List<String> commandList = buildCommand(main, args);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String cmd : commandList){
			if (first) first = false;
			else sb.append(" ");
			sb.append(cmd);
		}
		return sb.toString();
	}
	
	private <T> List<String> buildCommand(Class<T> main, String... args){
		CommandBuilder cmd = new CommandBuilder();
		cmd.put(java);
		if(classpath != null) cmd.put("-cp",classpath);
		if(xmx != null) cmd.put("-Xmx"+xmx);
		if(xms != null) cmd.put("-Xms"+xms);
		for (String key : properties.keySet())
			cmd.put("-D"+key+
					"="+
					encloseDoubleQuotes(properties.get(key)));
		cmd.put(main.getName()).put(args);
		return cmd.commandList;
	}
	
	class CommandBuilder {
		private List<String> commandList = new ArrayList<String>();
		CommandBuilder put(String... strs){
			if (strs.length > 0)
				commandList.addAll(Arrays.asList(strs));
			return this;
		}
		List<String> asList(){
			return commandList;
		}
	}
}