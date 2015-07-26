package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class MidiToAudioExporter implements TGLocalFileExporter{

	public static final String PROVIDER_ID = MidiToAudioExporter.class.getName();
	
	private MidiToAudioSettings settings;
	
	public MidiToAudioExporter(){
		this.settings = new MidiToAudioSettings();
		this.settings.setDefaults();
	}
	
	public String getProviderId() {
		return MidiToAudioExporter.PROVIDER_ID;
	}
	
	public String getExportName() {
		return "Audio File";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat(this.settings.getType().toString(), new String[]{this.settings.getType().getExtension()});
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new MidiToAudioStream(context);
	}

	public MidiToAudioSettings getSettings() {
		return settings;
	}
}
