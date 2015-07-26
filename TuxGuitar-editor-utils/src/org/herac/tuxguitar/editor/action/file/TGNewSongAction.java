package org.herac.tuxguitar.editor.action.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGNewSongAction extends TGActionBase{
	
	public static final String NAME = "action.song.new";
	
	public TGNewSongAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSongManager(context).newSong());
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGLoadSongAction.NAME, context);
	}
}
