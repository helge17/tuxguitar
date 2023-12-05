package org.herac.tuxguitar.android.browser.assets;

import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.util.TGContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TGAssetBrowser implements TGBrowser{
	
	private TGContext context;
	private TGAssetBrowserElement element;
	private TGAssetBrowserSettings data;
	
	public TGAssetBrowser(TGContext context, TGAssetBrowserSettings data){
		this.context = context;
		this.data = data;
	}
	
	public void open(TGBrowserCallBack<Object> cb){
		try{
			this.element = null;
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void close(TGBrowserCallBack<Object> cb){
		try{
			this.element = null;
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try{
			this.element = (TGAssetBrowserElement) element;
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try{
			this.element = new TGAssetBrowserElement(this.context, null, this.data.getPath());
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdUp(TGBrowserCallBack<Object> cb) {
		try {
			if( this.element != null && this.element.getParent() != null ){
				this.element = this.element.getParent();
			}
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb) {
		try {
			List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			if( this.element != null ) {
				elements.addAll(this.element.getChildreen());
				if( !elements.isEmpty() ){
					Collections.sort(elements, new TGAssetBrowserElementComparator());
				}
			}

			cb.onSuccess(elements);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}

	public void createElement(TGBrowserCallBack<TGBrowserElement> cb, String name) {
		try {
			cb.onSuccess(null);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			cb.onSuccess(((TGAssetBrowserElement) element).getInputStream());
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void getOutputStream(TGBrowserCallBack<OutputStream> cb, TGBrowserElement element) {
		try {
			throw new TGBrowserException("No writable file system");
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public boolean isWritable() {
		return false;
	}
}
