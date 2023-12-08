package org.herac.tuxguitar.app.view.dialog.matrix;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGMatrixEditorController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGMatrixEditor editor = TGMatrixEditor.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show();
		} else {
			editor.dispose();
		}
	}
}
