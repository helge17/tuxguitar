package org.herac.tuxguitar.gui.tools.browser.dialog;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;

public class TGBrowserMenuBar extends TGBrowserBar{
	private Menu menu;
	private Menu newCollection;
	private Menu openCollection;
	private Menu removeCollection;
	
	private MenuItem menuFileItem;
	private MenuItem menuCollectionItem;
	private MenuItem menuGoItem;
	private MenuItem open;
	private MenuItem exit;
	private MenuItem newItem;
	private MenuItem openItem;
	private MenuItem removeItem;
	private MenuItem close;
	private MenuItem root;
	private MenuItem back;
	private MenuItem refresh;
	
	public TGBrowserMenuBar(TGBrowserDialog browser){
		super(browser);
	}
	
	public void init(Shell shell){
		this.menu = new Menu(shell, SWT.BAR);
		
		//---File menu------------------------------------------------------
		Menu menuFile = new Menu(shell,SWT.DROP_DOWN);
		this.menuFileItem = new MenuItem(this.menu, SWT.CASCADE);
		this.menuFileItem.setMenu(menuFile);
		
		this.open = new MenuItem(menuFile,SWT.PUSH);
		this.open.setImage(TuxGuitar.instance().getIconManager().getFileOpen());
		this.open.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().openElement();
			}
		});
		
		new MenuItem(menuFile,SWT.SEPARATOR);
		
		this.exit = new MenuItem(menuFile,SWT.PUSH);
		this.exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getShell().dispose();
			}
		});
		
		//---Collection menu------------------------------------------------------
		Menu menuCollection = new Menu(shell,SWT.DROP_DOWN);
		this.menuCollectionItem = new MenuItem(this.menu, SWT.CASCADE);
		this.menuCollectionItem.setMenu(menuCollection);
		
		this.newCollection = new Menu(menuCollection.getShell(), SWT.DROP_DOWN);
		this.newItem = new MenuItem(menuCollection,SWT.CASCADE);
		this.newItem.setImage(TuxGuitar.instance().getIconManager().getBrowserNew());
		this.newItem.setMenu(this.newCollection);
		this.updateTypes();
		
		this.openCollection = new Menu(menuCollection.getShell(), SWT.DROP_DOWN);
		this.openItem = new MenuItem(menuCollection,SWT.CASCADE);
		this.openItem.setImage(TuxGuitar.instance().getIconManager().getFileOpen());
		this.openItem.setMenu(this.openCollection);
		
		this.removeCollection = new Menu(menuCollection.getShell(), SWT.DROP_DOWN);
		this.removeItem = new MenuItem(menuCollection,SWT.CASCADE);
		this.removeItem.setMenu(this.removeCollection);
		
		new MenuItem(menuCollection,SWT.SEPARATOR);
		
		this.close = new MenuItem(menuCollection,SWT.PUSH);
		this.close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				closeCollection();
			}
		});
		
		//---Go menu------------------------------------------------------
		final Menu menuGo = new Menu(shell,SWT.DROP_DOWN);  
		this.menuGoItem = new MenuItem(this.menu, SWT.CASCADE);
		this.menuGoItem.setMenu(menuGo);
		
		this.root = new MenuItem(menuGo,SWT.PUSH);
		this.root.setImage(TuxGuitar.instance().getIconManager().getBrowserRoot());
		this.root.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getConnection().cdRoot(TGBrowserDialog.CALL_CD_ROOT);
			}
		});
		
		//---Back Folder------------------------------------------------------
		this.back = new MenuItem(menuGo,SWT.PUSH);
		this.back.setImage(TuxGuitar.instance().getIconManager().getBrowserBack());
		this.back.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getConnection().cdUp(TGBrowserDialog.CALL_CD_UP);
			}
		});
		
		//---Refresh Folder------------------------------------------------------
		this.refresh = new MenuItem(menuGo,SWT.PUSH);
		this.refresh.setImage(TuxGuitar.instance().getIconManager().getBrowserRefresh());
		this.refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getConnection().listElements(TGBrowserDialog.CALL_LIST);
			}
		});
		
		shell.setMenuBar(this.menu);
	}
	
	public void updateItems(){
		this.open.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
		this.root.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
		this.back.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
		this.refresh.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
		this.newItem.setEnabled(!getBrowser().getConnection().isLocked());
		this.openItem.setEnabled(!getBrowser().getConnection().isLocked());
		this.removeItem.setEnabled(!getBrowser().getConnection().isLocked());
		this.close.setEnabled(!getBrowser().getConnection().isLocked());
	}
	
	public void loadProperties(){
		this.menuFileItem.setText(TuxGuitar.getProperty("browser.menu.file"));
		this.menuCollectionItem.setText(TuxGuitar.getProperty("browser.menu.collection"));
		this.menuGoItem.setText(TuxGuitar.getProperty("browser.menu.go"));
		this.open.setText(TuxGuitar.getProperty("browser.open"));
		this.exit.setText(TuxGuitar.getProperty("browser.exit"));
		this.newItem.setText(TuxGuitar.getProperty("browser.collection.new"));
		this.openItem.setText(TuxGuitar.getProperty("browser.collection.open"));
		this.removeItem.setText(TuxGuitar.getProperty("browser.collection.remove"));
		this.close.setText(TuxGuitar.getProperty("browser.collection.close"));
		this.root.setText(TuxGuitar.getProperty("browser.go-root"));
		this.back.setText(TuxGuitar.getProperty("browser.go-back"));
		this.refresh.setText(TuxGuitar.getProperty("browser.refresh"));
	}
	
	public void updateCollections(TGBrowserCollection selection){
		MenuItem[] openItems = this.openCollection.getItems();
		for(int i = 0;i < openItems.length; i ++){
			openItems[i].dispose();
		}
		MenuItem[] removeItems = this.removeCollection.getItems();
		for(int i = 0;i < removeItems.length; i ++){
			removeItems[i].dispose();
		}
		Iterator it = TGBrowserManager.instance().getCollections();
		while(it.hasNext()){
			final TGBrowserCollection collection = (TGBrowserCollection)it.next();
			if(collection.getData() != null){
				MenuItem openItem = new MenuItem(this.openCollection,SWT.PUSH);
				openItem.setText(collection.getData().getTitle());
				openItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						openCollection(collection);
					}
				});
				if(selection != null && selection.equals(collection)){
					openItem.setSelection(true);
				}
				
				MenuItem removeItem = new MenuItem(this.removeCollection,SWT.PUSH);
				removeItem.setText(collection.getData().getTitle());
				removeItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						removeCollection(collection);
					}
				});
				if(selection != null && selection.equals(collection)){
					removeItem.setSelection(true);
				}
			}
		}
	}
	
	public void updateTypes(){
		MenuItem[] items = this.newCollection.getItems();
		for(int i = 0;i < items.length; i ++){
			items[i].dispose();
		}
		Iterator bookTypes = TGBrowserManager.instance().getFactories();
		while(bookTypes.hasNext()){
			final TGBrowserFactory bookType = (TGBrowserFactory)bookTypes.next();
			MenuItem item = new MenuItem(this.newCollection,SWT.PUSH);
			item.setText(bookType.getName());
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					newCollection( bookType.getType());
				}
			});
		}
	}
	
	public void reload(Shell shell){
		if(this.menu != null && !this.menu.isDisposed()){
			this.menu.dispose();
		}
		this.init(shell);
		this.loadProperties();
		this.updateItems();
	}
}
