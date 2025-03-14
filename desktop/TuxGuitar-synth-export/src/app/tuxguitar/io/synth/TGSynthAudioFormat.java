package app.tuxguitar.io.synth;

import app.tuxguitar.io.base.TGFileFormat;

public class TGSynthAudioFormat extends TGFileFormat {

	public TGSynthAudioFormat(TGSynthAudioSettings settings) {
		super("Audio File", ("audio/" + settings.getType().getExtension()), new String[]{settings.getType().getExtension()});
	}

	public TGSynthAudioFormat() {
		this(new TGSynthAudioSettings());
	}
}
