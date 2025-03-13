package app.tuxguitar.ui.swt.toolbar;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ToolItem;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.swt.menu.SWTPopupMenu;
import app.tuxguitar.ui.toolbar.UIToolMenuItem;

public class SWTToolMenuItem extends SWTToolItem implements UIToolMenuItem {

	private UIPopupMenu menu;

	public SWTToolMenuItem(ToolItem item, SWTToolBar parent) {
		super(item, parent);

		this.menu = new SWTPopupMenu(this.getParent().getControl().getShell());
		this.getControl().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTToolMenuItem.this.openMenu();
			}
		});
	}

	public UIMenu getMenu() {
		return this.menu;
	}

	public void openMenu() {
		Rectangle rect = this.getControl().getBounds();
		Point pt = this.getControl().getParent().toDisplay(new Point(rect.x, rect.y));

		this.menu.open(new UIPosition(pt.x, pt.y + rect.height));
	}
}
