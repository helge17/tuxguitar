package app.tuxguitar.midi.synth.impl;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;

public class GervillSettings {

	public static final String GERVILL_SOUNDBANK_PATH = "tuxguitar-synth-gervill.soundbank.path";
	public static final String GERVILL_SOUNDBANK_FOLDER = "tuxguitar-synth-gervill.soundbank.folder";

	private TGContext context;
	private TGConfigManager config;

	public GervillSettings(TGContext context){
		this.context = context;
	}

	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-synth-gervill");
		}
		return this.config;
	}

	public void save(){
		this.getConfig().save();
	}

	public String getSoundbankPath() {
		return this.getConfig().getStringValue(GERVILL_SOUNDBANK_PATH);
	}

	public void setSoundbankPath(String soundbankPath) {
		this.getConfig().setValue(GERVILL_SOUNDBANK_PATH, soundbankPath);
	}

	public String getSoundbankFolder() {
		return this.getConfig().getStringValue(GERVILL_SOUNDBANK_FOLDER);
	}

	public void setSoundbankFolder(String soundbankFolder) {
		this.getConfig().setValue(GERVILL_SOUNDBANK_FOLDER, soundbankFolder);
	}
}
