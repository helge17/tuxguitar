package org.herac.tuxguitar.util.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGServiceReader;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPluginManager {
	
	private static final String PLUGIN_ERROR_ON_LOOKUP = "An error ocurred when trying to lookup plugin";
	private static final String PLUGIN_ERROR_ON_CONNECT = "An error ocurred when trying to connect plugin";
	private static final String PLUGIN_ERROR_ON_DISCONNECT = "An error ocurred when trying to disconnect plugin";
	private static final String PLUGIN_ERROR_ON_GET_STATUS = "An error ocurred when trying to get plugin status";
	private static final String PLUGIN_ERROR_ON_SET_STATUS = "An error ocurred when trying to set plugin status";
	
	private TGContext context;
	private List<TGPlugin> plugins;
	
	private TGPluginManager(TGContext context){
		this.context = context;
		this.plugins = new ArrayList<TGPlugin>();
		this.lookupPlugins();
	}
	
	public List<TGPlugin> getPlugins(){
		return this.plugins;
	}

	public void lookupPlugins(){
		try {
			this.plugins.clear();
			
			// Search available providers
			Iterator<TGPlugin> it = TGServiceReader.lookupProviders(TGPlugin.class, TGResourceManager.getInstance(this.context));
			while( it.hasNext() ){
				try {
					TGPlugin tgPlugin = it.next();
					if( tgPlugin.getModuleId() != null ) {
						this.plugins.add(tgPlugin);
					}
				} catch(Throwable throwable) {
					TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_LOOKUP, throwable));
				}
			}
		} catch(Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_LOOKUP, throwable));
		}
	}
	
	public void disconnectAll(){
		for(TGPlugin plugin : this.plugins) {
			this.disconnectPlugin(plugin);
		}
	}
	
	public void connectEnabled() {
		for(TGPlugin plugin : this.plugins) {
			if( this.isEnabled(plugin.getModuleId()) ) {
				this.connectPlugin(plugin);
			}
		}
	}
	
	public void connectPlugins(String moduleId) {
		for(TGPlugin plugin : this.plugins) {
			if( plugin.getModuleId().equals(moduleId) ) {
				this.connectPlugin(plugin);
			}
		}
	}
	
	public void disconnectPlugins(String moduleId) {
		for(TGPlugin plugin : this.plugins) {
			if( plugin.getModuleId().equals(moduleId) ) {
				this.disconnectPlugin(plugin);
			}
		}
	}
	
	public void connectPlugin(TGPlugin tgPlugin) {
		try {
			tgPlugin.connect(this.context);
		} catch(TGPluginException exception) {
			TGErrorManager.getInstance(this.context).handleError(exception);
		} catch(Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_CONNECT, throwable));
		}
	}
	
	public void disconnectPlugin(TGPlugin tgPlugin) {
		try {
			tgPlugin.disconnect(this.context);
		} catch(TGPluginException exception) {
			TGErrorManager.getInstance(this.context).handleError(exception);
		} catch(Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_DISCONNECT, throwable));
		}
	}
	
	public void updatePluginStatus(String moduleId, boolean enabled) {
		try {
			TGPluginProperties.getInstance(this.context).setEnabled(moduleId, enabled);
			
			if( enabled ) {
				this.connectPlugins(moduleId);
			} else {
				this.disconnectPlugins(moduleId);
			}
		} catch(Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
		}
	}
	
	public boolean isEnabled(String moduleId){
		try {
			return TGPluginProperties.getInstance(this.context).isEnabled(moduleId);
		} catch(Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_GET_STATUS,throwable));
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TGPlugin> List<T> getPluginInstances(Class<T> pluginClass){
		List<T> pluginInstances = new ArrayList<T>();
		for(TGPlugin plugin : this.plugins) {
			if( pluginClass.isInstance(plugin) ){
				pluginInstances.add( (T)plugin );
			}
		}
		return pluginInstances;
	}
	
	public static TGPluginManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPluginManager.class.getName(), new TGSingletonFactory<TGPluginManager>() {
			public TGPluginManager createInstance(TGContext context) {
				return new TGPluginManager(context);
			}
		});
	}
}
