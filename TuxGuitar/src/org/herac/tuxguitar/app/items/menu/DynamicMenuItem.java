/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.actions.note.ChangeVelocityAction;
import org.herac.tuxguitar.app.items.MenuItems;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVelocities;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DynamicMenuItem extends MenuItems{
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
		this.pianoPianissimo.setData(createChangeVelocityActionData(TGVelocities.PIANO_PIANISSIMO));
		this.pianoPianissimo.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.pianissimo = new MenuItem(this.menu, SWT.CHECK);
		this.pianissimo.setData(createChangeVelocityActionData(TGVelocities.PIANISSIMO));
		this.pianissimo.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.piano = new MenuItem(this.menu, SWT.CHECK);
		this.piano.setData(createChangeVelocityActionData(TGVelocities.PIANO));
		this.piano.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.mezzoPiano = new MenuItem(this.menu, SWT.CHECK);
		this.mezzoPiano.setData(createChangeVelocityActionData(TGVelocities.MEZZO_PIANO));
		this.mezzoPiano.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.mezzoForte = new MenuItem(this.menu, SWT.CHECK);
		this.mezzoForte.setData(createChangeVelocityActionData(TGVelocities.MEZZO_FORTE));
		this.mezzoForte.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.forte = new MenuItem(this.menu, SWT.CHECK);
		this.forte.setData(createChangeVelocityActionData(TGVelocities.FORTE));
		this.forte.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.fortissimo = new MenuItem(this.menu, SWT.CHECK);
		this.fortissimo.setData(createChangeVelocityActionData(TGVelocities.FORTISSIMO));
		this.fortissimo.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.forteFortissimo = new MenuItem(this.menu, SWT.CHECK);
		this.forteFortissimo.setData(createChangeVelocityActionData(TGVelocities.FORTE_FORTISSIMO));
		this.forteFortissimo.addSelectionListener(TuxGuitar.instance().getAction(ChangeVelocityAction.NAME));
		
		this.dynamicMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGNote note = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
		int velocity = ((note != null)?note.getVelocity():TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getVelocity());
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
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
	
	private ActionData createChangeVelocityActionData(int velocity){
		ActionData actionData = new ActionData();
		actionData.put(ChangeVelocityAction.PROPERTY_VELOCITY, new Integer(velocity));
		return actionData;
	}
}
