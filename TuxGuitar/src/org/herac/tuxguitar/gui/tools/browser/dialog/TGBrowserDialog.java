package org.herac.tuxguitar.gui.tools.browser.dialog;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.actions.file.FileActionUtils;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserConnection;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserConnectionHandler;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserFactoryHandler;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.gui.util.ConfirmDialog;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.song.models.TGSong;

public class TGBrowserDialog implements TGBrowserFactoryHandler,TGBrowserConnectionHandler,IconLoader,LanguageLoader{
	
	private static final int SHELL_WIDTH = 500;
	private static final int SHELL_HEIGHT = 350;
	
	public static final int CALL_OPEN = 1;
	public static final int CALL_CLOSE = 2;
	public static final int CALL_CD_ROOT = 3;
	public static final int CALL_CD_UP = 4;
	public static final int CALL_LIST = 5;
	public static final int CALL_ELEMENT = 6;
	
	private TGBrowserCollection collection;
	private TGBrowserConnection connection;
	private Shell dialog;
	protected Table table;
	protected TableColumn column;
	protected List elements;
	protected TGBrowserMenuBar menu;
	protected TGBrowserToolBar toolBar;
	
	public TGBrowserDialog(){
		this.connection = new TGBrowserConnection(this);
		this.menu = new TGBrowserMenuBar(this);
		this.toolBar = new TGBrowserToolBar(this);
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
		TGBrowserManager.instance().writeCollections();
		TuxGuitar.instance().getIconManager().removeLoader(this);
	}
	
	public void show(){
		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(),SWT.DIALOG_TRIM | SWT.RESIZE);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		
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
		
		TGBrowserManager.instance().setFactoryHandler(this);
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER);
	}
	
	private void initTable(Composite parent){
		this.table = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.table.setLinesVisible(TuxGuitar.instance().getConfig().getBooleanConfigValue(TGConfigKeys.BROWSER_LINES_VISIBLE));
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
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGBrowserDialog.this.table.removeAll();
						if(TGBrowserDialog.this.elements != null){
							Iterator it = TGBrowserDialog.this.elements.iterator();
							while(it.hasNext()){
								TGBrowserElement element = (TGBrowserElement)it.next();
								TableItem item = new TableItem(TGBrowserDialog.this.table, SWT.NONE);
								item.setImage(element.isFolder()?TuxGuitar.instance().getIconManager().getBrowserFolder():TuxGuitar.instance().getIconManager().getBrowserFile());
								item.setText(element.getName());
							}
						}
						updateColumn();
					}
				}
			}).start();
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
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGBrowserDialog.this.menu.updateCollections(selection);
						TGBrowserDialog.this.toolBar.updateCollections(selection);
					}
				}
			}).start();
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
	
	protected void addElements(List elements){
		this.elements = elements;
	}
	
	protected void openCollection(){
		if(!isDisposed() && getCollection() != null){
			TGBrowserFactory factory = TGBrowserManager.instance().getFactory(getCollection().getType());
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
			TGBrowserManager.instance().removeCollection(collection);
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
		new SyncThread(new Runnable() {
			public void run() {
				if(!isDisposed()){
					updateBars();
					TuxGuitar.instance().loadCursor(getShell(),( getConnection().isLocked() ? SWT.CURSOR_WAIT : SWT.CURSOR_ARROW ) );
				}
			}
		}).start();
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
			MessageDialog.errorMessage(getShell(),throwable);
		}
	}
	
	public void notifyCd(int callId) {
		if(!isDisposed()){
			this.getConnection().release();
			this.getConnection().listElements(CALL_LIST);
		}
	}
	
	public void notifyElements(int callId,List elements) {
		if(!isDisposed()){
			this.addElements(elements);
			this.updateTable();
			this.getConnection().release();
		}
	}
	
	public void notifyStream(int callId,final InputStream stream,final TGBrowserElement element) {
		if(!isDisposed()){
			ActionLock.lock();
			new SyncThread(new Runnable() {
				public void run() {
					if(!TuxGuitar.isDisposed()){
						TuxGuitar.instance().getPlayer().reset();
						if(TuxGuitar.instance().getFileHistory().isUnsavedFile()){
							ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
							confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
							int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
							if(status == ConfirmDialog.STATUS_CANCEL){
								getConnection().release();
								ActionLock.unlock();
								return;
							}
							if(status == ConfirmDialog.STATUS_YES){
								final String fileName = FileActionUtils.getFileName();
								if(fileName == null){
									getConnection().release();
									ActionLock.unlock();
									return;
								}
								new Thread(new Runnable() {
									public void run() {
										if(!TuxGuitar.isDisposed()){
											FileActionUtils.save(fileName);
											new SyncThread(new Runnable() {
												public void run() {
													if(!TuxGuitar.isDisposed()){
														openStream(stream,element);
													}
												}
											}).start();
										}
									}
								}).start();
								return;
							}
						}
						openStream(stream,element);
					}
				}
			}).start();
		}
	}
	
	protected void openStream(final InputStream stream,final TGBrowserElement element){
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					try {
						TGSong song = TGFileFormatManager.instance().getLoader().load(TuxGuitar.instance().getSongManager().getFactory(),stream);
						TuxGuitar.instance().fireNewSong(song,null);
					}catch (Throwable throwable) {
						TuxGuitar.instance().newSong();
						MessageDialog.errorMessage(getShell(),new TGFileFormatException(TuxGuitar.getProperty("file.open.error", new String[]{element.getName()}),throwable));
					}
					getConnection().release();
					ActionLock.unlock();
				}
			}
		}).start();
	}
	
	public void loadIcons() {
		if(!isDisposed()){
			this.getShell().setImage(TuxGuitar.instance().getIconManager().getAppIcon());
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
}
