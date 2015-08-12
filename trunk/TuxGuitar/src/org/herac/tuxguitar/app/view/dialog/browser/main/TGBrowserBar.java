package org.herac.tuxguitar.app.view.dialog.browser.main;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public abstract class TGBrowserBar implements TGEventListener{
	
	private TGBrowserDialog browser;
	
	public TGBrowserBar(TGBrowserDialog browser){
		this.browser = browser;
	}
	
	public abstract void init(Shell shell);
	
	public abstract void updateItems();
	
	public abstract void updateCollections(TGBrowserCollection selection);
	
	public abstract void loadProperties();
	
	protected TGBrowserDialog getBrowser(){
		return this.browser;
	}
	
	protected void newCollection(String type){
		TGBrowserFactory factory = TGBrowserManager.getInstance(getBrowser().getContext()).getFactory(type);
		if(factory != null){
			TGBrowserData data = factory.dataDialog(getBrowser().getShell());
			if(data != null){
				openCollection(addCollection(factory, data, true));
			}
		}
	}
	
	protected TGBrowserCollection addCollection(TGBrowserFactory factory,String data){
		return this.addCollection(factory,factory.parseData(data),false);
	}
	
	protected TGBrowserCollection addCollection(TGBrowserFactory factory,TGBrowserData data,boolean reload){
		TGBrowserCollection collection = new TGBrowserCollection();
		collection.setType(factory.getType());
		collection.setData(data);
		collection = TGBrowserManager.getInstance(getBrowser().getContext()).addCollection(collection);
		if(reload){
			getBrowser().updateCollections(collection);
		}
		return collection;
	}
	
	protected void openCollection(TGBrowserCollection collection){
		getBrowser().setCollection(collection);
		getBrowser().openCollection();
	}
	
	protected void removeCollection(TGBrowserCollection collection){
		getBrowser().removeCollection(collection);
	}
	
	protected void closeCollection(){
		getBrowser().closeCollection();
	}
	
	public void processEvent(TGEvent event) {
		if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
	}
}
