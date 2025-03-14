package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGRepeatCloseAction extends TGActionBase {

	public static final String NAME = "action.insert.close-repeat";

	public static final String ATTRIBUTE_REPEAT_COUNT = "repeatCount";

	public TGRepeatCloseAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader measureHeader = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
		int repeatCount = ((Integer) context.getAttribute(ATTRIBUTE_REPEAT_COUNT)).intValue();
		if( repeatCount >= 0 ){
			songManager.changeCloseRepeat(song, measureHeader.getStart(), repeatCount);
		}
	}
}
