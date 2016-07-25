package org.herac.tuxguitar.ui.swt.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMenu extends SWTEventReceiver<Menu> implements UIMenu {
	
	private List<UIMenuItem>  menuItems;
	
	public SWTMenu(Shell shell, int style) {
		super(new Menu(shell, style));
		
		this.menuItems = new ArrayList<UIMenuItem>();
	}
	
	public Integer getItemCount() {
		return this.menuItems.size();
	}
	
	public UIMenuItem getItem(int index) {
		return (index >= 0 && index < this.menuItems.size() ? this.menuItems.get(index) : null);
	}
	
	public List<UIMenuItem> getItems() {
		return new ArrayList<UIMenuItem>(this.menuItems);
	}
	
	public UIComponent createSeparator() {
		MenuItem menuItem = new MenuItem(this.getControl(), SWT.SEPARATOR);
		
		return this.append(new SWTMenuItem(menuItem, this));
	}
	
	public UIMenuActionItem createActionItem() {
		MenuItem menuItem = new MenuItem(this.getControl(), SWT.PUSH);
		
		return this.append(new SWTMenuActionItem(menuItem, this));
	}
	
	public UIMenuCheckableItem createCheckItem() {
		MenuItem menuItem = new MenuItem(this.getControl(), SWT.CHECK);
		
		return this.append(new SWTMenuCheckableItem(menuItem, this));
	}
	
	public UIMenuCheckableItem createRadioItem() {
		MenuItem menuItem = new MenuItem(this.getControl(), SWT.RADIO);
		
		return this.append(new SWTMenuCheckableItem(menuItem, this));
	}
	
	public UIMenuSubMenuItem createSubMenuItem() {
		MenuItem menuItem = new MenuItem(this.getControl(), SWT.CASCADE);
		
		return this.append(new SWTMenuSubMenuItem(menuItem, this));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends UIMenuItem> T append(UIMenuItem item) {
		this.menuItems.add(item);
		
		return (T) item;
	}
	
	public void dispose(SWTMenuItem item) {
		if( this.menuItems.contains(item)) { 
			this.menuItems.remove(item);
		}
		item.getControl().dispose();
	}
	
	public void dispose() {
		this.getControl().dispose();
	}

	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}
}
