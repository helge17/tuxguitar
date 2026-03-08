package app.tuxguitar.editor.action.measure;

import java.util.Arrays;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGStoredBeatList;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGCopyBeatAction extends TGActionBase{

	public static final String NAME = "action.beat.copy";

	public TGCopyBeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext){
		TGBeat beat = (TGBeat) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		TGTrack track = (TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if ((beat != null) && (track != null)) {
			TGClipboard.getInstance(this.getContext()).setData(
					new TGStoredBeatList(Arrays.asList(beat),
					track.getStrings(),
					track.isPercussion(),
					getSongManager(actionContext).getFactory()));
		}
	}
}
