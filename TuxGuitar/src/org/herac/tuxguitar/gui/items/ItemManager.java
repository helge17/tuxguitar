/*
 * Created on 18-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGUpdateListener;
import org.herac.tuxguitar.gui.items.menu.BeatMenuItem;
import org.herac.tuxguitar.gui.items.menu.CompositionMenuItem;
import org.herac.tuxguitar.gui.items.menu.EditMenuItem;
import org.herac.tuxguitar.gui.items.menu.FileMenuItem;
import org.herac.tuxguitar.gui.items.menu.HelpMenuItem;
import org.herac.tuxguitar.gui.items.menu.MarkerMenuItem;
import org.herac.tuxguitar.gui.items.menu.MeasureMenuItem;
import org.herac.tuxguitar.gui.items.menu.ToolMenuItem;
import org.herac.tuxguitar.gui.items.menu.TrackMenuItem;
import org.herac.tuxguitar.gui.items.menu.TransportMenuItem;
import org.herac.tuxguitar.gui.items.menu.ViewMenuItem;
import org.herac.tuxguitar.gui.items.tool.BeatToolItems;
import org.herac.tuxguitar.gui.items.tool.CompositionToolItems;
import org.herac.tuxguitar.gui.items.tool.DurationToolItems;
import org.herac.tuxguitar.gui.items.tool.DynamicToolItems;
import org.herac.tuxguitar.gui.items.tool.EditToolItems;
import org.herac.tuxguitar.gui.items.tool.FileToolItems;
import org.herac.tuxguitar.gui.items.tool.LayoutToolItems;
import org.herac.tuxguitar.gui.items.tool.MarkerToolItems;
import org.herac.tuxguitar.gui.items.tool.NoteEffectToolItems;
import org.herac.tuxguitar.gui.items.tool.PropertiesToolItems;
import org.herac.tuxguitar.gui.items.tool.TrackToolItems;
import org.herac.tuxguitar.gui.items.tool.TransportToolItems;
import org.herac.tuxguitar.gui.items.tool.ViewToolItems;
import org.herac.tuxguitar.gui.items.xml.ToolBarsReader;
import org.herac.tuxguitar.gui.items.xml.ToolBarsWriter;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.util.TGFileUtils;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ItemManager implements TGUpdateListener,IconLoader,LanguageLoader{
	
	private Menu menu;
	private Menu popupMenu;
	private CoolBar coolBar;
	private List loadedToolItems;
	private List loadedMenuItems;
	private List loadedPopupMenuItems;
	private ToolItems[] toolItems;
	
	private boolean layout_locked;
	private boolean shouldReloadToolBars;
	private boolean coolbarVisible;
	private boolean updateCoolBarWrapIndicesEnabled;
	
	public ItemManager(){
		this.loadedToolItems = new ArrayList();
		this.loadedMenuItems = new ArrayList();
		this.loadedPopupMenuItems = new ArrayList();
		this.layout_locked = false;
		this.setDefaultToolBars();
		this.loadItems();
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void loadItems(){
		this.createMenu();
		this.createPopupMenu();
		this.createCoolbar();
	}
	
	public void createCoolbar() {
		boolean initialized = (this.coolBar != null && !this.coolBar.isDisposed());
		
		this.layout_locked = true;
		this.updateCoolBarWrapIndicesEnabled = true;
		if( !initialized ){
			FormData coolData = new FormData();
			coolData.left = new FormAttachment(0);
			coolData.right = new FormAttachment(100);
			coolData.top = new FormAttachment(0,0);
			
			this.coolBar = new CoolBar(TuxGuitar.instance().getShell(),SWT.HORIZONTAL | SWT.FLAT);
			this.coolBar.setLayoutData(coolData);
			this.coolBar.addListener(SWT.Resize, new Listener() {
				public void handleEvent(Event event) {
					layoutCoolBar();
				}
			});
			this.coolBar.addListener(SWT.DragDetect, new Listener() {
				public void handleEvent(Event event) {
					disableUpdateCoolBarWrapIndices();
				}
			});
			
			TuxGuitar.instance().getkeyBindingManager().appendListenersTo(this.coolBar);
		}
		this.makeCoolItems();
		
		this.layout_locked = false;
		
		if( initialized ){
			this.layoutCoolBar();
		}
	}
	
	public void toogleToolbarVisibility(){
		if(this.coolBar != null && !this.coolBar.isDisposed()){
			this.layout_locked = true;
			this.coolBar.setVisible( !this.coolbarVisible );
			if( this.coolbarVisible ){
				this.clearCoolBar();
			}else{
				this.makeCoolItems();
			}
			
			this.layout_locked = false;
			
			this.layoutCoolBar();
		}
	}
	
	private void clearCoolBar(){
		if(this.coolBar != null && !this.coolBar.isDisposed()){
			this.loadedToolItems.clear();
			CoolItem[] items = this.coolBar.getItems();
			for(int i = 0;i < items.length; i ++){
				items[i].dispose();
			}
			Control[] controls = this.coolBar.getChildren();
			for(int i = 0;i < controls.length; i ++){
				controls[i].dispose();
			}
		}
		this.coolbarVisible = false;
	}
	
	protected void updateCoolBarWrapIndices(){
		int coolBarWidth = this.coolBar.getClientArea().width;
		int coolItemsWidth = 0;
		
		List coolItemIndices = new ArrayList();
		
		CoolItem[] items = this.coolBar.getItems();
		for(int i = 0;i < items.length; i ++){
			Point controlSise = items[i].getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point itemSize = items[i].computeSize(controlSise.x, controlSise.y);
			
			int nextCoolItemsWidth = ( coolItemsWidth + itemSize.x );
			if( nextCoolItemsWidth > coolBarWidth ){
				coolItemIndices.add( new Integer( i ) );
				nextCoolItemsWidth = itemSize.x;
			}
			coolItemsWidth = nextCoolItemsWidth;
		}
		
		int[] coolItemIndicesArray = new int[ coolItemIndices.size() ];
		for(int i = 0;i < coolItemIndicesArray.length; i ++){
			coolItemIndicesArray[i] = ((Integer)coolItemIndices.get(i)).intValue();
		}
		
		this.coolBar.setWrapIndices( coolItemIndicesArray );
	}
	
	protected void layoutCoolBar(){
		if(!this.layout_locked){
			this.layout_locked = true;
			if( this.updateCoolBarWrapIndicesEnabled ){
				this.updateCoolBarWrapIndices();
			}
			this.layoutShellLater();
			this.layout_locked = false;
		}
	}
	
	protected void layoutShell(){
		if(!this.layout_locked){
			this.layout_locked = true;
			TuxGuitar.instance().getShell().layout(true,true);
			this.layout_locked = false;
		}
	}
	
	protected void layoutShellLater(){
		try {
			TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
					layoutShell();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void makeCoolItems(){
		this.clearCoolBar();
		this.readToolBars();
		for(int i = 0; i < this.toolItems.length; i ++){
			if(this.toolItems[i].isEnabled()){
				this.makeToolBar(this.toolItems[i]);
			}
		}
		this.coolbarVisible = true;
	}
	
	private void makeToolBar(ToolItems item){
		ToolBar toolBar = new ToolBar(this.coolBar,SWT.HORIZONTAL | SWT.FLAT );
		item.showItems(toolBar);
		makeCoolItem(toolBar); 
		this.loadedToolItems.add(item);
	}
	
	private void makeCoolItem(ToolBar toolBar){
		Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		CoolItem coolItem = new CoolItem(this.coolBar, SWT.NONE);
		coolItem.setMinimumSize(size);
		coolItem.setPreferredSize(coolItem.computeSize(size.x, size.y));
		coolItem.setControl(toolBar);
	}
	
	public void createMenu() {
		Shell shell = TuxGuitar.instance().getShell();
		if(this.menu == null || this.menu.isDisposed()){
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
	
	public void createPopupMenu() {
		Shell shell = TuxGuitar.instance().getShell();
		if(this.popupMenu == null || this.popupMenu.isDisposed()){
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
	
	private void showMenuItems(List items){
		Iterator it = items.iterator();
		while(it.hasNext()){
			MenuItems item = (MenuItems)it.next();
			item.showItems();
		}
	}
	
	public void updateItems(){
		if(!isDisposed()){
			updateItems(this.loadedToolItems);
			updateItems(this.loadedMenuItems);
			updateItems(this.loadedPopupMenuItems);
		}
	}
	
	public void updateItems(List items){
		Iterator it = items.iterator();
		while(it.hasNext()){
			ItemBase item = (ItemBase)it.next();
			item.update();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			loadProperties(this.loadedToolItems);
			loadProperties(this.loadedMenuItems);
			loadProperties(this.loadedPopupMenuItems);
		}
	}
	
	public void loadProperties(List items){
		Iterator it = items.iterator();
		while(it.hasNext()){
			ItemBase item = (ItemBase)it.next();
			item.loadProperties();
		}
	}
	
	public void loadIcons(){
		this.loadItems();
	}
	
	public CoolBar getCoolbar(){
		return this.coolBar;
	}
	
	public Menu getPopupMenu(){
		return this.popupMenu;
	}
	
	public void readToolBars() {
		File file = new File(getCoolItemsFileName());
		if(!file.exists()){
			writeToolBars();
		}
		this.shouldReloadToolBars = false;
		ToolBarsReader.loadToolBars(this,file);
	}
	
	public void writeToolBars(){
		File file = new File(getCoolItemsFileName());
		ToolBarsWriter.saveToolBars(getToolBars(), file);
	}
	
	public void setToolBarStatus(String name, boolean enabled, int index){
		if(index >= 0 && index < this.toolItems.length){
			setToolBarPosition(name, index);
			setToolBarEnabled(index, enabled);
		}
	}
	
	public void setToolBarEnabled(int index, boolean enabled){
		this.shouldReloadToolBars = (this.shouldReloadToolBars || (this.toolItems[ index ].isEnabled() != enabled ));
		
		this.toolItems[ index ].setEnabled(enabled);
	}
	
	public void setToolBarPosition(String name, int index){
		if(index >= 0 && index < this.toolItems.length){
			ToolItems element = this.toolItems[index];
			if( ! element.getName().trim().toLowerCase().equals(name.trim().toLowerCase())){
				int oldIndex = -1;
				for(int i = 0; i < this.toolItems.length; i ++){
					if(this.toolItems[i].getName().trim().toLowerCase().equals(name.trim().toLowerCase())){
						oldIndex = i;
						break;
					}
				}
				if(oldIndex == -1){
					return;
				}
				this.toolItems[index] = this.toolItems[oldIndex];
				this.toolItems[oldIndex] = element;
				
				this.shouldReloadToolBars = true;
			}
		}
	}
	
	public ToolItems[] getToolBars(){
		return this.toolItems;
	}
	
	public boolean shouldReloadToolBars(){
		return this.shouldReloadToolBars;
	}
	
	public void setDefaultToolBars(){
		this.toolItems = new ToolItems[]{
				initToolItem(new FileToolItems(), true),
				initToolItem(new EditToolItems(), true),
				initToolItem(new PropertiesToolItems(), true),
				initToolItem(new TrackToolItems(), true),
				initToolItem(new DurationToolItems(), true),
				initToolItem(new BeatToolItems(), true),
				initToolItem(new CompositionToolItems(), true),
				initToolItem(new TransportToolItems(), true),
				initToolItem(new MarkerToolItems(), true),
				initToolItem(new LayoutToolItems(), true),
				initToolItem(new ViewToolItems(), true),
				initToolItem(new NoteEffectToolItems(), true),
				initToolItem(new DynamicToolItems(), true),
		};
		this.shouldReloadToolBars = true;
	}
	
	private ToolItems initToolItem(ToolItems item, boolean enabled){
		item.setEnabled(enabled);
		return item;
	}
	
	private boolean isDisposed(){
		return (this.coolBar.isDisposed() || this.menu.isDisposed() || this.popupMenu.isDisposed());
	}
	
	private String getCoolItemsFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "toolbars.xml";
	}

	public void doUpdate(int type) {
		if( type == TGUpdateListener.SELECTION ){
			this.updateItems();
		}
	}
	
	public void disableUpdateCoolBarWrapIndices() {
		if( this.updateCoolBarWrapIndicesEnabled ){
			this.coolBar.setWrapIndices( null );
		}
		this.updateCoolBarWrapIndicesEnabled = false;
	}
}
