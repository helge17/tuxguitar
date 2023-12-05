package org.herac.tuxguitar.app.view.dialog.piano;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPianoEditorController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGPianoEditor editor = TGPianoEditor.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show();
		} else {
			editor.dispose();
		}
	}
}
