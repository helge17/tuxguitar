/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.items.ToolItems;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
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
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));
		
		this.redo = new ToolItem(toolBar, SWT.PUSH);
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		this.voice1 = new ToolItem(toolBar, SWT.RADIO);
		this.voice1.addSelectionListener(this.createActionProcessor(TGSetVoice1Action.NAME));
		
		this.voice2 = new ToolItem(toolBar, SWT.RADIO);
		this.voice2.addSelectionListener(this.createActionProcessor(TGSetVoice2Action.NAME));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		this.modeSelection = new ToolItem(toolBar, SWT.RADIO);
		this.modeSelection.addSelectionListener(this.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		
		this.modeEdition = new ToolItem(toolBar, SWT.RADIO);
		this.modeEdition.addSelectionListener(this.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		
		this.notNaturalKey = new ToolItem(toolBar, SWT.CHECK);
		this.notNaturalKey.addSelectionListener(this.createActionProcessor(TGSetNaturalKeyAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.undo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canUndo());
		this.redo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canRedo());
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
		this.undo.setImage(TuxGuitar.getInstance().getIconManager().getEditUndo());
		this.redo.setImage(TuxGuitar.getInstance().getIconManager().getEditRedo());
		this.voice1.setImage(TuxGuitar.getInstance().getIconManager().getEditVoice1());
		this.voice2.setImage(TuxGuitar.getInstance().getIconManager().getEditVoice2());
		this.modeSelection.setImage(TuxGuitar.getInstance().getIconManager().getEditModeSelection());
		this.modeEdition.setImage(TuxGuitar.getInstance().getIconManager().getEditModeEdition());
		this.notNaturalKey.setImage(TuxGuitar.getInstance().getIconManager().getEditModeEditionNotNatural());
	}
}
