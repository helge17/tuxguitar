package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class DynamicMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem dynamicMenuItem;
	private UIMenuCheckableItem pianoPianissimo;
	private UIMenuCheckableItem pianissimo;
	private UIMenuCheckableItem piano;
	private UIMenuCheckableItem mezzoPiano;
	private UIMenuCheckableItem mezzoForte;
	private UIMenuCheckableItem forte;
	private UIMenuCheckableItem fortissimo;
	private UIMenuCheckableItem forteFortissimo;

	public DynamicMenuItem(UIMenuSubMenuItem dynamicMenuItem) {
		this.dynamicMenuItem = dynamicMenuItem;
	}

	public DynamicMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}

	public void showItems(){
		//--PPP--
		this.pianoPianissimo = this.dynamicMenuItem.getMenu().createCheckItem();
		this.pianoPianissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANO_PIANISSIMO));

		//--PP--
		this.pianissimo = this.dynamicMenuItem.getMenu().createCheckItem();
		this.pianissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANISSIMO));

		//--P--
		this.piano = this.dynamicMenuItem.getMenu().createCheckItem();
		this.piano.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANO));

		//--MP--
		this.mezzoPiano = this.dynamicMenuItem.getMenu().createCheckItem();
		this.mezzoPiano.addSelectionListener(this.createChangeVelocityAction(TGVelocities.MEZZO_PIANO));

		//--MF--
		this.mezzoForte = this.dynamicMenuItem.getMenu().createCheckItem();
		this.mezzoForte.addSelectionListener(this.createChangeVelocityAction(TGVelocities.MEZZO_FORTE));

		//--F--
		this.forte = this.dynamicMenuItem.getMenu().createCheckItem();
		this.forte.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTE));

		//--FF--
		this.fortissimo = this.dynamicMenuItem.getMenu().createCheckItem();
		this.fortissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTISSIMO));

		//--FFF--
		this.forteFortissimo = this.dynamicMenuItem.getMenu().createCheckItem();
		this.forteFortissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTE_FORTISSIMO));

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
		int velocity = ((note != null)?note.getVelocity():TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getVelocity());
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.pianoPianissimo.setChecked(velocity == TGVelocities.PIANO_PIANISSIMO);
		this.pianoPianissimo.setEnabled(!running);
		this.pianissimo.setChecked(velocity == TGVelocities.PIANISSIMO);
		this.pianissimo.setEnabled(!running);
		this.piano.setChecked(velocity == TGVelocities.PIANO);
		this.piano.setEnabled(!running);
		this.mezzoPiano.setChecked(velocity == TGVelocities.MEZZO_PIANO);
		this.mezzoPiano.setEnabled(!running);
		this.mezzoForte.setChecked(velocity == TGVelocities.MEZZO_FORTE);
		this.mezzoForte.setEnabled(!running);
		this.forte.setChecked(velocity == TGVelocities.FORTE);
		this.forte.setEnabled(!running);
		this.fortissimo.setChecked(velocity == TGVelocities.FORTISSIMO);
		this.fortissimo.setEnabled(!running);
		this.forteFortissimo.setChecked(velocity == TGVelocities.FORTE_FORTISSIMO);
		this.forteFortissimo.setEnabled(!running);
	}

	public void loadProperties(){
		this.dynamicMenuItem.setText(TuxGuitar.getProperty("dynamic"));
		this.pianoPianissimo.setText(TuxGuitar.getProperty("dynamic.piano-pianissimo"));
		this.pianissimo.setText(TuxGuitar.getProperty("dynamic.pianissimo"));
		this.piano.setText(TuxGuitar.getProperty("dynamic.piano"));
		this.mezzoPiano.setText(TuxGuitar.getProperty("dynamic.mezzo-piano"));
		this.mezzoForte.setText(TuxGuitar.getProperty("dynamic.mezzo-forte"));
		this.forte.setText(TuxGuitar.getProperty("dynamic.forte"));
		this.fortissimo.setText(TuxGuitar.getProperty("dynamic.fortissimo"));
		this.forteFortissimo.setText(TuxGuitar.getProperty("dynamic.forte-fortissimo"));
	}

	public void loadIcons(){
		this.pianoPianissimo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_PPP));
		this.pianissimo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_PP));
		this.piano.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_P));
		this.mezzoPiano.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_MP));
		this.mezzoForte.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_MF));
		this.forte.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_F));
		this.fortissimo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_FF));
		this.forteFortissimo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_FFF));
	}

	public TGActionProcessorListener createChangeVelocityAction(Integer velocity) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGChangeVelocityAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		return tgActionProcessor;
	}
}
