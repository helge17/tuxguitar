package org.herac.tuxguitar.app.view.toolbar;

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

public class TGToolBarSectionChord implements TGToolBarSection {
	
	private UIToolActionMenuItem menuItem;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createActionMenuItem();
		this.menuItem.addSelectionListener(toolBar.createActionProcessor(TGOpenChordDialogAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
		this.updateItems(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("insert.chord"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getChord());
	}
	
	public void updateItems(TGToolBar toolBar){
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		this.menuItem.setEnabled(!running);
		this.updateMenuItems(toolBar);
	}
	
	public void updateMenuItems(final TGToolBar toolBar) {
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
						processInsertChordAction(toolBar, (TGChord) event.getComponent().getData(TGChord.class.getName()));
					}
				});
				uiMenuItem = uiMenuActionItem;
			}
			uiMenuItem.setText(chord.getName());
			uiMenuItem.setData(TGChord.class.getName(), chord);
		}
	}
	
	public void processInsertChordAction(TGToolBar toolBar, TGChord chord) {
		TGActionProcessor tgActionProcessor = toolBar.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		tgActionProcessor.process();
	}
}
