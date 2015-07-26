package org.herac.tuxguitar.editor.action.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.song.helpers.TGSongSegmentHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGInsertMeasuresAction extends TGActionBase {
	
	public static final String NAME = "action.measure.insert";
	
	public static final String ATTRIBUTE_SONG_SEGMENT = TGSongSegment.class.getName();
	
	public static final String ATTRIBUTE_FROM_NUMBER = "fromNumber";
	public static final String ATTRIBUTE_TO_TRACK = "toTrack";
	public static final String ATTRIBUTE_THE_MOVE = "theMove";
	
	public TGInsertMeasuresAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGSongSegment segment = ((TGSongSegment) context.getAttribute(ATTRIBUTE_SONG_SEGMENT));
		int fromNumber = ((Integer) context.getAttribute(ATTRIBUTE_FROM_NUMBER)).intValue();
		int toTrack = ((Integer) context.getAttribute(ATTRIBUTE_TO_TRACK)).intValue();
		long theMove = ((Long) context.getAttribute(ATTRIBUTE_THE_MOVE)).longValue();

		TGSongManager tgSongManager = getSongManager(context);
		TGSongSegmentHelper tgSongSegmentHelper = new TGSongSegmentHelper(tgSongManager);
		tgSongSegmentHelper.insertMeasures(song, segment.clone(tgSongManager.getFactory()), fromNumber, theMove, toTrack);
	}
}
