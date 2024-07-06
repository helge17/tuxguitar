package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class DivisionMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem divisionMenuItem;
	private UIMenuCheckableItem[] divisionTypeMenuItems;

	public DivisionMenuItem(UIMenuSubMenuItem divisionMenuItem) {
		this.divisionMenuItem = divisionMenuItem;
	}

	public DivisionMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}

	public void showItems(){
		divisionTypeMenuItems = new UIMenuCheckableItem[TGDivisionType.DIVISION_TYPES.length];

		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			final int j = i;
			this.divisionTypeMenuItems[j] = this.divisionMenuItem.getMenu().createRadioItem();
			this.divisionTypeMenuItems[j].addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					DivisionMenuItem.this.createDivisionTypeAction(DivisionMenuItem.this.createDivisionType(TGDivisionType.DIVISION_TYPES[j])).process();
				}
			});
		}
	}

	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGDuration duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration();
		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			this.divisionTypeMenuItems[i].setChecked(duration.getDivision().isEqual(TGDivisionType.DIVISION_TYPES[i]));
			this.divisionTypeMenuItems[i].setEnabled(!running);
		}
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.divisionMenuItem, "duration.division-type", null);
		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			setMenuItemTextAndAccelerator(this.divisionTypeMenuItems[i], "duration.division-type." + Integer.toString(TGDivisionType.DIVISION_TYPES[i].getEnters()), null);
		}
	}

	public void loadIcons() {
		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			this.divisionTypeMenuItems[i].setImage(TuxGuitar.getInstance().getIconManager().getDivisionType(TGDivisionType.DIVISION_TYPES[i].getEnters()));
		}
	}

	private TGDivisionType createDivisionType(TGDivisionType tgDivisionTypeSrc) {
		TGFactory tgFactory = TGDocumentManager.getInstance(this.findContext()).getSongManager().getFactory();
		TGDivisionType tgDivisionTypeDst = tgFactory.newDivisionType();
		tgDivisionTypeDst.copyFrom(tgDivisionTypeSrc);
		return tgDivisionTypeDst;
	}

	private TGActionProcessorListener createDivisionTypeAction(TGDivisionType tgDivisionType){
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME);
		tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, tgDivisionType);
		return tgActionProcessor;
	}
}
