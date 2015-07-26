package org.herac.tuxguitar.io.musicxml;

import java.io.OutputStream;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;

public class MusicXMLSongStream implements TGSongStream {
	
	private TGSongStreamContext context;
	
	public MusicXMLSongStream(TGSongStreamContext context) {
		this.context = context;
	}
	
	public void process() throws TGFileFormatException {
		try{
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong song = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			
			MusicXMLWriter writer = new MusicXMLWriter(stream);
			writer.writeSong(song);
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}