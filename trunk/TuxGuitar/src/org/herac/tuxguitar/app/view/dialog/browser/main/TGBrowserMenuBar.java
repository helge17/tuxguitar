package org.herac.tuxguitar.app.view.dialog.browser.main;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGBrowserMenuBar extends TGBrowserBar{
	
	private UIMenuBar menu;
	
	private UIMenuSubMenuItem menuFileItem;
	private UIMenuSubMenuItem menuCollectionItem;
	private UIMenuSubMenuItem menuGoItem;
	private UIMenuActionItem open;
	private UIMenuActionItem exit;
	
	private UIMenuSubMenuItem newItem;
	private UIMenuSubMenuItem openItem;
	private UIMenuSubMenuItem removeItem;
	
	private UIMenuActionItem close;
	private UIMenuActionItem root;
	private UIMenuActionItem back;
	private UIMenuActionItem refresh;
	
	public TGBrowserMenuBar(TGBrowserDialog browser){
		super(browser);
	}
	
	public void createMenuBar(UIWindow window){
		this.menu = this.getBrowser().getUIFactory().createMenuBar(window);
		
		//---File menu------------------------------------------------------
		this.menuFileItem = this.menu.createSubMenuItem();
		
		this.open = this.menuFileItem.getMenu().createActionItem();
		this.open.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		this.open.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().openElement();
			}
		});
		
		this.menuFileItem.getMenu().createSeparator();
		
		this.exit = this.menuFileItem.getMenu().createActionItem();
		this.exit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().dispose();
			}
		});
		
		//---Collection menu------------------------------------------------------
		this.menuCollectionItem = this.menu.createSubMenuItem();
		
		this.newItem = this.menuCollectionItem.getMenu().createSubMenuItem();
		this.newItem.setImage(TuxGuitar.getInstance().getIconManager().getBrowserNew());
		this.updateTypes();
		
		this.openItem = this.menuCollectionItem.getMenu().createSubMenuItem();
		this.openItem.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		
		this.removeItem = this.menuCollectionItem.getMenu().createSubMenuItem();
		
		this.menuCollectionItem.getMenu().createSeparator();
		
		this.close = this.menuCollectionItem.getMenu().createActionItem();
		this.close.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				closeCollection();
			}
		});
		
		//---Go menu------------------------------------------------------
		this.menuGoItem = this.menu.createSubMenuItem();
		
		this.root = this.menuGoItem.getMenu().createActionItem();
		this.root.setImage(TuxGuitar.getInstance().getIconManager().getBrowserRoot());
		this.root.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().cdRoot();
			}
		});
		
		//---Back Folder------------------------------------------------------
		this.back = this.menuGoItem.getMenu().createActionItem();
		this.back.setImage(TuxGuitar.getInstance().getIconManager().getBrowserBack());
		this.back.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().cdUp();
			}
		});
		
		//---Refresh Folder------------------------------------------------------
		this.refresh = this.menuGoItem.getMenu().createActionItem();
		this.refresh.setImage(TuxGuitar.getInstance().getIconManager().getBrowserRefresh());
		this.refresh.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getBrowser().listElements();
			}
		});
		
		window.setMenuBar(this.menu);
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
		List<UIMenuItem> openItems = this.openItem.getMenu().getItems();
		for(UIMenuItem uiMenuItem : openItems){
			uiMenuItem.dispose();
		}
		
		List<UIMenuItem> removeItems = this.removeItem.getMenu().getItems();
		for(UIMenuItem uiMenuItem : removeItems){
			uiMenuItem.dispose();
		}
		
		Iterator<TGBrowserCollection> it = TGBrowserManager.getInstance(getBrowser().getContext()).getCollections();
		while(it.hasNext()){
			final TGBrowserCollection collection = (TGBrowserCollection)it.next();
			if( collection.getData() != null) {
				UIMenuActionItem openItem = this.openItem.getMenu().createActionItem();
				openItem.setText(collection.getData().getTitle());
				openItem.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						openCollection(collection);
					}
				});
				
				UIMenuActionItem removeItem = this.removeItem.getMenu().createActionItem();
				removeItem.setText(collection.getData().getTitle());
				removeItem.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						removeCollection(collection);
					}
				});
			}
		}
	}
	
	public void updateTypes() {
		List<UIMenuItem> newItems = this.newItem.getMenu().getItems();
		for(UIMenuItem uiMenuItem : newItems){
			uiMenuItem.dispose();
		}
		
		Iterator<TGBrowserFactory> bookTypes = TGBrowserManager.getInstance(getBrowser().getContext()).getFactories();
		while(bookTypes.hasNext()){
			final TGBrowserFactory bookType = (TGBrowserFactory)bookTypes.next();
			UIMenuActionItem item = this.newItem.getMenu().createActionItem();
			item.setText(bookType.getName());
			item.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					newCollection( bookType.getType());
				}
			});
		}
	}
	
	public void reload(UIWindow window){
		if( this.menu != null && !this.menu.isDisposed()){
			this.menu.dispose();
		}
		this.createMenuBar(window);
		this.loadProperties();
		this.updateItems();
	}
}
