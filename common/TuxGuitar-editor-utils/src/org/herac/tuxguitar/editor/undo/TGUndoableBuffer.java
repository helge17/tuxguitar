package org.herac.tuxguitar.editor.undo;

import java.util.ArrayList;
import java.util.List;

public class TGUndoableBuffer {
	
	private int indexOfNextAdd;
	private List<TGUndoableEdit> edits;
	
	public TGUndoableBuffer() {
		this.edits = new ArrayList<TGUndoableEdit>();
		this.indexOfNextAdd = 0;
	}

	public int getIndexOfNextAdd() {
		return indexOfNextAdd;
	}

	public void setIndexOfNextAdd(int indexOfNextAdd) {
		this.indexOfNextAdd = indexOfNextAdd;
	}

	public List<TGUndoableEdit> getEdits() {
		return edits;
	}

	public void setEdits(List<TGUndoableEdit> edits) {
		this.edits = edits;
	}
}
