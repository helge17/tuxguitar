package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class MidiToAudioFormat extends TGFileFormat {
	
	public MidiToAudioFormat(MidiToAudioSettings settings) {
		super("Audio File", ("audio/" + settings.getType().getExtension()), new String[]{settings.getType().getExtension()});
	}
	
	public MidiToAudioFormat() {
		this(new MidiToAudioSettings());
	}
}
