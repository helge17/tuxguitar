package org.herac.tuxguitar.app.view.component.tabfolder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
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
	
	private TGContext context;
	private TGSyncProcessLocked updateDocumentProcess;
	private TGSyncProcessLocked updateSelectionProcess;
	
	private TabFolder tabFolder;
	private List<TabItem> tabItems;
	private boolean ignoreEvents;
	private boolean currentUnsaved;
	
	public TGTabFolder(TGContext context) {
		this.context = context;
		this.tabItems = new ArrayList<TabItem>();
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
	}
	
	public void init(Composite parent) {
		this.tabFolder = new TabFolder(parent, SWT.NONE);
		this.tabFolder.setLayout(new GridLayout());
		this.tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGTabFolder.this.onTabItemSelected();
			}
		});
		
		this.updateDocument();
		
		TablatureEditor tablatureEditor = TablatureEditor.getInstance(this.context);
		tablatureEditor.showTablature(this.tabFolder);
		tablatureEditor.getTablature().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
	}
	
	public void updateFocus() {
		if(!this.isDisposed()) {
			TablatureEditor tablatureEditor = TablatureEditor.getInstance(this.context);
			if(!tablatureEditor.isDisposed()) {
				tablatureEditor.getTablature().setFocus();
			}
		}
	}
	
	public void updateCaret() {
		if(!this.isDisposed()) {
			TablatureEditor tablatureEditor = TablatureEditor.getInstance(this.context);
			if(!tablatureEditor.isDisposed()) {
				tablatureEditor.getTablature().resetScroll();
				tablatureEditor.getTablature().resetCaret();
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
				
				TabItem tabItem = this.findTabItem(i);
				tabItem.setText(this.createTabItemLabel(document));
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
				TabItem tabItem = this.tabItems.get(index);
				tabItem.dispose();
				this.tabItems.remove(index);
			}
		}
	}
	
	public TabItem findTabItem(int index) {
		if(!this.isDisposed()) {
			if( index >= 0 ) {
				while( this.tabItems.size() <= index ) {
					this.tabItems.add(new TabItem(this.tabFolder, SWT.NONE));
				}
				return this.tabItems.get(index);
			}
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
