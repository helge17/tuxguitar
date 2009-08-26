package org.herac.tuxguitar.jack;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.jack.sequencer.JackSequencerProviderPlugin;
import org.herac.tuxguitar.jack.settings.JackSettings;
import org.herac.tuxguitar.jack.settings.JackSettingsDialog;
import org.herac.tuxguitar.jack.synthesizer.JackOutputPortProviderPlugin;

public class JackPluginImpl extends TGPluginList implements TGPluginSetup {
	
	private JackClient jackClient;
	private JackSettings jackSettings;
	
	public JackPluginImpl(){
		this.jackClient = new JackClient();
		this.jackSettings = new JackSettings();
	}
	
	protected List getPlugins() throws TGPluginException {
		List plugins = new ArrayList();
		plugins.add( new JackOutputPortProviderPlugin(this.jackClient , this.jackSettings) );
		plugins.add( new JackSequencerProviderPlugin(this.jackClient) );
		return plugins;
	}
	
	public void closeAll(){
		if(this.jackClient.isOpen()){
			this.jackClient.close();
			this.jackClient.finalize();
		}
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "Jack Audio Connection Kit plugin support";
	}
	
	public String getName() {
		return "Jack Audio Connection Kit plugin support";
	}
	
	public String getVersion() {
		return "1.0";
	}
	
	public void setupDialog(Shell parent){
		JackSettingsDialog jackSettingsDialog = new JackSettingsDialog( this.jackSettings );
		jackSettingsDialog.open( parent );
	}
}
