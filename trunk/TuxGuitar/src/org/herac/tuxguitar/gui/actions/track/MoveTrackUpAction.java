/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.track;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableMoveTrackUp;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MoveTrackUpAction extends Action{
	public static final String NAME = "action.track.move-up";
	
	public MoveTrackUpAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		//comienza el undoable
		UndoableMoveTrackUp undoable = UndoableMoveTrackUp.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		TGTrack track = caret.getTrack();
		
		if(getSongManager().moveTrackUp(track)){
			updateTablature();
			TuxGuitar.instance().getMixer().update();
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo(track));
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
		}
		return 0;
	}
}
