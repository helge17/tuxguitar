package org.herac.tuxguitar.io.base;

import java.io.InputStream;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class TGSongLoaderHandle {

	private TGFactory factory;
	private TGSong song;
	private TGFileFormat format;
	private InputStream inputStream;
	
	public TGSongLoaderHandle() {
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

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void setInputStream(InputStream is) {
		this.inputStream = is;
	}
}
