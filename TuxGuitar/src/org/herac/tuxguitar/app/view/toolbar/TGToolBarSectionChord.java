package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGChord;

public class TGToolBarSectionChord implements TGToolBarSection {
	
	private ToolItem menuItem;
	
	private Menu menu;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.DROP_DOWN);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				processItemSelection(toolBar, event);
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
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
	
	public void processItemSelection(TGToolBar toolBar, SelectionEvent event) {
		if (event.detail == SWT.ARROW) {
			this.displayMenu();
		}
		else{
			toolBar.createActionProcessor(TGOpenChordDialogAction.NAME).process();
		}
	}
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
	
	public void updateMenuItems(final TGToolBar toolBar) {
		TGCustomChordManager customChordManager = TuxGuitar.getInstance().getCustomChordManager();
		MenuItem[] menuItems = this.menu.getItems();
		for( int i = customChordManager.countChords(); i < menuItems.length; i++) {
			menuItems[i].dispose();
		}
		
		for(int i = 0; i < customChordManager.countChords(); i++) {
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
			
			MenuItem menuItem = (this.menu.getItemCount() > i ? this.menu.getItem(i) : null);
			if( menuItem == null ) {
				menuItem = new MenuItem(this.menu, SWT.PUSH);
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						processInsertChordAction(toolBar, (TGChord)((MenuItem) e.widget).getData());
					}
				});
			}
			menuItem.setText(chord.getName());
			menuItem.setData(chord);
		}
	}
	
	public void processInsertChordAction(TGToolBar toolBar, TGChord chord) {
		TGActionProcessor tgActionProcessor = toolBar.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		tgActionProcessor.process();
	}
}
