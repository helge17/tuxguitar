package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.song.models.TGMeasure;

public class TGToolBarSectionComposition implements TGToolBarSection {
	
	private ToolItem menuItem;
	
	private Menu menu;
	private MenuItem tempo;
	private MenuItem timeSignature;
	private MenuItem repeatOpen;
	private MenuItem repeatClose;
	private MenuItem repeatAlternative;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		this.tempo = new MenuItem(this.menu, SWT.PUSH);
		this.tempo.addSelectionListener(toolBar.createActionProcessor(TGOpenTempoDialogAction.NAME));
		
		this.timeSignature = new MenuItem(this.menu, SWT.PUSH);
		this.timeSignature.addSelectionListener(toolBar.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.repeatOpen = new MenuItem(this.menu, SWT.PUSH);
		this.repeatOpen.addSelectionListener(toolBar.createActionProcessor(TGRepeatOpenAction.NAME));
		
		this.repeatClose = new MenuItem(this.menu, SWT.PUSH);
		this.repeatClose.addSelectionListener(toolBar.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		
		this.repeatAlternative = new MenuItem(this.menu, SWT.PUSH);
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
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
