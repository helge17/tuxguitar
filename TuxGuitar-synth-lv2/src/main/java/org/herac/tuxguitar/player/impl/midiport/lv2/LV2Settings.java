package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class LV2Settings {
	
	private static final String LV2_CLIENT_COMMAND = "lv2.client.command";
	
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
	
	public String getClientCommand() {
		return this.getConfig().getStringValue(LV2_CLIENT_COMMAND);
	}
}
