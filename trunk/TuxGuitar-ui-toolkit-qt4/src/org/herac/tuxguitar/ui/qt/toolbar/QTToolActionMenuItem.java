package org.herac.tuxguitar.ui.qt.toolbar;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;

import com.trolltech.qt.core.Qt.ArrowType;

public class QTToolActionMenuItem extends QTComponent<Void> implements UIToolActionMenuItem {
	
	private static final int ARROW_WIDTH = 16;
	
	private QTToolActionItem actionItem;
	private QTToolMenuItem menuItem;
	
	public QTToolActionMenuItem(QTToolBar parent) {
		super(null);
		
		this.actionItem = new QTToolActionItem(parent);
		this.menuItem = new QTToolMenuItem(parent);
		this.menuItem.getControl().setArrowType(ArrowType.DownArrow);
		if( parent.isHorizontal() ) {
			this.menuItem.getControl().setFixedWidth(ARROW_WIDTH);
		} else {
			this.menuItem.getControl().setFixedHeight(ARROW_WIDTH);
		}
	}

	public UIMenu getMenu() {
		return this.menuItem.getMenu();
	}
	
	public String getText() {
		return this.actionItem.getText();
	}

	public void setText(String text) {
		this.actionItem.setText(text);
	}

	public UIImage getImage() {
		return this.actionItem.getImage();
	}

	public void setImage(UIImage image) {
		this.actionItem.setImage(image);
	}

	public void addSelectionListener(UISelectionListener listener) {
		this.actionItem.addSelectionListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.actionItem.removeSelectionListener(listener);
	}

	public String getToolTipText() {
		return this.actionItem.getToolTipText();
	}

	public void setToolTipText(String text) {
		this.actionItem.setToolTipText(text);
		this.menuItem.setToolTipText(text);
	}

	public boolean isEnabled() {
		return this.actionItem.isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.actionItem.setEnabled(enabled);
		this.menuItem.setEnabled(enabled);
	}
}
