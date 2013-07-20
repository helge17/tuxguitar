/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CleanBeatAction extends TGActionBase{
	
	public static final String NAME = "action.note.general.clean-beat";
	
	public CleanBeatAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TGBeat beat = getEditor().getTablature().getCaret().getSelectedBeat();
		if( beat != null){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			//getSongManager().getMeasureManager().removeAllComponentsAt(caret.getMeasure(),caret.getSelectedComponent().getStart());
			getSongManager().getMeasureManager().cleanBeat(beat);
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
			updateTablature();
		}
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
