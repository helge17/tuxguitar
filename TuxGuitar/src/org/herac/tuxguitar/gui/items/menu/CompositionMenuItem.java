/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.composition.ChangeClefAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeInfoAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeKeySignatureAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeTempoAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeTimeSignatureAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeTripletFeelAction;
import org.herac.tuxguitar.gui.actions.insert.RepeatAlternativeAction;
import org.herac.tuxguitar.gui.actions.insert.RepeatCloseAction;
import org.herac.tuxguitar.gui.actions.insert.RepeatOpenAction;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompositionMenuItem extends MenuItems{
	private MenuItem compositionMenuItem;
	private Menu menu;
	private MenuItem timeSignature;
	private MenuItem tempo;
	private MenuItem clef;
	private MenuItem keySignature;
	private MenuItem repeatOpen;
	private MenuItem repeatClose;
	private MenuItem repeatAlternative;
	private MenuItem tripletFeel;
	
	private MenuItem properties;
	
	public CompositionMenuItem(Shell shell,Menu parent, int style) {
		this.compositionMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--TIME SIGNATURE--
		this.timeSignature = new MenuItem(this.menu, SWT.PUSH);
		this.timeSignature.addSelectionListener(TuxGuitar.instance().getAction(ChangeTimeSignatureAction.NAME));
		//--TEMPO--
		this.tempo = new MenuItem(this.menu, SWT.PUSH);
		this.tempo.addSelectionListener(TuxGuitar.instance().getAction(ChangeTempoAction.NAME));
		//--CLEF--
		this.clef = new MenuItem(this.menu, SWT.PUSH);
		this.clef.addSelectionListener(TuxGuitar.instance().getAction(ChangeClefAction.NAME));
		//--KEY SIGNATURE--
		this.keySignature = new MenuItem(this.menu, SWT.PUSH);
		this.keySignature.addSelectionListener(TuxGuitar.instance().getAction(ChangeKeySignatureAction.NAME));
		//--TRIPLET FEEL--
		this.tripletFeel = new MenuItem(this.menu, SWT.PUSH);
		this.tripletFeel.addSelectionListener(TuxGuitar.instance().getAction(ChangeTripletFeelAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--REPEAT OPEN--
		this.repeatOpen = new MenuItem(this.menu, SWT.PUSH);
		this.repeatOpen.addSelectionListener(TuxGuitar.instance().getAction(RepeatOpenAction.NAME));
		//--REPEAT CLOSE--
		this.repeatClose = new MenuItem(this.menu, SWT.PUSH);
		this.repeatClose.addSelectionListener(TuxGuitar.instance().getAction(RepeatCloseAction.NAME));
		//--REPEAT ALTERNATIVE--
		this.repeatAlternative = new MenuItem(this.menu, SWT.PUSH);
		this.repeatAlternative.addSelectionListener(TuxGuitar.instance().getAction(RepeatAlternativeAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--INFO--
		this.properties = new MenuItem(this.menu, SWT.PUSH);
		this.properties.addSelectionListener(TuxGuitar.instance().getAction(ChangeInfoAction.NAME));
		
		this.compositionMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		this.timeSignature.setEnabled(!running);
		this.tempo.setEnabled(!running);
		this.clef.setEnabled(!running);
		this.keySignature.setEnabled(!running);
		this.tripletFeel.setEnabled(!running);
		this.repeatOpen.setEnabled(!running);
		this.repeatClose.setEnabled(!running);
		this.repeatAlternative.setEnabled(!running);
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.compositionMenuItem, "composition", null);		
		setMenuItemTextAndAccelerator(this.timeSignature, "composition.timesignature", ChangeTimeSignatureAction.NAME);
		setMenuItemTextAndAccelerator(this.tempo, "composition.tempo", ChangeTempoAction.NAME);
		setMenuItemTextAndAccelerator(this.clef, "composition.clef", ChangeClefAction.NAME);
		setMenuItemTextAndAccelerator(this.keySignature, "composition.keysignature", ChangeKeySignatureAction.NAME);
		setMenuItemTextAndAccelerator(this.tripletFeel, "composition.tripletfeel", ChangeTripletFeelAction.NAME);
		setMenuItemTextAndAccelerator(this.repeatOpen, "repeat.open", RepeatOpenAction.NAME);
		setMenuItemTextAndAccelerator(this.repeatClose, "repeat.close", RepeatCloseAction.NAME);
		setMenuItemTextAndAccelerator(this.repeatAlternative, "repeat.alternative", RepeatAlternativeAction.NAME);
		setMenuItemTextAndAccelerator(this.properties, "composition.properties", ChangeInfoAction.NAME);
	}
	
	public void loadIcons() {
		this.timeSignature.setImage(TuxGuitar.instance().getIconManager().getCompositionTimeSignature());
		this.tempo.setImage(TuxGuitar.instance().getIconManager().getCompositionTempo());
		this.repeatOpen.setImage(TuxGuitar.instance().getIconManager().getCompositionRepeatOpen());
		this.repeatClose.setImage(TuxGuitar.instance().getIconManager().getCompositionRepeatClose());
		this.repeatAlternative.setImage(TuxGuitar.instance().getIconManager().getCompositionRepeatAlternative());
		this.properties.setImage(TuxGuitar.instance().getIconManager().getSongProperties());
	}
}
