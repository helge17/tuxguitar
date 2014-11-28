package org.herac.tuxguitar.io.midi;

import java.io.OutputStream;

import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiSongExporter implements TGLocalFileExporter{
	
	private OutputStream stream;
	private MidiSettings settings;
	
	public String getExportName() {
		return "Midi";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Midi", new String[]{"mid","midi"});
	}
	
	public boolean configure(TGSong song, boolean setDefaults) {
		this.settings = (setDefaults ? MidiSettings.getDefaults(): new MidiSettingsDialog().open() );
		return (this.settings != null);
	}
	
	public void init(TGFactory factory,OutputStream stream){
		this.stream = stream;
	}
	
	public void exportSong(TGSong tgSong) {
		if( this.stream != null && this.settings != null ){
			TGSongManager tgSongManager = new TGSongManager();

			GMChannelRouter gmChannelRouter = new GMChannelRouter();
			GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
			gmChannelRouterConfigurator.configureRouter(tgSong.getChannels());
			
			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS);
			midiSequenceParser.setTranspose(this.settings.getTranspose());
			midiSequenceParser.parse(new MidiSequenceHandlerImpl((tgSong.countTracks() + 1), gmChannelRouter, this.stream));
		}
	}
}
