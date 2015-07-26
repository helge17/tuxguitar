package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;

public class TGToolBarSectionMarker implements TGToolBarSection {
	
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
		this.menuItem.setToolTipText(toolBar.getText("marker"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getMarkerList());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		Menu menu = new Menu(item.getParent().getShell());
		
		//--ADD--
		MenuItem add = new MenuItem(menu, SWT.PUSH);
		add.addSelectionListener(toolBar.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		add.setText(toolBar.getText("marker.add"));
		add.setImage(toolBar.getIconManager().getMarkerAdd());
		
		//--LIST--
		MenuItem list = new MenuItem(menu, SWT.PUSH);
		list.addSelectionListener(toolBar.createActionProcessor(TGToggleMarkerListAction.NAME));
		list.setText(toolBar.getText("marker.list"));
		list.setImage(toolBar.getIconManager().getMarkerList());
		
		//--SEPARATOR--
		new MenuItem(menu, SWT.SEPARATOR);
		
		//--FIRST--
		MenuItem first = new MenuItem(menu, SWT.PUSH);
		first.addSelectionListener(toolBar.createActionProcessor(TGGoFirstMarkerAction.NAME));
		first.setText(toolBar.getText("marker.first"));
		first.setImage(toolBar.getIconManager().getMarkerFirst());
		
		//--PREVIOUS--
		MenuItem previous = new MenuItem(menu, SWT.PUSH);
		previous.addSelectionListener(toolBar.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		previous.setText(toolBar.getText("marker.previous"));
		previous.setImage(toolBar.getIconManager().getMarkerPrevious());
		
		//--PREVIOUS--
		MenuItem next = new MenuItem(menu, SWT.PUSH);
		next.addSelectionListener(toolBar.createActionProcessor(TGGoNextMarkerAction.NAME));
		next.setText(toolBar.getText("marker.next"));
		next.setImage(toolBar.getIconManager().getMarkerNext());
		
		//--LAST--
		MenuItem last = new MenuItem(menu, SWT.PUSH);
		last.addSelectionListener(toolBar.createActionProcessor(TGGoLastMarkerAction.NAME));
		last.setText(toolBar.getText("marker.last"));
		last.setImage(toolBar.getIconManager().getMarkerLast());
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
