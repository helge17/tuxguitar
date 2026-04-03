package app.tuxguitar.app.view.dialog.matrix;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

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
