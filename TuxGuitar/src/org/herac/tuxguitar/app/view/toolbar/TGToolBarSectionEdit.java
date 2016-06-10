package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionEdit implements TGToolBarSection {
	
	private UIToolActionItem undo;
	private UIToolActionItem redo;
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem voice1;
	private UIMenuActionItem voice2;
	private UIMenuActionItem modeSelection;
	private UIMenuActionItem modeEdition;
	private UIMenuActionItem notNaturalKey;
	
	public void createSection(final TGToolBar toolBar) {
		this.undo = toolBar.getControl().createActionItem();
		this.undo.addSelectionListener(toolBar.createActionProcessor(TGUndoAction.NAME));
		
		this.redo = toolBar.getControl().createActionItem();
		this.redo.addSelectionListener(toolBar.createActionProcessor(TGRedoAction.NAME));
		
		this.menuItem = toolBar.getControl().createMenuItem();
		
		this.voice1 = this.menuItem.getMenu().createActionItem();
		this.voice1.addSelectionListener(toolBar.createActionProcessor(TGSetVoice1Action.NAME));
		
		this.voice2 = this.menuItem.getMenu().createActionItem();
		this.voice2.addSelectionListener(toolBar.createActionProcessor(TGSetVoice2Action.NAME));
		
		this.menuItem.getMenu().createSeparator();
		
		this.modeSelection = this.menuItem.getMenu().createActionItem();
		this.modeSelection.addSelectionListener(toolBar.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		
		this.modeEdition = this.menuItem.getMenu().createActionItem();
		this.modeEdition.addSelectionListener(toolBar.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		
		this.notNaturalKey = this.menuItem.getMenu().createActionItem();
		this.notNaturalKey.addSelectionListener(toolBar.createActionProcessor(TGSetNaturalKeyAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void updateItems(TGToolBar toolBar){
		TablatureEditor editor = TablatureEditor.getInstance(toolBar.getContext());
		EditorKit editorKit = editor.getTablature().getEditorKit();
		
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		TGUndoableManager undoableManager = TGUndoableManager.getInstance(toolBar.getContext());
		
		this.undo.setEnabled(!running && undoableManager.canUndo());
		this.redo.setEnabled(!running && undoableManager.canRedo());
		
		this.voice1.setText(toolBar.getText("edit.voice-1", (editor.getTablature().getCaret().getVoice() == 0)));
		this.voice1.setEnabled(!running);
		
		this.voice2.setText(toolBar.getText("edit.voice-2", (editor.getTablature().getCaret().getVoice() == 1)));
		this.voice2.setEnabled(!running);
		
		this.modeSelection.setText(toolBar.getText("edit.mouse-mode-selection", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION)));
		this.modeSelection.setEnabled(!running);
		
		this.modeEdition.setText(toolBar.getText("edit.mouse-mode-edition", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION)));
		this.modeEdition.setEnabled(!running);
		
		this.notNaturalKey.setText(toolBar.getText("edit.not-natural-key", (!editorKit.isNatural())));
		this.notNaturalKey.setEnabled(!running && editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
	}
	
	public void loadProperties(TGToolBar toolBar){
		TablatureEditor editor = TablatureEditor.getInstance(toolBar.getContext());
		EditorKit editorKit = editor.getTablature().getEditorKit();
		
		this.undo.setToolTipText(toolBar.getText("edit.undo"));
		this.redo.setToolTipText(toolBar.getText("edit.redo"));
		this.menuItem.setToolTipText(toolBar.getText("edit"));
		
		this.voice1.setText(toolBar.getText("edit.voice-1", (editor.getTablature().getCaret().getVoice() == 0)));
		this.voice2.setText(toolBar.getText("edit.voice-2", (editor.getTablature().getCaret().getVoice() == 1)));
		this.modeSelection.setText(toolBar.getText("edit.mouse-mode-selection", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION)));
		this.modeEdition.setText(toolBar.getText("edit.mouse-mode-edition", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION)));
		this.notNaturalKey.setText(toolBar.getText("edit.not-natural-key", (!editorKit.isNatural())));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.undo.setImage(toolBar.getIconManager().getEditUndo());
		this.redo.setImage(toolBar.getIconManager().getEditRedo());
		this.menuItem.setImage(toolBar.getIconManager().getEditModeEdition());
		this.voice1.setImage(toolBar.getIconManager().getEditVoice1());
		this.voice2.setImage(toolBar.getIconManager().getEditVoice2());
		this.modeSelection.setImage(toolBar.getIconManager().getEditModeSelection());
		this.modeEdition.setImage(toolBar.getIconManager().getEditModeEdition());
		this.notNaturalKey.setImage(toolBar.getIconManager().getEditModeEditionNotNatural());
	}
}
