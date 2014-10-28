/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.track.UndoableMoveTrackUp;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MoveTrackUpAction extends TGActionBase{
	
	public static final String NAME = "action.track.move-up";
	
	public MoveTrackUpAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		//comienza el undoable
		UndoableMoveTrackUp undoable = UndoableMoveTrackUp.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		TGSong song = caret.getSong();
		TGTrack track = caret.getTrack();
		
		if(getSongManager().moveTrackUp(song, track)){
			updateSong();
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo(track));
			TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		}
	}
}
