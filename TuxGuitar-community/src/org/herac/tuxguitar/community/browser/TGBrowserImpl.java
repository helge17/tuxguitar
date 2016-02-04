package org.herac.tuxguitar.community.browser;

import java.io.InputStream;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserImpl implements TGBrowser {
	
	private TGBrowserConnection connection;
	private TGBrowserElementImpl element;
	
	public TGBrowserImpl(TGContext context){
		this.element = null;
		this.connection = new TGBrowserConnection(context);
	}
	
	public void open(TGBrowserCallBack<Object> cb) {
		try {
			cb.onSuccess(null);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void close(TGBrowserCallBack<Object> cb) {
		try {
			cb.onSuccess(null);
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
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try {
			if( element instanceof TGBrowserElementImpl ){
				TGBrowserElementImpl nextElement = (TGBrowserElementImpl)element;
				nextElement.setParent( this.element );
				this.element = nextElement;
			}
			
			cb.onSuccess(this.element);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb) {
		this.connection.fillElements(cb, this.element);
	}
	
	public void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			cb.onSuccess(((TGBrowserElementImpl) element).getInputStream());
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
}
