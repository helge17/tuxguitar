package app.tuxguitar.app.view.toolbar.main;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolMenuItem;
import app.tuxguitar.util.TGContext;

// a menu button in toolBar opening a list of actions
public class TGMainToolBarItemMenu extends TGMainToolBarItem {

	private UIToolMenuItem menuItem;
	private List<TGMainToolBarItemMenuItem> toolBarMenuItems;

	public TGMainToolBarItemMenu(String text, String iconFileName) {
		super(text, TGMainToolBarItem.MENU, null, iconFileName, null);
		this.toolBarMenuItems = new ArrayList<TGMainToolBarItemMenuItem>();
	}

	public void addMenuItem(TGMainToolBarItem item) {
		this.toolBarMenuItems.add(new TGMainToolBarItemMenuItem(item));
	}

	public void createMenu(UIToolBar toolBar, TGContext context) {
		this.menuItem = toolBar.createMenuItem();
		for (TGMainToolBarItemMenuItem toolBarMenuItem : toolBarMenuItems) {
			if (toolBarMenuItem.getType() == TGMainToolBarItem.SEPARATOR) {
				this.menuItem.getMenu().createSeparator();
			} else {
				UIMenuActionItem menuActionItem = this.menuItem.getMenu().createActionItem();
				menuActionItem
						.addSelectionListener(new TGActionProcessorListener(context, toolBarMenuItem.getActionName()));
				toolBarMenuItem.setMenuItem(menuActionItem);
			}
		}
	}

	@Override
	public void update(TGContext context, boolean running) {
		for (TGMainToolBarItemMenuItem toolBarMenuItem : toolBarMenuItems) {
			toolBarMenuItem.update(context, running);
		}
	}

	@Override
	public void loadIcons(TGIconManager iconManager) {
		this.menuItem.setImage(iconManager.getImageByName(this.getIconFileName()));
		for (TGMainToolBarItemMenuItem toolBarMenuItem : toolBarMenuItems) {
			toolBarMenuItem.loadIcons(iconManager);
		}
	}

	@Override
	public void loadProperties() {
		this.menuItem.setToolTipText(TuxGuitar.getProperty(this.getText()));
		for (TGMainToolBarItemMenuItem toolBarMenuItem : toolBarMenuItems) {
			toolBarMenuItem.loadProperties();
		}
	}

	public TGMainToolBarItemMenu clone() {
		TGMainToolBarItemMenu clone = new TGMainToolBarItemMenu(this.text, this.iconFileName);
		for (TGMainToolBarItemMenuItem menuItem : this.toolBarMenuItems) {
			clone.addMenuItem(menuItem);
		}
		return clone;
	}
}
