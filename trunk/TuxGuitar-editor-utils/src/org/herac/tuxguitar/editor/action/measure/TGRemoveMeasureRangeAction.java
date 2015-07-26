package org.herac.tuxguitar.editor.action.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGRemoveMeasureRangeAction extends TGActionBase {
	
	public static final String NAME = "action.measure.remove.range";
	
	public static final String ATTRIBUTE_MEASURE_NUMBER_1 = "measureNumber1";
	public static final String ATTRIBUTE_MEASURE_NUMBER_2 = "measureNumber2";
	
	public TGRemoveMeasureRangeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		TGSong song = ((TGSong) tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		int m1 = ((Integer) tgActionContext.getAttribute(ATTRIBUTE_MEASURE_NUMBER_1)).intValue();
		int m2 = ((Integer) tgActionContext.getAttribute(ATTRIBUTE_MEASURE_NUMBER_2)).intValue();
		if( m1 > 0 && m1 <= m2 && m2 <= song.countMeasureHeaders() ){
			if(m1 == 1 && m2 == song.countMeasureHeaders()){
				return;
			}
			
			for(int i = m1; i <= m2; i ++) {
				tgActionContext.setAttribute(TGRemoveMeasureAction.ATTRIBUTE_MEASURE_NUMBER, m1);
				TGActionManager.getInstance(getContext()).execute(TGRemoveMeasureAction.NAME, tgActionContext);
			}
		}
	}
}
