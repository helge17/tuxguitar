package org.herac.tuxguitar.app.view.dialog.browser.main;

import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCustomItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

public class TGBrowserToolBar extends TGBrowserBar{
	
	private UIToolBar toolBar;
	private UIToolActionItem root;
	private UIToolActionItem back;
	private UIToolActionItem refresh;
	private UIToolMenuItem newBrowser;
	private UIToolCustomItem collectionsItem;
	private UIDropDownSelect<TGBrowserCollection> collections;
	
	public TGBrowserToolBar(TGBrowserDialog browser){
		super(browser);
	}
	
	public void createToolBar(UIContainer parent){
		this.toolBar = this.getBrowser().getUIFactory().createHorizontalToolBar(parent);
		
		//---New Book----------------------------------------------------------
		this.newBrowser = this.toolBar.createMenuItem();
		this.newBrowser.setImage(TuxGuitar.getInstance().getIconManager().getBrowserNew());
		
		Iterator<TGBrowserFactory> bookTypes = TGBrowserManager.getInstance(getBrowser().getContext()).getFactories();
		while(bookTypes.hasNext()) {
			final TGBrowserFactory bookType = (TGBrowserFactory)bookTypes.next();
			
			UIMenuActionItem item = this.newBrowser.getMenu().createActionItem();
			item.setText(bookType.getName());
			item.setData(TGBrowserFactory.class.getName(), bookType);
			item.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					newCollection( bookType.getType());
				}
			});
		}
		
		this.toolBar.createSeparator();
		
		//---Root Folder------------------------------------------------------
		this.root = this.toolBar.createActionItem();
		this.root.setImage(TuxGuitar.getInstance().getIconManager().getBrowserRoot());
		this.root.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().getConnection().cdRoot(TGBrowserDialog.CALL_CD_ROOT);
			}
		});
		
		//---Back Folder------------------------------------------------------
		this.back = this.toolBar.createActionItem();
		this.back.setImage(TuxGuitar.getInstance().getIconManager().getBrowserBack());
		this.back.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().getConnection().cdUp(TGBrowserDialog.CALL_CD_UP);
			}
		});
		
		//---Refresh Folder------------------------------------------------------
		this.refresh = this.toolBar.createActionItem();
		this.refresh.setImage(TuxGuitar.getInstance().getIconManager().getBrowserRefresh());
		this.refresh.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().getConnection().listElements(TGBrowserDialog.CALL_LIST);
			}
		});
		
		//---Finish tool bar
		this.toolBar.createSeparator();
		
		//---Collections-------------------------------------------------------------
		this.collectionsItem = this.toolBar.createCustomItem();
		this.collectionsItem.setLayoutAttribute(UIToolCustomItem.FILL, true);
		
		this.collections = this.getBrowser().getUIFactory().createDropDownSelect(this.collectionsItem);
		this.collections.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
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
		this.collections.removeItems();
		this.collections.addItem(new UISelectItem<TGBrowserCollection>(TuxGuitar.getProperty("browser.collection.select"), null));
		
		Iterator<TGBrowserCollection> it = TGBrowserManager.getInstance(getBrowser().getContext()).getCollections();
		while(it.hasNext()){
			TGBrowserCollection collection = it.next();
			if( collection.getData() != null ) {
				this.collections.addItem(new UISelectItem<TGBrowserCollection>(collection.getData().getTitle(), collection));
			}
		}
		
		this.collections.setSelectedValue(selection);
	}
	
	public void reload(){
		if(!this.toolBar.isDisposed()) {
			UIContainer parent = (UIContainer) this.toolBar.getParent();
			
			this.dispose();
			this.createToolBar(parent);
			this.loadProperties();
			this.updateItems();
		}
	}
	
	public void loadProperties(){
		this.newBrowser.setToolTipText(TuxGuitar.getProperty("browser.collection.new"));
		this.root.setToolTipText(TuxGuitar.getProperty("browser.go-root"));
		this.back.setToolTipText(TuxGuitar.getProperty("browser.go-back"));
		this.refresh.setToolTipText(TuxGuitar.getProperty("browser.refresh"));
		this.updateCollections(getBrowser().getCollection());
	}
	
	protected void updateCollection(){
		TGBrowserCollection collection = this.collections.getSelectedValue();
		if(collection == null){
			closeCollection();
		}else{
			openCollection(collection);
		}
	}
	
	private void dispose(){
		this.toolBar.dispose();
	}
	
	public UIToolBar getControl() {
		return this.toolBar;
	}
}
