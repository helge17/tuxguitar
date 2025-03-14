package app.tuxguitar.app.view.component.tabfolder;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListAttributes;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.TGControl;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UICloseEvent;
import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UITabFolder;
import app.tuxguitar.ui.widget.UITabItem;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTabFolder implements TGEventListener {

	private TGContext context;
	private TGSyncProcessLocked updateDocumentProcess;
	private TGSyncProcessLocked updateSelectionProcess;

	private UITabFolder tabFolder;
	private List<UITabItem> tabItems;
	private boolean ignoreEvents;
	private boolean currentUnsaved;

	public TGTabFolder(TGContext context) {
		this.context = context;
		this.tabItems = new ArrayList<UITabItem>();
		this.createSyncProcesses();
		this.appendListeners();
	}

	public UITabFolder getControl() {
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

	public void init(UIContainer parent) {
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();

		this.tabFolder = factory.createTabFolder(parent, true);
		this.tabFolder.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTabFolder.this.onTabItemSelected();
			}
		});
		this.tabFolder.addTabCloseListener(new UICloseListener() {
			public void onClose(UICloseEvent event) {
				TGDocument document = ((UITabItem)event.getComponent()).getData(TGDocument.class.getName());
				if( document != null ) {
					closeDocument(document);
				}
			}
		});
		this.tabFolder.addFocusGainedListener(new UIFocusGainedListener() {
			public void onFocusGained(UIFocusEvent event) {
				TGTabFolder.this.updateFocus();
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

//	public void updateCaret() {
//		if(!this.isDisposed()) {
//			TGControl tgControl = this.findSelectedControl();
//			if( tgControl != null && !tgControl.isDisposed()) {
//				tgControl.resetScroll();
//				tgControl.getTablature().resetCaret();
//			}
//		}
//	}

	public void updateDocument() {
		this.updateTabItems();
		this.updateFocus();
//		this.updateCaret();
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
					this.tabFolder.setSelectedIndex(index);
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

				UITabItem uiTabItem = this.findTabItem(i);
				uiTabItem.setText(this.createTabItemLabel(document));
				uiTabItem.setData(TGDocument.class.getName(), document);
				if( currentDocument != null && currentDocument.equals(document) ) {
					this.tabFolder.setSelectedIndex(i);
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
				UITabItem uiTabItem = this.tabItems.get(index);
				uiTabItem.dispose();
				this.tabItems.remove(index);
			}
		}
	}

	public UITabItem findTabItem(int index) {
		if(!this.isDisposed()) {
			if( index >= 0 ) {
				while( this.tabItems.size() <= index ) {
					UITabItem uiTabItem = this.tabFolder.createTab();
					uiTabItem.setData(TGControl.class.getName(), new TGControl(this.context, uiTabItem));

					this.tabItems.add(uiTabItem);
				}
				return this.tabItems.get(index);
			}
		}
		return null;
	}

	public TGControl findControl(int index) {
		if(!this.isDisposed()) {
			UITabItem tabItem = this.findTabItem(index);
			if( tabItem != null ) {
				return tabItem.getData(TGControl.class.getName());
			}
		}
		return null;
	}

	public TGControl findSelectedControl() {
		if(!this.isDisposed()) {
			int index = this.tabFolder.getSelectedIndex();
			if( index >= 0 ) {
				return this.findControl(index);
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
			int index = this.tabFolder.getSelectedIndex();
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
