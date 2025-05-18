package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGInsertChordAction;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class ChordMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem chordMenuItem;
	private UIMenuActionItem insertChord;
	private UIMenuActionItem[] subMenuItems;

	private long lastEdit;

	public ChordMenuItem(UIMenuSubMenuItem chordMenuItem) {
		this.chordMenuItem = chordMenuItem;
	}

	public ChordMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}

	public void showItems() {
		//--INSERT CHORD--
		this.insertChord = this.chordMenuItem.getMenu().createActionItem();
		this.insertChord.addSelectionListener(this.createActionProcessor(TGOpenChordDialogAction.NAME));

		//--SEPARATOR--
		this.chordMenuItem.getMenu().createSeparator();

		//--CUSTOM CHORDS--
		this.addItems();

		this.loadIcons();
		this.loadProperties();
	}

	public void addItems() {
		this.disposeItems();
		this.subMenuItems = new UIMenuActionItem[TuxGuitar.getInstance().getCustomChordManager().countChords()];
		for(int i = 0;i < this.subMenuItems.length; i++){
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);

			this.subMenuItems[i] = this.chordMenuItem.getMenu().createActionItem();
			this.subMenuItems[i].setText(chord.getName());
			this.subMenuItems[i].addSelectionListener(this.createInsertChordAction(chord));
		}
	}

	public void disposeItems() {
		if(this.subMenuItems != null){
			for(int i = 0;i < this.subMenuItems.length; i++){
				this.subMenuItems[i].dispose();
			}
		}
	}

	public void setEnabled(boolean enabled) {
		this.chordMenuItem.setEnabled(enabled);
	}


	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		if(this.lastEdit != TuxGuitar.getInstance().getCustomChordManager().getLastEdit()){
			this.addItems();
			this.lastEdit = TuxGuitar.getInstance().getCustomChordManager().getLastEdit();
		}
		this.insertChord.setEnabled(!running);
		for(int i = 0;i < this.subMenuItems.length; i++){
			this.subMenuItems[i].setEnabled(!running);
		}
	}

	public void loadProperties() {
		setMenuItemTextAndAccelerator(this.chordMenuItem, "chord", null);
		setMenuItemTextAndAccelerator(this.insertChord, "insert.chord", TGOpenChordDialogAction.NAME);
	}

	public void loadIcons() {
		this.insertChord.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.CHORD));
	}

	public TGActionProcessorListener createInsertChordAction(TGChord chord) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		return tgActionProcessor;
	}
}
