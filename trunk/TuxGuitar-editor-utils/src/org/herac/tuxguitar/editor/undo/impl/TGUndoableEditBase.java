package org.herac.tuxguitar.editor.undo.impl;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGUndoableEditBase implements TGUndoableEdit{
	
	public static final String ATTRIBUTE_BY_PASS_UNDOABLE = "byPassUndoable";
	
	private TGContext context;
	
	public TGUndoableEditBase(TGContext context){
		this.context = context;
	}

	public TGContext getContext() {
		return context;
	}
	
	public TGSong getSong() {
		return TGUndoableEditBase.getSong(this.getContext());
	}
	
	public TGSongManager getSongManager() {
		return TGUndoableEditBase.getSongManager(this.getContext());
	}
	
	public TGActionProcessor createByPassUndoableAction(String actionId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.getContext(), actionId);
		tgActionProcessor.setAttribute(ATTRIBUTE_BY_PASS_UNDOABLE, Boolean.TRUE);
		return tgActionProcessor;
	}
	
	public void processByPassUndoableAction(TGActionProcessor tgActionProcessor, TGActionContext context) {
		tgActionProcessor.processOnCurrentThread(context);
	}
	
	public static TGSong getSong(TGContext context) {
		return TGDocumentManager.getInstance(context).getSong();
	}
	
	public static TGSongManager getSongManager(TGContext context) {
		return TGDocumentManager.getInstance(context).getSongManager();
	}
}
