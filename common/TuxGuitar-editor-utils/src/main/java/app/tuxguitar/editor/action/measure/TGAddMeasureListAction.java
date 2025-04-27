package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGAddMeasureListAction extends TGActionBase {

	public static final String NAME = "action.measure.add-list";

	public static final String ATTRIBUTE_MEASURE_NUMBER = "measureNumber";
	public static final String ATTRIBUTE_MEASURE_COUNT = "measureCount";

	public TGAddMeasureListAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext){
		TGSong song = ((TGSong) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		int number = ((Integer) actionContext.getAttribute(ATTRIBUTE_MEASURE_NUMBER)).intValue();
		int count = ((Integer) actionContext.getAttribute(ATTRIBUTE_MEASURE_COUNT)).intValue();

		if(count > 0 && number > 0 && number <=  (song.countMeasureHeaders() + 1)){
			TGActionManager actionManager = TGActionManager.getInstance(getContext());
			for( int i = 0 ; i < count ; i ++ ){
				actionContext.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, Integer.valueOf((number + i)));
				actionManager.execute(TGAddMeasureAction.NAME, actionContext);
			}
		}
	}
}
