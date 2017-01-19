package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiSongWriter extends MidiFileFormat implements TGSongWriter {
	
	public MidiSongWriter() {
		super();
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			TGSong tgSong = handle.getSong();
			TGSongManager tgSongManager = new TGSongManager();
			
			GMChannelRouter gmChannelRouter = new GMChannelRouter();
			GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
			gmChannelRouterConfigurator.configureRouter(tgSong.getChannels());
			
			MidiSettings settings = handle.getContext().getAttribute(MidiSettings.class.getName());
			if( settings == null ) {
				settings = MidiSettings.getDefaults();
			}
			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS);
			midiSequenceParser.setTranspose(settings.getTranspose());
			midiSequenceParser.parse(new MidiSequenceHandlerImpl((tgSong.countTracks() + 1), gmChannelRouter, handle.getOutputStream()));
		} catch (Throwable e) {
			throw new TGFileFormatException(e);
		}
	}
}
