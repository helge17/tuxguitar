package org.herac.tuxguitar.android.browser.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class TGFsBrowser implements TGBrowser{
	
	private TGContext context;
	private TGFsBrowserSettings data;
	private TGFsBrowserElement element;
	private File root;
	
	public TGFsBrowser(TGContext context, TGFsBrowserSettings data){
		this.context = context;
		this.data = data;
	}
	
	public void open(){
		this.root = new File(TGExpressionResolver.getInstance(this.context).resolve(this.data.getPath()));
		this.element = null;
	}
	
	public void close(){
		this.root = null;
		this.element = null;
	}
	
	public void cdElement(TGBrowserElement element) {
		this.element = (TGFsBrowserElement) element;
	}
	
	public void cdRoot() {
		this.element = new TGFsBrowserElement(this.root, null);
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
				File file = this.element.getFile();
				if( file.exists() && file.isDirectory() ) {
					File[] files = file.listFiles();
					if( files != null ) {
						for(int i = 0; i < files.length;i ++){
							elements.add(new TGFsBrowserElement(files[i], this.element));
						}
					}
				}
				if( !elements.isEmpty() ){
					Collections.sort(elements, new TGFsBrowserElementComparator());
				}
			}
			return elements;
		} catch (Throwable e) {
			throw new TGBrowserException(e);
		}
	}

	public TGBrowserElement createElement(String name) throws TGBrowserException {
		if( this.isWritable() ) {
			File file = new File(this.element.getFile(), name);
			
			return new TGFsBrowserElement(file, this.element);
		}
		return null;
	}

	public boolean isWritable() throws TGBrowserException {
		return (this.element != null && this.element.isFolder() && this.element.isWritable());
	}	
}
