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
import org.herac.tuxguitar.gui.actions.edit.SetVoice1Action;
import org.herac.tuxguitar.gui.actions.edit.SetVoice2Action;
import org.herac.tuxguitar.gui.actions.edit.UndoAction;
import org.herac.tuxguitar.gui.editors.tab.edit.EditorKit;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditMenuItem extends MenuItems{
	
	private MenuItem editMenuItem;
	private Menu menu; 
	private MenuItem undo;
	private MenuItem redo;
	private MenuItem modeSelection;
	private MenuItem modeEdition;
	private MenuItem notNaturalKey;
	private MenuItem voice1;
	private MenuItem voice2;
	
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
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--VOICE 1
		this.voice1 = new MenuItem(this.menu, SWT.RADIO);
		this.voice1.addSelectionListener(TuxGuitar.instance().getAction(SetVoice1Action.NAME));
		//--VOICE 2
		this.voice2 = new MenuItem(this.menu, SWT.RADIO);
		this.voice2.addSelectionListener(TuxGuitar.instance().getAction(SetVoice2Action.NAME));
		
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
		this.voice1.setSelection(kit.getTablature().getCaret().getVoice() == 0);
		this.voice2.setSelection(kit.getTablature().getCaret().getVoice() == 1);
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.editMenuItem, "edit.menu", null);
		setMenuItemTextAndAccelerator(this.undo, "edit.undo", UndoAction.NAME);
		setMenuItemTextAndAccelerator(this.redo, "edit.redo", RedoAction.NAME);
		setMenuItemTextAndAccelerator(this.modeSelection, "edit.mouse-mode-selection", SetMouseModeSelectionAction.NAME);
		setMenuItemTextAndAccelerator(this.modeEdition, "edit.mouse-mode-edition", SetMouseModeEditionAction.NAME);
		setMenuItemTextAndAccelerator(this.notNaturalKey, "edit.not-natural-key", SetNaturalKeyAction.NAME);
		setMenuItemTextAndAccelerator(this.voice1, "edit.voice-1", SetVoice1Action.NAME);
		setMenuItemTextAndAccelerator(this.voice2, "edit.voice-2", SetVoice2Action.NAME);
	}
	
	public void loadIcons(){
		this.undo.setImage(TuxGuitar.instance().getIconManager().getEditUndo());
		this.redo.setImage(TuxGuitar.instance().getIconManager().getEditRedo());
	}
}
