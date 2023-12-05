package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeKeySignatureAction extends TGActionBase {
	
	public static final String NAME = "action.composition.change-key-signature";
	
	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";
	public static final String ATTRIBUTE_KEY_SIGNATURE = "keySignature";
	
	public TGChangeKeySignatureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		int keySignature = ((Integer) context.getAttribute(ATTRIBUTE_KEY_SIGNATURE)).intValue();
		boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END)).booleanValue();
		
		TGSongManager songManager = getSongManager(context);
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));		
		
		songManager.getTrackManager().changeKeySignature(track, measure, keySignature, applyToEnd);
	}
}
