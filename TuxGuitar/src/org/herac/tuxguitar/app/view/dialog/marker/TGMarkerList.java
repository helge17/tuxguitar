package org.herac.tuxguitar.app.view.dialog.marker;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGGoToMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMarkerList implements TGEventListener {
	
	private TGContext context;
	private Shell dialog;
	private Table table;
	private List<TGMarker> markers;
	
	private Composite compositeTable;
	private TableColumn measureColumn;
	private TableColumn titleColumn;
	
	private Composite compositeButtons;
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonDelete;
	private Button buttonGo;
	private Button buttonClose;
	
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateProcess;
	
	private int markerCount;
	
	private TGMarkerList(TGContext context) {
		this.context = context;
		this.markerCount = 0;
		this.createSyncProcesses();
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout(2,false));
		this.dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		// ----------------------------------------------------------------------
		this.compositeTable = new Composite(this.dialog, SWT.NONE);
		this.compositeTable.setLayout(new GridLayout());
		this.compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.table = new Table(this.compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(250,200));
		this.table.setHeaderVisible(true);
		this.table.addListener (SWT.MouseDoubleClick, new Listener() {
			public void handleEvent (Event event) {
				TGMarkerList.this.goToSelectedMarker();
			}
		});
		this.measureColumn = new TableColumn(this.table, SWT.LEFT);
		this.measureColumn.setWidth(70);
		
		this.titleColumn = new TableColumn(this.table, SWT.LEFT);
		this.titleColumn.setWidth(180);
		
		this.loadTableItems();
		
		// ------------------BUTTONS--------------------------
		this.compositeButtons = new Composite(this.dialog, SWT.NONE);
		this.compositeButtons.setLayout(new GridLayout(1,false));
		this.compositeButtons.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.buttonAdd = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonAdd.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGMarkerList.this.openMarkerEditor(null);
			}
		});
		
		this.buttonEdit = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonEdit.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGMarkerList.this.openMarkerEditor(getSelectedMarker());
			}
		});
		
		this.buttonDelete = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonDelete.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGMarkerList.this.removeMarker(getSelectedMarker());
			}
		});
		
		this.buttonGo = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonGo.setLayoutData(makeGridData(SWT.FILL, SWT.BOTTOM,true));
		this.buttonGo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGMarkerList.this.goToSelectedMarker();
			}
		});
		
		this.buttonClose = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonClose.setLayoutData(makeGridData(SWT.FILL, SWT.BOTTOM,false));
		this.buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGMarkerList.this.dialog.dispose();
			}
		});
		
		this.loadIcons();
		this.loadProperties(false);
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
			}
		});
		this.dialog.setDefaultButton( this.buttonGo );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
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
	
	private GridData makeGridData(int horizontalAlignment,int verticalAlignment,boolean grabExcessVerticalSpace){
		GridData data = new GridData();
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = verticalAlignment;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = grabExcessVerticalSpace;
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		
		return data;
	}
	
	protected void loadTableItems(){
		int itemSelected = this.table.getSelectionIndex();
		
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
		
		this.table.removeAll();
		this.markers = TuxGuitar.getInstance().getSongManager().getMarkers(song);
		
		Iterator<TGMarker> it = this.markers.iterator();
		while (it.hasNext()) {
			TGMarker marker = (TGMarker) it.next();
			
			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setText(new String[] { Integer.toString(marker.getMeasure()),marker.getTitle() });
		}
		
		int markerCount = this.markers.size();
		if( this.markerCount >= markerCount ) {
			if( itemSelected >= 0 && itemSelected < markerCount ){
				this.table.select(itemSelected);
			}
		}
		this.markerCount = markerCount;
	}
	
	protected TGMarker getSelectedMarker(){
		int itemSelected = this.table.getSelectionIndex();
		if(itemSelected >= 0 && itemSelected < this.markers.size()){
			return (TGMarker)this.markers.get(itemSelected);
		}
		return null;
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
			this.measureColumn.setText(TuxGuitar.getProperty("measure"));
			this.titleColumn.setText(TuxGuitar.getProperty("title"));
			this.buttonAdd.setText(TuxGuitar.getProperty("add"));
			this.buttonEdit.setText(TuxGuitar.getProperty("edit"));
			this.buttonDelete.setText(TuxGuitar.getProperty("remove"));
			this.buttonGo.setText(TuxGuitar.getProperty("go"));
			this.buttonClose.setText(TuxGuitar.getProperty("close"));
			
			if(layout){
				this.table.layout();
				this.compositeTable.layout();
				this.compositeButtons.layout();
				this.dialog.pack(true);
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
		else if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
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
