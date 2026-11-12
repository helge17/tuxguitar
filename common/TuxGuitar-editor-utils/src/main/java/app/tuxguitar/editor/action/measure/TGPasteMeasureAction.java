package app.tuxguitar.editor.action.measure;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.song.helpers.TGSongSegmentHelper;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGPasteMeasureAction extends TGActionBase{

	public static final String NAME = "action.measure.paste";

	public static final String ATTRIBUTE_PASTE_MODE = "pasteMode";
	public static final String ATTRIBUTE_PASTE_COUNT = "pasteCount";

	public static final Integer TRANSFER_TYPE_REPLACE = 1;
	public static final Integer TRANSFER_TYPE_INSERT = 2;

	public TGPasteMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Integer pasteMode = context.getAttribute(ATTRIBUTE_PASTE_MODE);
		Integer pasteCount = context.getAttribute(ATTRIBUTE_PASTE_COUNT);
		TGSongSegment segment = TGClipboard.getInstance(this.getContext()).getSegment();
		if( pasteMode > 0 && pasteCount > 0 && segment != null ) {
			TGSongManager songManager = this.getSongManager(context);
			TGSongSegmentHelper helper = new TGSongSegmentHelper(songManager);
			TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			segment = helper.createSegmentCopies(segment, pasteCount);
			if(!segment.isEmpty() && fitsTrack(segment, track)) {
				this.paste(context, helper, segment, pasteMode, track);
			}
		}
	}

	private void paste(TGActionContext context, TGSongSegmentHelper helper, TGSongSegment segment, Integer pasteMode, TGTrack track){
		TGSongManager songManager = this.getSongManager(context);
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		TGMeasureHeader first = segment.getHeaders().get(0);

		//Si el segmento tiene una sola pista,
		//la pego en la pista seleccionada
		int toTrack = (segment.getTracks().size() == 1 ? track.getNumber() : 0);

		if( pasteMode.equals(TRANSFER_TYPE_REPLACE)) {
			int count = segment.getHeaders().size();
			int freeSpace =  (track.countMeasures() - (measure.getNumber() - 1));
			long theMove = (measure.getStart() - first.getStart());

			for(int i = freeSpace; i < count; i ++){
				songManager.addNewMeasureBeforeEnd(song);
			}
			helper.replaceMeasures(song, segment.clone(songManager.getFactory()), theMove, toTrack);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		} else if( pasteMode.equals(TRANSFER_TYPE_INSERT)) {
			int fromNumber = measure.getNumber();
			long theMove = (measure.getStart() - first.getStart());
			helper.insertMeasures(song, segment.clone(songManager.getFactory()), fromNumber, theMove, toTrack);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}

	// measures of one track paste onto a track of their kind: audio onto an audio track, notes onto any other
	private boolean fitsTrack(TGSongSegment segment, TGTrack track) {
		if( segment.getTracks().size() != 1 ) {
			return true;
		}
		List<TGMeasure> measures = segment.getTracks().get(0).getMeasures();
		boolean audio = measures.stream().anyMatch(measure -> !measure.getTakeClips().isEmpty());
		boolean notes = measures.stream().flatMap(measure -> measure.getBeats().stream()).anyMatch(beat -> !beat.isRestBeat());
		return (track.isAudio() ? !notes : !audio);
	}
}
