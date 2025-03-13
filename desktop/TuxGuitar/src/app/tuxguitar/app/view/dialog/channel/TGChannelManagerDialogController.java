package org.herac.tuxguitar.app.view.dialog.channel;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGChannelManagerDialogController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGChannelManagerDialog editor = TGChannelManagerDialog.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show(context);
		} else {
			editor.dispose();
		}
	}
}
