package org.herac.tuxguitar.app.view.toolbar.main;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;

public class TGMainToolBarSectionChord extends TGMainToolBarSection {
	
	private UIToolActionMenuItem menuItem;
	
	public TGMainToolBarSectionChord(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.menuItem = this.getToolBar().getControl().createActionMenuItem();
		this.menuItem.addSelectionListener(this.createActionProcessor(TGOpenChordDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
		this.updateItems();
	}
	
	public void loadProperties(){
		this.menuItem.setToolTipText(this.getText("insert.chord"));
	}
	
	public void loadIcons(){
		this.menuItem.setImage(this.getIconManager().getChord());
	}
	
	public void updateItems(){
		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		this.menuItem.setEnabled(!running);
		this.updateMenuItems();
	}
	
	public void updateMenuItems() {
		TGCustomChordManager customChordManager = TuxGuitar.getInstance().getCustomChordManager();
		UIMenu uiMenu = this.menuItem.getMenu(); 
		List<UIMenuItem> uiMenuItems = this.menuItem.getMenu().getItems();
		for(int i = customChordManager.countChords(); i < uiMenuItems.size(); i++) {
			uiMenuItems.get(i).dispose();
		}
		
		for(int i = 0; i < customChordManager.countChords(); i++) {
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
			
			UIMenuItem uiMenuItem = (uiMenu.getItemCount() > i ? uiMenu.getItem(i) : null);
			if( uiMenuItem == null ) {
				UIMenuActionItem uiMenuActionItem = uiMenu.createActionItem();
				uiMenuActionItem.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						processInsertChordAction((TGChord) event.getComponent().getData(TGChord.class.getName()));
					}
				});
				uiMenuItem = uiMenuActionItem;
			}
			uiMenuItem.setText(chord.getName());
			uiMenuItem.setData(TGChord.class.getName(), chord);
		}
	}
	
	public void processInsertChordAction(TGChord chord) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		tgActionProcessor.process();
	}
}
