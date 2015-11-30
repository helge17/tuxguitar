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
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createMenu(toolBar, (ToolItem) event.widget);
			}
		});
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("composition"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getCompositionTimeSignature());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGMeasure measure = toolBar.getTablature().getCaret().getMeasure();
		
		Menu menu = new Menu(item.getParent().getShell());
		
		MenuItem tempo = new MenuItem(menu, SWT.PUSH);
		tempo.addSelectionListener(toolBar.createActionProcessor(TGOpenTempoDialogAction.NAME));
		tempo.setEnabled(!running);
		tempo.setText(toolBar.getText("composition.tempo"));
		tempo.setImage(toolBar.getIconManager().getCompositionTempo());

		MenuItem timeSignature = new MenuItem(menu, SWT.PUSH);
		timeSignature.addSelectionListener(toolBar.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));
		timeSignature.setEnabled(!running);
		timeSignature.setText(toolBar.getText("composition.timesignature"));
		timeSignature.setImage(toolBar.getIconManager().getCompositionTimeSignature());

		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem repeatOpen = new MenuItem(menu, SWT.PUSH);
		repeatOpen.addSelectionListener(toolBar.createActionProcessor(TGRepeatOpenAction.NAME));
		repeatOpen.setEnabled( !running );
		repeatOpen.setText(toolBar.getText("repeat.open", (measure != null && measure.isRepeatOpen())));
		repeatOpen.setImage(toolBar.getIconManager().getCompositionRepeatOpen());

		MenuItem repeatClose = new MenuItem(menu, SWT.PUSH);
		repeatClose.addSelectionListener(toolBar.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		repeatClose.setEnabled( !running );
		repeatClose.setText(toolBar.getText("repeat.close", (measure != null && measure.getRepeatClose() > 0)));
		repeatClose.setImage(toolBar.getIconManager().getCompositionRepeatClose());
		
		MenuItem repeatAlternative = new MenuItem(menu, SWT.PUSH);
		repeatAlternative.addSelectionListener(toolBar.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
		repeatAlternative.setEnabled( !running );
		repeatAlternative.setText(toolBar.getText("repeat.alternative", (measure != null && measure.getHeader().getRepeatAlternative() > 0)));
		repeatAlternative.setImage(toolBar.getIconManager().getCompositionRepeatAlternative());
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
