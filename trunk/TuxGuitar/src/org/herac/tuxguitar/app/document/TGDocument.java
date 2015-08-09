package org.herac.tuxguitar.app.document;

import java.net.URL;

import org.herac.tuxguitar.editor.undo.TGUndoableBuffer;
import org.herac.tuxguitar.song.models.TGSong;

public class TGDocument {
	
	private URL url;
	private TGSong song;
	private TGUndoableBuffer undoableBuffer;
	private boolean unsaved;
	private boolean unwanted;
	
	public TGDocument() {
		super();
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public TGSong getSong() {
		return song;
	}

	public void setSong(TGSong song) {
		this.song = song;
	}

	public TGUndoableBuffer getUndoableBuffer() {
		return undoableBuffer;
	}

	public void setUndoableBuffer(TGUndoableBuffer undoableBuffer) {
		this.undoableBuffer = undoableBuffer;
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
	}

	public boolean isUnwanted() {
		return unwanted;
	}

	public void setUnwanted(boolean unwanted) {
		this.unwanted = unwanted;
	}
}
