/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteNoteOrRestAction extends TGActionBase{
	
	public static final String NAME = "action.beat.general.delete-note-or-rest";
	
	public DeleteNoteOrRestAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		Caret caret = getEditor().getTablature().getCaret();
		TGBeat beat = caret.getSelectedBeat();
		TGVoice voice = beat.getVoice( caret.getVoice() );
		if( beat.isTextBeat() && beat.isRestBeat() ){
			getSongManager().getMeasureManager().removeText(beat);
		}else if(voice.isRestVoice()){
			getSongManager().getMeasureManager().removeVoice(voice ,true);
		}else{
			int string = caret.getSelectedString().getNumber();
			getSongManager().getMeasureManager().removeNote(caret.getMeasure(),beat.getStart(), caret.getVoice(), string);
		}
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		updateTablature();
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
