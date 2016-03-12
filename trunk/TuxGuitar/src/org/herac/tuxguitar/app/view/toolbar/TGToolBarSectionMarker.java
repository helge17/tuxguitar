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
	
	private Menu menu;
	private MenuItem add;
	private MenuItem list;
	private MenuItem first;
	private MenuItem previous;
	private MenuItem next;
	private MenuItem last;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		//--ADD--
		this.add = new MenuItem(this.menu, SWT.PUSH);
		this.add.addSelectionListener(toolBar.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		
		//--LIST--
		this.list = new MenuItem(this.menu, SWT.PUSH);
		this.list.addSelectionListener(toolBar.createActionProcessor(TGToggleMarkerListAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--FIRST--
		this.first = new MenuItem(this.menu, SWT.PUSH);
		this.first.addSelectionListener(toolBar.createActionProcessor(TGGoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = new MenuItem(this.menu, SWT.PUSH);
		this.previous.addSelectionListener(toolBar.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = new MenuItem(this.menu, SWT.PUSH);
		this.next.addSelectionListener(toolBar.createActionProcessor(TGGoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = new MenuItem(this.menu, SWT.PUSH);
		this.last.addSelectionListener(toolBar.createActionProcessor(TGGoLastMarkerAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("marker"));
		this.add.setText(toolBar.getText("marker.add"));
		this.list.setText(toolBar.getText("marker.list"));
		this.first.setText(toolBar.getText("marker.first"));
		this.previous.setText(toolBar.getText("marker.previous"));
		this.next.setText(toolBar.getText("marker.next"));
		this.last.setText(toolBar.getText("marker.last"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getMarkerList());
		this.add.setImage(toolBar.getIconManager().getMarkerAdd());
		this.list.setImage(toolBar.getIconManager().getMarkerList());
		this.first.setImage(toolBar.getIconManager().getMarkerFirst());
		this.previous.setImage(toolBar.getIconManager().getMarkerPrevious());
		this.next.setImage(toolBar.getIconManager().getMarkerNext());
		this.last.setImage(toolBar.getIconManager().getMarkerLast());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
