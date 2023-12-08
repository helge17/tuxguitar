package org.herac.tuxguitar.app.view.menu.impl;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import org.herac.tuxguitar.app.tools.custom.TGCustomTool;
import org.herac.tuxguitar.app.tools.custom.TGCustomToolManager;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

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
		this.plugins.setImage(TuxGuitar.getInstance().getIconManager().getToolsPlugins());
		this.keyBindings.setImage(TuxGuitar.getInstance().getIconManager().getToolsShortcuts());
		this.config.setImage(TuxGuitar.getInstance().getIconManager().getToolsSettings());
	}

	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.transpose.setEnabled( !running );
	}
}
