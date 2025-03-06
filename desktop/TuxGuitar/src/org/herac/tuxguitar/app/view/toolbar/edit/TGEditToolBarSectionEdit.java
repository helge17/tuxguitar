package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.action.impl.edit.TGToggleFreeEditionModeAction;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.util.TGContext;

public class TGEditToolBarSectionEdit extends TGEditToolBarSection {

	private static final String SECTION_TITLE = "edit";

	private UIToolCheckableItem modeSelection;
	private UIToolCheckableItem modeEdition;
	private UIToolCheckableItem notNaturalKey;
	private UIToolCheckableItem voice1;
	private UIToolCheckableItem voice2;
	private UIToolCheckableItem freeEditionMode;
	private UIToolActionItem openMeasureErrorsDialog;

	public TGEditToolBarSectionEdit(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}

	public void createSectionToolBars() {
		UIToolBar toolBar = this.createToolBar();

		this.modeSelection = toolBar.createCheckItem();
		this.modeSelection.addSelectionListener(this.createActionProcessor(TGSetMouseModeSelectionAction.NAME));

		this.modeEdition = toolBar.createCheckItem();
		this.modeEdition.addSelectionListener(this.createActionProcessor(TGSetMouseModeEditionAction.NAME));

		this.notNaturalKey = toolBar.createCheckItem();
		this.notNaturalKey.addSelectionListener(this.createActionProcessor(TGSetNaturalKeyAction.NAME));

		toolBar.createSeparator();

		this.voice1 = toolBar.createCheckItem();
		this.voice1.addSelectionListener(this.createActionProcessor(TGSetVoice1Action.NAME));

		this.voice2 = toolBar.createCheckItem();
		this.voice2.addSelectionListener(this.createActionProcessor(TGSetVoice2Action.NAME));

		toolBar = this.createToolBar();
		
		this.freeEditionMode = toolBar.createCheckItem();
		this.freeEditionMode.addSelectionListener(this.createActionProcessor(TGToggleFreeEditionModeAction.NAME));
		
		this.openMeasureErrorsDialog = toolBar.createActionItem();
		this.openMeasureErrorsDialog.addSelectionListener(this.createActionProcessor(TGOpenMeasureErrorsDialogAction.NAME));
		
	}

	public void updateSectionItems() {
		TGContext context = this.getToolBar().getContext();
		TablatureEditor editor = TablatureEditor.getInstance(context);
		EditorKit editorKit = editor.getTablature().getEditorKit();

		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();

		this.modeSelection.setChecked(editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION);
		this.modeSelection.setEnabled(!running);

		this.modeEdition.setChecked(editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.modeEdition.setEnabled(!running);

		this.freeEditionMode.setChecked(TGDocumentManager.getInstance(context).getSongManager().isFreeEditionMode(editor.getTablature().getCaret().getMeasure()));
		this.freeEditionMode.setEnabled(!running);
		
		this.openMeasureErrorsDialog.setEnabled(!running);

		this.notNaturalKey.setChecked(!editorKit.isNatural());
		this.notNaturalKey.setEnabled(!running && editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);

		this.voice1.setChecked(editor.getTablature().getCaret().getVoice() == 0);
		this.voice1.setEnabled(!running);

		this.voice2.setChecked(editor.getTablature().getCaret().getVoice() == 1);
		this.voice2.setEnabled(!running);
	}

	public void loadSectionProperties() {
		this.modeSelection.setToolTipText(this.getText("edit.mouse-mode-selection"));
		this.modeEdition.setToolTipText(this.getText("edit.mouse-mode-edition"));
		this.notNaturalKey.setToolTipText(this.getText("edit.not-natural-key"));
		this.voice1.setToolTipText(this.getText("edit.voice-1"));
		this.voice2.setToolTipText(this.getText("edit.voice-2"));
		this.freeEditionMode.setToolTipText(this.getText("edit.free-edition-mode"));
		this.openMeasureErrorsDialog.setToolTipText(this.getText("edit.measure-errors-dialog"));
	}

	public void loadSectionIcons() {
		this.modeSelection.setImage(this.getIconManager().getEditModeSelection());
		this.modeEdition.setImage(this.getIconManager().getEditModeEdition());
		this.notNaturalKey.setImage(this.getIconManager().getEditModeEditionNotNatural());
		this.voice1.setImage(this.getIconManager().getEditVoice1());
		this.voice2.setImage(this.getIconManager().getEditVoice2());
		this.freeEditionMode.setImage(this.getIconManager().getFreeEditionMode());
		this.openMeasureErrorsDialog.setImage(this.getIconManager().getMeasureErrors());
	}
}
