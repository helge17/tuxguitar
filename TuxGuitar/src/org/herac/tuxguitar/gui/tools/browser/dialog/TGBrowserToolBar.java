package org.herac.tuxguitar.gui.tools.browser.dialog;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;

public class TGBrowserToolBar extends TGBrowserBar{
	private Composite composite;
	private ToolBar toolBar;
	private ToolItem newBrowser;
	private ToolItem root;
	private ToolItem back;
	private ToolItem refresh;
	protected Menu newBrowserMenu;
	protected TGBrowserCollectionCombo collections;
	
	public TGBrowserToolBar(TGBrowserDialog browser){
		super(browser);
	}
	
	public void init(Shell shell){
		this.composite = new Composite(shell,SWT.NONE);
		this.composite.setLayout(getLayout());
		this.composite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		this.initItems();
	}
	
	public void initItems(){
		this.toolBar = new ToolBar(this.composite,SWT.FLAT | SWT.WRAP);
		
		//---New Book----------------------------------------------------------
		this.newBrowserMenu = new Menu(this.composite);
		Iterator bookTypes = TGBrowserManager.instance().getFactories();
		while(bookTypes.hasNext()) {
			final TGBrowserFactory bookType = (TGBrowserFactory)bookTypes.next();
			MenuItem item = new MenuItem(this.newBrowserMenu, SWT.PUSH);
			item.setText(bookType.getName());
			item.setData(bookType);
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					newCollection( bookType.getType());
				}
			});
		}
		this.newBrowser = new ToolItem(this.toolBar,SWT.DROP_DOWN);
		this.newBrowser.setImage(TuxGuitar.instance().getIconManager().getBrowserNew());
		this.newBrowser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					ToolItem item = (ToolItem) event.widget;
					Rectangle rect = item.getBounds();
					Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
					TGBrowserToolBar.this.newBrowserMenu.setLocation(pt.x, pt.y + rect.height);
					TGBrowserToolBar.this.newBrowserMenu.setVisible(true);
				}
			}
		});
		
		new ToolItem(this.toolBar, SWT.SEPARATOR);
		//---Root Folder------------------------------------------------------
		this.root = new ToolItem(this.toolBar,SWT.PUSH);
		this.root.setImage(TuxGuitar.instance().getIconManager().getBrowserRoot());
		this.root.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getConnection().cdRoot(TGBrowserDialog.CALL_CD_ROOT);
			}
		});
		
		//---Back Folder------------------------------------------------------
		this.back = new ToolItem(this.toolBar,SWT.PUSH);
		this.back.setImage(TuxGuitar.instance().getIconManager().getBrowserBack());
		this.back.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getConnection().cdUp(TGBrowserDialog.CALL_CD_UP);
			}
		});
		
		//---Refresh Folder------------------------------------------------------
		this.refresh = new ToolItem(this.toolBar,SWT.PUSH);
		this.refresh.setImage(TuxGuitar.instance().getIconManager().getBrowserRefresh());
		this.refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().getConnection().listElements(TGBrowserDialog.CALL_LIST);
			}
		});
		
		//---Finish tool bar
		new ToolItem(this.toolBar, SWT.SEPARATOR);
		
		//---Collections-------------------------------------------------------------
		this.collections = new TGBrowserCollectionCombo(this.composite, SWT.READ_ONLY);
		this.collections.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		this.collections.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateCollection();
			}
		});
	}
	
	public void updateItems(){
		this.newBrowser.setEnabled(!getBrowser().getConnection().isLocked());
		this.collections.setEnabled(!getBrowser().getConnection().isLocked());
		this.root.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
		this.back.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
		this.refresh.setEnabled(!getBrowser().getConnection().isLocked() && getBrowser().getConnection().isOpen());
	}
	
	public void updateCollections(TGBrowserCollection selection){
		int index = 0;
		this.collections.removeAll();
		
		Iterator it = TGBrowserManager.instance().getCollections();
		while(it.hasNext()){
			TGBrowserCollection collection = (TGBrowserCollection)it.next();
			if(collection.getData() != null){
				this.collections.add(collection);
				if(selection != null && selection.equals(collection)){
					this.collections.select(index);
				}
				index ++;
			}
		}
	}
	
	public void reload(){
		this.disposeItems();
		this.initItems();
		this.loadProperties();
		this.updateItems();
		this.composite.layout(true,true);
	}
	
	public void loadProperties(){
		this.newBrowser.setToolTipText(TuxGuitar.getProperty("browser.collection.new"));
		this.root.setToolTipText(TuxGuitar.getProperty("browser.go-root"));
		this.back.setToolTipText(TuxGuitar.getProperty("browser.go-back"));
		this.refresh.setToolTipText(TuxGuitar.getProperty("browser.refresh"));
		this.updateCollections(getBrowser().getCollection());
	}
	
	protected void updateCollection(){
		TGBrowserCollection collection = this.collections.getSelection();
		if(collection == null){
			closeCollection();
		}else{
			openCollection(collection);
		}
	}
	
	private void disposeItems(){
		Control[] controls = this.composite.getChildren();
		for(int i = 0; i < controls.length; i ++){
			controls[i].dispose();
		}
	}
	
	private GridLayout getLayout(){
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
}
