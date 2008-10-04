/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.note;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InsertNoteAction extends Action{
	public static final String NAME = "action.note.general.insert";
	
	public InsertNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING);
	}
	
	protected int execute(TypedEvent e){
		Caret caret = getEditor().getTablature().getCaret();
		TGBeat beat = caret.getSelectedBeat();
		if(beat != null){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			if(beat.getVoice(caret.getVoice()).isEmpty()){
				getSongManager().getMeasureManager().addSilence(beat, caret.getDuration().clone(getSongManager().getFactory()), caret.getVoice());
			}
			else{
				long start = beat.getStart();
				long length = beat.getVoice(caret.getVoice()).getDuration().getTime();
				getSongManager().getMeasureManager().moveVoices(caret.getMeasure(),start,length,caret.getVoice(),beat.getVoice(caret.getVoice()).getDuration());
			}
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
			
			updateTablature();
			
		}
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
