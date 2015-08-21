package org.herac.tuxguitar.jack.console;

import java.util.List;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.connection.JackConnectionManager;
import org.herac.tuxguitar.jack.connection.JackConnectionPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class JackConsolePlugin extends org.herac.tuxguitar.app.tools.custom.TGToolItemPlugin {
	
	private JackConsoleDialog jackConsoleDialog;
	
	protected String getItemName() {
		return "Jack Console";
	}
	
	protected void doAction(TGContext context) {
		if( this.jackConsoleDialog == null ){
			JackConnectionManager jackConnectionManager = findConnectionManager(context);
			if( jackConnectionManager != null ){
				 this.jackConsoleDialog = new JackConsoleDialog(context, jackConnectionManager);
			}
		}
		if( this.jackConsoleDialog != null && this.jackConsoleDialog.isDisposed() ){
			this.jackConsoleDialog.show();
		}
	}
	
	private JackConnectionManager findConnectionManager(TGContext context) {
		JackConnectionPlugin plugin = findConnectionPlugin(context);
		if( plugin != null ){
			return plugin.getJackConnectionManager();
		}
		return null;
	}
	
	private JackConnectionPlugin findConnectionPlugin(TGContext context){
		List<JackConnectionPlugin> pluginInstances = TGPluginManager.getInstance(context).getPluginInstances(JackConnectionPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return pluginInstances.get(0);
		}
		return null;
	}
	
	public String getModuleId(){
		return JackPlugin.MODULE_ID;
	}
}
