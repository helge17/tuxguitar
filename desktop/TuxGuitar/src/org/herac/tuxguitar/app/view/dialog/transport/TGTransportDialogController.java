package org.herac.tuxguitar.app.view.dialog.transport;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGTransportDialogController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGTransportDialog editor = TGTransportDialog.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show();
		}else{
			editor.dispose();
		}
	}
}
