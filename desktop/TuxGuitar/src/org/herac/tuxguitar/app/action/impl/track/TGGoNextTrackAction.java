package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGGoNextTrackAction extends TGActionBase{
	
	public static final String NAME = "action.track.go-next";
	
	public TGGoNextTrackAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGTrack nextTrack = getSongManager(context).getTrack(song, track.getNumber() + 1);
		if( nextTrack != null ){
			TablatureEditor.getInstance(getContext()).getTablature().getCaret().update(nextTrack.getNumber());
		}
	}
}
