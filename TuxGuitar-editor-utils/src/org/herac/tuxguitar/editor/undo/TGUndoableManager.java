package org.herac.tuxguitar.editor.undo;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGUndoableManager {
	
	private static final int LIMIT = 100;
	
	private TGUndoableBuffer buffer;
	
	public TGUndoableManager() {
		super();
	}
	
	public void discardAllEdits() {
		this.reset();
	}
	
	private void reset() {
		this.getBuffer().setIndexOfNextAdd(0);
		this.getBuffer().getEdits().clear();
	}
	
	public synchronized void undo(TGActionContext actionContext) throws TGCannotUndoException {
		TGUndoableEdit edit = editToBeUndone();
		if( edit == null ) {
			throw new TGCannotUndoException();
		}
		try{
			edit.undo(actionContext);
		}catch(Throwable throwable){
			throw new TGCannotUndoException(throwable);
		}
		this.decrementIndexOfNextAdd();
	}
	
	public synchronized void redo(TGActionContext actionContext) throws TGCannotRedoException {
		TGUndoableEdit edit = editToBeRedone();
		if( edit == null ) {
			throw new TGCannotRedoException();
		}
		try{
			edit.redo(actionContext);
		}catch(Throwable throwable){
			throw new TGCannotRedoException();
		}
		this.incrementIndexOfNextAdd();
	}
	
	public synchronized boolean canUndo() {
		boolean canUndo = false;
		TGUndoableEdit edit = editToBeUndone();
		if( edit != null ) {
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
		this.checkForUnused();
		this.checkForLimit();
		this.getBuffer().getEdits().add(this.getBuffer().getIndexOfNextAdd(), anEdit);
		this.incrementIndexOfNextAdd();
	}
	
	private void checkForUnused() {
		TGUndoableBuffer buffer = this.getBuffer();
		while (buffer.getEdits().size() > buffer.getIndexOfNextAdd()) {
			this.remove(buffer.getEdits().get(buffer.getIndexOfNextAdd()));
		}
	}
	
	private void checkForLimit() {
		TGUndoableBuffer buffer = this.getBuffer();
		while (buffer.getEdits().size() >= LIMIT) {
			this.remove(buffer.getEdits().get(0));
			this.decrementIndexOfNextAdd();
		}
	}
	
	private void remove(TGUndoableEdit edit) {
		this.getBuffer().getEdits().remove(edit);
	}
	
	private TGUndoableEdit editToBeUndone() {
		TGUndoableBuffer buffer = this.getBuffer();
		int index = (buffer.getIndexOfNextAdd() - 1);
		if (index >= 0 && index < buffer.getEdits().size()) {
			return (TGUndoableEdit) buffer.getEdits().get(index);
		}
		return null;
	}
	
	private TGUndoableEdit editToBeRedone() {
		TGUndoableBuffer buffer = this.getBuffer();
		int index = (buffer.getIndexOfNextAdd());
		if (index >= 0 && index < buffer.getEdits().size()) {
			return (TGUndoableEdit) buffer.getEdits().get(index);
		}
		return null;
	}
	
	private void incrementIndexOfNextAdd() {
		this.getBuffer().setIndexOfNextAdd(this.getBuffer().getIndexOfNextAdd() + 1);
	}
	
	private void decrementIndexOfNextAdd() {
		this.getBuffer().setIndexOfNextAdd(this.getBuffer().getIndexOfNextAdd() - 1);
	}
	
	private TGUndoableBuffer getBuffer() {
		if( this.buffer == null ) {
			this.setBuffer(new TGUndoableBuffer());
		}
		return this.buffer;
	}
	
	public void setBuffer(TGUndoableBuffer buffer) {
		this.buffer = buffer;
	}
	
	public static TGUndoableManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGUndoableManager.class.getName(), new TGSingletonFactory<TGUndoableManager>() {
			public TGUndoableManager createInstance(TGContext context) {
				return new TGUndoableManager();
			}
		});
	}
}
