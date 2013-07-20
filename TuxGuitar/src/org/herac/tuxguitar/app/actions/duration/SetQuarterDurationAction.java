/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetQuarterDurationAction extends TGActionBase{
	
	public static final String NAME = "action.note.duration.set-quarter";
	
	public static final int VALUE = TGDuration.QUARTER;
	
	public SetQuarterDurationAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		Caret caret = getEditor().getTablature().getCaret();
		TGBeat beat = caret.getSelectedBeat();
		if(beat != null){
			TGVoice voice = beat.getVoice( caret.getVoice() );
			TGDuration duration = getSelectedDuration();
			if(duration.getValue() != VALUE || (!voice.isEmpty() && voice.getDuration().getValue() != VALUE)){
				//comienza el undoable
				UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
				
				getSelectedDuration().setValue(VALUE);
				getSelectedDuration().setDotted(false);
				getSelectedDuration().setDoubleDotted(false);
				setDurations();
				
				//termia el undoable
				addUndoableEdit(undoable.endUndo());
			}
		}
	}
	
	private void setDurations() {
		Caret caret = getEditor().getTablature().getCaret();
		caret.changeDuration(getSelectedDuration().clone(getSongManager().getFactory()));
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
	
	public TGDuration getSelectedDuration(){
		return getEditor().getTablature().getCaret().getDuration();
	}
}
