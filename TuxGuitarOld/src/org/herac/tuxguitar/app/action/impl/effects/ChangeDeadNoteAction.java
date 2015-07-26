/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.effects;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeDeadNoteAction extends TGActionBase{
	
	public static final String NAME = "action.note.effect.change-dead";
	
	public ChangeDeadNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		TGNote note = caret.getSelectedNote();
		if(note == null){
			note = getSongManager().getFactory().newNote();
			note.setValue(0);
			note.setVelocity(caret.getVelocity());
			note.setString(caret.getSelectedString().getNumber());
			
			TGDuration duration = getSongManager().getFactory().newDuration();
			duration.copyFrom(caret.getDuration());
			
			getSongManager().getMeasureManager().addNote(caret.getMeasure(),caret.getPosition(),note,duration,caret.getVoice());
		}
		getSongManager().getMeasureManager().changeDeadNote(note);
		TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		updateSong();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	public void updateSong() {
		updateMeasure(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
