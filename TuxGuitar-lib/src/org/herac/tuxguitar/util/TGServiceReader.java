package org.herac.tuxguitar.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class TGServiceReader {
	
	private static final String SERVICE_PATH = new String("META-INF/services/");
	
	public static Iterator lookupProviders(Class spi){
		return TGServiceReader.lookupProviders(spi,TGClassLoader.instance().getClassLoader());
	}
	
	public static Iterator lookupProviders(Class spi,ClassLoader loader){
		try{
			if (spi == null || loader == null){
				throw new IllegalArgumentException();
			}
			return new IteratorImpl(spi,loader,loader.getResources(SERVICE_PATH + spi.getName()));
		}catch (IOException ioex){
			return Collections.EMPTY_LIST.iterator();
		}
	}
	
	private static final class IteratorImpl implements Iterator{
		private Class spi;
		private ClassLoader loader;
		private Enumeration urls;
		private Iterator iterator;
		
		public IteratorImpl(Class spi,ClassLoader loader,Enumeration urls){
			this.spi = spi;
			this.loader = loader;
			this.urls = urls;
			this.initialize();
		}
		
		private void initialize(){
			List providers = new ArrayList();
			while (this.urls.hasMoreElements()) {
				URL url = (URL) this.urls.nextElement();
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
					String line = null;
					while((line = reader.readLine()) != null){
						String provider = uncommentLine(line).trim();
						if(provider != null && provider.length() > 0){
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
		
		public Object next() {
			if (!hasNext()){
				throw new NoSuchElementException();
			}
			try {
				Object provider = this.loader.loadClass( (String)this.iterator.next() ).newInstance();
				if(this.spi.isInstance(provider)){
					return provider;
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
