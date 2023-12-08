package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGRemoveUnusedVoiceAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.remove-unused-voice";
	
	public TGRemoveUnusedVoiceAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGVoice voice = (TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		if( measure != null && voice != null ){			
			for( int v = 0 ; v < TGBeat.MAX_VOICES ; v ++ ){
				if( voice.getIndex() != v ){
					getSongManager(context).getMeasureManager().removeMeasureVoices( measure, v );
				}
			}
		}
	}
}
