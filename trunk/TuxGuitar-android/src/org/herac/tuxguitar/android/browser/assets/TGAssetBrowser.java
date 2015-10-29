package org.herac.tuxguitar.android.browser.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.util.TGContext;

public class TGAssetBrowser implements TGBrowser{
	
	private TGContext context;
	private TGAssetBrowserElement element;
	private TGAssetBrowserSettings data;
	
	public TGAssetBrowser(TGContext context, TGAssetBrowserSettings data){
		this.context = context;
		this.data = data;
	}
	
	public void open(){
		this.element = null;
	}
	
	public void close(){
		this.element = null;
	}
	
	public void cdElement(TGBrowserElement element) {
		this.element = (TGAssetBrowserElement) element;
	}
	
	public void cdRoot() {
		this.element = new TGAssetBrowserElement(this.context, null, this.data.getPath());
	}
	
	public void cdUp() {
		if( this.element != null && this.element.getParent() != null ){
			this.element = this.element.getParent();
		}
	}
	
	public List<TGBrowserElement> listElements() throws TGBrowserException {
		try {
			List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			if( this.element != null ) {
				elements.addAll(this.element.getChildreen());
				if( !elements.isEmpty() ){
					Collections.sort(elements, new TGAssetBrowserElementComparator());
				}
			}
			return elements;
		} catch (Throwable e) {
			throw new TGBrowserException(e);
		}
	}

	public TGBrowserElement createElement(String name) throws TGBrowserException {
		return null;
	}

	public boolean isWritable() throws TGBrowserException {
		return false;
	}
}
