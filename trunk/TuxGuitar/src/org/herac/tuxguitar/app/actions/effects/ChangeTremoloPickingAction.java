/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.effects;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.editors.effects.TremoloPickingEditor;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTremoloPickingAction extends Action{
	
	public static final String NAME = "action.note.effect.change-tremolo-picking";
	
	public ChangeTremoloPickingAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		TGNote note = getEditor().getTablature().getCaret().getSelectedNote();
		if(note != null){
			TremoloPickingEditor tremoloPickingEditor = new TremoloPickingEditor();
			tremoloPickingEditor.show(note);
			if(!tremoloPickingEditor.isCancelled()){
				changeTremoloPicking(tremoloPickingEditor.getResult());
			}
		}
		return 0;
	}
	
	private void changeTremoloPicking(TGEffectTremoloPicking effect){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		getSongManager().getMeasureManager().changeTremoloPicking(caret.getMeasure(),caret.getPosition(),caret.getSelectedString().getNumber(),effect);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
