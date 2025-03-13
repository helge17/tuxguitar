package app.tuxguitar.community.io;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongStream;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
