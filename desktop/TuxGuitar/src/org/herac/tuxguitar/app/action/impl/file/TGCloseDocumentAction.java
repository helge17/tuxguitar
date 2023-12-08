package org.herac.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGCloseDocumentAction extends TGActionBase {
	
	public static final String NAME = "action.file.close-document";
	
	public TGCloseDocumentAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context){
		TGDocument document = context.getAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT);
		
		List<TGDocument> documents = new ArrayList<TGDocument>();
		documents.add(document);
		context.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENTS, documents);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGCloseDocumentsAction.NAME, context);
	}
}