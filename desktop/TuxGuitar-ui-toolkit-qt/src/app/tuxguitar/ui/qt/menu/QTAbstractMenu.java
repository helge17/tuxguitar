package app.tuxguitar.ui.qt.menu;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;
import app.tuxguitar.ui.qt.QTComponent;

import io.qt.core.QObject;
import io.qt.gui.QAction;
import io.qt.widgets.QMenu;
import io.qt.widgets.QWidget;

public abstract class QTAbstractMenu<T extends QWidget> extends QTComponent<T> implements UIMenu {

	private List<UIMenuItem>  menuItems;

	public QTAbstractMenu(T control) {
		super(control);

		this.menuItems = new ArrayList<UIMenuItem>();
	}

	public Integer getItemCount() {
		return this.menuItems.size();
	}

	public UIMenuItem getItem(int index) {
		return (index >= 0 && index < this.menuItems.size() ? this.menuItems.get(index) : null);
	}

	public List<UIMenuItem> getItems() {
		return new ArrayList<UIMenuItem>(this.menuItems);
	}

	public UIComponent createSeparator() {
		return new QTMenuSeparatorItem(this);
	}

	public UIMenuActionItem createActionItem() {
		return new QTMenuActionItem(this);
	}

	public UIMenuCheckableItem createCheckItem() {
		return new QTMenuCheckableItem(this);
	}

	public UIMenuCheckableItem createRadioItem() {
		return new QTMenuRadioItem(this);
	}

	public UIMenuSubMenuItem createSubMenuItem() {
		return new QTMenuSubMenuItem(this);
	}

	public void addItem(QTMenuItem<? extends QObject> item) {
		this.menuItems.add(item);
	}

	public void removeItem(QTMenuItem<? extends QObject> item) {
		if( this.menuItems.contains(item)) {
			this.menuItems.remove(item);
		}
	}

	public void dispose() {
		List<UIMenuItem> menuItems = new ArrayList<UIMenuItem>(this.menuItems);
		for(UIMenuItem menuItem : menuItems) {
			if(!menuItem.isDisposed()) {
				menuItem.dispose();
			}
		}
		this.getControl().dispose();

		super.dispose();
	}

	public abstract QMenu createNativeMenu();

	public abstract QAction createNativeAction();

	public abstract QAction createNativeSeparator();
}
