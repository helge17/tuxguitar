package org.herac.tuxguitar.ui.jfx.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.MenuItem;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class JFXAbstractMenu<T> extends JFXEventReceiver<T> implements JFXMenuItemContainer {
	
	private List<UIMenuItem>  menuItems;
	
	public JFXAbstractMenu(T control) {
		super(control);
		
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
		return new JFXMenuSeparatorItem(this);
	}
	
	public UIMenuActionItem createActionItem() {
		return new JFXMenuActionItem(this);
	}
	
	public UIMenuCheckableItem createCheckItem() {
		return new JFXMenuCheckableItem(this);
	}
	
	public UIMenuCheckableItem createRadioItem() {
		return new JFXMenuRadioItem(this);
	}
	
	public UIMenuSubMenuItem createSubMenuItem() {
		return new JFXMenuSubMenuItem(this);
	}
	
	public void addItem(JFXMenuItem<? extends MenuItem> item) {
		this.menuItems.add(item);
	}
	
	public void removeItem(JFXMenuItem<? extends MenuItem> item) {
		if( this.menuItems.contains(item)) { 
			this.menuItems.remove(item);
		}
	}
}