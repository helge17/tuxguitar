package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

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
