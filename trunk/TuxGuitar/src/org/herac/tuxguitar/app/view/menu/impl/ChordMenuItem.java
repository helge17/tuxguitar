package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.song.models.TGChord;

public class ChordMenuItem extends TGMenuItem{
	private MenuItem chordMenuItem;
	private Menu menu;
	private MenuItem insertChord;
	private MenuItem[] subMenuItems;
	
	private long lastEdit;
	
	public ChordMenuItem(Shell shell,Menu parent, int style) {
		this.chordMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems() {
		//--INSERT CHORD--
		this.insertChord = new MenuItem(this.menu, SWT.PUSH);
		this.insertChord.addSelectionListener(this.createActionProcessor(TGOpenChordDialogAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--CUSTOM CHORDS--
		this.addItems();
		
		this.chordMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void addItems() {
		this.disposeItems();
		this.subMenuItems = new MenuItem[TuxGuitar.getInstance().getCustomChordManager().countChords()];
		for(int i = 0;i < this.subMenuItems.length; i++){
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
			
			this.subMenuItems[i] = new MenuItem(this.menu, SWT.PUSH);
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
	
	public void widgetSelected(SelectionEvent event) {
		if (event.detail == SWT.ARROW && this.subMenuItems != null && this.subMenuItems.length > 0) {
			ToolItem item = (ToolItem) event.widget;
			Rectangle rect = item.getBounds();
			Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
			this.menu.setLocation(pt.x, pt.y + rect.height);
			this.menu.setVisible(true);
		}else{
			new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGOpenChordDialogAction.NAME).process();
		}
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
		//Nothing to do
	}
	
	public TGActionProcessorListener createInsertChordAction(TGChord chord) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		return tgActionProcessor;
	}
}
