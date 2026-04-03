package app.tuxguitar.app.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGGoLastTrackAction extends TGActionBase{

	public static final String NAME = "action.track.go-last";

	public TGGoLastTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGTrack track = getSongManager(context).getLastTrack(song);
		if( track != null ){
			TablatureEditor.getInstance(getContext()).getTablature().getCaret().update(track.getNumber());
		}
	}
}
