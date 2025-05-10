package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import app.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class MarkerMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem markerMenuItem;
	private UIMenuActionItem add;
	private UIMenuActionItem list;
	private UIMenuActionItem first;
	private UIMenuActionItem last;
	private UIMenuActionItem next;
	private UIMenuActionItem previous;

	public MarkerMenuItem(UIMenu parent) {
		this.markerMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		//--ADD--
		this.add = this.markerMenuItem.getMenu().createActionItem();
		this.add.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));

		//--LIST--
		this.list = this.markerMenuItem.getMenu().createActionItem();
		this.list.addSelectionListener(this.createActionProcessor(TGToggleMarkerListAction.NAME));

		//--SEPARATOR--
		this.markerMenuItem.getMenu().createSeparator();

		//--FIRST--
		this.first = this.markerMenuItem.getMenu().createActionItem();
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMarkerAction.NAME));

		//--PREVIOUS--
		this.previous = this.markerMenuItem.getMenu().createActionItem();
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMarkerAction.NAME));

		//--NEXT--
		this.next = this.markerMenuItem.getMenu().createActionItem();
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMarkerAction.NAME));

		//--LAST--
		this.last = this.markerMenuItem.getMenu().createActionItem();
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMarkerAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		//Nothing to do
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.markerMenuItem, "marker", null);
		setMenuItemTextAndAccelerator(this.add, "marker.add", TGOpenMarkerEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.list, "marker.list", TGToggleMarkerListAction.NAME);
		setMenuItemTextAndAccelerator(this.first, "marker.first", TGGoFirstMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "marker.last", TGGoLastMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "marker.previous", TGGoPreviousMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "marker.next", TGGoNextMarkerAction.NAME);
	}

	public void loadIcons(){
		this.add.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MARKER_ADD));
		this.list.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MARKER_LIST));
		this.first.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MARKER_FIRST));
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MARKER_PREVIOUS));
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MARKER_NEXT));
		this.last.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MARKER_LAST));
	}
}
