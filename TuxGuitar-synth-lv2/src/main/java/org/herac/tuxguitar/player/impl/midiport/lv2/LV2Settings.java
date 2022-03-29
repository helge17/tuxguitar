package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class LV2Settings {
	
	private static final Integer LV2_UI_SERVER_DEFAULT_PORT_VALUE = 60982;
	private static final String LV2_UI_CLIENT_COMMAND = "lv2.ui.client.command";
	private static final String LV2_UI_SERVER_DEFAULT_PORT = "lv2.ui.server.default.port";
	
	private TGContext context;
	private TGConfigManager config;
	
	public LV2Settings(TGContext context){
		this.context = context;
	}
	
	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-synth-lv2");
		}
		return this.config;
	}
	
	public void save(){
		this.getConfig().save();
	}
	
	public String getUIClientCommand() {
		return this.getConfig().getStringValue(LV2_UI_CLIENT_COMMAND);
	}
	
	public Integer getUIServerPort() {
		return this.getConfig().getIntegerValue(LV2_UI_SERVER_DEFAULT_PORT, LV2_UI_SERVER_DEFAULT_PORT_VALUE);
	}
}
