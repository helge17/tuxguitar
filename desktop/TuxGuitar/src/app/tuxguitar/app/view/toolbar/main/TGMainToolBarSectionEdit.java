package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionEdit extends TGMainToolBarSection {

	private UIToolActionItem undo;
	private UIToolActionItem redo;

	public TGMainToolBarSectionEdit(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}

	public void createSection() {
		this.undo = this.getToolBar().createActionItem();
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));

		this.redo = this.getToolBar().createActionItem();
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void updateItems() {
		boolean running = MidiPlayer.getInstance(this.getContext()).isRunning();
		TGUndoableManager undoableManager = TGUndoableManager.getInstance(this.getContext());

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
