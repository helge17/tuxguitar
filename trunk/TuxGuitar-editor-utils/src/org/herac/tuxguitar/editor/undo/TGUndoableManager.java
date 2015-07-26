package org.herac.tuxguitar.editor.undo;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGUndoableManager {
	
	private static final int LIMIT = 100;
	private int indexOfNextAdd;
	private List<Object> edits;
	
	public TGUndoableManager() {
		this.init();
	}
	
	public void discardAllEdits() {
		this.reset();
	}
	
	public synchronized void undo() throws TGCannotUndoException {
		TGUndoableEdit edit = editToBeUndone();
		if (edit == null) {
			throw new TGCannotUndoException();
		}
		try{
			edit.undo();
		}catch(Throwable throwable){
			throw new TGCannotUndoException(throwable);
		}
		this.indexOfNextAdd--;
	}
	
	public synchronized void redo() throws TGCannotRedoException {
		TGUndoableEdit edit = editToBeRedone();
		if (edit == null) {
			throw new TGCannotRedoException();
		}
		try{
			edit.redo();
		}catch(Throwable throwable){
			throw new TGCannotRedoException();
		}
		this.indexOfNextAdd++;
	}
	
	public synchronized boolean canUndo() {
		boolean canUndo = false;
		TGUndoableEdit edit = editToBeUndone();
		if (edit != null) {
			canUndo = edit.canUndo();
		}
		return canUndo;
	}
	
	public synchronized boolean canRedo() {
		boolean canRedo = false;
		TGUndoableEdit edit = editToBeRedone();
		if (edit != null) {
			canRedo = edit.canRedo();
		}
		return canRedo;
	}
	
	public synchronized void addEdit(TGUndoableEdit anEdit) {
		checkForUnused();
		checkForLimit();
		this.edits.add(this.indexOfNextAdd, anEdit);
		this.indexOfNextAdd++;
	}
	
	private void checkForUnused() {
		while (this.edits.size() > this.indexOfNextAdd) {
			TGUndoableEdit edit = (TGUndoableEdit) this.edits.get(this.indexOfNextAdd);
			remove(edit);
		}
	}
	
	private void checkForLimit() {
		while (this.edits.size() >= LIMIT) {
			TGUndoableEdit edit = (TGUndoableEdit) this.edits.get(0);
			remove(edit);
			this.indexOfNextAdd--;
		}
	}
	
	private void remove(TGUndoableEdit edit) {
		this.edits.remove(edit);
	}
	
	private TGUndoableEdit editToBeUndone() {
		int index = this.indexOfNextAdd - 1;
		if (index >= 0 && index < this.edits.size()) {
			return (TGUndoableEdit) this.edits.get(index);
		}
		return null;
	}
	
	private TGUndoableEdit editToBeRedone() {
		int index = this.indexOfNextAdd;
		if (index >= 0 && index < this.edits.size()) {
			return (TGUndoableEdit) this.edits.get(index);
		}
		return null;
	}
	
	private void init() {
		this.indexOfNextAdd = 0;
		this.edits = new ArrayList<Object>();
	}
	
	private void reset() {
		this.indexOfNextAdd = 0;
		this.edits.clear();
	}
	
	public static TGUndoableManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGUndoableManager.class.getName(), new TGSingletonFactory<TGUndoableManager>() {
			public TGUndoableManager createInstance(TGContext context) {
				return new TGUndoableManager();
			}
		});
	}
}
