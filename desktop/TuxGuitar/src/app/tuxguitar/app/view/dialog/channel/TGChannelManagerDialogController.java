package app.tuxguitar.app.view.dialog.channel;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

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
