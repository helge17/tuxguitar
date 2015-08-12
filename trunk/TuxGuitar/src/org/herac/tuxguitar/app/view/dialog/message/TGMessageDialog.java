package org.herac.tuxguitar.app.view.dialog.message;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGMessageDialog {

	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_TITLE = "title";
	public static final String ATTRIBUTE_MESSAGE = "message";

	public void show(final TGViewContext context) {
		String title = context.getAttribute(ATTRIBUTE_TITLE);
		String message = context.getAttribute(ATTRIBUTE_MESSAGE);
		Integer style = context.getAttribute(ATTRIBUTE_STYLE);
		
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		
		MessageBox messageBox = new MessageBox(parent, style);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}
}
