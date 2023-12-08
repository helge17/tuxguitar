package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTimeSignatureAction extends TGActionBase {
	
	public static final String NAME = "action.composition.change-time-signature";
	
	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";
	
	public TGChangeTimeSignatureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader header = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));		
		TGTimeSignature timeSignature = ((TGTimeSignature) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE));
		boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END)).booleanValue();
		
		songManager.changeTimeSignature(song, header, timeSignature, applyToEnd);
	}
}
