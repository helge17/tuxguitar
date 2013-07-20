/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.effects;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.effects.HarmonicEditor;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeHarmonicNoteAction extends TGActionBase{
	
	public static final String NAME = "action.note.effect.change-harmonic";
	
	public ChangeHarmonicNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TGNote note = getEditor().getTablature().getCaret().getSelectedNote();
		if(note != null){
			HarmonicEditor harmonicEditor = new HarmonicEditor();
			harmonicEditor.show(note);
			if(!harmonicEditor.isCancelled()){
				changeHarmonic(harmonicEditor.getResult());
			}
		}
	}
	
	private void changeHarmonic(TGEffectHarmonic effect){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		getSongManager().getMeasureManager().changeHarmonicNote(caret.getMeasure(),caret.getPosition(),caret.getSelectedString().getNumber(),effect);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
