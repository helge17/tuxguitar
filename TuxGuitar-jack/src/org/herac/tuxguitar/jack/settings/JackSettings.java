package org.herac.tuxguitar.jack.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.system.plugins.TGPluginConfigManager;

public class JackSettings {
	
	private List listeners;
	private TGConfigManager config;
	
	public JackSettings(){
		this.listeners = new ArrayList();
		this.config = new TGPluginConfigManager("tuxguitar-jack");
		this.config.init();
	}
	
	public TGConfigManager getConfig(){
		return this.config;
	}
	
	public void notifyChanges(){
		this.getConfig().save();
		this.getConfig().load();
		this.fireListeners();
	}
	
	public void addListener(JackSettingsListener listener){
		if( !this.listeners.contains( listener ) ){
			this.listeners.add( listener );
		}
	}
	
	public void removeListener(JackSettingsListener listener){
		if( this.listeners.contains( listener ) ){
			this.listeners.remove( listener );
		}
	}
	
	public void fireListeners(){
		Iterator it = this.listeners.iterator();
		while( it.hasNext() ){
			JackSettingsListener listener = (JackSettingsListener) it.next();
			listener.loadSettings( getConfig() );
		}
	}
}
