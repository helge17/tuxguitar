package org.herac.tuxguitar.util.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.util.TGClassLoader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGServiceReader;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;

public class TGPluginManager {
	
	private static final String PLUGIN_ERROR_ON_INIT = "An error ocurred when trying to init plugin";
	private static final String PLUGIN_ERROR_ON_CLOSE = "An error ocurred when trying to close plugin";
	private static final String PLUGIN_ERROR_ON_SET_STATUS = "An error ocurred when trying to set plugin status";
	private static final String PLUGIN_ERROR_ON_GET_STATUS = "An error ocurred when trying to get plugin status";
	
	private List plugins;
	
	public static TGPluginManager getInstance(TGContext context) {
		return (TGPluginManager) TGSingletonUtil.getInstance(context, TGPluginManager.class.getName(), new TGSingletonFactory() {
			public Object createInstance(TGContext context) {
				return new TGPluginManager(context);
			}
		});
	}
	
	private TGPluginManager(TGContext context){
		this.plugins = new ArrayList();
		this.initialize(context);
	}
	
	public void initialize(TGContext context){
		this.initPlugins(context);
	}
	
	public List getPlugins(){
		return this.plugins;
	}

	public void initPlugins(TGContext context){
		try{
			//Search available providers
			Iterator it = TGServiceReader.lookupProviders(TGPlugin.class,TGClassLoader.instance().getClassLoader());
			while(it.hasNext()){
				try{
					TGPlugin tgPlugin = (TGPlugin)it.next();
					if( tgPlugin.getModuleId() != null ){
						tgPlugin.init(context);
						this.plugins.add(tgPlugin);
					}
				}catch(TGPluginException exception){
					TGErrorManager.getInstance().handleError(exception);
				}catch(Throwable throwable){
					TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_INIT,throwable));
				}
			}
		}catch(Throwable throwable){
			TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_INIT,throwable));
		}
	}
	
	public void closePlugins(){
		Iterator it = this.plugins.iterator();
		while(it.hasNext()){
			try{
				((TGPlugin)it.next()).close();
			}catch(TGPluginException exception){
				TGErrorManager.getInstance().handleError(exception);
			}catch(Throwable throwable){
				TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_CLOSE,throwable));
			}
		}
	}
	
	public void openPlugins(){
		Iterator it = this.plugins.iterator();
		while(it.hasNext()){
			try{
				TGPlugin tgPlugin = (TGPlugin)it.next();
				tgPlugin.setEnabled( isEnabled(tgPlugin.getModuleId()) );
			}catch(TGPluginException exception){
				TGErrorManager.getInstance().handleError(exception);
			}catch(Throwable throwable){
				TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
			}
		}
	}
	
	public void setEnabled(String moduleId, boolean enabled){
		try{
			TGPluginProperties.instance().setEnabled(moduleId, enabled);
			
			Iterator it = this.plugins.iterator();
			while(it.hasNext()){
				try{
					TGPlugin tgPlugin = (TGPlugin)it.next();
					if( tgPlugin.getModuleId().equals(moduleId) ){
						tgPlugin.setEnabled(enabled);
					}
				}catch(TGPluginException exception){
					TGErrorManager.getInstance().handleError(exception);
				}catch(Throwable throwable){
					TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
				}
			}
		}catch(Throwable throwable){
			TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_SET_STATUS,throwable));
		}
	}
	
	public boolean isEnabled(String moduleId){
		try{
			return TGPluginProperties.instance().isEnabled(moduleId);
		}catch(Throwable throwable){
			TGErrorManager.getInstance().handleError(new TGPluginException(PLUGIN_ERROR_ON_GET_STATUS,throwable));
		}
		return false;
	}
	
	public List getPluginInstances(Class pluginClass){
		List pluginInstances = new ArrayList();
				
		Iterator it = this.plugins.iterator();
		while(it.hasNext()){
			TGPlugin pluginInstance = (TGPlugin)it.next();
			if( pluginClass.isInstance(pluginInstance) ){
				pluginInstances.add( pluginInstance );
			}
		}
		
		return pluginInstances;
	}
}
