package app.tuxguitar.io.gervill;

import java.io.OutputStream;

import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;

public class MidiToAudioSongWriter implements TGSongWriter {

	public static final TGFileFormat FILE_FORMAT = new MidiToAudioFormat();

	public MidiToAudioSongWriter() {
		super();
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			MidiToAudioSettings settings = handle.getContext().getAttribute(MidiToAudioSettings.class.getName());
			if( settings == null ) {
				settings = new MidiToAudioSettings();
			}

			OutputStream stream = handle.getOutputStream();
			TGSong tgSong = handle.getSong();
			TGSongManager tgSongManager = new TGSongManager();

			GMChannelRouter gmChannelRouter = new GMChannelRouter();
			GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
			gmChannelRouterConfigurator.configureRouter(tgSong.getChannels());

			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS);
			MidiSequenceHandlerImpl midiSequenceHandler = new MidiSequenceHandlerImpl((tgSong.countTracks() + 1), gmChannelRouter);
			midiSequenceParser.parse(midiSequenceHandler);
			MidiToAudioWriter.write(stream, midiSequenceHandler.getEvents(), settings );
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
