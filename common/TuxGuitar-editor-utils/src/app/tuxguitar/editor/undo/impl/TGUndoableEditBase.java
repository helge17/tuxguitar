package app.tuxguitar.editor.undo.impl;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
