package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.composition.TGDoubleBarAction;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGRemoveMeasureAction extends TGActionBase {

	public static final String NAME = "action.measure.remove";

	public static final String ATTRIBUTE_MEASURE_NUMBER = "measureNumber";

	public TGRemoveMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		int number = ((Integer) context.getAttribute(ATTRIBUTE_MEASURE_NUMBER)).intValue();

		if( number > 0 && number <=  (song.countMeasureHeaders() + 1)){
			TGSongManager tgSongManager = getSongManager(context);
			// if last measure is being removed and if preceding one has a double bar, remove the double bar
			if ((number > 1) && (number == song.countMeasureHeaders())) {
				TGMeasureHeader prevHeader = tgSongManager.getMeasureHeader(song, number-1);
				if (prevHeader.isDoubleBar()) {
					TGMeasureHeader header = (TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, prevHeader);
					TGActionManager.getInstance(getContext()).execute(TGDoubleBarAction.NAME, context);
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
				}
			}
			// effectively remove measure
			tgSongManager.removeMeasure(song, number);
	
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
