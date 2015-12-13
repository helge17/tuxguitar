package org.herac.tuxguitar.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Vector;

import org.herac.tuxguitar.resource.TGResourceException;
import org.herac.tuxguitar.resource.TGResourceLoader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGClassLoader implements TGResourceLoader {
	
	private String[] filePaths;
	private URLClassLoaderImpl classLoader;
	
	private TGClassLoader(){
		this.classLoader = new URLClassLoaderImpl();
	}
	
	@SuppressWarnings("unchecked")
	public <T> Class<T> loadClass(String name) throws TGResourceException {
		try {
			return (Class<T>)this.classLoader.loadClass(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}

	public InputStream getResourceAsStream(String name) throws TGResourceException {
		try {
			if( this.getFilePaths() != null){
				for( int i = 0; i < this.getFilePaths().length ; i ++ ){
					File file = new File(this.getFilePaths()[i] + File.separator + name);
					if( TGFileUtils.isExistentAndReadable( file ) ){
						return new FileInputStream( file );
					}
				}
			}
			return this.classLoader.getResourceAsStream(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}

	public URL getResource(String name) throws TGResourceException {
		try {
			if( this.getFilePaths() != null ){
				for( int i = 0; i < this.getFilePaths().length ; i ++ ){
					File file = new File(this.getFilePaths()[i] + File.separator + name);
					if( TGFileUtils.isExistentAndReadable( file ) ){
						return file.toURI().toURL();
					}
				}
			}
			return this.classLoader.getResource(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}

	public Enumeration<URL> getResources(String name) throws TGResourceException {
		try {
			Vector<URL> vector = new Vector<URL>();
			if( this.getFilePaths() != null){
				for( int i = 0; i < this.getFilePaths().length ; i ++ ){
					File file = new File(this.getFilePaths()[i] + File.separator + name);
					if( TGFileUtils.isExistentAndReadable( file ) ){
						vector.addElement( file.toURI().toURL() );
					}
				}
			}
			Enumeration<URL> resources = this.classLoader.getResources(name);
			while( resources.hasMoreElements() ){
				URL url = (URL)resources.nextElement();
				if( !vector.contains(url) ){
					vector.addElement( url );
				}
			}
			return vector.elements();
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
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
	
	public ClassLoader getClassLoader(){
		return this.classLoader;
	}
	
	public String[] getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(String[] filePaths) {
		this.filePaths = filePaths;
	}

	public static TGClassLoader getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGClassLoader.class.getName(), new TGSingletonFactory<TGClassLoader>() {
			public TGClassLoader createInstance(TGContext context) {
				return new TGClassLoader();
			}
		});
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