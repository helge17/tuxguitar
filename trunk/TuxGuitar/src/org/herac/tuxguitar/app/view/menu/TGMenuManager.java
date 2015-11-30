package org.herac.tuxguitar.app.view.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.view.menu.impl.BeatMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.CompositionMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.EditMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.FileMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.HelpMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.MarkerMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.MeasureMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.ToolMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.TrackMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.TransportMenuItem;
import org.herac.tuxguitar.app.view.menu.impl.ViewMenuItem;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.io.base.TGFileFormatAvailabilityEvent;
import org.herac.tuxguitar.util.TGContext;

public class TGMenuManager implements TGEventListener {
	
	private TGContext context;
	private Menu menu;
	private Menu popupMenu;
	private List<TGMenuItem> loadedMenuItems;
	private List<TGMenuItem> loadedPopupMenuItems;
	
	private TGSyncProcessLocked loadIconsProcess;
	private TGSyncProcessLocked loadPropertiesProcess;
	private TGSyncProcessLocked updateItemsProcess;
	private TGSyncProcessLocked createMenuProcess;
	
	public TGMenuManager(TGContext context){
		this.context = context;
		this.loadedMenuItems = new ArrayList<TGMenuItem>();
		this.loadedPopupMenuItems = new ArrayList<TGMenuItem>();
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
	
	private void showMenuItems(List<TGMenuItem> items){
		Iterator<TGMenuItem> it = items.iterator();
		while(it.hasNext()){
			TGMenuItem item = (TGMenuItem)it.next();
			item.showItems();
		}
	}
	
	public void updateItems(){
		if(!isDisposed()){
			updateItems(this.loadedMenuItems);
			updateItems(this.loadedPopupMenuItems);
		}
	}
	
	public void updateItems(List<TGMenuItem> menuItems){
		for(TGMenuItem menuItem : menuItems) {
			menuItem.update();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			loadProperties(this.loadedMenuItems);
			loadProperties(this.loadedPopupMenuItems);
		}
	}
	
	public void loadProperties(List<TGMenuItem> menuItems){
		for(TGMenuItem menuItem : menuItems) {
			menuItem.loadProperties();
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
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
		
		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		
		this.loadPropertiesProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});

		this.createMenuProcess = new TGSyncProcessLocked(this.context, new Runnable() {
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
