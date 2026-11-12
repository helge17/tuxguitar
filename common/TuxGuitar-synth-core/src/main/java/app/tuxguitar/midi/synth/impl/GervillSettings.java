package app.tuxguitar.midi.synth.impl;

import app.tuxguitar.midi.synth.TGLazyConfig;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;

public class GervillSettings {

	public static final String GERVILL_SOUNDBANK_PATH = "tuxguitar-synth-gervill.soundbank.path";
	public static final String GERVILL_SOUNDBANK_FOLDER = "tuxguitar-synth-gervill.soundbank.folder";

	private TGLazyConfig config;

	public GervillSettings(TGContext context){
		this.config = new TGLazyConfig(context, "tuxguitar-synth-gervill");
	}

	public TGConfigManager getConfig(){
		return this.config.get();
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
