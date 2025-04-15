package app.tuxguitar.app.view.dialog.errors;

import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGMeasureErrorDialogController implements TGOpenViewController {
	
	@Override
	public void openView(TGViewContext viewContext) {
		TGMeasureErrorDialog editor = TGMeasureErrorDialog.getInstance(viewContext.getContext());
		if( editor.isDisposed()){
			editor.show(viewContext);
		}
	}
}
