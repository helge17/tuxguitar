package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
