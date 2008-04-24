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
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableAddTrack;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddTrackAction extends Action{
	public static final String NAME = "action.track.add";
	
	public AddTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		//comienza el undoable
		UndoableAddTrack undoable = UndoableAddTrack.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		Caret caret = getEditor().getTablature().getCaret();
		
		TGTrack track = getSongManager().createTrack();
		updateTablature();
		caret.update(track.getNumber(),caret.getPosition(),1);
		TuxGuitar.instance().getMixer().update();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo(track));
		
		return 0;
	}
}
