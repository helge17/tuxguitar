package org.herac.tuxguitar.app.view.dialog.fretboard;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

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
