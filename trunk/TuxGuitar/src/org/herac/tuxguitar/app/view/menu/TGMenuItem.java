package org.herac.tuxguitar.app.view.menu;

import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.keybindings.KeyBinding;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGMenuItem {
	
	public abstract void update();
	
	public abstract void loadProperties();
	
	public abstract void showItems();
	
	protected void setMenuItemTextAndAccelerator(MenuItem menuItem, String key,String action) {
		String text = TuxGuitar.getProperty(key);
		if (action != null) {
			KeyBinding keyBinding = TuxGuitar.getInstance().getKeyBindingManager().getKeyBindingForAction(action);
			if (keyBinding != null) {
				text += "\t" + keyBinding.toString() + "\u0000";
			}
		}
		menuItem.setText(text);
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}
	
	public TGContext findContext() {
		return TuxGuitar.getInstance().getContext();
	}
}
