package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGRepeatOpenAction extends TGActionBase {

	public static final String NAME = "action.insert.open-repeat";

	public TGRepeatOpenAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader measureHeader = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));

		songManager.changeOpenRepeat(song, measureHeader.getStart());
		
		if (measureHeader.isRepeatOpen()) {
			// conflict with double bar?
			TGMeasureHeader previousHeader = songManager.getPrevMeasureHeader(song, measureHeader);
			if ((previousHeader != null) && previousHeader.isDoubleBar()) {
				TGActionManager actionManager = TGActionManager.getInstance(getContext());
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, previousHeader);
				actionManager.execute(TGDoubleBarAction.NAME, context);
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, measureHeader);
			}
		}
	}
}
