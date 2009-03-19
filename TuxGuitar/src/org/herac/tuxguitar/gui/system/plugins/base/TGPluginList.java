package org.herac.tuxguitar.gui.system.plugins.base;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.system.plugins.TGPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;

public abstract class TGPluginList extends TGPluginAdapter{
	
	private List plugins;
	
	public TGPluginList(){
		super();
	}
	
	public void init() throws TGPluginException {
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.init();
		}
	}
	
	public void close() throws TGPluginException {
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.close();
		}
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.setEnabled( enabled);
		}
	}
	
	private Iterator getIterator() throws TGPluginException {
		if(this.plugins == null){
			this.plugins = getPlugins();
		}
		return this.plugins.iterator();
	}
	
	protected abstract List getPlugins() throws TGPluginException ;
}
