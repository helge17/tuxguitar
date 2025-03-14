package app.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

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