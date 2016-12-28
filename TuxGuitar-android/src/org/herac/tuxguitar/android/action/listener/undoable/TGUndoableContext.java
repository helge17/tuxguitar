package org.herac.tuxguitar.android.action.listener.undoable;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;

public class TGUndoableContext {

	private TGUndoableJoined undoable;
	private Map<Object, TGUndoableEdit> undoableEdits;
	private Integer level;
	
	public TGUndoableContext(){
		this.undoableEdits = new HashMap<Object, TGUndoableEdit>();
		this.reset();
	}
	
	public void reset() {
		this.level = 0;
		this.undoable = null;
		this.undoableEdits.clear();
	}
	
	public void addUndoableToCurrentLevel(TGUndoableEdit undoableEdit) {
		this.undoableEdits.put(this.level, undoableEdit);
	}
	
	public TGUndoableEdit getUndoableFromCurrentLevel() {
		if( this.undoableEdits.containsKey(this.level)) {
			return this.undoableEdits.get(this.level);
		}
		return null;
	}
	
	public void incrementLevel() {
		this.level ++;
	}
	
	public void decrementLevel() {
		this.level --;
	}
	
	public TGUndoableJoined getUndoable() {
		return undoable;
	}

	public void setUndoable(TGUndoableJoined undoable) {
		this.undoable = undoable;
	}

	public Map<Object, TGUndoableEdit> getUndoableEdits() {
		return undoableEdits;
	}

	public void setUndoableEdits(Map<Object, TGUndoableEdit> undoableEdits) {
		this.undoableEdits = undoableEdits;
	}

	public Integer getLevel() {
		return level;
	}

	public static TGUndoableContext getInstance(TGActionContext actionContext) {
		synchronized (TGUndoableContext.class) {
			String key = TGUndoableContext.class.getName();
			if( actionContext.hasAttribute(key) ) {
				return actionContext.getAttribute(key);
			}
			actionContext.setAttribute(key, new TGUndoableContext());
			
			return getInstance(actionContext);
		}
	}
}
