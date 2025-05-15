package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.composition.TGOpenClefDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenKeySignatureDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTripletFeelDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class CompositionMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem compositionMenuItem;
	private UIMenuActionItem timeSignature;
	private UIMenuActionItem tempo;
	private UIMenuActionItem clef;
	private UIMenuActionItem keySignature;
	private UIMenuActionItem repeatOpen;
	private UIMenuActionItem repeatClose;
	private UIMenuActionItem repeatAlternative;
	private UIMenuActionItem tripletFeel;
	private UIMenuActionItem properties;

	public CompositionMenuItem(UIMenu parent) {
		this.compositionMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		//--TIME SIGNATURE--
		this.timeSignature = this.compositionMenuItem.getMenu().createActionItem();
		this.timeSignature.addSelectionListener(this.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));

		//--TEMPO--
		this.tempo = this.compositionMenuItem.getMenu().createActionItem();
		this.tempo.addSelectionListener(this.createActionProcessor(TGOpenTempoDialogAction.NAME));

		//--CLEF--
		this.clef = this.compositionMenuItem.getMenu().createActionItem();
		this.clef.addSelectionListener(this.createActionProcessor(TGOpenClefDialogAction.NAME));

		//--KEY SIGNATURE--
		this.keySignature = this.compositionMenuItem.getMenu().createActionItem();
		this.keySignature.addSelectionListener(this.createActionProcessor(TGOpenKeySignatureDialogAction.NAME));

		//--TRIPLET FEEL--
		this.tripletFeel = this.compositionMenuItem.getMenu().createActionItem();
		this.tripletFeel.addSelectionListener(this.createActionProcessor(TGOpenTripletFeelDialogAction.NAME));

		//--SEPARATOR--
		this.compositionMenuItem.getMenu().createSeparator();

		//--REPEAT OPEN--
		this.repeatOpen = this.compositionMenuItem.getMenu().createActionItem();
		this.repeatOpen.addSelectionListener(this.createActionProcessor(TGRepeatOpenAction.NAME));

		//--REPEAT CLOSE--
		this.repeatClose = this.compositionMenuItem.getMenu().createActionItem();
		this.repeatClose.addSelectionListener(this.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));

		//--REPEAT ALTERNATIVE--
		this.repeatAlternative = this.compositionMenuItem.getMenu().createActionItem();
		this.repeatAlternative.addSelectionListener(this.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));

		//--SEPARATOR--
		this.compositionMenuItem.getMenu().createSeparator();

		//--INFO--
		this.properties = this.compositionMenuItem.getMenu().createActionItem();
		this.properties.addSelectionListener(this.createActionProcessor(TGOpenSongInfoDialogAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		boolean isPercussion = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().isPercussion();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.timeSignature.setEnabled(!running);
		this.tempo.setEnabled(!running);
		this.clef.setEnabled(!running && !isPercussion);
		this.keySignature.setEnabled(!running && !isPercussion);
		this.tripletFeel.setEnabled(!running);
		this.repeatOpen.setEnabled(!running);
		this.repeatClose.setEnabled(!running);
		this.repeatAlternative.setEnabled(!running);
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.compositionMenuItem, "composition", null);
		setMenuItemTextAndAccelerator(this.timeSignature, "composition.timesignature", TGOpenTimeSignatureDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.tempo, "composition.tempo", TGOpenTempoDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.clef, "composition.clef", TGOpenClefDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.keySignature, "composition.keysignature", TGOpenKeySignatureDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.tripletFeel, "composition.tripletfeel", TGOpenTripletFeelDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.repeatOpen, "repeat.open", TGRepeatOpenAction.NAME);
		setMenuItemTextAndAccelerator(this.repeatClose, "repeat.close", TGOpenRepeatCloseDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.repeatAlternative, "repeat.alternative", TGOpenRepeatAlternativeDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.properties, "composition.properties", TGOpenSongInfoDialogAction.NAME);
	}

	public void loadIcons() {
		this.timeSignature.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TIME_SIGNATURE));
		this.tempo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TEMPO));
		this.clef.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.CLEF));
		this.keySignature.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.KEY_SIGNATURE));
		this.tripletFeel.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRIPLET_FEEL));
		this.repeatOpen.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.REPEAT_OPEN));
		this.repeatClose.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.REPEAT_CLOSE));
		this.repeatAlternative.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.REPEAT_ALTERNATIVE));
		this.properties.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SONG_PROPERTIES));
	}
}
