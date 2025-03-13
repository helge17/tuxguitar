package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGUserFileUtils;

public class TGCustomTemplateDeleteAction extends TGActionBase {

	public static final String NAME = "action.custom-template.delete";

	public TGCustomTemplateDeleteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext) {
		TGUserFileUtils.deleteUserTemplate();
		TGEventManager.getInstance(getContext()).fireEvent(new TGUpdateEvent(TGUpdateEvent.CUSTOM_TEMPLATE_CHANGED, getContext()));
	}

}
