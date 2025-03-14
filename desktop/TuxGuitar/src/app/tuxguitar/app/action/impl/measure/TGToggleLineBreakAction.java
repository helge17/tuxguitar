package app.tuxguitar.app.action.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGMeasureHeaderImpl;
import app.tuxguitar.util.TGContext;

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
