package org.herac.tuxguitar.app.items;


import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.keybindings.KeyBinding;

public abstract class MenuItems implements ItemBase {
	
	public abstract void showItems();
	
	protected void setMenuItemTextAndAccelerator(MenuItem menuItem, String key,String action) {
		String text = TuxGuitar.getProperty(key);
		if (action != null) {
			KeyBinding keyBinding = TuxGuitar.instance().getKeyBindingManager().getKeyBindingForAction(action);
			if (keyBinding != null) {
				text += "\t" + keyBinding.toString() + "\u0000";
			}
		}
		menuItem.setText(text);
	}
}
