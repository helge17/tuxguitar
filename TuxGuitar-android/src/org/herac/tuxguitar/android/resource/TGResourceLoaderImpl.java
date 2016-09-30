package org.herac.tuxguitar.android.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.resource.TGResourceException;
import org.herac.tuxguitar.resource.TGResourceLoader;

import android.content.Context;
import android.content.res.AssetManager;
import dalvik.system.DexClassLoader;

public class TGResourceLoaderImpl implements TGResourceLoader {
	
	private static final String ASSET_PLUGINS = "plugins";
	private static final Integer ASSET_BUFFER_SIZE = (8 * 1024);
	
	private static ClassLoader classLoader;
	
	private TGActivity activity;
	
	public TGResourceLoaderImpl(TGActivity activity) {
		this.activity = activity;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> loadClass(String name) throws TGResourceException {
		try {
			return (Class<T>) getClassLoader().loadClass(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) throws TGResourceException {
		try {
			return getClassLoader().getResourceAsStream(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}

	@Override
	public URL getResource(String name) throws TGResourceException {
		try {
			return getClassLoader().getResource(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}

	@Override
	public Enumeration<URL> getResources(String name) throws TGResourceException {
		try {
			return getClassLoader().getResources(name);
		} catch (Throwable e) {
			throw new TGResourceException(e);
		}
	}
	
	public ClassLoader createClassLoader() throws TGResourceException {
		Context context = this.activity.getApplicationContext();
		
		String optimizedDirectory = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
		
		List<String> fileNames = this.unpackPlugins(optimizedDirectory);
		if(!fileNames.isEmpty()) {
			return new DexClassLoader(this.createPath(fileNames), optimizedDirectory, this.createLibraryPath(), context.getClassLoader());
		}
		return context.getClassLoader();
	}
	
	public List<String> unpackPlugins(String path) {
		try {
			List<String> fileNames = new ArrayList<String>();
			
			AssetManager assetManager = this.activity.getAssets();
			String[] assets = assetManager.list(ASSET_PLUGINS);
			if( assets != null ) {
				for(String asset : assets) {
					File pluginFileName = new File(path, asset);
					
					InputStream inputStream = new BufferedInputStream(assetManager.open(ASSET_PLUGINS + File.separator + asset));
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pluginFileName));
					byte[] buffer = new byte[ASSET_BUFFER_SIZE];
					int length = 0;
					while((length = inputStream.read(buffer, 0, ASSET_BUFFER_SIZE)) > 0) {
						outputStream.write(buffer, 0, length);
					}
					outputStream.close();
					inputStream.close();
					
					fileNames.add(pluginFileName.getAbsolutePath());
				}
			}
			
			return fileNames;
		} catch (IOException e) {
			throw new TGResourceException(e);
		}
	}
	
	public String createPath(List<String> fileNames) {
		StringBuffer path = new StringBuffer();
		for(String fileName : fileNames) {
			if( path.length() > 0 ) {
				path.append(File.pathSeparator);
			}
			path.append(fileName);
		}
		return path.toString();
	}
	
	public String createLibraryPath() {
		return this.activity.getApplicationInfo().nativeLibraryDir;
	}
	
	public ClassLoader getClassLoader() throws TGResourceException {
		synchronized (TGResourceLoaderImpl.class) {
			if( classLoader == null ) {
				classLoader = this.createClassLoader();
			}
		}
		return classLoader;
	}
}
