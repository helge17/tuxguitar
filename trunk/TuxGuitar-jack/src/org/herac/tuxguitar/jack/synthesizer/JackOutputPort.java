package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.settings.JackSettings;
import org.herac.tuxguitar.jack.settings.JackSettingsListener;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiReceiver;

public class JackOutputPort extends MidiOutputPort implements JackSettingsListener {
	
	private JackClient jackClient;
	private JackSettings jackSettings;
	private JackReceiver jackReceiver;
	private JackOutputPortRouter jackOutputPortRouter;
	
	
	public JackOutputPort( JackClient jackClient , JackSettings jackSettings ){
		super("tuxguitar-jack","Jack Midi Port");
		this.jackOutputPortRouter = new JackOutputPortRouter();
		this.jackReceiver = new JackReceiver(jackClient, this);
		this.jackSettings = jackSettings;
		this.jackClient = jackClient;
	}
	
	public void open(){
		this.loadSettings( this.jackSettings.getConfig() );
		this.jackSettings.addListener( this );
		if(!this.jackClient.isPortsOpen()){
			this.jackClient.openPorts(this.jackOutputPortRouter.getPortCount());
		}
	}
	
	public void close(){
		if(this.jackClient.isPortsOpen()){
			this.jackClient.closePorts();
		}
		this.jackSettings.removeListener( this );
	}
	
	public MidiReceiver getReceiver(){
		this.open();
		return this.jackReceiver;
	}
	
	public void check(){
		// Not implemented
	}
	
	public JackOutputPortRouter getRouter(){
		return this.jackOutputPortRouter;
	}
	
	public void loadSettings(TGConfigManager config) {
		boolean connected = this.jackClient.isPortsOpen();
		if( connected ){
			this.jackClient.closePorts();
		}
		this.jackOutputPortRouter.loadSettings(config);
		if( connected ){
			this.jackClient.openPorts(this.jackOutputPortRouter.getPortCount());
		}
	}
}
