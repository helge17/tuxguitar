/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.insert;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.undo.undoables.custom.UndoableChangeOpenRepeat;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RepeatOpenAction extends TGActionBase{
	
	public static final String NAME = "action.insert.open-repeat";
	
	public RepeatOpenAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		//comienza el undoable
		UndoableChangeOpenRepeat undoable = UndoableChangeOpenRepeat.startUndo();
		TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		
		TGMeasureImpl measure = getEditor().getTablature().getCaret().getMeasure();
		getSongManager().changeOpenRepeat(measure.getStart());
		updateSong();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	public void updateSong() {
		updateMeasure(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
