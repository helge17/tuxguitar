package org.herac.tuxguitar.jack.console;

import java.util.List;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.connection.JackConnectionManager;
import org.herac.tuxguitar.jack.connection.JackConnectionPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class JackConsolePlugin extends org.herac.tuxguitar.app.tools.custom.TGToolItemPlugin {
	
	private JackConsoleDialog jackConsoleDialog;
	
	protected String getItemName() {
		return "Jack Console";
	}
	
	protected void doAction() {
		if( this.jackConsoleDialog == null ){
			JackConnectionManager jackConnectionManager = findConnectionManager();
			if( jackConnectionManager != null ){
				 this.jackConsoleDialog = new JackConsoleDialog(getContext(), jackConnectionManager);
			}
		}
		if( this.jackConsoleDialog != null && this.jackConsoleDialog.isDisposed() ){
			this.jackConsoleDialog.show();
		}
	}
	
	private JackConnectionManager findConnectionManager() {
		JackConnectionPlugin plugin = findConnectionPlugin();
		if( plugin != null ){
			return plugin.getJackConnectionManager();
		}
		return null;
	}
	
	private JackConnectionPlugin findConnectionPlugin(){
		List pluginInstances = TGPluginManager.getInstance(getContext()).getPluginInstances(JackConnectionPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return ((JackConnectionPlugin)pluginInstances.get(0));
		}
		return null;
	}
	
	public String getModuleId(){
		return JackPlugin.MODULE_ID;
	}
}
