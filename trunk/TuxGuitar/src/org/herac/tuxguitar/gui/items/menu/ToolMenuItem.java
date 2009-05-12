/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.settings.EditConfigAction;
import org.herac.tuxguitar.gui.actions.settings.EditKeyBindingsAction;
import org.herac.tuxguitar.gui.actions.settings.EditPluginsAction;
import org.herac.tuxguitar.gui.actions.tools.ScaleAction;
import org.herac.tuxguitar.gui.actions.tools.TGBrowserAction;
import org.herac.tuxguitar.gui.actions.tools.TransposeAction;
import org.herac.tuxguitar.gui.items.MenuItems;
import org.herac.tuxguitar.gui.tools.custom.TGCustomTool;
import org.herac.tuxguitar.gui.tools.custom.TGCustomToolManager;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ToolMenuItem extends MenuItems {
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
		this.transpose.addSelectionListener(TuxGuitar.instance().getAction(TransposeAction.NAME));
		
		this.scale = new MenuItem(this.menu, SWT.PUSH);
		this.scale.addSelectionListener(TuxGuitar.instance().getAction(ScaleAction.NAME));
		
		this.browser = new MenuItem(this.menu, SWT.PUSH);
		this.browser.addSelectionListener(TuxGuitar.instance().getAction(TGBrowserAction.NAME));
		
		Iterator it = TGCustomToolManager.instance().getCustomTools();
		while(it.hasNext()){
			TGCustomTool tool = (TGCustomTool)it.next();
			MenuItem menuItem = new MenuItem(this.menu, SWT.PUSH);
			menuItem.setText(tool.getName());
			menuItem.addSelectionListener(TuxGuitar.instance().getAction(tool.getAction()));
		}
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--PLUGINS--
		this.plugins = new MenuItem(this.menu, SWT.PUSH);
		this.plugins.addSelectionListener(TuxGuitar.instance().getAction(EditPluginsAction.NAME));
		
		//--KEY BINDINGS--
		this.keyBindings = new MenuItem(this.menu, SWT.PUSH);
		this.keyBindings.addSelectionListener(TuxGuitar.instance().getAction(EditKeyBindingsAction.NAME));
		
		//--CONFIG--
		this.config = new MenuItem(this.menu, SWT.PUSH);
		this.config.addSelectionListener(TuxGuitar.instance().getAction(EditConfigAction.NAME));
		
		this.settingsMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.settingsMenuItem, "tools", null);
		setMenuItemTextAndAccelerator(this.transpose, "tools.transpose", TransposeAction.NAME);
		setMenuItemTextAndAccelerator(this.scale, "tools.scale", ScaleAction.NAME);
		setMenuItemTextAndAccelerator(this.browser, "tools.browser", TGBrowserAction.NAME);
		setMenuItemTextAndAccelerator(this.plugins, "tools.plugins", EditPluginsAction.NAME);
		setMenuItemTextAndAccelerator(this.keyBindings, "tools.shortcuts", EditKeyBindingsAction.NAME);
		setMenuItemTextAndAccelerator(this.config, "tools.settings", EditConfigAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}
	
	public void update(){
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		this.transpose.setEnabled( !running );
	}
}