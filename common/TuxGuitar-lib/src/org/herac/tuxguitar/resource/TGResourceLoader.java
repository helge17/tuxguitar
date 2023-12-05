package org.herac.tuxguitar.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public interface TGResourceLoader {
	
	<T> Class<T> loadClass(String name) throws TGResourceException;
	
	InputStream getResourceAsStream(String name) throws TGResourceException;
	
	URL getResource(String name) throws TGResourceException;
	
	Enumeration<URL> getResources(String name) throws TGResourceException;
}
