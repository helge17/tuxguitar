package org.herac.tuxguitar.editor.action.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGLoadSongAction extends TGActionBase{
	
	public static final String NAME = "action.song.load";
	
	public TGLoadSongAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGDocumentManager tgDocumentManager = TGDocumentManager.getInstance(getContext());
		tgDocumentManager.setSong(song);
	}
}
