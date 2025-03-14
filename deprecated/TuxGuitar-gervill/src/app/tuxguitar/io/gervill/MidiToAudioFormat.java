package app.tuxguitar.io.gervill;

import app.tuxguitar.io.base.TGFileFormat;

public class MidiToAudioFormat extends TGFileFormat {

	public MidiToAudioFormat(MidiToAudioSettings settings) {
		super("Gervill Audio File", ("audio/" + settings.getType().getExtension()), new String[]{settings.getType().getExtension()});
	}

	public MidiToAudioFormat() {
		this(new MidiToAudioSettings());
	}
}
