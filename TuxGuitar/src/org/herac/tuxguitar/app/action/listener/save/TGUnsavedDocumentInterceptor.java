package org.herac.tuxguitar.app.action.listener.save;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

public class TGUnsavedDocumentInterceptor implements TGActionInterceptor, TGEventListener {
	
	private static final String UNSAVED_INTERCEPTOR_DOCUMENTS = "unsavedInterceptor_documents";
	private static final String UNSAVED_INTERCEPTOR_ACTION_ID = "unsavedInterceptor_actionId";
	private static final String UNSAVED_INTERCEPTOR_BY_PASS = "unsavedInterceptor_byPass";
	
	private TGContext context;
	private List<String> actionIds;
	
	public TGUnsavedDocumentInterceptor(TGContext context) {
		this.context = context;
		this.actionIds = new ArrayList<String>();
	}
	
	public boolean containsActionId(String id) {
		return this.actionIds.contains(id);
	}
	
	public void addActionId(String id) {
		this.actionIds.add(id);
	}
	
	public void removeActionId(String id) {
		this.actionIds.remove(id);
	}
	
	public boolean intercept(final String id, final TGActionContext context) throws TGActionException {
		if(!this.isByPassInContext(context) && this.containsActionId(id)) {
			List<TGDocument> unsavedDocuments = this.findUnsavedDocuments(context);
			if(!unsavedDocuments.isEmpty()) {
				TGDocument document = unsavedDocuments.remove(0);
				
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, document.getSong());
				context.setAttribute(UNSAVED_INTERCEPTOR_ACTION_ID, id);
				
				TGActionProcessor tgLoadSongAction = new TGActionProcessor(this.context, TGLoadSongAction.NAME);
				tgLoadSongAction.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, document.getSong());
				tgLoadSongAction.setOnFinish(new Runnable() {
					public void run() {
						openSaveDialog(context);
					}
				});
				tgLoadSongAction.process();
				
				return true;
			}
		}
		return false;
	}
	
	public void openSaveDialog(TGActionContext context) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("file.save-changes-question"));
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO | TGConfirmDialog.BUTTON_CANCEL);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_YES);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, this.createThreadRunnable(createSaveActionRunnable(context)));
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_NO, this.createThreadRunnable(createInterceptedActionRunnable(context)));
		tgActionProcessor.process();
	}
	
	public Runnable createThreadRunnable(final Runnable target) {
		return new Runnable() {
			public void run() {
				new Thread(target).start();
			}
		};
	}
	
	public Runnable createSaveActionRunnable(final TGActionContext tgActionContext) {
		return new Runnable() {
			public void run() {
				executeSaveAction(tgActionContext);
			}
		};
	}
	
	public Runnable createInterceptedActionRunnable(final TGActionContext tgActionContext) {
		return new Runnable() {
			public void run() {
				executeInterceptedAction(tgActionContext);
			}
		};
	}
	
	private boolean isByPassInContext(TGActionContext context) {
		return Boolean.TRUE.equals(context.getAttribute(UNSAVED_INTERCEPTOR_BY_PASS));
	}
	
	private boolean containsUnsavedDocument(TGActionContext context) {
		return (!this.findUnsavedDocuments(context).isEmpty());
	}
	
	private List<TGDocument> findUnsavedDocuments(TGActionContext context) {
		List<TGDocument> unsavedDocuments = context.getAttribute(UNSAVED_INTERCEPTOR_DOCUMENTS);
		if( unsavedDocuments == null ) {
			unsavedDocuments = new ArrayList<TGDocument>();
			
			List<TGDocument> documents = this.findDocuments(context);
			for(TGDocument document : documents) {
				if( document.isUnsaved() ) {
					unsavedDocuments.add(document);
				}
			}
			context.setAttribute(UNSAVED_INTERCEPTOR_DOCUMENTS, unsavedDocuments);
		}
		return unsavedDocuments;
	}
	
	private List<TGDocument> findDocuments(TGActionContext context) {
		List<TGDocument> documents = context.getAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENTS);
		if( documents == null ) {
			documents = TGDocumentListManager.getInstance(this.context).getDocuments();
		}
		return documents;
	}
	
	public void executeInterceptedAction(TGActionContext context) {
		if(!this.containsUnsavedDocument(context)) {
			context.setAttribute(UNSAVED_INTERCEPTOR_BY_PASS, true);
		}
		String actionId = context.getAttribute(UNSAVED_INTERCEPTOR_ACTION_ID);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.execute(actionId, context);
	}
	
	public void executeSaveAction(TGActionContext tgActionContext) {
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.execute(TGSaveFileAction.NAME, tgActionContext);
	}
	
	public void checkForInterceptedAction(TGEvent event) {
		if( TGWriteSongAction.NAME.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID)) ) {
			TGActionContext actionContext = event.getAttribute(TGActionEvent.ATTRIBUTE_SOURCE_CONTEXT);
			if( actionContext.getAttribute(UNSAVED_INTERCEPTOR_ACTION_ID) != null ) {
				this.createThreadRunnable(createInterceptedActionRunnable(actionContext)).run();
			}
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForInterceptedAction(event);
		}
	}
}
