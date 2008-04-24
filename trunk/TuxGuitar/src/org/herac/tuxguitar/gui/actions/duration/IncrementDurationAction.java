/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.duration;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IncrementDurationAction extends Action{
	public static final String NAME = "action.note.duration.increment-duration";
	
	public IncrementDurationAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		TGDuration duration = getEditor().getTablature().getCaret().getDuration();
		if(duration.getValue() < TGDuration.SIXTY_FOURTH){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			this.changeDuration(duration.getValue() * 2);
			
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			this.updateTablature();
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
		
		return 0;
	}
	
	private void changeDuration(int value) {
		Caret caret = getEditor().getTablature().getCaret();
		caret.getDuration().setValue(value);
		caret.getDuration().setDotted(false);
		caret.getDuration().setDoubleDotted(false);
		caret.changeDuration(caret.getDuration().clone(getSongManager().getFactory()));
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
