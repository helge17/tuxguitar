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
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackGeneric;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MoveBeatsLeftAction extends Action{
	public static final String NAME = "action.beat.general.move-left";
	
	public MoveBeatsLeftAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		Caret caret = getEditor().getTablature().getCaret();		
		TGBeat beat = caret.getSelectedBeat();
		TGMeasure measure = caret.getMeasure();
		TGTrack track = caret.getTrack();
		TGDuration duration = (beat != null ? beat.getVoice( caret.getVoice() ).getDuration() : null );
		if(beat != null && measure != null && track != null && duration != null){
			//comienza el undoable
			UndoableTrackGeneric undoable = UndoableTrackGeneric.startUndo(track);
			
			getSongManager().getTrackManager().moveTrackBeats(track, measure.getStart(), beat.getStart(), -duration.getTime() );
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo(track));
			
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			updateTablature();
		}
		return 0;
	}
}
