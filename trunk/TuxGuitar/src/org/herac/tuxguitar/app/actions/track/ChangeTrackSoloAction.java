/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.track;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.undo.undoables.track.UndoableTrackSoloMute;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTrackSoloAction extends Action{
	
	public static final String NAME = "action.track.change-solo";
	
	public ChangeTrackSoloAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		TGTrack track = getEditor().getTablature().getCaret().getTrack();
		if( track != null ){
			//comienza el undoable
			UndoableTrackSoloMute undoable = UndoableTrackSoloMute.startUndo(track);
			
			getSongManager().getTrackManager().changeSolo(track, !track.isSolo());
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo(track));
			
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			if (TuxGuitar.instance().getPlayer().isRunning()) {
				TuxGuitar.instance().getPlayer().updateTracks();
			}
		}
		return 0;
	}
}
