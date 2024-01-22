package org.herac.tuxguitar.editor.action.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.song.helpers.TGSongSegmentHelper;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
		
		if( pasteMode > 0 && pasteCount > 0 ) {
			TGSongManager songManager = this.getSongManager(context);
			TGSongSegmentHelper helper = new TGSongSegmentHelper(songManager);
			TGSongSegment segment = TGClipboard.getInstance(this.getContext()).getSegment();
			if( segment != null ) {
				segment = helper.createSegmentCopies(segment, pasteCount);
				if(!segment.isEmpty()) {
					TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
					TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
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
						segment = segment.clone(songManager.getFactory());
						helper.replaceMeasures(song, segment, theMove, toTrack);
					} else if( pasteMode.equals(TRANSFER_TYPE_INSERT)) {
						int fromNumber = measure.getNumber();
						long theMove = (measure.getStart() - first.getStart());

						// TODO, BUG here: source and destination tracks may not have the same tuning
						// even not the same number of strings
						// yet, measures are pasted: measure contains beats, beat contains voices, voice contains notes, note contains (string number + fret number)
						// if not the same tuning, notes can change
						// also possible to create a note on the 5th string of a 4 string instrument
						
						segment = segment.clone(songManager.getFactory());
						helper.insertMeasures(song, segment, fromNumber, theMove, toTrack);
					}
				}
			}
		}
	}
}
