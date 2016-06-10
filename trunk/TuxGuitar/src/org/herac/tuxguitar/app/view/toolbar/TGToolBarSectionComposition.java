package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionComposition implements TGToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem tempo;
	private UIMenuActionItem timeSignature;
	private UIMenuActionItem repeatOpen;
	private UIMenuActionItem repeatClose;
	private UIMenuActionItem repeatAlternative;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createMenuItem();
		
		this.tempo = this.menuItem.getMenu().createActionItem();
		this.tempo.addSelectionListener(toolBar.createActionProcessor(TGOpenTempoDialogAction.NAME));
		
		this.timeSignature = this.menuItem.getMenu().createActionItem();
		this.timeSignature.addSelectionListener(toolBar.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));
		
		this.menuItem.getMenu().createSeparator();
		
		this.repeatOpen = this.menuItem.getMenu().createActionItem();
		this.repeatOpen.addSelectionListener(toolBar.createActionProcessor(TGRepeatOpenAction.NAME));
		
		this.repeatClose = this.menuItem.getMenu().createActionItem();
		this.repeatClose.addSelectionListener(toolBar.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		
		this.repeatAlternative = this.menuItem.getMenu().createActionItem();
		this.repeatAlternative.addSelectionListener(toolBar.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar) {
		TGMeasure measure = toolBar.getTablature().getCaret().getMeasure();
		
		this.menuItem.setToolTipText(toolBar.getText("composition"));
		this.tempo.setText(toolBar.getText("composition.tempo"));
		this.timeSignature.setText(toolBar.getText("composition.timesignature"));
		this.repeatOpen.setText(toolBar.getText("repeat.open", (measure != null && measure.isRepeatOpen())));
		this.repeatClose.setText(toolBar.getText("repeat.close", (measure != null && measure.getRepeatClose() > 0)));
		this.repeatAlternative.setText(toolBar.getText("repeat.alternative", (measure != null && measure.getHeader().getRepeatAlternative() > 0)));
	}
	
	public void loadIcons(TGToolBar toolBar) {
		this.menuItem.setImage(toolBar.getIconManager().getCompositionTimeSignature());
		this.tempo.setImage(toolBar.getIconManager().getCompositionTempo());
		this.timeSignature.setImage(toolBar.getIconManager().getCompositionTimeSignature());
		this.repeatOpen.setImage(toolBar.getIconManager().getCompositionRepeatOpen());
		this.repeatClose.setImage(toolBar.getIconManager().getCompositionRepeatClose());
		this.repeatAlternative.setImage(toolBar.getIconManager().getCompositionRepeatAlternative());
	}
	
	public void updateItems(TGToolBar toolBar) {
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGMeasure measure = toolBar.getTablature().getCaret().getMeasure();
		
		this.tempo.setEnabled(!running);
		this.timeSignature.setEnabled(!running);
		this.repeatOpen.setEnabled( !running );
		this.repeatOpen.setText(toolBar.getText("repeat.open", (measure != null && measure.isRepeatOpen())));
		this.repeatClose.setEnabled( !running );
		this.repeatClose.setText(toolBar.getText("repeat.close", (measure != null && measure.getRepeatClose() > 0)));
		this.repeatAlternative.setEnabled( !running );
		this.repeatAlternative.setText(toolBar.getText("repeat.alternative", (measure != null && measure.getHeader().getRepeatAlternative() > 0)));
	}
}
