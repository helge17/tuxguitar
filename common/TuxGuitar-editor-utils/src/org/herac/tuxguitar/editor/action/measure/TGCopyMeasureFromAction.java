package org.herac.tuxguitar.editor.action.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGCopyMeasureFromAction extends TGActionBase {
	
	public static final String NAME = "action.measure.copy.from";
	
	public static final String ATTRIBUTE_FROM = "from";
	
	public TGCopyMeasureFromAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGMeasure from = ((TGMeasure) context.getAttribute(ATTRIBUTE_FROM));
		
		songManager.getTrackManager().copyMeasureFrom(measure, from);
	}
}
