package org.herac.tuxguitar.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class TGClassLoader{
	
	private static TGClassLoader instance;
	
	private URLClassLoaderImpl classLoader;
	
	private TGClassLoader(){
		this.classLoader = new URLClassLoaderImpl();
	}
	
	public static TGClassLoader instance(){
		if(instance == null){
			instance = new TGClassLoader();
		}
		return instance;
	}
	
	public ClassLoader getClassLoader(){
		return this.classLoader;
	}
	
	public Object newInstance(String loadClassName){
		Object object = null;
		try {
			object = getClassLoader().loadClass(loadClassName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public void addPath(String path){
		try {
			this.classLoader.addURL(new File(path).toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void addPaths(File folder){
		if(folder != null && folder.exists() && folder.isDirectory()){
			String[] files = folder.list();
			for(int i = 0;i < files.length;i++){
				try {
					this.addPath( (folder.getAbsolutePath() + File.separator + files[i]) );
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		}
	}
	
	private class URLClassLoaderImpl extends URLClassLoader{
		
		public URLClassLoaderImpl(){
			super(new URL[]{},TGClassLoader.class.getClassLoader());
		}
		
		public void addURL(URL url){
			super.addURL(url);
		}
		
	}
	
}