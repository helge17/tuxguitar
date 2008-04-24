/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.edit.RedoAction;
import org.herac.tuxguitar.gui.actions.edit.SetMouseModeEditionAction;
import org.herac.tuxguitar.gui.actions.edit.SetMouseModeSelectionAction;
import org.herac.tuxguitar.gui.actions.edit.SetNaturalKeyAction;
import org.herac.tuxguitar.gui.actions.edit.UndoAction;
import org.herac.tuxguitar.gui.editors.tab.edit.EditorKit;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditMenuItem implements MenuItems{
	
	private MenuItem editMenuItem;
	private Menu menu; 
	private MenuItem undo;
	private MenuItem redo;
	private MenuItem modeSelection;
	private MenuItem modeEdition;
	private MenuItem notNaturalKey;
	
	public EditMenuItem(Shell shell,Menu parent, int style) {
		this.editMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//---------------------------------------------------
		//--UNDO--
		this.undo = new MenuItem(this.menu, SWT.PUSH);
		this.undo.addSelectionListener(TuxGuitar.instance().getAction(UndoAction.NAME));
		//--REDO--
		this.redo = new MenuItem(this.menu, SWT.PUSH);
		this.redo.addSelectionListener(TuxGuitar.instance().getAction(RedoAction.NAME));
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--TABLATURE EDIT MODE
		this.modeSelection = new MenuItem(this.menu, SWT.RADIO);
		this.modeSelection.addSelectionListener(TuxGuitar.instance().getAction(SetMouseModeSelectionAction.NAME));
		//--SCORE EDIT MODE
		this.modeEdition = new MenuItem(this.menu, SWT.RADIO);
		this.modeEdition.addSelectionListener(TuxGuitar.instance().getAction(SetMouseModeEditionAction.NAME));
		//--NATURAL NOTES
		this.notNaturalKey = new MenuItem(this.menu, SWT.CHECK);
		this.notNaturalKey.addSelectionListener(TuxGuitar.instance().getAction(SetNaturalKeyAction.NAME));
		
		this.editMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		EditorKit kit = TuxGuitar.instance().getTablatureEditor().getTablature().getEditorKit();
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		this.undo.setEnabled(!running && TuxGuitar.instance().getUndoableManager().canUndo());
		this.redo.setEnabled(!running && TuxGuitar.instance().getUndoableManager().canRedo());
		this.modeSelection.setSelection(kit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION);
		this.modeSelection.setEnabled(!running);
		this.modeEdition.setSelection(kit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.modeEdition.setEnabled(!running);
		this.notNaturalKey.setSelection(!kit.isNatural());
		this.notNaturalKey.setEnabled(!running && kit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
	}
	
	public void loadProperties(){
		this.editMenuItem.setText(TuxGuitar.getProperty("edit.menu"));
		this.undo.setText(TuxGuitar.getProperty("edit.undo"));
		this.redo.setText(TuxGuitar.getProperty("edit.redo"));
		this.modeSelection.setText(TuxGuitar.getProperty("edit.mouse-mode-selection"));
		this.modeEdition.setText(TuxGuitar.getProperty("edit.mouse-mode-edition"));
		this.notNaturalKey.setText(TuxGuitar.getProperty("edit.not-natural-key"));
	}
	
	public void loadIcons(){
		this.undo.setImage(TuxGuitar.instance().getIconManager().getEditUndo());
		this.redo.setImage(TuxGuitar.instance().getIconManager().getEditRedo());
	}
}
