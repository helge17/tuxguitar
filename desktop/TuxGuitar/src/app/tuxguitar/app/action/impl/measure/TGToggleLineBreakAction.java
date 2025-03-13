package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGMeasureHeaderImpl;
import org.herac.tuxguitar.util.TGContext;

public class TGToggleLineBreakAction extends TGActionBase {

	public static final String NAME = "action.measure.toggle-linebreak";

	public TGToggleLineBreakAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext context) {
		TGMeasureHeaderImpl header = (TGMeasureHeaderImpl) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		header.toggleLineBreak();
	}

}
