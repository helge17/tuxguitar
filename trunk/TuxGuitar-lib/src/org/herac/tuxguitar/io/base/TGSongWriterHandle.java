package org.herac.tuxguitar.io.base;

import java.io.OutputStream;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class TGSongWriterHandle {
	
	private TGFactory factory;
	private TGSong song;
	private TGFileFormat format;
	private OutputStream outputStream;
	
	public TGSongWriterHandle(){
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

	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	public void setOutputStream(OutputStream os) {
		this.outputStream = os;
	}
}
