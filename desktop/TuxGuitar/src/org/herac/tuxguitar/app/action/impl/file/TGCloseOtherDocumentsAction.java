package org.herac.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGCloseOtherDocumentsAction extends TGActionBase {
	
	public static final String NAME = "action.file.close-others";
	
	public TGCloseOtherDocumentsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGDocument current = TGDocumentListManager.getInstance(this.getContext()).findCurrentDocument();
		List<TGDocument> documents = new ArrayList<TGDocument>(TGDocumentListManager.getInstance(getContext()).getDocuments());
		documents.remove(current);
		context.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENTS, documents);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGCloseDocumentsAction.NAME, context);
	}
}