package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class TGMainToolBarSectionEdit extends TGMainToolBarSection {
	
	private UIToolActionItem undo;
	private UIToolActionItem redo;
	
	public TGMainToolBarSectionEdit(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.undo = this.getToolBar().getControl().createActionItem();
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));
		
		this.redo = this.getToolBar().getControl().createActionItem();
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void updateItems() {
		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		TGUndoableManager undoableManager = TGUndoableManager.getInstance(this.getToolBar().getContext());
		
		this.undo.setEnabled(!running && undoableManager.canUndo());
		this.redo.setEnabled(!running && undoableManager.canRedo());
	}
	
	public void loadProperties() {
		this.undo.setToolTipText(this.getText("edit.undo"));
		this.redo.setToolTipText(this.getText("edit.redo"));
	}
	
	public void loadIcons() {
		this.undo.setImage(this.getIconManager().getEditUndo());
		this.redo.setImage(this.getIconManager().getEditRedo());
	}
}
