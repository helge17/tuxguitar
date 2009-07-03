package org.herac.tuxguitar.io.gervill;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiToAudioExporter implements TGLocalFileExporter{
	
	private MidiToAudioSettings settings;
	private OutputStream stream;
	
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
	
	public void init(TGFactory factory,OutputStream stream){
		this.stream = stream;
	}
	
	public void exportSong(TGSong song) throws TGFileFormatException {
		try{
			if( this.stream != null ){
				TGSongManager manager = new TGSongManager();
				manager.setSong(song);
				MidiSequenceParser parser = new MidiSequenceParser(manager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS,100,0);
				MidiSequenceHandlerImpl sequence = new MidiSequenceHandlerImpl( (song.countTracks() + 1) );
				parser.parse(sequence);
				MidiToAudioWriter.write(this.stream, sequence.getEvents(), this.settings );
			}
		}catch(Throwable throwable){
			throw new TGFileFormatException( throwable.getMessage() , throwable );
		}
	}
}
