package app.tuxguitar.io.midi;

import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;

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
			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager, settings.getFlags());
			midiSequenceParser.setTranspose(settings.getTranspose());
			midiSequenceParser.parse(new MidiSequenceHandlerImpl((tgSong.countTracks() + 1), gmChannelRouter, handle.getOutputStream()));
		} catch (Throwable e) {
			throw new TGFileFormatException(e);
		}
	}
}
