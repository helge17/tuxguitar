package org.herac.tuxguitar.app.view.component.tabfolder;

import org.herac.tuxguitar.editor.undo.TGUndoableBuffer;
import org.herac.tuxguitar.song.models.TGSong;

public class TGTabDocument {
	
	private String name;
	private TGSong song;
	private TGUndoableBuffer undoableBuffer;
	
	public TGTabDocument() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}
