package org.herac.tuxguitar.community.browser;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public class TGBrowserImpl extends TGBrowser {
	
	private TGBrowserConnection connection;
	private TGBrowserElementImpl element;
	
	public TGBrowserImpl(TGBrowserDataImpl data){
		this.element = null;
		this.connection = new TGBrowserConnection();
	}
	
	public void open() throws TGBrowserException {
		// TODO Auto-generated method stub
	}
	
	public void close() throws TGBrowserException {
		// TODO Auto-generated method stub
	}
	
	public void cdRoot() throws TGBrowserException {
		this.element = null;
	}
	
	public void cdUp() throws TGBrowserException {
		if( this.element != null ){
			this.element = this.element.getParent();
		}
	}
	
	public void cdElement(TGBrowserElement element) throws TGBrowserException {
		if( element instanceof TGBrowserElementImpl ){
			TGBrowserElementImpl nextElement = (TGBrowserElementImpl)element;
			nextElement.setParent( this.element );
			this.element = nextElement;
		}
	}
	
	public List listElements() throws TGBrowserException {
		List elements = new ArrayList();
		this.connection.getElements(elements , this.element );
		return elements;
	}
}
