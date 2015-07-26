package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdElementHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdRootHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdUpHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCloseHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserListElementsHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserOpenHandler;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserImpl implements TGBrowser {
	
	private TGBrowserConnection connection;
	private TGBrowserElementImpl element;
	
	public TGBrowserImpl(TGContext context, TGBrowserDataImpl data){
		this.element = null;
		this.connection = new TGBrowserConnection(context);
	}
	
	public void open(TGBrowserOpenHandler handler) {
		handler.onSuccess();
	}
	
	public void close(TGBrowserCloseHandler handler) {
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
	
	public void cdElement(TGBrowserElement element, TGBrowserCdElementHandler handler) {
		if( element instanceof TGBrowserElementImpl ){
			TGBrowserElementImpl nextElement = (TGBrowserElementImpl)element;
			nextElement.setParent( this.element );
			this.element = nextElement;
		}
		
		handler.onSuccess();
	}
	
	public void listElements(TGBrowserListElementsHandler handler) {
		this.connection.fillElements(this.element, handler);
	}
}
