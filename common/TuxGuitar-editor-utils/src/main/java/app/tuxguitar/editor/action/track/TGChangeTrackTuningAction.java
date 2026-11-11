package app.tuxguitar.editor.action.track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGTrackManager;
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
		if( track == null ) {
			return;
		}
		TGTrackManager trackManager = getSongManager(context).getTrackManager();
		if( strings != null ){
			changeTuning(trackManager, track, strings);
		}
		if( offset != null ) {
			trackManager.changeOffset(track, offset);
		}
	}

	// the notes keep their pitch: on their strings when every string moves by the same interval, else laid again
	private void changeTuning(TGTrackManager trackManager, TGTrack track, List<TGString> strings){
		int transposition = getTransposition(track, strings);
		if (transposition != 0) {
			int[] transpositions = new int[strings.size()];
			Arrays.fill(transpositions, transposition);
			track.setStrings(strings);
			trackManager.transposeNotes(track, transpositions, true, true );
		} else {
			List<Integer> fromStrings = new ArrayList<Integer>();
			for (int i=1; i<=track.getStrings().size(); i++) {
				fromStrings.add(track.getString(i).getValue());
			}
			track.setStrings(strings);
			trackManager.allocateNotesToStrings(fromStrings, track, strings);
		}
	}

	// the interval every string moves by; 0 when they do not all move by the same one, or the string count changes
	private static int getTransposition(TGTrack track, List<TGString> strings){
		if (track.getStrings().size() != strings.size()) {
			return 0;
		}
		int transposition = track.getString(1).getValue() - strings.get(0).getValue();
		for (int i=1; i<strings.size();i++) {
			if ((track.getString(i+1).getValue() - strings.get(i).getValue()) != transposition) {
				return 0;
			}
		}
		return transposition;
	}

}
