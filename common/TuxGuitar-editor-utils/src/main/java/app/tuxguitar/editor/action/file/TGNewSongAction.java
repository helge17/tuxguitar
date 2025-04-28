package app.tuxguitar.editor.action.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGNewSongAction extends TGActionBase{

	public static final String NAME = "action.song.new-default";

	public TGNewSongAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSongManager(context).newSong());

		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGLoadSongAction.NAME, context);
	}
}
