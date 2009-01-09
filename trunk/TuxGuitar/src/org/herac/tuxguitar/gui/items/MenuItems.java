package org.herac.tuxguitar.gui.items;


import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.keybindings.KeyBinding;

public abstract class MenuItems implements ItemBase {
	
	public abstract void showItems();
	
	protected void setMenuItemTextAndAccelerator(MenuItem menuItem, String key,String action) {
		String text = TuxGuitar.getProperty(key);
		if (action != null) {
			KeyBinding keyBinding = TuxGuitar.instance().getkeyBindingManager().getKeyBindingForAction(action);
			if (keyBinding != null) {
				text += "\t" + keyBinding.toString();
			}
		}
		menuItem.setText(text);
	}
}
