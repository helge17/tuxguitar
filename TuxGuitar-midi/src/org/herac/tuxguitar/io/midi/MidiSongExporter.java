package org.herac.tuxguitar.io.midi;

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

public class MidiSongExporter implements TGSongStream {
	
	private TGSongStreamContext context;
	
	public MidiSongExporter(TGSongStreamContext context) {
		this.context = context;
	}
	
	public void process() throws TGFileFormatException {
		try {
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong tgSong = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGSongManager tgSongManager = new TGSongManager();
			
			GMChannelRouter gmChannelRouter = new GMChannelRouter();
			GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
			gmChannelRouterConfigurator.configureRouter(tgSong.getChannels());
			
			MidiSettings settings = this.context.getAttribute(MidiSettings.class.getName());
			if( settings == null ) {
				settings = MidiSettings.getDefaults();
			}
			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS);
			midiSequenceParser.setTranspose(settings.getTranspose());
			midiSequenceParser.parse(new MidiSequenceHandlerImpl((tgSong.countTracks() + 1), gmChannelRouter, stream));
		} catch (Throwable e) {
			throw new TGFileFormatException(e);
		}
	}
}
