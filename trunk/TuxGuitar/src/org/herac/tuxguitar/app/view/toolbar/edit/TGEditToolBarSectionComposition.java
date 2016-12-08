package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionComposition extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "composition";
	
	private UIToolActionItem tempo;
	private UIToolActionItem timeSignature;
	private UIToolCheckableItem repeatOpen;
	private UIToolCheckableItem repeatClose;
	private UIToolCheckableItem repeatAlternative;
	
	public TGEditToolBarSectionComposition(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}
	
	public void createSectionToolBars() {
		UIToolBar toolBar = this.createToolBar();
		
		this.tempo = toolBar.createActionItem();
		this.tempo.addSelectionListener(this.createActionProcessor(TGOpenTempoDialogAction.NAME));
		
		this.timeSignature = toolBar.createActionItem();
		this.timeSignature.addSelectionListener(this.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));
		
		toolBar.createSeparator();
		
		this.repeatOpen = toolBar.createCheckItem();
		this.repeatOpen.addSelectionListener(this.createActionProcessor(TGRepeatOpenAction.NAME));
		
		this.repeatClose = toolBar.createCheckItem();
		this.repeatClose.addSelectionListener(this.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		
		this.repeatAlternative = toolBar.createCheckItem();
		this.repeatAlternative.addSelectionListener(this.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void updateItems(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGMeasure measure = this.getTablature().getCaret().getMeasure();
		
		this.tempo.setEnabled(!running);
		this.timeSignature.setEnabled(!running);
		this.repeatOpen.setEnabled( !running );
		this.repeatOpen.setChecked(measure != null && measure.isRepeatOpen());
		this.repeatClose.setEnabled( !running );
		this.repeatClose.setChecked(measure != null && measure.getRepeatClose() > 0);
		this.repeatAlternative.setEnabled( !running );
		this.repeatAlternative.setChecked(measure != null && measure.getHeader().getRepeatAlternative() > 0);
	}
	
	public void loadProperties(){
		this.tempo.setToolTipText(this.getText("composition.tempo"));
		this.timeSignature.setToolTipText(this.getText("composition.timesignature"));
		this.repeatOpen.setToolTipText(this.getText("repeat.open"));
		this.repeatClose.setToolTipText(this.getText("repeat.close"));
		this.repeatAlternative.setToolTipText(this.getText("repeat.alternative"));
	}
	
	public void loadIcons(){
		this.tempo.setImage(this.getIconManager().getCompositionTempo());
		this.timeSignature.setImage(this.getIconManager().getCompositionTimeSignature());
		this.repeatOpen.setImage(this.getIconManager().getCompositionRepeatOpen());
		this.repeatClose.setImage(this.getIconManager().getCompositionRepeatClose());
		this.repeatAlternative.setImage(this.getIconManager().getCompositionRepeatAlternative());
	}
}
