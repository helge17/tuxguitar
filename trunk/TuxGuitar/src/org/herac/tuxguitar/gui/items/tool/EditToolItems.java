/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.edit.RedoAction;
import org.herac.tuxguitar.gui.actions.edit.SetMouseModeEditionAction;
import org.herac.tuxguitar.gui.actions.edit.SetMouseModeSelectionAction;
import org.herac.tuxguitar.gui.actions.edit.SetNaturalKeyAction;
import org.herac.tuxguitar.gui.actions.edit.SetVoice1Action;
import org.herac.tuxguitar.gui.actions.edit.SetVoice2Action;
import org.herac.tuxguitar.gui.actions.edit.UndoAction;
import org.herac.tuxguitar.gui.editors.tab.edit.EditorKit;
import org.herac.tuxguitar.gui.items.ToolItems;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditToolItems extends ToolItems {
	public static final String NAME = "edit.items";
	private ToolItem undo;
	private ToolItem redo;
	
	private ToolItem voice1;
	private ToolItem voice2;
	
	private ToolItem modeSelection;
	private ToolItem modeEdition;
	private ToolItem notNaturalKey;
	
	public EditToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.undo = new ToolItem(toolBar, SWT.PUSH);
		this.undo.addSelectionListener(TuxGuitar.instance().getAction(UndoAction.NAME));
		
		this.redo = new ToolItem(toolBar, SWT.PUSH);
		this.redo.addSelectionListener(TuxGuitar.instance().getAction(RedoAction.NAME));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		this.voice1 = new ToolItem(toolBar, SWT.RADIO);
		this.voice1.addSelectionListener(TuxGuitar.instance().getAction(SetVoice1Action.NAME));
		
		this.voice2 = new ToolItem(toolBar, SWT.RADIO);
		this.voice2.addSelectionListener(TuxGuitar.instance().getAction(SetVoice2Action.NAME));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		this.modeSelection = new ToolItem(toolBar, SWT.RADIO);
		this.modeSelection.addSelectionListener(TuxGuitar.instance().getAction(SetMouseModeSelectionAction.NAME));
		
		this.modeEdition = new ToolItem(toolBar, SWT.RADIO);
		this.modeEdition.addSelectionListener(TuxGuitar.instance().getAction(SetMouseModeEditionAction.NAME));
		
		this.notNaturalKey = new ToolItem(toolBar, SWT.CHECK);
		this.notNaturalKey.addSelectionListener(TuxGuitar.instance().getAction(SetNaturalKeyAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		this.undo.setEnabled(!running && TuxGuitar.instance().getUndoableManager().canUndo());
		this.redo.setEnabled(!running && TuxGuitar.instance().getUndoableManager().canRedo());
		this.voice1.setSelection(getEditor().getTablature().getCaret().getVoice() == 0);
		this.voice1.setEnabled(!running);
		this.voice2.setSelection(getEditor().getTablature().getCaret().getVoice() == 1);
		this.voice2.setEnabled(!running);
		this.modeSelection.setSelection(getEditor().getTablature().getEditorKit().getMouseMode() == EditorKit.MOUSE_MODE_SELECTION);
		this.modeSelection.setEnabled(!running);
		this.modeEdition.setSelection(getEditor().getTablature().getEditorKit().getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.modeEdition.setEnabled(!running);
		this.notNaturalKey.setSelection(!getEditor().getTablature().getEditorKit().isNatural());
		this.notNaturalKey.setEnabled(!running && getEditor().getTablature().getEditorKit().getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
	}
	
	public void loadProperties(){
		this.undo.setToolTipText(TuxGuitar.getProperty("edit.undo"));
		this.redo.setToolTipText(TuxGuitar.getProperty("edit.redo"));
		this.voice1.setToolTipText(TuxGuitar.getProperty("edit.voice-1"));
		this.voice2.setToolTipText(TuxGuitar.getProperty("edit.voice-2"));
		this.modeSelection.setToolTipText(TuxGuitar.getProperty("edit.mouse-mode-selection"));
		this.modeEdition.setToolTipText(TuxGuitar.getProperty("edit.mouse-mode-edition"));
		this.notNaturalKey.setToolTipText(TuxGuitar.getProperty("edit.not-natural-key"));
	}
	
	public void loadIcons(){
		this.undo.setImage(TuxGuitar.instance().getIconManager().getEditUndo());
		this.redo.setImage(TuxGuitar.instance().getIconManager().getEditRedo());
		this.voice1.setImage(TuxGuitar.instance().getIconManager().getEditVoice1());
		this.voice2.setImage(TuxGuitar.instance().getIconManager().getEditVoice2());
		this.modeSelection.setImage(TuxGuitar.instance().getIconManager().getEditModeSelection());
		this.modeEdition.setImage(TuxGuitar.instance().getIconManager().getEditModeEdition());
		this.notNaturalKey.setImage(TuxGuitar.instance().getIconManager().getEditModeEditionNotNatural());
	}
}
