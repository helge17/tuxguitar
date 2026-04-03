package app.tuxguitar.app.view.menu;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import app.tuxguitar.ui.menu.UIMenuItem;
import app.tuxguitar.util.TGContext;

public abstract class TGMenuItem {

	public abstract void update();

	public abstract void loadProperties();

	public abstract void showItems();

	public void setMenuItemTextAndAccelerator(UIMenuItem menuItem, String key,String action) {
		menuItem.setKeyCombination(action != null ? KeyBindingActionManager.getInstance(this.findContext()).getKeyBindingForAction(action) : null);
		menuItem.setText(TuxGuitar.getProperty(key));
	}

	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}

	public TGContext findContext() {
		return TuxGuitar.getInstance().getContext();
	}
}
