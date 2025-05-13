package app.tuxguitar.app.view.menu.impl;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import app.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import app.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import app.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import app.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.tools.custom.TGCustomTool;
import app.tuxguitar.app.tools.custom.TGCustomToolManager;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class ToolMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem settingsMenuItem;
	private UIMenuActionItem scale;
	private UIMenuActionItem browser;
	private UIMenuActionItem transpose;
	private UIMenuActionItem plugins;
	private UIMenuActionItem config;
	private UIMenuActionItem keyBindings;
	private HashMap<UIMenuActionItem, String> pluginsMap = new HashMap<UIMenuActionItem, String>();	// plugins menu items and their names

	public ToolMenuItem(UIMenu parent) {
		this.settingsMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		//--TRANSPOSE--
		this.transpose = this.settingsMenuItem.getMenu().createActionItem();
		this.transpose.addSelectionListener(this.createActionProcessor(TGOpenTransposeDialogAction.NAME));

		//--SCALE--
		this.scale = this.settingsMenuItem.getMenu().createActionItem();
		this.scale.addSelectionListener(this.createActionProcessor(TGOpenScaleDialogAction.NAME));

		//--BROWSER--
		this.browser = this.settingsMenuItem.getMenu().createActionItem();
		this.browser.addSelectionListener(this.createActionProcessor(TGToggleBrowserAction.NAME));

		//--CUSTOM TOOLS--
		Iterator<TGCustomTool> it = TGCustomToolManager.instance().getCustomTools();
		while(it.hasNext()){
			TGCustomTool tool = (TGCustomTool)it.next();
			UIMenuActionItem uiMenuItem = this.settingsMenuItem.getMenu().createActionItem();
			pluginsMap.put(uiMenuItem, tool.getName());
			uiMenuItem.addSelectionListener(this.createActionProcessor(tool.getAction()));
		}

		//--SEPARATOR--
		this.settingsMenuItem.getMenu().createSeparator();

		//--PLUGINS--
		this.plugins = this.settingsMenuItem.getMenu().createActionItem();
		this.plugins.addSelectionListener(this.createActionProcessor(TGOpenPluginListDialogAction.NAME));

		//--KEY BINDINGS--
		this.keyBindings = this.settingsMenuItem.getMenu().createActionItem();
		this.keyBindings.addSelectionListener(this.createActionProcessor(TGOpenKeyBindingEditorAction.NAME));

		//--CONFIG--
		this.config = this.settingsMenuItem.getMenu().createActionItem();
		this.config.addSelectionListener(this.createActionProcessor(TGOpenSettingsEditorAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.settingsMenuItem, "tools", null);
		setMenuItemTextAndAccelerator(this.transpose, "tools.transpose", TGOpenTransposeDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.scale, "tools.scale", TGOpenScaleDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.browser, "tools.browser", TGToggleBrowserAction.NAME);
		setMenuItemTextAndAccelerator(this.plugins, "tools.plugins", TGOpenPluginListDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.keyBindings, "tools.shortcuts", TGOpenKeyBindingEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.config, "tools.settings", TGOpenSettingsEditorAction.NAME);
		// update labels for plugins menu items
		for (Map.Entry<UIMenuActionItem, String> mapItem : pluginsMap.entrySet()) {
			mapItem.getKey().setText(TuxGuitar.getProperty(mapItem.getValue()));
		}
	}

	public void loadIcons(){
		this.browser.setImage(TuxGuitar.getInstance().getIconManager().getBrowserCollection());
		this.plugins.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TOOLS_PLUGINS));
		this.keyBindings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TOOLS_SHORTCUTS));
		this.config.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TOOLS_SETTINGS));
	}

	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.transpose.setEnabled( !running );
	}
}
