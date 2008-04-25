package org.herac.tuxguitar.io.midi;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiSongExporter implements TGSongExporter{
	
	private MidiSettings settings;
	
	public String getExportName() {
		return "Midi";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Midi","*.mid;*.midi");
	}
	
	public boolean configure(boolean setDefaults) {
		this.settings = (setDefaults ? MidiSettings.getDefaults(): new MidiSettingsDialog().open() );
		return (this.settings != null);
	}
	
	public void exportSong(OutputStream stream, TGSong song) {
		TGSongManager manager = new TGSongManager();
		manager.setSong(song);
		MidiSequenceParser parser = new MidiSequenceParser(manager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS,100,this.settings.getTranspose());
		MidiSequenceHandlerImpl sequence = new MidiSequenceHandlerImpl( (song.countTracks() + 1) , stream);
		parser.parse(sequence);
	}
}
