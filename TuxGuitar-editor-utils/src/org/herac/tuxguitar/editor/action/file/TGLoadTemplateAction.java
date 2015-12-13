package org.herac.tuxguitar.editor.action.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.template.TGTemplate;
import org.herac.tuxguitar.editor.template.TGTemplateManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGLoadTemplateAction extends TGActionBase{
	
	public static final String NAME = "action.song.new-from-template";
	
	public static final String ATTRIBUTE_TEMPLATE = TGTemplate.class.getName();
	
	public TGLoadTemplateAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTemplateManager tgTemplateManager = TGTemplateManager.getInstance(getContext());
		TGTemplate tgTemplate = context.getAttribute(ATTRIBUTE_TEMPLATE);
		if( tgTemplate == null ) {
			tgTemplate = tgTemplateManager.getDefaultTemplate();
		}
		
		TGSong tgSong = null;
		if( tgTemplate != null ){
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
