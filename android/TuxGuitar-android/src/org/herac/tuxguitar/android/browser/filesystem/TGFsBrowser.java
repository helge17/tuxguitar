package org.herac.tuxguitar.android.browser.filesystem;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserElementComparator;
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
	
	public void open(TGBrowserCallBack<Object> cb){
		try {
			this.root = new File(TGExpressionResolver.getInstance(this.context).resolve(this.data.getPath()));
			this.element = null;
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void close(TGBrowserCallBack<Object> cb){
		try {
			this.root = null;
			this.element = null;
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try {
			this.element = (TGFsBrowserElement) element;
			
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try {
			this.element = new TGFsBrowserElement(this.root, null);
			
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
					Collections.sort(elements, new TGBrowserElementComparator());
				}
			}
			
			cb.onSuccess(elements);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}

	public void createElement(TGBrowserCallBack<TGBrowserElement> cb, String name) {
		try {
			TGBrowserElement element = null;
			if( this.isWritable() ) {
				File file = new File(this.element.getFile(), name);
				
				element = new TGFsBrowserElement(file, this.element);
			}
			cb.onSuccess(element);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			cb.onSuccess(((TGFsBrowserElement) element).getInputStream());
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void getOutputStream(TGBrowserCallBack<OutputStream> cb, TGBrowserElement element) {
		try {
			cb.onSuccess(((TGFsBrowserElement) element).getOutputStream());
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public boolean isWritable() {
		return (this.element != null && this.element.isFolder() && this.element.isWritable());
	}	
}
