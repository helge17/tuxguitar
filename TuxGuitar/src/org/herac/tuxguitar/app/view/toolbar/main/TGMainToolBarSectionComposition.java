package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGMainToolBarSectionComposition extends TGMainToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem tempo;
	private UIMenuActionItem timeSignature;
	private UIMenuActionItem repeatOpen;
	private UIMenuActionItem repeatClose;
	private UIMenuActionItem repeatAlternative;
	
	public TGMainToolBarSectionComposition(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.menuItem = this.getToolBar().getControl().createMenuItem();
		
		this.tempo = this.menuItem.getMenu().createActionItem();
		this.tempo.addSelectionListener(this.createActionProcessor(TGOpenTempoDialogAction.NAME));
		
		this.timeSignature = this.menuItem.getMenu().createActionItem();
		this.timeSignature.addSelectionListener(this.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));
		
		this.menuItem.getMenu().createSeparator();
		
		this.repeatOpen = this.menuItem.getMenu().createActionItem();
		this.repeatOpen.addSelectionListener(this.createActionProcessor(TGRepeatOpenAction.NAME));
		
		this.repeatClose = this.menuItem.getMenu().createActionItem();
		this.repeatClose.addSelectionListener(this.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		
		this.repeatAlternative = this.menuItem.getMenu().createActionItem();
		this.repeatAlternative.addSelectionListener(this.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties() {
		TGMeasure measure = this.getTablature().getCaret().getMeasure();
		
		this.menuItem.setToolTipText(this.getText("composition"));
		this.tempo.setText(this.getText("composition.tempo"));
		this.timeSignature.setText(this.getText("composition.timesignature"));
		this.repeatOpen.setText(this.getText("repeat.open", (measure != null && measure.isRepeatOpen())));
		this.repeatClose.setText(this.getText("repeat.close", (measure != null && measure.getRepeatClose() > 0)));
		this.repeatAlternative.setText(this.getText("repeat.alternative", (measure != null && measure.getHeader().getRepeatAlternative() > 0)));
	}
	
	public void loadIcons() {
		this.menuItem.setImage(this.getIconManager().getCompositionTimeSignature());
		this.tempo.setImage(this.getIconManager().getCompositionTempo());
		this.timeSignature.setImage(this.getIconManager().getCompositionTimeSignature());
		this.repeatOpen.setImage(this.getIconManager().getCompositionRepeatOpen());
		this.repeatClose.setImage(this.getIconManager().getCompositionRepeatClose());
		this.repeatAlternative.setImage(this.getIconManager().getCompositionRepeatAlternative());
	}
	
	public void updateItems() {
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGMeasure measure = this.getTablature().getCaret().getMeasure();
		
		this.tempo.setEnabled(!running);
		this.timeSignature.setEnabled(!running);
		this.repeatOpen.setEnabled( !running );
		this.repeatOpen.setText(this.getText("repeat.open", (measure != null && measure.isRepeatOpen())));
		this.repeatClose.setEnabled( !running );
		this.repeatClose.setText(this.getText("repeat.close", (measure != null && measure.getRepeatClose() > 0)));
		this.repeatAlternative.setEnabled( !running );
		this.repeatAlternative.setText(this.getText("repeat.alternative", (measure != null && measure.getHeader().getRepeatAlternative() > 0)));
	}
}
