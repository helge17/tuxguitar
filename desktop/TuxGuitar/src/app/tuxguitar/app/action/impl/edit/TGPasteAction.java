package app.tuxguitar.app.action.impl.edit;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import app.tuxguitar.app.view.component.tab.Selector;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.helpers.TGStoredBeatList;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.managers.TGTrackManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGPasteAction extends TGActionBase {

	public static final String NAME = "action.edit.paste";

	public TGPasteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		TGClipboard clipboard = TGClipboard.getInstance(getContext());
		TGStoredBeatList beatList = TGClipboard.getInstance(this.getContext()).getBeats();
		if (clipboard.getSegment() != null) {
			TGActionManager.getInstance(this.getContext()).execute(TGOpenMeasurePasteDialogAction.NAME, tgActionContext);
		} else if (beatList != null && beatList.getBeats().size() > 0) {
			TGFactory factory = getSongManager(tgActionContext).getFactory();
			TGSongManager songManager = this.getSongManager(tgActionContext);
			TGTrackManager trackManager = songManager.getTrackManager();
			TGBeat beat = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			TGBeatRange beatRange = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
			TGTrack destTrack = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

			// where to paste to?
			TGBeat destinationBeat = beat;
			if ((beatRange != null) && !beatRange.isEmpty()) {
				destinationBeat = beatRange.getBeats().get(0);
			}

			// don't copy paste between percussion/non-percussion tracks
			if (beatList.isPercussionTrack() == destTrack.isPercussion()) {
				// clone clipboard content before modifying it, so it can be re-pasted later
				TGStoredBeatList beatsListToPaste = beatList.clone(factory);
				// then adapt notes to destination track (tuning might differ from source track)
				trackManager.allocateNotesToStrings(beatsListToPaste.getStringValues(), beatsListToPaste.getBeats(),
						destTrack.getStrings(), destTrack.getMaxFret());

				// replace beats at required position
				List<TGBeat> newBeats = trackManager.replaceBeats(destTrack, beatsListToPaste.getBeats(), destinationBeat.getPreciseStart());

				// need to add extra beats at the end? (e.g. when pasting at end of song)
				if (beatsListToPaste.getBeats().size() > newBeats.size()) {
					TGMeasureManager measureManager = songManager.getMeasureManager();
					TGSong song = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
					TGMeasureHeader newHeader = songManager.addNewMeasureBeforeEnd(song);
					TGMeasure newMeasure = destTrack.getMeasure(newHeader.getNumber()-1);
					long beatPreciseStart = newHeader.getPreciseStart();
					for (int i=newBeats.size(); i<beatsListToPaste.getBeats().size(); i++) {
						TGBeat beatToInsert = beatsListToPaste.getBeats().get(i);
						beatToInsert.setPreciseStart(beatPreciseStart);
						beatPreciseStart += measureManager.getMinimumDuration(beatToInsert) .getPreciseTime();
						measureManager.addBeat(newMeasure, beatToInsert);
						newBeats.add(beatToInsert);
					}
					trackManager.moveOutOfBoundsBeatsToNewMeasure(destTrack, newHeader.getStart());
				}

				// re-select new beats
				if ((newBeats!=null) && (newBeats.size()>0))  {	// test is theoretically useless, just a precaution
					Selector selector = TablatureEditor.getInstance(getContext()).getTablature().getSelector();
					selector.initializeSelection(newBeats.get(0));
					selector.updateSelection(newBeats.get(newBeats.size()-1));
				}
			}
		}
	}

}
