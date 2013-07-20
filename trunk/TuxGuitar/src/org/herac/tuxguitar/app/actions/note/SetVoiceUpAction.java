/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.note;

import java.util.Iterator;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.graphics.control.TGBeatGroup;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGVoiceImpl;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetVoiceUpAction extends TGActionBase{
	
	public static final String NAME = "action.beat.general.voice-up";
	
	public SetVoiceUpAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		Caret caret = getEditor().getTablature().getCaret();
		TGBeatImpl beat = caret.getSelectedBeat();
		if( beat != null ){
			TGVoiceImpl voice = beat.getVoiceImpl( caret.getVoice() );
			TGBeatGroup group = voice.getBeatGroup();
			if(!voice.isEmpty() && !voice.isRestVoice() && group != null ){
				//comienza el undoable
				UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
				
				Iterator it = group.getVoices().iterator();
				while( it.hasNext() ){
					TGVoice current = (TGVoice)it.next();
					getSongManager().getMeasureManager().changeVoiceDirection(current, TGVoice.DIRECTION_UP);
				}
				
				//termia el undoable
				addUndoableEdit(undoable.endUndo());
				TuxGuitar.instance().getFileHistory().setUnsavedFile();
				
				updateTablature();
			}
		}
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
