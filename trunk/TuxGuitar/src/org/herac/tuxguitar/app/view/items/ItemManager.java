package org.herac.tuxguitar.app.view.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editor.TGUpdateEvent;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.view.items.menu.BeatMenuItem;
import org.herac.tuxguitar.app.view.items.menu.CompositionMenuItem;
import org.herac.tuxguitar.app.view.items.menu.EditMenuItem;
import org.herac.tuxguitar.app.view.items.menu.FileMenuItem;
import org.herac.tuxguitar.app.view.items.menu.HelpMenuItem;
import org.herac.tuxguitar.app.view.items.menu.MarkerMenuItem;
import org.herac.tuxguitar.app.view.items.menu.MeasureMenuItem;
import org.herac.tuxguitar.app.view.items.menu.ToolMenuItem;
import org.herac.tuxguitar.app.view.items.menu.TrackMenuItem;
import org.herac.tuxguitar.app.view.items.menu.TransportMenuItem;
import org.herac.tuxguitar.app.view.items.menu.ViewMenuItem;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.io.base.TGFileFormatAvailabilityEvent;
import org.herac.tuxguitar.util.TGContext;

public class ItemManager implements TGEventListener {
	
	private TGContext context;
	private Menu menu;
	private Menu popupMenu;
	private List<MenuItems> loadedMenuItems;
	private List<MenuItems> loadedPopupMenuItems;
	
	private TGSyncProcess loadIconsProcess;
	private TGSyncProcess loadPropertiesProcess;
	private TGSyncProcess updateItemsProcess;
	private TGSyncProcess createMenuProcess;
	
	public ItemManager(TGContext context){
		this.context = context;
		this.loadedMenuItems = new ArrayList<MenuItems>();
		this.loadedPopupMenuItems = new ArrayList<MenuItems>();
		this.createSyncProcesses();
		this.loadItems();
		this.appendListeners();
	}
	
	public void loadItems(){
		this.createMenuProcess.process();
		this.createPopupMenu();
	}
	
	public void createMenu() {
		Shell shell = TuxGuitar.getInstance().getShell();
		if(!shell.isDisposed()) {
			if( this.menu == null || this.menu.isDisposed() ) {
				this.menu = new Menu(shell, SWT.BAR);
			}
			MenuItem[] items = this.menu.getItems();
			for(int i = 0; i < items.length;i ++){
				items[i].dispose();
			}
			
			this.loadedMenuItems.clear();
			this.loadedMenuItems.add(new FileMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new EditMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new ViewMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new CompositionMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new TrackMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new MeasureMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new BeatMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new MarkerMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new TransportMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new ToolMenuItem(shell,this.menu, SWT.CASCADE));
			this.loadedMenuItems.add(new HelpMenuItem(shell,this.menu, SWT.CASCADE));
			this.showMenuItems(this.loadedMenuItems);
			shell.setMenuBar(this.menu);
		}
	}
	
	public void createPopupMenu() {
		Shell shell = TuxGuitar.getInstance().getShell();
		if( this.popupMenu == null || this.popupMenu.isDisposed() ){
			this.popupMenu = new Menu(shell, SWT.POP_UP);
		}
		MenuItem[] items = this.popupMenu.getItems();
		for(int i = 0; i < items.length;i ++){
			items[i].dispose();
		}
		this.loadedPopupMenuItems.clear();
		this.loadedPopupMenuItems.add(new EditMenuItem(shell,this.popupMenu, SWT.CASCADE));
		this.loadedPopupMenuItems.add(new CompositionMenuItem(shell,this.popupMenu, SWT.CASCADE));
		this.loadedPopupMenuItems.add(new TrackMenuItem(shell,this.popupMenu, SWT.CASCADE));
		this.loadedPopupMenuItems.add(new MeasureMenuItem(shell,this.popupMenu, SWT.CASCADE));
		this.loadedPopupMenuItems.add(new BeatMenuItem(shell,this.popupMenu, SWT.CASCADE)); 
		this.loadedPopupMenuItems.add(new MarkerMenuItem(shell,this.popupMenu, SWT.CASCADE));
		this.loadedPopupMenuItems.add(new TransportMenuItem(shell,this.popupMenu, SWT.CASCADE));
		this.showMenuItems(this.loadedPopupMenuItems);
	}
	
	private void showMenuItems(List<MenuItems> items){
		Iterator<MenuItems> it = items.iterator();
		while(it.hasNext()){
			MenuItems item = (MenuItems)it.next();
			item.showItems();
		}
	}
	
	public void updateItems(){
		if(!isDisposed()){
			updateItems(this.loadedMenuItems);
			updateItems(this.loadedPopupMenuItems);
		}
	}
	
	public void updateItems(List<? extends ItemBase> items){
		Iterator<? extends ItemBase> it = items.iterator();
		while(it.hasNext()){
			ItemBase item = (ItemBase)it.next();
			item.update();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			loadProperties(this.loadedMenuItems);
			loadProperties(this.loadedPopupMenuItems);
		}
	}
	
	public void loadProperties(List<? extends ItemBase> items){
		Iterator<? extends ItemBase> it = items.iterator();
		while(it.hasNext()){
			ItemBase item = (ItemBase)it.next();
			item.loadProperties();
		}
	}
	
	public void loadIcons(){
		this.loadItems();
	}
	
	public Menu getPopupMenu(){
		return this.popupMenu;
	}
	
	private boolean isMenuDisposed(){
		return (this.menu == null || this.menu.isDisposed());
	}
	
	private boolean isPopupMenuDisposed(){
		return (this.popupMenu == null || this.popupMenu.isDisposed());
	}
	
	private boolean isDisposed(){
		return (this.isMenuDisposed() || this.isPopupMenuDisposed());
	}
	
	public void createSyncProcesses() {		
		this.updateItemsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				updateItems();
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

		this.createMenuProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				createMenu();
			}
		});
	}
	
	public void appendListeners() {
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
		TuxGuitar.getInstance().getFileFormatManager().addFileFormatAvailabilityListener(this);
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItemsProcess.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGFileFormatAvailabilityEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.createMenuProcess.process();
		}
	}
}
