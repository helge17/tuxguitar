package app.tuxguitar.app.view.toolbar.edit;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionComposition extends TGEditToolBarSection {

	private static final String SECTION_TITLE = "composition";

	private UIToolActionItem timeSignature;
	private UIToolActionItem tempo;
	private UIToolCheckableItem repeatOpen;
	private UIToolCheckableItem repeatClose;
	private UIToolCheckableItem repeatAlternative;

	public TGEditToolBarSectionComposition(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}

	public void createSectionToolBars() {
		UIToolBar toolBar = this.createToolBar();

		this.timeSignature = toolBar.createActionItem();
		this.timeSignature.addSelectionListener(this.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));

		this.tempo = toolBar.createActionItem();
		this.tempo.addSelectionListener(this.createActionProcessor(TGOpenTempoDialogAction.NAME));

		toolBar.createSeparator();

		this.repeatOpen = toolBar.createCheckItem();
		this.repeatOpen.addSelectionListener(this.createActionProcessor(TGRepeatOpenAction.NAME));

		this.repeatClose = toolBar.createCheckItem();
		this.repeatClose.addSelectionListener(this.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));

		this.repeatAlternative = toolBar.createCheckItem();
		this.repeatAlternative.addSelectionListener(this.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
	}

	public void updateSectionItems() {
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGMeasure measure = this.getTablature().getCaret().getMeasure();

		this.timeSignature.setEnabled(!running);
		this.tempo.setEnabled(!running);
		this.repeatOpen.setEnabled( !running );
		this.repeatOpen.setChecked(measure != null && measure.isRepeatOpen());
		this.repeatClose.setEnabled( !running );
		this.repeatClose.setChecked(measure != null && measure.getRepeatClose() > 0);
		this.repeatAlternative.setEnabled( !running );
		this.repeatAlternative.setChecked(measure != null && measure.getHeader().getRepeatAlternative() > 0);
	}

	public void loadSectionProperties() {
		this.timeSignature.setToolTipText(this.getText("composition.timesignature"));
		this.tempo.setToolTipText(this.getText("composition.tempo"));
		this.repeatOpen.setToolTipText(this.getText("repeat.open"));
		this.repeatClose.setToolTipText(this.getText("repeat.close"));
		this.repeatAlternative.setToolTipText(this.getText("repeat.alternative"));
	}

	public void loadSectionIcons() {
		this.timeSignature.setImage(this.getIconManager().getImageByName(TGIconManager.TIME_SIGNATURE));
		this.tempo.setImage(this.getIconManager().getImageByName(TGIconManager.TEMPO));
		this.repeatOpen.setImage(this.getIconManager().getImageByName(TGIconManager.REPEAT_OPEN));
		this.repeatClose.setImage(this.getIconManager().getImageByName(TGIconManager.REPEAT_CLOSE));
		this.repeatAlternative.setImage(this.getIconManager().getImageByName(TGIconManager.REPEAT_ALTERNATIVE));
	}
}
