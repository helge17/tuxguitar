package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class TGSongPersistenceHandle {
	
	private TGFactory factory;
	private TGSong song;
	private TGFileFormat format;
	private TGSongStreamContext context;
	
	public TGSongPersistenceHandle() {
		super();
	}

	public TGFactory getFactory() {
		return this.factory;
	}

	public void setFactory(TGFactory factory) {
		this.factory = factory;
	}

	public TGSong getSong() {
		return this.song;
	}

	public void setSong(TGSong song) {
		this.song = song;
	}

	public TGFileFormat getFormat() {
		return this.format;
	}

	public void setFormat(TGFileFormat format) {
		this.format = format;
	}

	public TGSongStreamContext getContext() {
		return context;
	}

	public void setContext(TGSongStreamContext context) {
		this.context = context;
	}
}
