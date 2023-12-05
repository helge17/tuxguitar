package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeClefAction extends TGActionBase {
	
	public static final String NAME = "action.composition.change-clef";
	
	public static final String ATTRIBUTE_CLEF = "clef";
	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";
	
	public TGChangeClefAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		int clef = ((Integer) context.getAttribute(ATTRIBUTE_CLEF)).intValue();
		boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END)).booleanValue();
		
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));		
		
		getSongManager(context).getTrackManager().changeClef(track, measure, clef, applyToEnd);
	}
}
