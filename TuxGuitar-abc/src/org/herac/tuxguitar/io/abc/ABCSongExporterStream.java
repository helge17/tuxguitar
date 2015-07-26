package org.herac.tuxguitar.io.abc;

import java.io.OutputStream;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;

public class ABCSongExporterStream implements TGSongStream {
	
	private TGSongStreamContext context;
	
	public ABCSongExporterStream(TGSongStreamContext context) {
		this.context = context;
	}
	
	public void process() throws TGFileFormatException {
		try{
			ABCSettings settings = this.context.getAttribute(ABCSettings.class.getName());
			if( settings == null ) {
				settings = ABCSettings.getDefaults();
			}
			
			OutputStream stream = this.context.getAttribute(OutputStream.class.getName());
			TGSong song = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			
			new ABCOutputStream(stream, settings).writeSong(song);
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}