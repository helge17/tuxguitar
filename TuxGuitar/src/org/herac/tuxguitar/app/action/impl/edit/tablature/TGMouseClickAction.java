package org.herac.tuxguitar.app.action.impl.edit.tablature;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.util.TGContext;

public class TGMouseClickAction extends TGActionBase{
	
	public static final String NAME = "action.edit.tablature.mouse-click";
	
	public TGMouseClickAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		EditorKit editorKit = TablatureEditor.getInstance(getContext()).getTablature().getEditorKit();
		if( editorKit.fillSelection(context)) {
			TGActionManager actionManager = TGActionManager.getInstance(getContext());
			
			actionManager.execute(TGMoveToAction.NAME, context);
			if( editorKit.isMouseEditionAvailable() && editorKit.fillAddOrRemoveBeat(context) ) {
				
				if( editorKit.fillRemoveNoteContext(context) ) {
					
					actionManager.execute(TGDeleteNoteAction.NAME, context);
					
				} else if (editorKit.fillCreateNoteContext(context)) {
					
					actionManager.execute(TGChangeNoteAction.NAME, context);
					actionManager.execute(TGMoveToAction.NAME, context);
				}
			}
		}
	}
}
