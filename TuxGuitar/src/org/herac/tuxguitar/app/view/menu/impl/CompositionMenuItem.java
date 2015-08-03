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
import org.herac.tuxguitar.app.action.impl.composition.TGOpenClefDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenKeySignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTripletFeelDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompositionMenuItem extends TGMenuItem{
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
		this.timeSignature.addSelectionListener(this.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));
		//--TEMPO--
		this.tempo = new MenuItem(this.menu, SWT.PUSH);
		this.tempo.addSelectionListener(this.createActionProcessor(TGOpenTempoDialogAction.NAME));
		//--CLEF--
		this.clef = new MenuItem(this.menu, SWT.PUSH);
		this.clef.addSelectionListener(this.createActionProcessor(TGOpenClefDialogAction.NAME));
		//--KEY SIGNATURE--
		this.keySignature = new MenuItem(this.menu, SWT.PUSH);
		this.keySignature.addSelectionListener(this.createActionProcessor(TGOpenKeySignatureDialogAction.NAME));
		//--TRIPLET FEEL--
		this.tripletFeel = new MenuItem(this.menu, SWT.PUSH);
		this.tripletFeel.addSelectionListener(this.createActionProcessor(TGOpenTripletFeelDialogAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--REPEAT OPEN--
		this.repeatOpen = new MenuItem(this.menu, SWT.PUSH);
		this.repeatOpen.addSelectionListener(this.createActionProcessor(TGRepeatOpenAction.NAME));
		//--REPEAT CLOSE--
		this.repeatClose = new MenuItem(this.menu, SWT.PUSH);
		this.repeatClose.addSelectionListener(this.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		//--REPEAT ALTERNATIVE--
		this.repeatAlternative = new MenuItem(this.menu, SWT.PUSH);
		this.repeatAlternative.addSelectionListener(this.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--INFO--
		this.properties = new MenuItem(this.menu, SWT.PUSH);
		this.properties.addSelectionListener(this.createActionProcessor(TGOpenSongInfoDialogAction.NAME));
		
		this.compositionMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
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
		this.timeSignature.setImage(TuxGuitar.getInstance().getIconManager().getCompositionTimeSignature());
		this.tempo.setImage(TuxGuitar.getInstance().getIconManager().getCompositionTempo());
		this.repeatOpen.setImage(TuxGuitar.getInstance().getIconManager().getCompositionRepeatOpen());
		this.repeatClose.setImage(TuxGuitar.getInstance().getIconManager().getCompositionRepeatClose());
		this.repeatAlternative.setImage(TuxGuitar.getInstance().getIconManager().getCompositionRepeatAlternative());
		this.properties.setImage(TuxGuitar.getInstance().getIconManager().getSongProperties());
	}
}
