package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import app.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolMenuItem;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionMarker extends TGMainToolBarSection {

	private UIToolMenuItem menuItem;

	private UIMenuActionItem add;
	private UIMenuActionItem list;
	private UIMenuActionItem first;
	private UIMenuActionItem previous;
	private UIMenuActionItem next;
	private UIMenuActionItem last;

	public TGMainToolBarSectionMarker(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}

	public void createSection() {
		this.menuItem = this.getToolBar().createMenuItem();

		//--ADD--
		this.add = this.menuItem.getMenu().createActionItem();
		this.add.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));

		//--LIST--
		this.list = this.menuItem.getMenu().createActionItem();
		this.list.addSelectionListener(this.createActionProcessor(TGToggleMarkerListAction.NAME));

		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();

		//--FIRST--
		this.first = this.menuItem.getMenu().createActionItem();
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMarkerAction.NAME));

		//--PREVIOUS--
		this.previous = this.menuItem.getMenu().createActionItem();
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMarkerAction.NAME));

		//--PREVIOUS--
		this.next = this.menuItem.getMenu().createActionItem();
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMarkerAction.NAME));

		//--LAST--
		this.last = this.menuItem.getMenu().createActionItem();
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMarkerAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void loadProperties(){
		this.menuItem.setToolTipText(this.getText("marker"));
		this.add.setText(this.getText("marker.add"));
		this.list.setText(this.getText("marker.list"));
		this.first.setText(this.getText("marker.first"));
		this.previous.setText(this.getText("marker.previous"));
		this.next.setText(this.getText("marker.next"));
		this.last.setText(this.getText("marker.last"));
	}

	public void loadIcons(){
		this.menuItem.setImage(this.getIconManager().getMarkerList());
		this.add.setImage(this.getIconManager().getMarkerAdd());
		this.list.setImage(this.getIconManager().getMarkerList());
		this.first.setImage(this.getIconManager().getMarkerFirst());
		this.previous.setImage(this.getIconManager().getMarkerPrevious());
		this.next.setImage(this.getIconManager().getMarkerNext());
		this.last.setImage(this.getIconManager().getMarkerLast());
	}

	public void updateItems(){
		//Nothing to do
	}
}
