package org.herac.tuxguitar.gui.tools.browser.dialog;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;

public abstract class TGBrowserBar implements LanguageLoader{
	private TGBrowserDialog browser;
	
	public TGBrowserBar(TGBrowserDialog browser){
		this.browser = browser;
	}
	
	public abstract void init(Shell shell);
	
	public abstract void updateItems();
	
	public abstract void updateCollections(TGBrowserCollection selection);
	
	protected TGBrowserDialog getBrowser(){
		return this.browser;
	}
	
	protected void newCollection(String type){
		TGBrowserFactory factory = TGBrowserManager.instance().getFactory(type);
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
		collection = TGBrowserManager.instance().addCollection(collection);
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
	
}
