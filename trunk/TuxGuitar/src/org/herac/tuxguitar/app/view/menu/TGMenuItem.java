package org.herac.tuxguitar.app.view.menu;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGMenuItem {
	
	public abstract void update();
	
	public abstract void loadProperties();
	
	public abstract void showItems();
	
	public void setMenuItemTextAndAccelerator(UIMenuItem menuItem, String key,String action) {
		menuItem.setKeyConvination(action != null ? KeyBindingActionManager.getInstance(this.findContext()).getKeyBindingForAction(action) : null);
		menuItem.setText(TuxGuitar.getProperty(key));
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}
	
	public TGContext findContext() {
		return TuxGuitar.getInstance().getContext();
	}
}
