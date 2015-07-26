package org.herac.tuxguitar.app.tools.browser.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdElementHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdRootHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdUpHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCloseHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserListElementsHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserOpenHandler;

public class TGBrowserImpl implements TGBrowser {
	
	private File root;
	private TGBrowserElementImpl element;
	private TGBrowserDataImpl data;
	
	public TGBrowserImpl(TGBrowserDataImpl data){
		this.data = data;
	}
	
	public void open(TGBrowserOpenHandler handler) {
		try {
			this.root = new File(this.data.getPath());
			
			handler.onSuccess();
		} catch(Throwable throwable) {
			handler.handleError(throwable);
		}
	}
	
	public void close(TGBrowserCloseHandler handler) {
		this.root = null;
		
		handler.onSuccess();
	}
	
	public void cdElement(TGBrowserElement element, TGBrowserCdElementHandler handler) {
		this.element = (TGBrowserElementImpl)element;
		
		handler.onSuccess();
	}
	
	public void cdRoot(TGBrowserCdRootHandler handler) {
		this.element = null;
		
		handler.onSuccess();
	}
	
	public void cdUp(TGBrowserCdUpHandler handler) {
		if( this.element != null ){
			this.element = this.element.getParent();
		}
		
		handler.onSuccess();
	}
	
	public void listElements(TGBrowserListElementsHandler handler) {
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
			handler.onSuccess(elements);
		} catch (Throwable throwable ) {
			handler.handleError(throwable);
		}
	}	
}
