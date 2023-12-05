package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGShareSongStream implements TGSongStream { 
	
	private TGContext context;
	private TGSongStreamContext streamContext;
	
	public TGShareSongStream(TGContext context, TGSongStreamContext streamContext){
		this.context = context;
		this.streamContext = streamContext;
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public void process() throws TGFileFormatException {
		TGSong srcSong = this.streamContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGSong song = srcSong.clone(new TGFactory());
		new Thread( new Runnable() {
			public void run() {
				new TGShareSong(getContext()).process( song );
			}
		} ).start();
	}
}
