package org.herac.tuxguitar.app.tools.browser.filesystem;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public class TGBrowserImpl implements TGBrowser {
	
	private File root;
	private TGBrowserElementImpl element;
	private TGBrowserSettingsModel data;
	
	public TGBrowserImpl(TGBrowserSettingsModel data){
		this.data = data;
	}
	
	public void open(TGBrowserCallBack<Object> cb) {
		try {
			this.root = new File(this.data.getPath());
			
			cb.onSuccess(null);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void close(TGBrowserCallBack<Object> cb) {
		try {
			this.root = null;
			
			cb.onSuccess(null);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try {
			this.element = (TGBrowserElementImpl)element;
			
			cb.onSuccess(this.element);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try {
			this.element = null;
			
			cb.onSuccess(this.element);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void cdUp(TGBrowserCallBack<Object> cb) {
		try {
			if( this.element != null ){
				this.element = this.element.getParent();
			}
			
			cb.onSuccess(this.element);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb) {
		try { 
			List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			File file = ((this.element != null)?this.element.getFile():this.root);
			if( file.exists() && file.isDirectory() ){
				File[] files = file.listFiles();
				for(int i = 0; i < files.length;i ++){
					elements.add(new TGBrowserElementImpl(this.element,files[i]));
				}
			}
			if( !elements.isEmpty() ){
				Collections.sort(elements, new TGBrowserElementComparator());
			}
			cb.onSuccess(elements);
		} catch (Throwable throwable ) {
			cb.handleError(throwable);
		}
	}
	
	public void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			cb.onSuccess(((TGBrowserElementImpl) element).getInputStream());
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
}
