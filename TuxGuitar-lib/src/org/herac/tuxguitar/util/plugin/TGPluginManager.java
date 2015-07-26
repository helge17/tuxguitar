package org.herac.tuxguitar.util.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.util.TGClassLoader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGServiceReader;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPluginManager {
	
	private static final String PLUGIN_ERROR_ON_INIT = "An error ocurred when trying to init plugin";
	private static final String PLUGIN_ERROR_ON_CLOSE = "An error ocurred when trying to close plugin";
	private static final String PLUGIN_ERROR_ON_SET_STATUS = "An error ocurred when trying to set plugin status";
	private static final String PLUGIN_ERROR_ON_GET_STATUS = "An error ocurred when trying to get plugin status";
	
	private TGContext context;
	private List<TGPlugin> plugins;
	
	public static TGPluginManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPluginManager.class.getName(), new TGSingletonFactory<TGPluginManager>() {
			public TGPluginManager createInstance(TGContext context) {
				return new TGPluginManager(context);
			}
		});
	}
	
	private TGPluginManager(TGContext context){
		this.context = context;
		this.plugins = new ArrayList<TGPlugin>();
		this.initialize();
	}
	
	public void initialize(){
		this.initPlugins();
	}
	
	public List<TGPlugin> getPlugins(){
		return this.plugins;
	}

	public void initPlugins(){
		try{
			//Search available providers
			Iterator<TGPlugin> it = TGServiceReader.lookupProviders(TGPlugin.class,TGClassLoader.getInstance().getClassLoader());
			while(it.hasNext()){
				try{
					TGPlugin tgPlugin = (TGPlugin)it.next();
					if( tgPlugin.getModuleId() != null ){
						tgPlugin.init(this.context);
						this.plugins.add(tgPlugin);
					}
				}catch(TGPluginException exception){
					TGErrorManager.getInstance(this.context).handleError(exception);
				}catch(Throwable throwable){
					TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_INIT,throwable));
				}
			}
		}catch(Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_INIT,throwable));
		}
	}
	
	public void closePlugins(){
		Iterator<TGPlugin> it = this.plugins.iterator();
		while(it.hasNext()){
			try{
				((TGPlugin)it.next()).close();
			}catch(TGPluginException exception){
				TGErrorManager.getInstance(this.context).handleError(exception);
			}catch(Throwable throwable){
				TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_CLOSE,throwable));
			}
		}
	}
	
	public void openPlugins(){
		Iterator<TGPlugin> it = this.plugins.iterator();
		while(it.hasNext()){
			try{
				TGPlugin tgPlugin = (TGPlugin)it.next();
				tgPlugin.setEnabled( isEnabled(tgPlugin.getModuleId()) );
			}catch(TGPluginException exception){
				TGErrorManager.getInstance(this.context).handleError(exception);
			}catch(Throwable throwable){
				TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
			}
		}
	}
	
	public void setEnabled(String moduleId, boolean enabled){
		try{
			TGPluginProperties.getInstance(this.context).setEnabled(moduleId, enabled);
			
			Iterator<TGPlugin> it = this.plugins.iterator();
			while(it.hasNext()){
				try{
					TGPlugin tgPlugin = (TGPlugin)it.next();
					if( tgPlugin.getModuleId().equals(moduleId) ){
						tgPlugin.setEnabled(enabled);
					}
				}catch(TGPluginException exception){
					TGErrorManager.getInstance(this.context).handleError(exception);
				}catch(Throwable throwable){
					TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
				}
			}
		}catch(Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
		}
	}
	
	public boolean isEnabled(String moduleId){
		try{
			return TGPluginProperties.getInstance(this.context).isEnabled(moduleId);
		}catch(Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(new TGPluginException(PLUGIN_ERROR_ON_GET_STATUS,throwable));
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TGPlugin> List<T> getPluginInstances(Class<T> pluginClass){
		List<T> pluginInstances = new ArrayList<T>();
				
		Iterator<TGPlugin> it = this.plugins.iterator();
		while(it.hasNext()){
			TGPlugin pluginInstance = (TGPlugin)it.next();
			if( pluginClass.isInstance(pluginInstance) ){
				pluginInstances.add( (T)pluginInstance );
			}
		}
		
		return pluginInstances;
	}
}
