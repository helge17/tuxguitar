package org.herac.tuxguitar.app.action.impl.edit;

import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsAction;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.helpers.TGStoredBeatList;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
		} else if (beatList != null && beatList.getLength() > 0) {
			TGFactory factory = getSongManager(tgActionContext).getFactory();
			TGSongManager songManager = this.getSongManager(tgActionContext);
			TGTrackManager trackManager = songManager.getTrackManager();
			TGBeat start = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			TGTrack destTrack = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			
			// don't copy paste between percussion/non-percussion tracks
			if (beatList.isPercussionTrack() == songManager.isPercussionChannel(destTrack.getSong(), destTrack.getChannelId())) {
				TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
				tgActionContext.setAttribute(TGMoveBeatsAction.ATTRIBUTE_MOVE, -beatList.getLength());
				tgActionManager.execute(TGMoveBeatsAction.NAME, tgActionContext);
				
				// clone clipboard content before modifying it, so it can be re-pasted later
				TGStoredBeatList beatsListToPaste = beatList.clone(factory);
				// then adapt notes to destination track (tuning might differ from source track)
				trackManager.allocateNotesToStrings(beatsListToPaste.getStringValues(), beatsListToPaste.getBeats(),
						destTrack.getStrings(), destTrack.getMaxFret());
				
				// paste
				List<TGBeat> newBeats = trackManager.addBeats(destTrack, beatsListToPaste, start.getStart());
				trackManager.moveOutOfBoundsBeatsToNewMeasure(destTrack, start.getStart());
				
				// re-select new beats
				if (newBeats.size()>0)  {	// test is theoretically useless, just a precaution
					Selector selector = TablatureEditor.getInstance(getContext()).getTablature().getSelector();
					selector.initializeSelection(newBeats.get(0));
					selector.updateSelection(newBeats.get(newBeats.size()-1));
				}
			}
		}
	}

}
