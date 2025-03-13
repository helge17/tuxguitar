package app.tuxguitar.app.view.dialog.fretboard;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGFretBoardEditorController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGFretBoardEditor editor = TGFretBoardEditor.getInstance(context.getContext());
		if( editor.isVisible()){
			editor.hideFretBoard();
		} else {
			editor.showFretBoard();
		}
	}
}
