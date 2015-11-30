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
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGChord;

public class TGToolBarSectionChord implements TGToolBarSection {
	
	private ToolItem menuItem;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.DROP_DOWN);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				processItemSelection(toolBar, event);
			}
		});
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
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
	}
	
	public void processItemSelection(TGToolBar toolBar, SelectionEvent event) {
		if (event.detail == SWT.ARROW) {
			this.createMenu(toolBar, (ToolItem) event.widget);
		}
		else{
			toolBar.createActionProcessor(TGOpenChordDialogAction.NAME).process();
		}
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		Menu menu = new Menu(item.getParent().getShell());
		
		TGCustomChordManager customChordManager = TuxGuitar.getInstance().getCustomChordManager();
		for(int i = 0; i < customChordManager.countChords(); i++){
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(chord.getName());
			menuItem.addSelectionListener(this.createInsertChordAction(toolBar, chord));
		}
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
	
	public TGActionProcessorListener createInsertChordAction(TGToolBar toolBar, TGChord chord) {
		TGActionProcessorListener tgActionProcessor = toolBar.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		return tgActionProcessor;
	}
}
