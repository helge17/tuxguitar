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
			TGMeasureManager measureManager = songManager.getMeasureManager();
			TGBeat beat = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			TGBeatRange beatRange = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
			TGTrack destTrack = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			TGSong song = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);

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

				// need to add new measure(s)?
				TGMeasureHeader lastHeader = songManager.getLastMeasureHeader(song);
				long endSong = lastHeader.getPreciseStart() + lastHeader.getPreciseLength();
				long endLastBeatToPaste = 0;
				for (TGBeat beatToInsert : beatsListToPaste.getBeats()) {
					endLastBeatToPaste = Math.max(endLastBeatToPaste, destinationBeat.getPreciseStart() + beatToInsert.getPreciseStart() + measureManager.getMaximumDuration(beatToInsert).getPreciseTime());
				}
				while (endLastBeatToPaste > endSong) {
					lastHeader = songManager.addNewMeasureBeforeEnd(song);
					measureManager.autoCompleteSilences(destTrack.getMeasure(lastHeader.getNumber()-1));
					endSong = lastHeader.getPreciseStart() + lastHeader.getPreciseLength();
				}

				// replace beats at required position
				List<TGBeat> newBeats = trackManager.replaceBeats(destTrack, beatsListToPaste.getBeats(), destinationBeat.getPreciseStart());

				// re-select new beats
				if ((newBeats!=null) && (newBeats.size()>0))  {	// test is theoretically useless, just a precaution
					Selector selector = TablatureEditor.getInstance(getContext()).getTablature().getSelector();
					selector.initializeSelection(newBeats.get(0));
					// look for last updated beat (after pasting)
					TGBeat lastUpdatedBeat = newBeats.get(newBeats.size()-1);
					long endLastUpdatedBeat = lastUpdatedBeat.getPreciseStart() + measureManager.getMaximumDuration(lastUpdatedBeat).getPreciseTime();
					while (endLastUpdatedBeat < endLastBeatToPaste) {
						// last updated beat has been split over several measures, need to find the last one to re-select
						TGBeat nextBeat = measureManager.getBeatPrecise(destTrack, endLastUpdatedBeat);
						if (nextBeat != null) {
							lastUpdatedBeat = nextBeat;
							endLastUpdatedBeat = lastUpdatedBeat.getPreciseStart() + measureManager.getMaximumDuration(lastUpdatedBeat).getPreciseTime();;
						}
						else {
							break;
						}
					}
					selector.updateSelection(lastUpdatedBeat);
				}
			}
		}
	}

}
