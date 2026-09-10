package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGTrackReplacePercussionAction extends TGActionBase {

	public static final String NAME = "action.track.replace-percussion";
	public static final String ATTRIBUTE_PERCUSSION_FIND = "replace-percussion.find";
	public static final String ATTRIBUTE_PERCUSSION_REPLACE = "replace-percussion.replace";

	public TGTrackReplacePercussionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGSongManager songManager = (TGSongManager) context
				.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGTrack track = (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		int find = (int) context.getAttribute(ATTRIBUTE_PERCUSSION_FIND);
		int replace = (int) context.getAttribute(ATTRIBUTE_PERCUSSION_REPLACE);

		if ((songManager != null) && (track != null) && (find != 0) && (replace != 0)) {
			songManager.getTrackManager().replacePercussionNotes(track, find, replace);
		}
	}
}