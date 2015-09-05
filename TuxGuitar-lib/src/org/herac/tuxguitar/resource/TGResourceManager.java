package org.herac.tuxguitar.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGResourceManager implements TGResourceLoader {
	
	private TGResourceLoader resourceLoader;
	
	private TGResourceManager(){
		super();
	}
	
	public <T> Class<T> loadClass(String name) throws TGResourceException {
		return this.resourceLoader.loadClass(name);
	}

	public InputStream getResourceAsStream(String name) throws TGResourceException {
		return this.resourceLoader.getResourceAsStream(name);
	}

	public URL getResource(String name) throws TGResourceException {
		return this.resourceLoader.getResource(name);
	}

	public Enumeration<URL> getResources(String name) throws TGResourceException {
		return this.resourceLoader.getResources(name);
	}
	
	public TGResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(TGResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public static TGResourceManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGResourceManager.class.getName(), new TGSingletonFactory<TGResourceManager>() {
			public TGResourceManager createInstance(TGContext context) {
				return new TGResourceManager();
			}
		});
	}
}
