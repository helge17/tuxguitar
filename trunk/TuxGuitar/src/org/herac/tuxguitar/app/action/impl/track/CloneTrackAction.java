/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.track.UndoableCloneTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CloneTrackAction extends TGActionBase{
	
	public static final String NAME = "action.track.clone";
	
	public CloneTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		//comienza el undoable
		UndoableCloneTrack undoable = UndoableCloneTrack.startUndo();
		TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		
		Caret caret = getEditor().getTablature().getCaret();
		
		getSongManager().cloneTrack(caret.getTrack());
		updateSong();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
}
