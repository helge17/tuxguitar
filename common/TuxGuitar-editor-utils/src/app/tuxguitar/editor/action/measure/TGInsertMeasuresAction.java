package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.song.helpers.TGSongSegmentHelper;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
