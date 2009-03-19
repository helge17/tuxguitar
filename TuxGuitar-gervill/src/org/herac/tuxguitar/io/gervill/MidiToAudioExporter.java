package org.herac.tuxguitar.io.gervill;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiToAudioExporter implements TGSongExporter{
	
	private MidiToAudioSettings settings;
	
	public MidiToAudioExporter(){
		this.settings = new MidiToAudioSettings();
		this.settings.setDefaults();
	}
	
	public String getExportName() {
		return "Audio File";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat(this.settings.getType().toString(), ("*." + this.settings.getType().getExtension()) );
	}
	
	public boolean configure(boolean setDefaults) {
		if( !setDefaults ){
			return new MidiToAudioSettingsDialog().open( this.settings );
		}
		this.settings.setDefaults();
		
		return true;
	}
	
	public void exportSong(OutputStream stream, TGSong song) throws TGFileFormatException {
		try{
			TGSongManager manager = new TGSongManager();
			manager.setSong(song);
			MidiSequenceParser parser = new MidiSequenceParser(manager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS,100,0);
			MidiSequenceHandlerImpl sequence = new MidiSequenceHandlerImpl( (song.countTracks() + 1) );
			parser.parse(sequence);
			MidiToAudioWriter.write(stream, sequence.getEvents(), this.settings );
		}catch(Throwable throwable){
			throw new TGFileFormatException( throwable.getMessage() , throwable );
		}
	}
}
