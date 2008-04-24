package org.herac.tuxguitar.gui.system.plugins.base;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.system.plugins.TGPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;

public abstract class TGPluginList extends TGPluginAdapter{
	
	private boolean enabled;
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
		this.enabled = enabled;
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.setEnabled(this.enabled);
		}
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	private Iterator getIterator(){
		if(this.plugins == null){
			this.plugins = getPlugins();
		}
		return this.plugins.iterator();
	}
	
	protected abstract List getPlugins();
}
