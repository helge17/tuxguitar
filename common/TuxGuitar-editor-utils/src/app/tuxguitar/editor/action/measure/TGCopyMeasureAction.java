package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.song.helpers.TGSongSegmentHelper;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGCopyMeasureAction extends TGActionBase{

	public static final String NAME = "action.measure.copy";

	public static final String ATTRIBUTE_ALL_TRACKS = "copyAllTracks";
	public static final String ATTRIBUTE_MEASURE_NUMBER_1 = "measureNumber1";
	public static final String ATTRIBUTE_MEASURE_NUMBER_2 = "measureNumber2";
	public static final String ATTRIBUTE_COPY_MARKERS = "copyMarkers";

	public TGCopyMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Integer m1 = context.getAttribute(ATTRIBUTE_MEASURE_NUMBER_1);
		Integer m2 = context.getAttribute(ATTRIBUTE_MEASURE_NUMBER_2);
		Boolean allTracks = context.getAttribute(ATTRIBUTE_ALL_TRACKS);
		if( m1 > 0 && m1 <= m2 ){
			TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGSongSegmentHelper helper = new TGSongSegmentHelper(this.getSongManager(context));
			TGSongSegment segment = null;
			if( Boolean.TRUE.equals(allTracks)) {
				segment = helper.copyMeasures(song, m1, m2);
			} else {
				segment = helper.copyMeasures(song, m1, m2, (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
			}

			if (!Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_COPY_MARKERS))) {
				for(TGMeasureHeader header : segment.getHeaders()){
					header.setMarker(null);
				}
			}
			TGClipboard.getInstance(this.getContext()).setData(segment);
		}
	}
}
