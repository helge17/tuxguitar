package org.herac.tuxguitar.ui.qt.toolbar;

import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.qt.menu.QTPopupMenu;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;
import io.qt.core.QPoint;
import io.qt.core.QSize;
import io.qt.widgets.QToolButton;

public class QTToolMenuItem extends QTToolAbstractButtonItem<QToolButton> implements UIToolMenuItem {
	
	private UIPopupMenu menu;
	
	public QTToolMenuItem(QTToolBar parent) {
		super(new QToolButton(parent.getControl()), parent);
		
		this.menu = new QTPopupMenu();
		this.getControl().clicked.connect(this, "openMenu()");
	}

	public UIMenu getMenu() {
		return this.menu;
	}
	
	public void openMenu() {
		QSize size = this.getControl().size();
		QPoint position = this.getControl().mapToGlobal(new QPoint(0, size.height()));
		
		this.menu.open(new UIPosition(position.x(), position.y()));
	}
}
