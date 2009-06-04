package org.herac.tuxguitar.gui.system.plugins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.util.TGClassLoader;
import org.herac.tuxguitar.util.TGServiceReader;

public class TGPluginManager {
	
	private List plugins;
	
	public TGPluginManager(){
		this.plugins = new ArrayList();
		this.initPlugins();
	}
	
	public List getPlugins(){
		return this.plugins;
	}
	
	public void initPlugins(){
		try{
			//Search available providers
			Iterator it = TGServiceReader.lookupProviders(TGPlugin.class,TGClassLoader.instance().getClassLoader());
			while(it.hasNext()){
				try{
					TGPlugin plugin = (TGPlugin)it.next();
					plugin.init();
					this.plugins.add(plugin);
				}catch(TGPluginException exception){
					MessageDialog.errorMessage(exception);
				}catch(Throwable throwable){
					MessageDialog.errorMessage(new TGPluginException("An error ocurred when trying to init plugin",throwable));
				}
			}
		}catch(Throwable throwable){
			MessageDialog.errorMessage(new TGPluginException("An error ocurred when trying to init plugin",throwable));
		}
	}
	
	public void closePlugins(){
		Iterator it = this.plugins.iterator();
		while(it.hasNext()){
			try{
				((TGPlugin)it.next()).close();
			}catch(TGPluginException exception){
				MessageDialog.errorMessage(exception);
			}catch(Throwable throwable){
				MessageDialog.errorMessage(new TGPluginException("An error ocurred when trying to close plugin",throwable));
			}
		}
	}
	
	public void openPlugins(){
		Iterator it = this.plugins.iterator();
		while(it.hasNext()){
			try{
				TGPlugin plugin = (TGPlugin)it.next();
				plugin.setEnabled(isEnabled(plugin));
			}catch(TGPluginException exception){
				MessageDialog.errorMessage(exception);
			}catch(Throwable throwable){
				MessageDialog.errorMessage(new TGPluginException("An error ocurred when trying to set plugin status",throwable));
			}
		}
	}
	
	public void setEnabled(TGPlugin plugin,boolean enabled){
		try{
			TGPluginProperties.instance().setProperty(getEnabledProperty(plugin),enabled);
			TGPluginProperties.instance().save();
			plugin.setEnabled(enabled);
		}catch(TGPluginException exception){
			MessageDialog.errorMessage(exception);
		}catch(Throwable throwable){
			MessageDialog.errorMessage(new TGPluginException("An error ocurred when trying to set plugin status",throwable));
		}
	}
	
	public boolean isEnabled(TGPlugin plugin){
		try{
			return TGPluginProperties.instance().getBooleanConfigValue(getEnabledProperty(plugin),true);
		}catch(Throwable throwable){
			MessageDialog.errorMessage(new TGPluginException("An error ocurred when trying to get plugin status",throwable));
		}
		return false;
	}
	
	public String getEnabledProperty(TGPlugin plugin){
		return (plugin.getClass().getName() + ".enabled");
	}
	
}
