/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.note;

import java.util.Iterator;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGBeatGroup;
import org.herac.tuxguitar.gui.editors.tab.TGBeatImpl;
import org.herac.tuxguitar.gui.editors.tab.TGVoiceImpl;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetVoiceAutoAction extends Action{
	public static final String NAME = "action.beat.general.voice-auto";
	
	public SetVoiceAutoAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
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
					getSongManager().getMeasureManager().changeVoiceDirection(current, TGVoice.DIRECTION_NONE);
				}
				
				//termia el undoable
				addUndoableEdit(undoable.endUndo());
				TuxGuitar.instance().getFileHistory().setUnsavedFile();
				
				updateTablature();
			}
		}
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
