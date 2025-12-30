package app.tuxguitar.app.view.dialog.marker;

import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.marker.TGGoToMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import app.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGProcess;
import app.tuxguitar.editor.util.TGSyncProcess;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UITable;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMarkerList implements TGEventListener {

	private TGContext context;
	private UIWindow dialog;
	private UITable<TGMarker> table;
	private List<TGMarker> markers;

	private UIPanel compositeTable;
	private UIPanel compositeButtons;
	private UIButton buttonAdd;
	private UIButton buttonEdit;
	private UIButton buttonDelete;
	private UIButton buttonGo;
	private UIButton buttonClose;

	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateProcess;

	private TGMarkerList(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
	}

	public void show(TGViewContext context) {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(uiParent, false, true);
		this.dialog.setLayout(dialogLayout);
		// ----------------------------------------------------------------------

		UITableLayout tableLayout = new UITableLayout();
		this.compositeTable = uiFactory.createPanel(this.dialog, false);
		this.compositeTable.setLayout(tableLayout);
		dialogLayout.set(this.compositeTable, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.table = uiFactory.createTable(this.compositeTable, true);
		this.table.setColumns(2);
		this.table.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				TGMarkerList.this.goToSelectedMarker();
			}
		});
		tableLayout.set(this.table, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		tableLayout.set(this.table, UITableLayout.PACKED_WIDTH, 250f);
		tableLayout.set(this.table, UITableLayout.PACKED_HEIGHT, 200f);

		this.loadTableItems();

		// ------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		this.compositeButtons = uiFactory.createPanel(this.dialog, false);
		this.compositeButtons.setLayout(buttonsLayout);
		dialogLayout.set(this.compositeButtons, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.buttonAdd = uiFactory.createButton(this.compositeButtons);
		this.buttonAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMarkerList.this.openMarkerEditor(null);
			}
		});

		this.buttonEdit = uiFactory.createButton(this.compositeButtons);
		this.buttonEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMarkerList.this.openMarkerEditor(getSelectedMarker());
			}
		});

		this.buttonDelete = uiFactory.createButton(this.compositeButtons);
		this.buttonDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMarkerList.this.removeMarker(getSelectedMarker());
			}
		});

		this.buttonGo = uiFactory.createButton(this.compositeButtons);
		this.buttonGo.setDefaultButton();
		this.buttonGo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMarkerList.this.goToSelectedMarker();
			}
		});

		this.buttonClose = uiFactory.createButton(this.compositeButtons);
		this.buttonClose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMarkerList.this.dialog.dispose();
			}
		});

		buttonsLayout.set(this.buttonAdd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, 80f, 25f, null);
		buttonsLayout.set(this.buttonEdit, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, 80f, 25f, null);
		buttonsLayout.set(this.buttonDelete, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, 80f, 25f, null);
		buttonsLayout.set(this.buttonGo, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(this.buttonClose, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false, 1, 1, 80f, 25f, null);

		this.loadIcons();
		this.loadProperties(false);

		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
			}
		});

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		this.dialog.setMinimumSize((int)dialog.getPackedContentSize().getWidth(), (int)dialog.getPackedContentSize().getHeight());
	}

	public void addListeners(){
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}

	public void removeListeners(){
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}

	public void dispose(){
		if(!this.isDisposed()){
			this.dialog.dispose();
		}
	}

	public void update(){
		if(!this.isDisposed()){
			this.loadTableItems();
		}
	}

	public void fireUpdateProcess(){
		this.updateProcess.process();
	}

	protected void loadTableItems(){
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
		TGMarker selection = this.table.getSelectedValue();

		this.table.removeItems();
		this.markers = TuxGuitar.getInstance().getSongManager().getMarkers(song);

		Iterator<TGMarker> it = this.markers.iterator();
		while (it.hasNext()) {
			TGMarker marker = it.next();
			UITableItem<TGMarker> item = new UITableItem<TGMarker>(marker);
			item.setText(0, Integer.toString(marker.getMeasure()));
			item.setText(1, marker.getTitle());
			this.table.addItem(item);
		}

		if( selection != null ) {
			this.table.setSelectedValue(selection);
		}
	}

	protected TGMarker getSelectedMarker(){
		return this.table.getSelectedValue();
	}

	public void goToSelectedMarker() {
		TGMarker marker = this.getSelectedMarker();
		if( marker != null ) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGGoToMarkerAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
			tgActionProcessor.process();
		}
	}

	public void openMarkerEditor(TGMarker marker) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenMarkerEditorAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
		tgActionProcessor.process();
	}

	public void removeMarker(TGMarker marker) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGRemoveMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
		tgActionProcessor.process();
	}

	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}

	public void loadIcons() {
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		}
	}

	public void loadProperties() {
		this.loadProperties(true);
	}

	public void loadProperties(boolean layout) {
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("marker.list"));
			this.table.setColumnName(0, TuxGuitar.getProperty("measure"));
			this.table.setColumnName(1, TuxGuitar.getProperty("title"));
			this.buttonAdd.setText(TuxGuitar.getProperty("add"));
			this.buttonEdit.setText(TuxGuitar.getProperty("edit"));
			this.buttonDelete.setText(TuxGuitar.getProperty("remove"));
			this.buttonGo.setText(TuxGuitar.getProperty("go"));
			this.buttonClose.setText(TuxGuitar.getProperty("close"));

			if( layout){
				this.dialog.pack();
			}
		}
	}

	public void createSyncProcesses() {
		this.updateProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				update();
			}
		});
		this.loadIconsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
	}

	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type ==  TGUpdateEvent.SONG_LOADED ){
			this.fireUpdateProcess();
		}
	}

	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
	}

	public static TGMarkerList getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMarkerList.class.getName(), new TGSingletonFactory<TGMarkerList>() {
			public TGMarkerList createInstance(TGContext context) {
				return new TGMarkerList(context);
			}
		});
	}
}
