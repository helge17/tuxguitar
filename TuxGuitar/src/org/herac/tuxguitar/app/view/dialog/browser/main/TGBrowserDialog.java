package org.herac.tuxguitar.app.view.dialog.browser.main;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserConnection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserConnectionHandler;
import org.herac.tuxguitar.app.tools.browser.TGBrowserFactoryHandler;
import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorHandler;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBrowserDialog implements TGBrowserFactoryHandler, TGBrowserConnectionHandler,TGEventListener{
	
	private static final int SHELL_WIDTH = 500;
	private static final int SHELL_HEIGHT = 350;
	
	public static final int CALL_OPEN = 1;
	public static final int CALL_CLOSE = 2;
	public static final int CALL_CD_ROOT = 3;
	public static final int CALL_CD_UP = 4;
	public static final int CALL_LIST = 5;
	public static final int CALL_ELEMENT = 6;
	
	private TGContext context;
	private TGBrowserCollection collection;
	private TGBrowserConnection connection;
	private Shell dialog;
	private Table table;
	private TableColumn column;
	private List<TGBrowserElement> elements;
	private TGBrowserMenuBar menu;
	private TGBrowserToolBar toolBar;
	private TGCursorController cursorController;
	
	public TGBrowserDialog(TGContext context){
		this.context = context;
		this.connection = new TGBrowserConnection(this);
		this.menu = new TGBrowserMenuBar(this);
		this.toolBar = new TGBrowserToolBar(this);
	}
	
	public TGContext getContext() {
		return context;
	}

	public TGBrowserConnection getConnection(){
		return this.connection;
	}
	
	public TGBrowserCollection getCollection() {
		return this.collection;
	}
	
	public void setCollection(TGBrowserCollection collection) {
		this.collection = collection;
	}
	
	public Shell getShell(){
		return this.dialog;
	}
	
	public void exit(){
		this.getConnection().release();
		this.getConnection().close(CALL_CLOSE);
		TGBrowserManager.getInstance(this.context).writeCollections();
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
	}
	
	public void show(){
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(),SWT.DIALOG_TRIM | SWT.RESIZE);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		
		this.menu.init(getShell());
		this.toolBar.init(getShell());
		this.initTable(this.dialog);
		this.updateCollections(null);
		this.updateTable();
		this.dialog.setSize(SHELL_WIDTH,SHELL_HEIGHT);
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				exit();
			}
		});
		
		this.loadProperties();
		this.updateBars();
		
		TGBrowserManager.getInstance(this.context).setFactoryHandler(this);
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER);
	}
	
	private void initTable(Composite parent){
		this.table = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.table.setLinesVisible(TuxGuitar.getInstance().getConfig().getBooleanValue(TGConfigKeys.BROWSER_LINES_VISIBLE));
		this.table.setHeaderVisible(false);
		
		this.column = new TableColumn(this.table, SWT.LEFT);
		
		this.table.addListener (SWT.MouseDoubleClick, new Listener() {
			public void handleEvent (Event event) {
				openElement();
			}
		});
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
	
	private void updateTable(){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGBrowserDialog.this.table.removeAll();
						if(TGBrowserDialog.this.elements != null){
							Iterator<TGBrowserElement> it = TGBrowserDialog.this.elements.iterator();
							while(it.hasNext()){
								TGBrowserElement element = (TGBrowserElement)it.next();
								TableItem item = new TableItem(TGBrowserDialog.this.table, SWT.NONE);
								item.setImage(element.isFolder()?TuxGuitar.getInstance().getIconManager().getBrowserFolder():TuxGuitar.getInstance().getIconManager().getBrowserFile());
								item.setText(element.getName());
							}
						}
						updateColumn();
					}
				}
			});
		}
	}
	
	protected void updateColumn(){
		if(!isDisposed()){
			this.column.pack();
		}
	}
	
	public void updateBars(){
		if(!isDisposed()){
			this.menu.updateItems();
			this.toolBar.updateItems();
		}
	}
	
	public void updateCollections(final TGBrowserCollection selection){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGBrowserDialog.this.menu.updateCollections(selection);
						TGBrowserDialog.this.toolBar.updateCollections(selection);
					}
				}
			});
		}
	}
	
	public TGBrowserElement getSelection(int index){
		if(!isDisposed() && getConnection().isOpen()){
			if(this.elements != null && index >= 0 && index < this.elements.size()){
				return (TGBrowserElement)this.elements.get(index);
			}
		}
		return null;
	}
	
	protected void removeElements(){
		this.elements = null;
	}
	
	protected void addElements(List<TGBrowserElement> elements){
		this.elements = elements;
	}
	
	protected void openCollection(){
		if(!isDisposed() && getCollection() != null){
			TGBrowserFactory factory = TGBrowserManager.getInstance(this.context).getFactory(getCollection().getType());
			getConnection().open(CALL_OPEN,factory.newTGBrowser(getCollection().getData()));
		}
	}
	
	protected void closeCollection(){
		if(!isDisposed() && getCollection() != null){
			this.getConnection().close(CALL_CLOSE);
		}
	}
	
	protected void removeCollection(TGBrowserCollection collection){
		if(collection != null){
			TGBrowserManager.getInstance(this.context).removeCollection(collection);
			if( getCollection() != null && getCollection().equals( collection ) ){
				this.getConnection().close(CALL_CLOSE);
			}else{
				this.updateCollections( getCollection() );
			}
		}
	}
	
	public void openElement(){
		TGBrowserElement element = getSelection(this.table.getSelectionIndex());
		if(element != null){
			this.getConnection().openStream(CALL_ELEMENT,element);
		}
	}
	
	public void notifyLockStatusChanged(){
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if(!isDisposed()){
					updateBars();
					loadCursor(getConnection().isLocked() ? SWT.CURSOR_WAIT : SWT.CURSOR_ARROW);
				}
			}
		});
	}
	
	public void notifyOpened(int callId) {
		if(!isDisposed()){
			this.removeElements();
			this.updateTable();
			this.updateCollections(getCollection());
			this.getConnection().release();
			this.getConnection().listElements(CALL_LIST);
		}
	}
	
	public void notifyClosed(int callId) {
		if(callId != CALL_OPEN){
			this.setCollection(null);
		}
		this.removeElements();
		this.updateCollections(getCollection());
		this.updateTable();
		if(callId != CALL_OPEN){
			this.getConnection().release();
		}
	}
	
	public void notifyError(int callId,Throwable throwable){
		if(!isDisposed()){
			this.updateTable();
			this.getConnection().release();
//			MessageDialog.errorMessage(getShell(),throwable);
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public void notifyCd(int callId) {
		if(!isDisposed()){
			this.getConnection().release();
			this.getConnection().listElements(CALL_LIST);
		}
	}
	
	public void notifyElements(int callId, List<TGBrowserElement> elements) {
		if(!isDisposed()){
			this.addElements(elements);
			this.updateTable();
			this.getConnection().release();
		}
	}
	
	public void notifyStream(int callId,final InputStream stream,final TGBrowserElement element) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGReadSongAction.NAME);
		tgActionProcessor.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);
		tgActionProcessor.setOnFinish(new Runnable() {
			public void run() {
				getConnection().release();
			}
		});
		tgActionProcessor.setAttribute(TGErrorHandler.class.getName(), new TGErrorHandler() {
			public void handleError(Throwable throwable) {
				getConnection().release();
				
				TGMessageDialogUtil.errorMessage(getContext(), getShell(), TuxGuitar.getProperty("file.open.error", new String[]{element.getName()}));
			}
		});
		tgActionProcessor.process();
	}
	
	public void loadIcons() {
		if(!isDisposed()){
			this.getShell().setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.reload();
		}
	}
	
	public void loadProperties() {
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("browser.dialog"));
			this.menu.loadProperties();
			this.toolBar.loadProperties();
		}
	}
	
	public void loadCursor(int cursorStyle) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context, this.dialog);
			}
			this.cursorController.loadCursor(cursorStyle);
		}
	}
	
	public void notifyAdded() {
		reload();
	}
	
	public void notifyRemoved() {
		if(getCollection() != null){
			closeCollection();
		}
		reload();
	}
	
	protected void reload(){
		if(!isDisposed()){
			this.menu.reload(getShell());
			this.toolBar.reload();
			this.updateTable();
			this.updateCollections(getCollection());
			this.getShell().layout();
		}
	}

	public void processEvent(TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
	}
	
	public static TGBrowserDialog getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserDialog.class.getName(), new TGSingletonFactory<TGBrowserDialog>() {
			public TGBrowserDialog createInstance(TGContext context) {
				return new TGBrowserDialog(context);
			}
		});
	}
}
