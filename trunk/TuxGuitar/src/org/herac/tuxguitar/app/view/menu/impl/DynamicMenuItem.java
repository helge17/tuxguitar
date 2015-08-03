/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVelocities;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DynamicMenuItem extends TGMenuItem{
	private MenuItem dynamicMenuItem;
	private Menu menu;
	private MenuItem pianoPianissimo;
	private MenuItem pianissimo;
	private MenuItem piano;
	private MenuItem mezzoPiano;
	private MenuItem mezzoForte;
	private MenuItem forte;
	private MenuItem fortissimo;
	private MenuItem forteFortissimo;
	
	public DynamicMenuItem(Shell shell,Menu parent, int style) {
		this.dynamicMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		
		this.pianoPianissimo = new MenuItem(this.menu, SWT.CHECK);
		this.pianoPianissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANO_PIANISSIMO));
		
		this.pianissimo = new MenuItem(this.menu, SWT.CHECK);
		this.pianissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANISSIMO));
		
		this.piano = new MenuItem(this.menu, SWT.CHECK);
		this.piano.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANO));
		
		this.mezzoPiano = new MenuItem(this.menu, SWT.CHECK);
		this.mezzoPiano.addSelectionListener(this.createChangeVelocityAction(TGVelocities.MEZZO_PIANO));
		
		this.mezzoForte = new MenuItem(this.menu, SWT.CHECK);
		this.mezzoForte.addSelectionListener(this.createChangeVelocityAction(TGVelocities.MEZZO_FORTE));
		
		this.forte = new MenuItem(this.menu, SWT.CHECK);
		this.forte.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTE));
		
		this.fortissimo = new MenuItem(this.menu, SWT.CHECK);
		this.fortissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTISSIMO));
		
		this.forteFortissimo = new MenuItem(this.menu, SWT.CHECK);
		this.forteFortissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTE_FORTISSIMO));
		
		this.dynamicMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
		int velocity = ((note != null)?note.getVelocity():TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getVelocity());
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.pianoPianissimo.setSelection(velocity == TGVelocities.PIANO_PIANISSIMO);
		this.pianoPianissimo.setEnabled(!running);
		this.pianissimo.setSelection(velocity == TGVelocities.PIANISSIMO);
		this.pianissimo.setEnabled(!running);
		this.piano.setSelection(velocity == TGVelocities.PIANO);
		this.piano.setEnabled(!running);
		this.mezzoPiano.setSelection(velocity == TGVelocities.MEZZO_PIANO);
		this.mezzoPiano.setEnabled(!running);
		this.mezzoForte.setSelection(velocity == TGVelocities.MEZZO_FORTE);
		this.mezzoForte.setEnabled(!running);
		this.forte.setSelection(velocity == TGVelocities.FORTE);
		this.forte.setEnabled(!running);
		this.fortissimo.setSelection(velocity == TGVelocities.FORTISSIMO);
		this.fortissimo.setEnabled(!running);
		this.forteFortissimo.setSelection(velocity == TGVelocities.FORTE_FORTISSIMO);
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
		//Nothing to do
	}
	
	public TGActionProcessorListener createChangeVelocityAction(Integer velocity) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGChangeVelocityAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		return tgActionProcessor;
	}
}
