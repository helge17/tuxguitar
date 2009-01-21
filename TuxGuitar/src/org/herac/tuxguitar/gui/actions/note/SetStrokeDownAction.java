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
import org.herac.tuxguitar.gui.editors.effects.StrokeEditor;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGStroke;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetStrokeDownAction extends Action{
	public static final String NAME = "action.beat.general.set-stroke-down";
	
	public SetStrokeDownAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		TGBeat beat = getEditor().getTablature().getCaret().getSelectedBeat();
		if(beat != null && !beat.isRestBeat()){
			StrokeEditor editor = new StrokeEditor();
			editor.open(beat);
			if( editor.getStatus() != StrokeEditor.STATUS_CANCEL ){
				int direction = ( editor.getStatus() == StrokeEditor.STATUS_CLEAN ? TGStroke.STROKE_NONE : TGStroke.STROKE_DOWN );
				int value = editor.getValue();
				
				//comienza el undoable
				UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
				if(getSongManager().getMeasureManager().setStroke( beat.getMeasure(), beat.getStart(), value, direction ) ){
					//termia el undoable
					addUndoableEdit(undoable.endUndo());
					TuxGuitar.instance().getFileHistory().setUnsavedFile();
				}
				updateTablature();
			}
		}
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
