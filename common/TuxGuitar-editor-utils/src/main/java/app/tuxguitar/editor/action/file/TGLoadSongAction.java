package app.tuxguitar.editor.action.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
