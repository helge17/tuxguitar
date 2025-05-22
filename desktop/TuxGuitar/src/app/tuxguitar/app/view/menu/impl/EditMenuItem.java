package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.edit.TGCutAction;
import app.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import app.tuxguitar.app.action.impl.edit.TGCopyAction;
import app.tuxguitar.app.action.impl.edit.TGPasteAction;
import app.tuxguitar.app.action.impl.edit.TGRepeatAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import app.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import app.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.app.action.impl.edit.TGToggleFreeEditionModeAction;
import app.tuxguitar.app.action.impl.selector.TGClearSelectionAction;
import app.tuxguitar.app.action.impl.selector.TGExtendSelectionFirstAction;
import app.tuxguitar.app.action.impl.selector.TGExtendSelectionLastAction;
import app.tuxguitar.app.action.impl.selector.TGExtendSelectionLeftAction;
import app.tuxguitar.app.action.impl.selector.TGExtendSelectionNextAction;
import app.tuxguitar.app.action.impl.selector.TGExtendSelectionPreviousAction;
import app.tuxguitar.app.action.impl.selector.TGExtendSelectionRightAction;
import app.tuxguitar.app.action.impl.selector.TGSelectAllAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class EditMenuItem extends TGMenuItem{

	private UIMenuSubMenuItem editMenuItem;
	private UIMenuActionItem cut;
	private UIMenuActionItem copy;
	private UIMenuActionItem paste;
	private UIMenuActionItem repeat;
	private UIMenuActionItem undo;
	private UIMenuActionItem redo;
	private UIMenuActionItem selectAll;
	private UIMenuActionItem selectNone;
	private UIMenuSubMenuItem extendSelection;
	private UIMenuActionItem extendSelectionLeft;
	private UIMenuActionItem extendSelectionRight;
	private UIMenuActionItem extendSelectionPrevious;
	private UIMenuActionItem extendSelectionNext;
	private UIMenuActionItem extendSelectionFirst;
	private UIMenuActionItem extendSelectionLast;
	private UIMenuCheckableItem modeSelection;
	private UIMenuCheckableItem modeEdition;
	private UIMenuCheckableItem notNaturalKey;
	private UIMenuCheckableItem voice1;
	private UIMenuCheckableItem voice2;
	private UIMenuCheckableItem freeEditionMode;
	private UIMenuActionItem openMeasureErrorsDialog;

	public EditMenuItem(UIMenu parent) {
		this.editMenuItem = parent.createSubMenuItem();
	}

	public void showItems() {
		//--CUT--
		this.cut = this.editMenuItem.getMenu().createActionItem();
		this.cut.addSelectionListener(this.createActionProcessor(TGCutAction.NAME));

		//--COPY--
		this.copy = this.editMenuItem.getMenu().createActionItem();
		this.copy.addSelectionListener(this.createActionProcessor(TGCopyAction.NAME));

		//--PASTE--
		this.paste = this.editMenuItem.getMenu().createActionItem();
		this.paste.addSelectionListener(this.createActionProcessor(TGPasteAction.NAME));

		//--REPEAT--
		this.repeat = this.editMenuItem.getMenu().createActionItem();
		this.repeat.addSelectionListener(this.createActionProcessor(TGRepeatAction.NAME));

		//--SEPARATOR--
		this.editMenuItem.getMenu().createSeparator();

		//--UNDO--
		this.undo = this.editMenuItem.getMenu().createActionItem();
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));

		//--REDO--
		this.redo = this.editMenuItem.getMenu().createActionItem();
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));

		//--SEPARATOR--
		this.editMenuItem.getMenu().createSeparator();

		//--Selection--
		this.selectAll = this.editMenuItem.getMenu().createActionItem();
		this.selectAll.addSelectionListener(this.createActionProcessor(TGSelectAllAction.NAME));

		this.selectNone = this.editMenuItem.getMenu().createActionItem();
		this.selectNone.addSelectionListener(this.createActionProcessor(TGClearSelectionAction.NAME));

		this.extendSelection = this.editMenuItem.getMenu().createSubMenuItem();
		this.extendSelectionLeft = this.extendSelection.getMenu().createActionItem();
		this.extendSelectionLeft.addSelectionListener(this.createActionProcessor(TGExtendSelectionLeftAction.NAME));
		this.extendSelectionRight = this.extendSelection.getMenu().createActionItem();
		this.extendSelectionRight.addSelectionListener(this.createActionProcessor(TGExtendSelectionRightAction.NAME));
		this.extendSelectionPrevious = this.extendSelection.getMenu().createActionItem();
		this.extendSelectionPrevious.addSelectionListener(this.createActionProcessor(TGExtendSelectionPreviousAction.NAME));
		this.extendSelectionNext = this.extendSelection.getMenu().createActionItem();
		this.extendSelectionNext.addSelectionListener(this.createActionProcessor(TGExtendSelectionNextAction.NAME));
		this.extendSelectionFirst = this.extendSelection.getMenu().createActionItem();
		this.extendSelectionFirst.addSelectionListener(this.createActionProcessor(TGExtendSelectionFirstAction.NAME));
		this.extendSelectionLast = this.extendSelection.getMenu().createActionItem();
		this.extendSelectionLast.addSelectionListener(this.createActionProcessor(TGExtendSelectionLastAction.NAME));

		//--SEPARATOR--
		this.editMenuItem.getMenu().createSeparator();

		//--TABLATURE EDIT MODE--
		this.modeSelection = this.editMenuItem.getMenu().createRadioItem();
		this.modeSelection.addSelectionListener(this.createActionProcessor(TGSetMouseModeSelectionAction.NAME));

		//--SCORE EDIT MODE--
		this.modeEdition = this.editMenuItem.getMenu().createRadioItem();
		this.modeEdition.addSelectionListener(this.createActionProcessor(TGSetMouseModeEditionAction.NAME));

		//--NATURAL NOTES--
		this.notNaturalKey = this.editMenuItem.getMenu().createCheckItem();
		this.notNaturalKey.addSelectionListener(this.createActionProcessor(TGSetNaturalKeyAction.NAME));

		// -- FREE EDITION MODE --
		this.freeEditionMode = this.editMenuItem.getMenu().createCheckItem();
		this.freeEditionMode.addSelectionListener(this.createActionProcessor(TGToggleFreeEditionModeAction.NAME));
		this.openMeasureErrorsDialog = this.editMenuItem.getMenu().createActionItem();
		this.openMeasureErrorsDialog.addSelectionListener(this.createActionProcessor(TGOpenMeasureErrorsDialogAction.NAME));

		//--SEPARATOR--
		this.editMenuItem.getMenu().createSeparator();

		//--VOICE 1--
		this.voice1 = this.editMenuItem.getMenu().createRadioItem();
		this.voice1.addSelectionListener(this.createActionProcessor(TGSetVoice1Action.NAME));

		//--VOICE 2--
		this.voice2 = this.editMenuItem.getMenu().createRadioItem();
		this.voice2.addSelectionListener(this.createActionProcessor(TGSetVoice2Action.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		EditorKit kit = TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit();
		Tablature tablature = TablatureEditor.getInstance(findContext()).getTablature();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.cut.setEnabled(!running && tablature.getSelector().isActive());
		this.copy.setEnabled(!running);
		this.paste.setEnabled(!running && TGClipboard.getInstance(findContext()).hasContents());
		this.repeat.setEnabled(!running && tablature.getSelector().isActive());
		this.undo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canUndo());
		this.redo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canRedo());
		this.selectAll.setEnabled(!running && !TuxGuitar.getInstance().getSongManager().getTrackManager().hasMeasureDurationError(tablature.getCaret().getTrack()));
		this.selectNone.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelection.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelectionLeft.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelectionRight.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelectionPrevious.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelectionNext.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelectionFirst.setEnabled(!running && tablature.getSelector().isActive());
		this.extendSelectionLast.setEnabled(!running && tablature.getSelector().isActive());
		this.modeSelection.setChecked(kit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION);
		this.modeSelection.setEnabled(!running);
		this.modeEdition.setChecked(kit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.modeEdition.setEnabled(!running);
		this.notNaturalKey.setChecked(!kit.isNatural());
		this.notNaturalKey.setEnabled(!running && kit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.voice1.setChecked(kit.getTablature().getCaret().getVoice() == 0);
		this.voice2.setChecked(kit.getTablature().getCaret().getVoice() == 1);
		this.freeEditionMode.setChecked(tablature.getSongManager().isFreeEditionMode(tablature.getCaret().getMeasure()));
		this.freeEditionMode.setEnabled(!running);
		this.openMeasureErrorsDialog.setEnabled(!running);
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.editMenuItem, "edit.menu", null);
		setMenuItemTextAndAccelerator(this.cut, "edit.cut", TGCutAction.NAME);
		setMenuItemTextAndAccelerator(this.copy, "edit.copy", TGCopyAction.NAME);
		setMenuItemTextAndAccelerator(this.paste, "edit.paste", TGPasteAction.NAME);
		setMenuItemTextAndAccelerator(this.repeat, "edit.repeat", TGRepeatAction.NAME);
		setMenuItemTextAndAccelerator(this.undo, "edit.undo", TGUndoAction.NAME);
		setMenuItemTextAndAccelerator(this.redo, "edit.redo", TGRedoAction.NAME);
		setMenuItemTextAndAccelerator(this.selectAll, "action.selection.select-all", TGSelectAllAction.NAME);
		setMenuItemTextAndAccelerator(this.selectNone, "action.selection.clear", TGClearSelectionAction.NAME);
		setMenuItemTextAndAccelerator(this.extendSelection, "edit.selection.extend", null);
		setMenuItemTextAndAccelerator(this.extendSelectionLeft, "action.selection.extend-left", TGExtendSelectionLeftAction.NAME);
		setMenuItemTextAndAccelerator(this.extendSelectionRight, "action.selection.extend-right", TGExtendSelectionRightAction.NAME);
		setMenuItemTextAndAccelerator(this.extendSelectionPrevious, "action.selection.extend-previous", TGExtendSelectionPreviousAction.NAME);
		setMenuItemTextAndAccelerator(this.extendSelectionNext, "action.selection.extend-next", TGExtendSelectionNextAction.NAME);
		setMenuItemTextAndAccelerator(this.extendSelectionFirst, "action.selection.extend-first", TGExtendSelectionFirstAction.NAME);
		setMenuItemTextAndAccelerator(this.extendSelectionLast, "action.selection.extend-last", TGExtendSelectionLastAction.NAME);
		setMenuItemTextAndAccelerator(this.modeSelection, "edit.mouse-mode-selection", TGSetMouseModeSelectionAction.NAME);
		setMenuItemTextAndAccelerator(this.modeEdition, "edit.mouse-mode-edition", TGSetMouseModeEditionAction.NAME);
		setMenuItemTextAndAccelerator(this.notNaturalKey, "edit.not-natural-key", TGSetNaturalKeyAction.NAME);
		setMenuItemTextAndAccelerator(this.voice1, "edit.voice-1", TGSetVoice1Action.NAME);
		setMenuItemTextAndAccelerator(this.voice2, "edit.voice-2", TGSetVoice2Action.NAME);
		setMenuItemTextAndAccelerator(this.freeEditionMode, "edit.free-edition-mode", TGToggleFreeEditionModeAction.NAME);
		setMenuItemTextAndAccelerator(this.openMeasureErrorsDialog, "edit.measure-errors-dialog", TGOpenMeasureErrorsDialogAction.NAME);
	}

	public void loadIcons(){
		this.cut.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_CUT));
		this.copy.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_COPY));
		this.paste.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_PASTE));
		this.repeat.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_REPEAT));
		this.undo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.UNDO));
		this.redo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.REDO));
		this.modeSelection.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_MODE_SELECTION));
		this.modeEdition.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_MODE_EDITION));
		this.notNaturalKey.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_MODE_EDITION_NO_NATURAL));
		this.voice1.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_VOICE_1));
		this.voice2.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_VOICE_2));
		this.freeEditionMode.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_MODE_FREE));
		this.openMeasureErrorsDialog.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EDIT_MEASURE_STATUS_CHECK));
	}
}
