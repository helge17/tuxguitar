/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditMenuItem extends TGMenuItem{
	
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
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));
		//--REDO--
		this.redo = new MenuItem(this.menu, SWT.PUSH);
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--TABLATURE EDIT MODE
		this.modeSelection = new MenuItem(this.menu, SWT.RADIO);
		this.modeSelection.addSelectionListener(this.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		//--SCORE EDIT MODE
		this.modeEdition = new MenuItem(this.menu, SWT.RADIO);
		this.modeEdition.addSelectionListener(this.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		//--NATURAL NOTES
		this.notNaturalKey = new MenuItem(this.menu, SWT.CHECK);
		this.notNaturalKey.addSelectionListener(this.createActionProcessor(TGSetNaturalKeyAction.NAME));
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--VOICE 1
		this.voice1 = new MenuItem(this.menu, SWT.RADIO);
		this.voice1.addSelectionListener(this.createActionProcessor(TGSetVoice1Action.NAME));
		//--VOICE 2
		this.voice2 = new MenuItem(this.menu, SWT.RADIO);
		this.voice2.addSelectionListener(this.createActionProcessor(TGSetVoice2Action.NAME));
		
		this.editMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		EditorKit kit = TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.undo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canUndo());
		this.redo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canRedo());
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
		setMenuItemTextAndAccelerator(this.undo, "edit.undo", TGUndoAction.NAME);
		setMenuItemTextAndAccelerator(this.redo, "edit.redo", TGRedoAction.NAME);
		setMenuItemTextAndAccelerator(this.modeSelection, "edit.mouse-mode-selection", TGSetMouseModeSelectionAction.NAME);
		setMenuItemTextAndAccelerator(this.modeEdition, "edit.mouse-mode-edition", TGSetMouseModeEditionAction.NAME);
		setMenuItemTextAndAccelerator(this.notNaturalKey, "edit.not-natural-key", TGSetNaturalKeyAction.NAME);
		setMenuItemTextAndAccelerator(this.voice1, "edit.voice-1", TGSetVoice1Action.NAME);
		setMenuItemTextAndAccelerator(this.voice2, "edit.voice-2", TGSetVoice2Action.NAME);
	}
	
	public void loadIcons(){
		this.undo.setImage(TuxGuitar.getInstance().getIconManager().getEditUndo());
		this.redo.setImage(TuxGuitar.getInstance().getIconManager().getEditRedo());
	}
}
