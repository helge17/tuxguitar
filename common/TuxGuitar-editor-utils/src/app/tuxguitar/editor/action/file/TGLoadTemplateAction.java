package app.tuxguitar.editor.action.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.template.TGTemplate;
import app.tuxguitar.editor.template.TGTemplateManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGLoadTemplateAction extends TGActionBase{

	public static final String NAME = "action.song.new";

	public static final String ATTRIBUTE_TEMPLATE = TGTemplate.class.getName();

	public TGLoadTemplateAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTemplateManager tgTemplateManager = TGTemplateManager.getInstance(getContext());
		TGTemplate tgTemplate = context.getAttribute(ATTRIBUTE_TEMPLATE);
		TGSong tgSong = null;
		if( tgTemplate == null ) {
			tgSong = tgTemplateManager.getDefaultTemplateAsSong();
		} else {
			tgSong = tgTemplateManager.getTemplateAsSong(tgTemplate);
		}

		if( tgSong != null ){
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgSong);

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGLoadSongAction.NAME, context);
		} else {
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGNewSongAction.NAME, context);
		}
	}
}
