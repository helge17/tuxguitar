package org.herac.tuxguitar.editor.action.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGCleanMeasureListAction extends TGActionBase {
	
	public static final String NAME = "action.measure.clean-list";
	
	public static final String ATTRIBUTE_MEASURE_NUMBER_1 = "measureNumber1";
	public static final String ATTRIBUTE_MEASURE_NUMBER_2 = "measureNumber2";
	
	public TGCleanMeasureListAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext actionContext){
		TGTrack track = ((TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGMeasureHeader header = ((TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
		int m1 = ((Integer) actionContext.getAttribute(ATTRIBUTE_MEASURE_NUMBER_1)).intValue();
		int m2 = ((Integer) actionContext.getAttribute(ATTRIBUTE_MEASURE_NUMBER_2)).intValue();
		if(m1 > 0 && m1 <= m2){
			TGSongManager songManager = getSongManager(actionContext);
			TGActionManager actionManager = TGActionManager.getInstance(getContext());
			for( int number = m1 ; number <= m2 ; number ++ ){
				TGMeasure nextMeasure = songManager.getTrackManager().getMeasure( track , number );
				if( nextMeasure != null ){
					actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, nextMeasure);
					actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, nextMeasure.getHeader());
					actionManager.execute(TGCleanMeasureAction.NAME, actionContext);
				}
			}
		}
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
	}
}
