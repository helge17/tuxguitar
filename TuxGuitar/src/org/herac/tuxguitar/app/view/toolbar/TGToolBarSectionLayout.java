package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;

public class TGToolBarSectionLayout implements TGToolBarSection {
	
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
		this.menuItem.setToolTipText(toolBar.getText("view.layout"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getLayoutScore());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		TGLayout layout = toolBar.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		Menu menu = new Menu(item.getParent().getShell());
		
		MenuItem pageLayout = new MenuItem(menu, SWT.PUSH);
		pageLayout.addSelectionListener(toolBar.createActionProcessor(TGSetPageLayoutAction.NAME));
		pageLayout.setText(toolBar.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		pageLayout.setImage(toolBar.getIconManager().getLayoutPage());
		
		MenuItem linearLayout = new MenuItem(menu, SWT.PUSH);
		linearLayout.addSelectionListener(toolBar.createActionProcessor(TGSetLinearLayoutAction.NAME));
		linearLayout.setText(toolBar.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		linearLayout.setImage(toolBar.getIconManager().getLayoutLinear());
		
		MenuItem multitrack = new MenuItem(menu, SWT.PUSH);
		multitrack.addSelectionListener(toolBar.createActionProcessor(TGSetMultitrackViewAction.NAME));
		multitrack.setText(toolBar.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		multitrack.setImage(toolBar.getIconManager().getLayoutMultitrack());
		
		MenuItem scoreEnabled = new MenuItem(menu, SWT.PUSH);
		scoreEnabled.addSelectionListener(toolBar.createActionProcessor(TGSetScoreEnabledAction.NAME));
		scoreEnabled.setText(toolBar.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		scoreEnabled.setImage(toolBar.getIconManager().getLayoutScore());
		
		MenuItem compact = new MenuItem(menu, SWT.PUSH);
		compact.addSelectionListener(toolBar.createActionProcessor(TGSetCompactViewAction.NAME));
		compact.setText(toolBar.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
		compact.setImage(toolBar.getIconManager().getLayoutCompact());
		compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || toolBar.getSong().countTracks() == 1);
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
