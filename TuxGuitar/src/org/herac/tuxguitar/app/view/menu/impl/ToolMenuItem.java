/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import org.herac.tuxguitar.app.tools.custom.TGCustomTool;
import org.herac.tuxguitar.app.tools.custom.TGCustomToolManager;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ToolMenuItem extends TGMenuItem {
	private MenuItem settingsMenuItem;
	private Menu menu;
	private MenuItem scale;
	private MenuItem browser;
	private MenuItem transpose;
	private MenuItem plugins;
	private MenuItem config;
	private MenuItem keyBindings;
	
	public ToolMenuItem(Shell shell,Menu parent, int style) {
		this.settingsMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		this.transpose = new MenuItem(this.menu, SWT.PUSH);
		this.transpose.addSelectionListener(this.createActionProcessor(TGOpenTransposeDialogAction.NAME));
		
		this.scale = new MenuItem(this.menu, SWT.PUSH);
		this.scale.addSelectionListener(this.createActionProcessor(TGOpenScaleDialogAction.NAME));
		
		this.browser = new MenuItem(this.menu, SWT.PUSH);
		this.browser.addSelectionListener(this.createActionProcessor(TGToggleBrowserAction.NAME));
		
		Iterator<TGCustomTool> it = TGCustomToolManager.instance().getCustomTools();
		while(it.hasNext()){
			TGCustomTool tool = (TGCustomTool)it.next();
			MenuItem menuItem = new MenuItem(this.menu, SWT.PUSH);
			menuItem.setText(tool.getName());
			menuItem.addSelectionListener(this.createActionProcessor(tool.getAction()));
		}
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--PLUGINS--
		this.plugins = new MenuItem(this.menu, SWT.PUSH);
		this.plugins.addSelectionListener(this.createActionProcessor(TGOpenPluginListDialogAction.NAME));
		
		//--KEY BINDINGS--
		this.keyBindings = new MenuItem(this.menu, SWT.PUSH);
		this.keyBindings.addSelectionListener(this.createActionProcessor(TGOpenKeyBindingEditorAction.NAME));
		
		//--CONFIG--
		this.config = new MenuItem(this.menu, SWT.PUSH);
		this.config.addSelectionListener(this.createActionProcessor(TGOpenSettingsEditorAction.NAME));
		
		this.settingsMenuItem.setMenu(this.menu);
		
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
	}
	
	public void loadIcons(){
		//Nothing to do
	}
	
	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.transpose.setEnabled( !running );
	}
}