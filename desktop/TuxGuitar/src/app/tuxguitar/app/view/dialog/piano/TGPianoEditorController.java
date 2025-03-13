package app.tuxguitar.app.view.dialog.piano;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

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
