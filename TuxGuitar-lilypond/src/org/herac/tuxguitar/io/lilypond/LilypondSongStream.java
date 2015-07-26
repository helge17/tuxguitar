package org.herac.tuxguitar.io.lilypond;

import java.io.OutputStream;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;

public class LilypondSongStream implements TGSongStream {
	
	private TGSongStreamContext context;
	
	public LilypondSongStream(TGSongStreamContext context) {
		this.context = context;
	}
	
	public void process() throws TGFileFormatException {
		try{
			LilypondSettings settings = this.context.getAttribute(LilypondSettings.class.getName());
			if( settings == null ) {
				settings = LilypondSettings.getDefaults();
			}
			
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong song = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			
			new LilypondOutputStream(stream, settings).writeSong(song);
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}