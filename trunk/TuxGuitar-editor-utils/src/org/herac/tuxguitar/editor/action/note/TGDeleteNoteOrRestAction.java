package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGDeleteNoteOrRestAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.delete-note-or-rest";
	
	public TGDeleteNoteOrRestAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
		
		if( beat.isTextBeat() && beat.isRestBeat() ){
			songManager.getMeasureManager().removeText(beat);
		} 
		else if( voice.isRestVoice() ){
			songManager.getMeasureManager().removeVoice(voice, true);
		}
		else {
			songManager.getMeasureManager().removeNote(measure, beat.getStart(), voice.getIndex(), string.getNumber());
		}
	}
}
