package org.herac.tuxguitar.io.ascii;

import java.io.OutputStream;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;

public class ASCIISongStream implements TGSongStream {
	
	private TGSongStreamContext context;
	
	public ASCIISongStream(TGSongStreamContext context) {
		this.context = context;
	}
	
	public void process() throws TGFileFormatException {
		try{
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong song = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			
			ASCIITabOutputStream writer = new ASCIITabOutputStream(stream);
			writer.writeSong(song);
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}