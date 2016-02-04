package org.herac.tuxguitar.android.browser.assets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.util.TGContext;

import android.content.res.AssetManager;

public class TGAssetBrowserElement implements TGBrowserElement{
	
	private TGContext context;
	private TGAssetBrowserElement parent;
	private String name;
	private List<TGBrowserElement> childreen;
	
	public TGAssetBrowserElement(TGContext context, TGAssetBrowserElement parent, String name) {
		this.context = context;
		this.parent = parent;
		this.name = name;
	}
	
	public TGAssetBrowserElement getParent() {
		return this.parent;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isFolder() {
		return (this.name.indexOf(".") == -1);
	}
	
	public boolean isWritable() {
		return false;
	}
	
	public List<TGBrowserElement> getChildreen() throws TGBrowserException {
		if( this.childreen == null ) {
			this.childreen = this.findChildreen();
		}
		return this.childreen;
	}
	
	public List<TGBrowserElement> findChildreen() throws TGBrowserException {
		try {
			List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			
			AssetManager assetManager = findAssetManager();
			if( assetManager != null ) {
				String[] assets = assetManager.list(this.getFullPath());
				if( assets != null ) {
					for(int i = 0; i < assets.length; i ++){
						elements.add(new TGAssetBrowserElement(this.context, this, assets[i]));
					}
				}
			}
			
			return elements;
		} catch (IOException e) {
			throw new TGBrowserException();
		}
	}
	
	public InputStream getInputStream() throws TGBrowserException {
		if(!isFolder()){
			try {
				AssetManager assetManager = findAssetManager();
				if( assetManager != null ) {
					return assetManager.open(this.getFullPath());
				}
			} catch (IOException e) {
				throw new TGBrowserException(e);
			}
		}
		return null;
	}
	
	private String getFullPath() throws TGBrowserException {
		String path = this.getName();
		
		TGAssetBrowserElement parent = this.getParent();
		while( parent != null ) {
			path = (parent.getName() + File.separator + path);
			parent = parent.getParent();
		}
		return path;
	}
	
	private AssetManager findAssetManager() {
		TGActivityController controller = TGActivityController.getInstance(this.context);
		if( controller.getActivity() != null ) {
			return controller.getActivity().getAssets();
		}
		return null;
	}
}
