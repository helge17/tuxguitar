package org.herac.tuxguitar.app.view.component.tabfolder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.component.tab.TGControl;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTabFolder implements TGEventListener {
	
	private static final int TAB_HEIGHT = 28;
	
	private TGContext context;
	private TGSyncProcessLocked updateDocumentProcess;
	private TGSyncProcessLocked updateSelectionProcess;
	
	private CTabFolder tabFolder;
	private List<CTabItem> tabItems;
	private boolean ignoreEvents;
	private boolean currentUnsaved;
	
	public TGTabFolder(TGContext context) {
		this.context = context;
		this.tabItems = new ArrayList<CTabItem>();
		this.createSyncProcesses();
		this.appendListeners();
	}
	
	public Control getControl() {
		return this.tabFolder;
	}
	
	public boolean isDisposed() {
		return (this.tabFolder == null || this.tabFolder.isDisposed());
	}
	
	public void appendListeners(){
		TGEditorManager tgEditorManager = TGEditorManager.getInstance(this.context);
		tgEditorManager.addUpdateListener(this);
		tgEditorManager.addRedrawListener(new TGTabEventListener(this.context));
	}
	
	public void init(Composite parent) {
		this.tabFolder = new CTabFolder(parent, SWT.TOP);
		this.tabFolder.setTabHeight(TAB_HEIGHT);
		this.tabFolder.setLayout(new FillLayout());
		this.tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGTabFolder.this.onTabItemSelected();
			}
		});
		
		this.tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				TGDocument document = (TGDocument) event.item.getData();
				if( document != null ) {
					closeDocument(document);
					event.doit = false;
				}
			}
		});
		
		this.updateDocument();
	}
	
	public void updateFocus() {
		if(!this.isDisposed()) {
			TGControl tgControl = this.findSelectedControl();
			if( tgControl != null && !tgControl.isDisposed()) {
				tgControl.setFocus();
			}
		}
	}
	
	public void updateCaret() {
		if(!this.isDisposed()) {
			TGControl tgControl = this.findSelectedControl();
			if( tgControl != null && !tgControl.isDisposed()) {
				tgControl.resetScroll();
				tgControl.getTablature().resetCaret();
			}
		}
	}
	
	public void updateDocument() {
		this.updateTabItems();
		this.updateFocus();
		this.updateCaret();
	}
	
	public void updateSelection() {
		if(!this.isDisposed()) {
			TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
			TGDocument currentDocument = documentManager.findCurrentDocument();
			if( currentDocument.isUnsaved() != this.currentUnsaved || documentManager.countDocuments() != this.tabItems.size() ) {
				this.updateTabItems();
			}
			else {
				this.ignoreEvents = true;
				
				int index = documentManager.findCurrentDocumentIndex();
				if( index >= 0 && index < this.tabItems.size() ) {
					this.tabFolder.setSelection(index);
				}
				
				this.ignoreEvents = false;
			}
		}
	}
	
	public void updateTabItems() {
		if(!this.isDisposed()) {
			this.ignoreEvents = true;
			this.disposeOrphanItems();
			
			TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
			TGDocument currentDocument = documentManager.findCurrentDocument();
			List<TGDocument> documents = documentManager.getDocuments();
			for(int i = 0 ; i < documents.size() ; i ++) {
				TGDocument document = documents.get(i);
				
				CTabItem cTabItem = this.findTabItem(i);
				cTabItem.setText(this.createTabItemLabel(document));
				cTabItem.setData(document);
				if( currentDocument != null && currentDocument.equals(document) ) {
					this.tabFolder.setSelection(i);
				}
			}
			
			this.currentUnsaved = currentDocument.isUnsaved();
			this.ignoreEvents = false;
		}
	}
	
	public void disposeOrphanItems() {
		if(!this.isDisposed()) {
			List<TGDocument> documents = TGDocumentListManager.getInstance(this.context).getDocuments();
			while( this.tabItems.size() > documents.size() ) {
				int index = (this.tabItems.size() - 1);
				CTabItem cTabItem = this.tabItems.get(index);
				cTabItem.dispose();
				this.tabItems.remove(index);
			}
		}
	}
	
	public CTabItem findTabItem(int index) {
		if(!this.isDisposed()) {
			if( index >= 0 ) {
				while( this.tabItems.size() <= index ) {
					CTabItem cTabItem = new CTabItem(this.tabFolder, SWT.NONE);
					cTabItem.setControl(new TGControl(this.context, this.tabFolder));
					cTabItem.setShowClose(true);
					
					this.tabItems.add(cTabItem);
				}
				return this.tabItems.get(index);
			}
		}
		return null;
	}
	
	public TGControl findControl(int index) {
		CTabItem tabItem = this.findTabItem(index);
		if( tabItem != null ) {
			return (TGControl) tabItem.getControl();
		}
		return null;
	}
	
	public TGControl findSelectedControl() {
		int index = this.tabFolder.getSelectionIndex();
		if( index >= 0 ) {
			return this.findControl(index);
		}
		return null;
	}
	
	public String createTabItemLabel(TGDocument document) {
		StringBuffer sb = new StringBuffer();
		if( document.isUnsaved() ) {
			sb.append("*");
		}
		sb.append(TGDocumentListManager.getInstance(this.context).getDocumentName(document));
		
		return sb.toString();
	}
	
	public void createSyncProcesses() {
		this.updateDocumentProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateDocument();
			}
		});
		this.updateSelectionProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateSelection();
			}
		});
	}
	
	public void onTabItemSelected() {
		if(!this.ignoreEvents) {
			if( MidiPlayer.getInstance(this.context).isRunning() ) {
				this.updateSelection();
			} else {
				this.loadSelectedDocument();
			}
			this.updateFocus();
		}
	}
	
	public void loadSelectedDocument() {
		if(!this.isDisposed()) {
			TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
			List<TGDocument> documents = documentManager.getDocuments();
			int index = this.tabFolder.getSelectionIndex();
			if( index >= 0 && index < documents.size() ) {
				TGDocument document = documents.get(index);
				TGDocument currentDocument = documentManager.findCurrentDocument();
				if( currentDocument == null || !currentDocument.equals(document) ) {
					this.loadDocument(document);
				}
			}
		}
	}
	
	public void loadDocument(TGDocument document) {
		if(!this.isDisposed()) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGLoadSongAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, document.getSong());
			tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, document.isUnwanted());
			tgActionProcessor.process();
		}
	}
	
	public void closeDocument(TGDocument document) {
		if(!this.isDisposed()) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGCloseDocumentAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT, document);
			tgActionProcessor.process();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
			if( type == TGUpdateEvent.SELECTION ){
				this.updateSelectionProcess.process();
			} 
			else if( type == TGUpdateEvent.SONG_LOADED || type == TGUpdateEvent.SONG_SAVED ){
				this.updateDocumentProcess.process();
			} 
		}
	}
	
	public static TGTabFolder getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTabFolder.class.getName(), new TGSingletonFactory<TGTabFolder>() {
			public TGTabFolder createInstance(TGContext context) {
				return new TGTabFolder(context);
			}
		});
	}
}
