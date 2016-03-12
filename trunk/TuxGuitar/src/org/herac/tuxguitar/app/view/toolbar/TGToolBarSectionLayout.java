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
	
	private Menu menu;
	private MenuItem pageLayout;
	private MenuItem linearLayout;
	private MenuItem multitrack;
	private MenuItem scoreEnabled;
	private MenuItem compact;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		this.pageLayout = new MenuItem(this.menu, SWT.PUSH);
		this.pageLayout.addSelectionListener(toolBar.createActionProcessor(TGSetPageLayoutAction.NAME));
		
		this.linearLayout = new MenuItem(this.menu, SWT.PUSH);
		this.linearLayout.addSelectionListener(toolBar.createActionProcessor(TGSetLinearLayoutAction.NAME));
		
		this.multitrack = new MenuItem(this.menu, SWT.PUSH);
		this.multitrack.addSelectionListener(toolBar.createActionProcessor(TGSetMultitrackViewAction.NAME));
		
		this.scoreEnabled = new MenuItem(this.menu, SWT.PUSH);
		this.scoreEnabled.addSelectionListener(toolBar.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		this.compact = new MenuItem(this.menu, SWT.PUSH);
		this.compact.addSelectionListener(toolBar.createActionProcessor(TGSetCompactViewAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar) {
		TGLayout layout = toolBar.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.menuItem.setToolTipText(toolBar.getText("view.layout"));
		this.pageLayout.setText(toolBar.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		this.linearLayout.setText(toolBar.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		this.multitrack.setText(toolBar.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		this.scoreEnabled.setText(toolBar.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		this.compact.setText(toolBar.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getLayoutScore());
		this.pageLayout.setImage(toolBar.getIconManager().getLayoutPage());
		this.linearLayout.setImage(toolBar.getIconManager().getLayoutLinear());
		this.multitrack.setImage(toolBar.getIconManager().getLayoutMultitrack());
		this.scoreEnabled.setImage(toolBar.getIconManager().getLayoutScore());
		this.compact.setImage(toolBar.getIconManager().getLayoutCompact());
	}
	
	public void updateItems(TGToolBar toolBar){
		TGLayout layout = toolBar.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.pageLayout.setText(toolBar.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		this.linearLayout.setText(toolBar.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		this.multitrack.setText(toolBar.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		this.scoreEnabled.setText(toolBar.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		this.compact.setText(toolBar.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || toolBar.getSong().countTracks() == 1);
	}
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
