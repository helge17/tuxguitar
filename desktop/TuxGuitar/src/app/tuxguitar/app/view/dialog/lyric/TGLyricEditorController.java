package app.tuxguitar.app.view.dialog.lyric;

import app.tuxguitar.app.view.controller.TGToggleViewController;
import app.tuxguitar.app.view.controller.TGViewContext;

public class TGLyricEditorController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGLyricEditor editor = TGLyricEditor.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show();
		}else{
			editor.dispose();
		}
	}
}
