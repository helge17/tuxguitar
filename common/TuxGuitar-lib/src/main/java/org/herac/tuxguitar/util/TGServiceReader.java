package org.herac.tuxguitar.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.herac.tuxguitar.resource.TGResourceException;
import org.herac.tuxguitar.resource.TGResourceLoader;
import org.herac.tuxguitar.resource.TGResourceManager;

public class TGServiceReader {
	
	private static final String SERVICE_PATH = new String("META-INF/services/");
	
	public static <T> Iterator<T> lookupProviders(Class<T> spi, TGContext context){
		return TGServiceReader.lookupProviders(spi, TGResourceManager.getInstance(context));
	}
	
	public static <T> Iterator<T> lookupProviders(Class<T> spi, TGResourceLoader loader){
		try{
			if (spi == null || loader == null){
				throw new IllegalArgumentException();
			}
			return new IteratorImpl<T>(spi, loader, loader.getResources(SERVICE_PATH + spi.getName()));
		}catch (TGResourceException e){
			return new ArrayList<T>().iterator();
		}
	}
	
	private static final class IteratorImpl<T> implements Iterator<T> {
		
		private Class<T> spi;
		private TGResourceLoader loader;
		private Enumeration<URL> urls;
		private Iterator<String> iterator;
		
		public IteratorImpl(Class<T> spi, TGResourceLoader loader, Enumeration<URL> urls){
			this.spi = spi;
			this.loader = loader;
			this.urls = urls;
			this.initialize();
		}
		
		private void initialize(){
			List<String> providers = new ArrayList<String>();
			while (this.urls.hasMoreElements()) {
				URL url = this.urls.nextElement();
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
					String line = null;
					while((line = reader.readLine()) != null){
						String provider = uncommentLine(line).trim();
						if( provider != null && provider.length() > 0 ){
							providers.add(provider);
						}
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			this.iterator = providers.iterator();
		}
		
		private String uncommentLine(String line){
			int index = line.indexOf('#');
			if(index >= 0){
				return (line.substring(0,index));
			}
			return line;
		}
		
		public boolean hasNext() {
			return (this.iterator != null && this.iterator.hasNext());
		}
		
		@SuppressWarnings("unchecked")
		public T next() {
			if (!hasNext()){
				throw new NoSuchElementException();
			}
			try {
				Object provider = this.loader.loadClass( this.iterator.next() ).newInstance();
				if( this.spi.isInstance(provider) ){
					return (T) provider;
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
			throw new NoSuchElementException();
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
