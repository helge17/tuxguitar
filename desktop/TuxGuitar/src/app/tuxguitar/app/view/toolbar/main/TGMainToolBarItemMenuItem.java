package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.ui.menu.UIMenuItem;
import app.tuxguitar.util.TGContext;

// a menu action (inside a menu, opened by a toolBar button)
public class TGMainToolBarItemMenuItem extends TGMainToolBarItem {

	private static final String SELECTED_CHAR = "\u2713";
	private UIMenuItem menuItem;
	private boolean checked;

	// build a menu item from a (checkable) button description, or a separator
	public TGMainToolBarItemMenuItem(TGMainToolBarItem button) {
		super(button.getGroupName(), button.getText(), ((button.type == TGMainToolBarItem.SEPARATOR) ? SEPARATOR : MENU),
				button.getActionName(), button.getIconFileName(), button.getUpdater());
		for (String key : button.attributes.keySet()) {
			this.setAttribute(key, button.attributes.get(key));
		}
		this.menuItem = null;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void setMenuItem(UIMenuItem menuItem) {
		this.menuItem = menuItem;
	}

	@Override
	public void update(TGContext context, boolean running) {
		if (this.updater != null) {
			this.checked = this.updater.checked(context, running);
			this.loadProperties();
		}
	}

	@Override
	public void loadProperties() {
		if (menuItem != null) {
			menuItem.setText((this.checked ? SELECTED_CHAR : "") + TuxGuitar.getProperty(this.getText()));
		}
	}

	@Override
	public void loadIcons(TGIconManager iconManager) {
		if (menuItem != null) {
			menuItem.setImage(iconManager.getImageByName(this.iconFileName));
		}
	}

}
