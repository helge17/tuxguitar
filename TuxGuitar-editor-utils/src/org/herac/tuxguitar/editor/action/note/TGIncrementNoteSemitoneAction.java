package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

public class TGIncrementNoteSemitoneAction extends TGActionBase {
	
	public static final String NAME = "action.note.general.increment-semitone";
	
	public TGIncrementNoteSemitoneAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGNote note = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));		
		if( note != null ){
			TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
			TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
			TGSongManager songManager = getSongManager(context);
			
			if( songManager.getMeasureManager().moveSemitoneUp(measure, beat.getStart(), note.getString())){
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		}
	}
}
