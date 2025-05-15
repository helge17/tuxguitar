package app.tuxguitar.app.view.toolbar.edit;

import app.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import app.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import app.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import app.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.app.action.impl.edit.TGToggleFreeEditionModeAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.util.TGContext;

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
		this.modeSelection.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_MODE_SELECTION));
		this.modeEdition.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_MODE_EDITION));
		this.notNaturalKey.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_MODE_EDITION_NO_NATURAL));
		this.voice1.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_VOICE_1));
		this.voice2.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_VOICE_2));
		this.freeEditionMode.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_MODE_FREE));
		this.openMeasureErrorsDialog.setImage(this.getIconManager().getImageByName(TGIconManager.EDIT_MEASURE_STATUS_CHECK));
	}
}
