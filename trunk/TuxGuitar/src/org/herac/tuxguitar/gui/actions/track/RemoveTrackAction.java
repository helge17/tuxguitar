/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.track;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableRemoveTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveTrackAction extends Action{
	public static final String NAME = "action.track.remove";
	
	public RemoveTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		Caret caret = getEditor().getTablature().getCaret();
		
		if(getSongManager().getSong().countTracks() <= 1){
			//TuxGuitar.instance().getAction(NewFileAction.NAME).process(e);
			TuxGuitar.instance().newSong();
			return 0;
		}
		//comienza el undoable
		UndoableRemoveTrack undoable = UndoableRemoveTrack.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		TGTrackImpl track = caret.getTrack();
		TGTrackImpl nextCaretTrack = (TGTrackImpl)getSongManager().getTrack(track.getNumber() + 1);
		if(nextCaretTrack == null){
			nextCaretTrack =  (TGTrackImpl)getSongManager().getTrack(track.getNumber() - 1);
		}
		getSongManager().removeTrack(track);
		updateTablature();
		
		//move the caret to the next or previous track
		if(nextCaretTrack != null){
			caret.update(nextCaretTrack.getNumber(),getSongManager().getTrackManager().getMeasureAt(nextCaretTrack, caret.getMeasure().getStart()).getStart(),1);
		}
		TuxGuitar.instance().getMixer().update();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		return 0;
	}
}
