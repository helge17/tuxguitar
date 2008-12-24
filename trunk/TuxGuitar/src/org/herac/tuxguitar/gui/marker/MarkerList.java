package org.herac.tuxguitar.gui.marker;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.TGUpdateListener;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeMarker;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGMarker;

public class MarkerList implements TGUpdateListener, IconLoader,LanguageLoader{
	
	private static MarkerList instance;
	
	protected Shell dialog;
	private Table table;
	private List markers;
	
	private Composite compositeTable;
	private TableColumn measureColumn;
	private TableColumn titleColumn;
	
	private Composite compositeButtons;
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonDelete;
	private Button buttonGo;
	private Button buttonClose;
	
	public static MarkerList instance(){
		if(instance == null){
			instance = new MarkerList();
		}
		return instance;
	}
	
	private MarkerList() {
		super();
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM);
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
				new MarkerNavigator().goToSelectedMarker(getSelectedMarker());
				TuxGuitar.instance().updateCache(true);
			}
		});
		this.measureColumn = new TableColumn(this.table, SWT.LEFT);
		this.measureColumn.setWidth(70);
		
		this.titleColumn = new TableColumn(this.table, SWT.LEFT);
		this.titleColumn.setWidth(180);
		
		this.loadTableItems(false);
		
		// ------------------BUTTONS--------------------------
		this.compositeButtons = new Composite(this.dialog, SWT.NONE);
		this.compositeButtons.setLayout(new GridLayout(1,false));
		this.compositeButtons.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.buttonAdd = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonAdd.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!ActionLock.isLocked() && !TuxGuitar.instance().isLocked()){
					ActionLock.lock();
					Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
					TGMarker marker = TuxGuitar.instance().getSongManager().getFactory().newMarker();
					marker.setMeasure(caret.getMeasure().getNumber());
					if(new MarkerEditor(marker,MarkerEditor.STATUS_NEW).open(MarkerList.this.dialog)){
						TuxGuitar.instance().updateCache(true);
						loadTableItems(true);
					}
					ActionLock.unlock();
				}
			}
		});
		
		this.buttonEdit = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonEdit.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!ActionLock.isLocked() && !TuxGuitar.instance().isLocked()){
					ActionLock.lock();
					TGMarker marker = getSelectedMarker();
					if(marker != null){
						if(new MarkerEditor(marker,MarkerEditor.STATUS_EDIT).open(MarkerList.this.dialog)){
							TuxGuitar.instance().updateCache(true);
							loadTableItems(true);
						}
					}
					ActionLock.unlock();
				}
			}
		});
		
		this.buttonDelete = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonDelete.setLayoutData(makeGridData(SWT.FILL, SWT.TOP,false));
		this.buttonDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!ActionLock.isLocked() && !TuxGuitar.instance().isLocked()){
					ActionLock.lock();
					TGMarker marker = getSelectedMarker();
					// comienza el undoable
					UndoableChangeMarker undoable = UndoableChangeMarker.startUndo(marker);
					
					TuxGuitar.instance().getSongManager().removeMarker(marker);
					
					// termia el undoable
					TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(null));
					TuxGuitar.instance().getFileHistory().setUnsavedFile();
					TuxGuitar.instance().updateCache(true);
					loadTableItems(true);
					ActionLock.unlock();
				}
			}
		});
		
		this.buttonGo = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonGo.setLayoutData(makeGridData(SWT.FILL, SWT.BOTTOM,true));
		this.buttonGo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!ActionLock.isLocked() && !TuxGuitar.instance().isLocked()){
					ActionLock.lock();
					new MarkerNavigator().goToSelectedMarker(getSelectedMarker());
					TuxGuitar.instance().updateCache(true);
					ActionLock.unlock();
				}
			}
		});
		
		this.buttonClose = new Button(this.compositeButtons, SWT.PUSH);
		this.buttonClose.setLayoutData(makeGridData(SWT.FILL, SWT.BOTTOM,false));
		this.buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				MarkerList.this.dialog.dispose();
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
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.instance().getIconManager().removeLoader(this);
		TuxGuitar.instance().getLanguageManager().removeLoader(this);
		TuxGuitar.instance().getEditorManager().removeUpdateListener(this);
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
	
	public void update(){
		this.update(false);
	}
	
	public void update(final boolean keepSelection){
		if(!isDisposed()){
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed()){
						loadTableItems(keepSelection);
					}
				}
			}).start();
		}
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
	
	protected void loadTableItems(boolean keepSelection){
		int itemSelected = (keepSelection ? this.table.getSelectionIndex() : -1 );
		
		this.table.removeAll();
		this.markers = TuxGuitar.instance().getSongManager().getMarkers();
		
		Iterator it = this.markers.iterator();
		while (it.hasNext()) {
			TGMarker marker = (TGMarker) it.next();
			
			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setText(new String[] { Integer.toString(marker.getMeasure()),marker.getTitle() });
		}
		
		if(itemSelected >= 0 && itemSelected < this.markers.size()){
			this.table.select(itemSelected);
		}
	}
	
	protected TGMarker getSelectedMarker(){
		int itemSelected = this.table.getSelectionIndex();
		if(itemSelected >= 0 && itemSelected < this.markers.size()){
			return (TGMarker)this.markers.get(itemSelected);
		}
		return null;
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void loadIcons() {
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
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
	
	public void doUpdate(int type) {
		if( type ==  TGUpdateListener.SONG_LOADED ){
			this.update();
		}
	}
}
