package org.herac.tuxguitar.io.gervill;

import java.io.OutputStream;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiToAudioStream implements TGSongStream {
	
	private TGSongStreamContext context;
	
	public MidiToAudioStream(TGSongStreamContext context) {
		this.context = context;
	}
	
	public void process() throws TGFileFormatException {
		try{
			MidiToAudioSettings settings = this.context.getAttribute(MidiToAudioSettings.class.getName());
			if( settings == null ) {
				settings = new MidiToAudioSettings();
				settings.setDefaults();
			}
			
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong tgSong = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGSongManager tgSongManager = new TGSongManager();
			
			GMChannelRouter gmChannelRouter = new GMChannelRouter();
			GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
			gmChannelRouterConfigurator.configureRouter(tgSong.getChannels());
			
			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS);
			MidiSequenceHandlerImpl midiSequenceHandler = new MidiSequenceHandlerImpl((tgSong.countTracks() + 1), gmChannelRouter);
			midiSequenceParser.parse(midiSequenceHandler);
			MidiToAudioWriter.write(stream, midiSequenceHandler.getEvents(), settings );
			
			settings.setDefaults();
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
