package org.herac.tuxguitar.app.action.impl.file;

import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.util.TGContext;

public class TGCloseDocumentsAction extends TGActionBase {
	
	public static final String NAME = "action.file.close-documents";
	
	public TGCloseDocumentsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context){
		TGActionManager actionManager = TGActionManager.getInstance(getContext());
		
		TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.getContext());
		TGDocument currentDocument = documentManager.findCurrentDocument();
		int currentIndex = documentManager.findCurrentDocumentIndex();
		
		List<TGDocument> documentsToClose = context.getAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENTS);
		documentManager.removeDocuments(documentsToClose);
		
		if(!documentManager.containsDocument(currentDocument)) {
			TGDocument next = this.findNextDocument(documentManager, currentIndex);
			if( next != null ) {
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, next.getSong());
				context.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, next.isUnwanted());
				
				actionManager.execute(TGLoadSongAction.NAME, context);
			} else {
				context.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, true);
				
				actionManager.execute(TGLoadTemplateAction.NAME, context);
			}
		}
	}
	
	private TGDocument findNextDocument(TGDocumentListManager documentManager, int removedIndex) {
		List<TGDocument> documents = documentManager.getDocuments();
		
		int startIndex = (removedIndex >= 0 ? removedIndex : 0);
		for(int i = startIndex; i >= 0; i --) {
			if( documents.size() > i ) {
				return documents.get(i);
			}
		}
		return null;
	}
}