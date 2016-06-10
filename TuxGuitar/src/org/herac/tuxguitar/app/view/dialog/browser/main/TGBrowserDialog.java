package org.herac.tuxguitar.app.view.dialog.browser.main;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserConnection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserConnectionHandler;
import org.herac.tuxguitar.app.tools.browser.TGBrowserFactoryHandler;
import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
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
	private UIWindow dialog;
	private UITable<TGBrowserElement> table;
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
	
	public UIWindow getWindow(){
		return this.dialog;
	}
	
	public void exit(){
		this.getConnection().release();
		this.getConnection().close(CALL_CLOSE);
		TGBrowserManager.getInstance(this.context).writeCollections();
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
	}
	
	public void show() {
		this.dialog = getUIFactory().createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		
		this.menu.createMenuBar(getWindow());
		this.toolBar.createToolBar(getWindow());
		this.initTable(this.dialog);
		this.createLayout();
		
		this.updateCollections(null);
		this.updateTable();
		
		this.dialog.setBounds(new UIRectangle(0, 0, SHELL_WIDTH,SHELL_HEIGHT));
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				exit();
			}
		});
		
		this.loadProperties();
		this.updateBars();
		
		TGBrowserManager.getInstance(this.context).setFactoryHandler(this);
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
	}
	
	public void createLayout() {
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(this.toolBar.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		uiLayout.set(this.table, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		uiLayout.set(this.table, UITableLayout.PACKED_WIDTH, 10f);
		uiLayout.set(this.table, UITableLayout.PACKED_HEIGHT, 10f);
		
		this.dialog.setLayout(uiLayout);
	}
	
	private void initTable(UIContainer parent){
		this.table = getUIFactory().createTable(parent, true);
		this.table.setColumns(1);
		this.table.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
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
						TGBrowserDialog.this.table.removeItems();
						if( TGBrowserDialog.this.elements != null ){
							Iterator<TGBrowserElement> it = TGBrowserDialog.this.elements.iterator();
							while(it.hasNext()){
								TGBrowserElement element = it.next();
								TGIconManager iconManager = TGIconManager.getInstance(TGBrowserDialog.this.context);
								
								UITableItem<TGBrowserElement> item = new UITableItem<TGBrowserElement>(element);
								item.setImage(element.isFolder() ? iconManager.getBrowserFolder(): iconManager.getBrowserFile());
								item.setText(0, element.getName());
								TGBrowserDialog.this.table.addItem(item);
							}
						}
					}
				}
			});
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
	
	public void openElement() {
		if(!isDisposed() && getConnection().isOpen()) {
			TGBrowserElement element = this.table.getSelectedValue();
			if( element != null ){
				this.getConnection().openStream(CALL_ELEMENT,element);
			}
		}
	}
	
	public void notifyLockStatusChanged(){
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if(!isDisposed()){
					updateBars();
					loadCursor(getConnection().isLocked() ? UICursor.WAIT : UICursor.NORMAL);
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
				
				TGMessageDialogUtil.errorMessage(getContext(), getWindow(), TuxGuitar.getProperty("file.open.error", new String[]{element.getName()}));
			}
		});
		tgActionProcessor.process();
	}
	
	public void loadIcons() {
		if(!isDisposed()){
			this.getWindow().setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
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
	
	public void loadCursor(UICursor cursor) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context, this.dialog);
			}
			this.cursorController.loadCursor(cursor);
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
			this.menu.reload(getWindow());
			this.toolBar.reload();
			this.updateTable();
			this.updateCollections(getCollection());
			this.createLayout();
			this.getWindow().layout();
		}
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
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
