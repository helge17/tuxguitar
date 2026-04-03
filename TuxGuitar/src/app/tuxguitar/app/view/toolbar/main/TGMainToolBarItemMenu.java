package app.tuxguitar.app.view.toolbar.main;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolMenuItem;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.util.TGContext;

// a menu button in toolBar opening a list of actions
public class TGMainToolBarItemMenu extends TGMainToolBarItem {

	private UIToolMenuItem menuItem;
	private List<TGMainToolBarItemConfig> toolBarMenuItemConfigs;
	private List<TGMainToolBarItemMenuItem> toolBarMenuItems;
	private boolean displaySelectedItemIcon;

	public TGMainToolBarItemMenu(TGMainToolBarItemConfig config) {
		super(config);
		this.displaySelectedItemIcon = config.displaySelectedItemIcon();
		this.toolBarMenuItemConfigs = config.getSubItemConfigs();
		this.toolBarMenuItems = new ArrayList<TGMainToolBarItemMenuItem>();
	}

	@Override
	public UIControl getControl() {
		// unused, control is stored in toolBar section
		return null;
	}

	public void createMenu(UIToolBar toolBar, TGContext context) {
		this.menuItem = toolBar.createMenuItem();
		for (TGMainToolBarItemConfig toolBarMenuItemConfig : toolBarMenuItemConfigs) {
			if (toolBarMenuItemConfig.getType() == TGMainToolBarItem.SEPARATOR) {
				this.menuItem.getMenu().createSeparator();
			} else {
				TGMainToolBarItemMenuItem toolBarMenuItem = new TGMainToolBarItemMenuItem(toolBarMenuItemConfig);
				UIMenuActionItem menuActionItem = this.menuItem.getMenu().createActionItem();
				TGActionProcessorListener actionProcessorListener = new TGActionProcessorListener(context, toolBarMenuItemConfig.getActionName());
				for (String key : toolBarMenuItemConfig.getAttributes().keySet()) {
					actionProcessorListener.setAttribute(key, toolBarMenuItemConfig.getAttributes().get(key));
				}
				menuActionItem.addSelectionListener(actionProcessorListener);
				toolBarMenuItem.setMenuItem(menuActionItem);
				this.toolBarMenuItems.add(toolBarMenuItem);
			}
		}
	}

	@Override
	public void update(TGContext context, boolean running) {
		for (TGMainToolBarItemMenuItem toolBarMenuItem : toolBarMenuItems) {
			toolBarMenuItem.update(context, running);
			if (this.displaySelectedItemIcon && toolBarMenuItem.isChecked()) {
				this.menuItem.setImage(TGIconManager.getInstance(context).getImageByName(toolBarMenuItem.getIconFileName()));
			}
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

}
