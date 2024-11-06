package org.herac.tuxguitar.io.gervill;

import java.io.OutputStream;

import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

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
