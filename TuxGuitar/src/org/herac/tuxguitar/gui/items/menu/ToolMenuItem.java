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
import org.herac.tuxguitar.gui.items.MenuItems;
import org.herac.tuxguitar.gui.tools.custom.TGCustomTool;
import org.herac.tuxguitar.gui.tools.custom.TGCustomToolManager;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ToolMenuItem implements MenuItems {
    private MenuItem settingsMenuItem;
    private Menu menu;
    private MenuItem scale;
    private MenuItem browser;
    private MenuItem plugins;
    private MenuItem config;
    private MenuItem keyBindings;
    
    public ToolMenuItem(Shell shell,Menu parent, int style) {
        this.settingsMenuItem = new MenuItem(parent, style);
        this.menu = new Menu(shell, SWT.DROP_DOWN);
    }

    
    public void showItems(){  
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
        this.settingsMenuItem.setText(TuxGuitar.getProperty("tools"));
        this.scale.setText(TuxGuitar.getProperty("tools.scale"));
        this.browser.setText(TuxGuitar.getProperty("tools.browser"));
        this.plugins.setText(TuxGuitar.getProperty("tools.plugins"));
        this.keyBindings.setText(TuxGuitar.getProperty("tools.shortcuts"));
        this.config.setText(TuxGuitar.getProperty("tools.settings"));
    }         
    
    public void loadIcons(){	
    	//Nothing to do
    }
    
    public void update(){
    	//Nothing to do
    }    
}