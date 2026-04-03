package app.tuxguitar.editor.action.track;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGChangeTrackTuningAction extends TGActionBase {

	public static final String NAME = "action.track.change-tuning";

	public static final String ATTRIBUTE_OFFSET = "offset";
	public static final String ATTRIBUTE_STRINGS = "strings";

	public TGChangeTrackTuningAction(TGContext context) {
		super(context, NAME);
	}

	@SuppressWarnings("unchecked")
	protected void processAction(TGActionContext context){
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		Integer offset = context.getAttribute(ATTRIBUTE_OFFSET);
		List<TGString> strings = ((List<TGString>) context.getAttribute(ATTRIBUTE_STRINGS));
		if( track != null ) {
			TGSongManager songManager = getSongManager(context);
			if( strings != null ){
				int transposition = 0; // did all strings change by the same value? if so, set variable to a non-zero value
				if (track.getStrings().size() == strings.size()) {
					transposition = track.getString(1).getValue() - strings.get(0).getValue();
					for (int i=1; i<strings.size();i++) {
						if ((track.getString(i+1).getValue() - strings.get(i).getValue()) != transposition) {
							transposition = 0;
							break;
						}
					}
				}
				if (transposition != 0) {
					// same number of strings, all shifted by the same value
					int[] transpositions = new int[strings.size()];
					for (int i=0; i<strings.size();i++) {
						transpositions[i] = transposition;
					}
					track.setStrings(strings);
					songManager.getTrackManager().transposeNotes(track, transpositions, true, true );
				} else {
					// reallocate notes to strings
					List<Integer> fromStrings = new ArrayList<Integer>();
					for (int i=1; i<=track.getStrings().size(); i++) {
						fromStrings.add(track.getString(i).getValue());
					}
					track.setStrings(strings);
					songManager.getTrackManager().allocateNotesToStrings(fromStrings, track, strings);
				}
			}
			if( offset != null ) {
				songManager.getTrackManager().changeOffset(track, offset);
			}
		}
	}

}
