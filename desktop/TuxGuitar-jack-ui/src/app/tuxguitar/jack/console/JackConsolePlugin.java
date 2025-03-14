package app.tuxguitar.jack.console;

import java.util.List;

import app.tuxguitar.jack.JackPlugin;
import app.tuxguitar.jack.connection.JackConnectionManager;
import app.tuxguitar.jack.connection.JackConnectionPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginManager;

public class JackConsolePlugin extends app.tuxguitar.app.tools.custom.TGToolItemPlugin {

	private JackConsoleDialog jackConsoleDialog;

	protected String getItemName() {
		return "jack.console.title";
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
