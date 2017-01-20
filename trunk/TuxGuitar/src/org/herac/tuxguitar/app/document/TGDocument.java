package org.herac.tuxguitar.app.document;

import java.net.URI;

import org.herac.tuxguitar.editor.undo.TGUndoableBuffer;
import org.herac.tuxguitar.song.models.TGSong;

public class TGDocument {
	
	private URI uri;
	private TGSong song;
	private TGUndoableBuffer undoableBuffer;
	private boolean unsaved;
	private boolean unwanted;
	
	public TGDocument() {
		super();
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
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
