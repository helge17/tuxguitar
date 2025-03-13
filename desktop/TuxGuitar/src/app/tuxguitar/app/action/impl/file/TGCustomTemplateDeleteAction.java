package org.herac.tuxguitar.app.action.impl.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGUserFileUtils;

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
