/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.effects;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.TGActionBase;
import org.herac.tuxguitar.app.editors.effects.GraceEditor;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeGraceNoteAction extends TGActionBase{
	
	public static final String NAME = "action.note.effect.change-grace";
	
	public ChangeGraceNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TGNote note = getEditor().getTablature().getCaret().getSelectedNote();
		if(note != null){
			GraceEditor graceEditor = new GraceEditor();
			graceEditor.show(note);
			if(!graceEditor.isCancelled()){
				changeGrace(graceEditor.getResult());
			}
		}
	}
	
	private void changeGrace(TGEffectGrace effect){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		getSongManager().getMeasureManager().changeGraceNote(caret.getMeasure(),caret.getPosition(),caret.getSelectedString().getNumber(),effect);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
