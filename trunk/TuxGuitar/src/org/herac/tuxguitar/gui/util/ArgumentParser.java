package org.herac.tuxguitar.gui.util;

import java.io.File;
import java.net.URL;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.util.TGVersion;

public class ArgumentParser {
	
	private static final String TG_DEFAULT_URL = "tuxguitar.default.url";
	
	private static final String[] OPTION_HELP = new String[]{"-h","--help"};
	private static final String[] OPTION_VERSION = new String[]{"-v","--version"};
	private static final String[] OPTION_JRE_INFO = new String[]{"-i","--system-info"};
	
	private String[] arguments;
	private boolean processAndExit;
	private URL url;
	
	public ArgumentParser(String[] arguments){
		this.arguments = arguments;
		this.processAndExit = false;
		this.parse();
	}
	
	private void parse(){
		try{
			checkHelp();
			checkVersion();
			checkSystemInfo();
			if(!processAndExit()){
				checkProperties();
				checkURL();
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	private void checkHelp(){
		for(int i = 0;i < this.arguments.length;i++){
			for(int j = 0;j < OPTION_HELP.length;j++){
				if(this.arguments[i].equals(OPTION_HELP[j])){
					print("usage: TuxGuitar [file]");
					print("Options:");
					print("	-h, --help                 Show help options");
					print("	-v, --version              Show version information and exit");
					print("	-i, --system-info          Show the JVM system information");
					print("	-D<name>=<value>           Set a JVM system property");
					
					this.processAndExit = true;
				}
			}
		}
	}
	
	private void checkVersion(){
		for(int i = 0;i < this.arguments.length;i++){
			for(int j = 0;j < OPTION_VERSION.length;j++){
				if(this.arguments[i].equals(OPTION_VERSION[j])){
					print(TuxGuitar.APPLICATION_NAME + " - " + TGVersion.CURRENT.getVersion());
					
					this.processAndExit = true;
				}
			}
		}
	}
	
	private void checkSystemInfo(){
		for(int i = 0;i < this.arguments.length;i++){
			for(int j = 0;j < OPTION_JRE_INFO.length;j++){
				if(this.arguments[i].equals(OPTION_JRE_INFO[j])){
					print("System Info:");
					print("-> OS-Name:           " + System.getProperty("os.name"));
					print("-> OS-Arch:           " + System.getProperty("os.arch"));
					print("-> OS-Version:        " + System.getProperty("os.version"));
					print("-> JVM-Name:          " + System.getProperty("java.vm.name"));
					print("-> JVM-Version:       " + System.getProperty("java.vm.version"));
					print("-> JVM-Vendor:        " + System.getProperty("java.vm.vendor"));
					print("-> Java-Version:      " + System.getProperty("java.version"));
					print("-> Java-Vendor:       " + System.getProperty("java.vendor"));
					print("-> Java-Home:         " + System.getProperty("java.home"));
					print("-> Java-Class-Path:   " + System.getProperty("java.class.path"));
					print("-> Java-Library-Path: " + System.getProperty("java.library.path"));
					
					this.processAndExit = true;
				}
			}
		}
	}
	
	private void checkProperties(){
		for(int i = 0;i < this.arguments.length;i++){
			int indexKey = this.arguments[i].indexOf("-D");
			int indexValue = this.arguments[i].indexOf("=");
			if(indexKey == 0 && indexValue > indexKey && (indexValue + 1) < this.arguments[i].length() ){
				String key =  this.arguments[i].substring(2, indexValue);
				String value =  this.arguments[i].substring( indexValue + 1 );
				if( key != null && key.length() > 0 && value != null && value.length() > 0){
					System.setProperty( key, value);
				}
			}
		}
	}
	
	private void checkURL(){
		String propertyUrl = System.getProperty(TG_DEFAULT_URL);
		if( propertyUrl != null && makeURL( propertyUrl ) ){
			return;
		}
		for(int i = 0;i < this.arguments.length;i++){
			if(makeURL(this.arguments[i])){
				return;
			}
		}
	}
	
	private boolean makeURL(String spec){
		try{
			File file = new File(spec);
			if(file.exists()){
				this.url = file.toURI().toURL();
			}else{
				this.url = new URL(spec);
			}
		}catch(Throwable throwable){
			this.url = null;
		}
		return (this.url != null);
	}
	
	public boolean processAndExit(){
		return this.processAndExit;
	}
	
	public URL getURL() {
		return this.url;
	}
	
	protected void print(String s){
		print(s, true);
	}
	
	protected void print(String s, boolean ignoreNull){
		if(!ignoreNull || s != null){
			System.out.println( s );
		}
	}
	
}
