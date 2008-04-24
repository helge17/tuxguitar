/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.effects;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.effects.TrillEditor;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTrillNoteAction extends Action{
	public static final String NAME = "action.note.effect.change-trill";
	
	public ChangeTrillNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		TGNote note = getEditor().getTablature().getCaret().getSelectedNote();
		if(note != null){
			changeTrill(new TrillEditor().show(note));
		}
		return 0;
	}
	
	private void changeTrill(TGEffectTrill effect){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		getSongManager().getMeasureManager().changeTrillNote(caret.getMeasure(),caret.getPosition(),caret.getSelectedString().getNumber(),effect);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
