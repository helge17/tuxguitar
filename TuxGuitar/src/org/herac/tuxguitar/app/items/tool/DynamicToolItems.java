/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.tool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.TGActionProcessor;
import org.herac.tuxguitar.app.actions.note.ChangeVelocityAction;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.items.ToolItems;
import org.herac.tuxguitar.song.models.TGVelocities;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DynamicToolItems  extends ToolItems{
	public static final String NAME = "dynamic.items";
	
	private ToolBar toolBar;
	
	private ToolItem pianoPianissimo;
	private ToolItem pianissimo;
	private ToolItem piano;
	private ToolItem mezzoPiano;
	private ToolItem mezzoForte;
	private ToolItem forte;
	private ToolItem fortissimo;
	private ToolItem forteFortissimo;
	
	
	public DynamicToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.toolBar = toolBar;
		//--PPP--
		this.pianoPianissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.pianoPianissimo.setData(createChangeVelocityActionData(TGVelocities.PIANO_PIANISSIMO));
		this.pianoPianissimo.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--PP--
		this.pianissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.pianissimo.setData(createChangeVelocityActionData(TGVelocities.PIANISSIMO));
		this.pianissimo.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--P--
		this.piano = new ToolItem(this.toolBar, SWT.CHECK);
		this.piano.setData(createChangeVelocityActionData(TGVelocities.PIANO));
		this.piano.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--MP--
		this.mezzoPiano = new ToolItem(this.toolBar, SWT.CHECK);
		this.mezzoPiano.setData(createChangeVelocityActionData(TGVelocities.MEZZO_PIANO));
		this.mezzoPiano.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--MF--
		this.mezzoForte = new ToolItem(this.toolBar, SWT.CHECK);
		this.mezzoForte.setData(createChangeVelocityActionData(TGVelocities.MEZZO_FORTE));
		this.mezzoForte.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--F--
		this.forte = new ToolItem(this.toolBar, SWT.CHECK);
		this.forte.setData(createChangeVelocityActionData(TGVelocities.FORTE));
		this.forte.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--FF--
		this.fortissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.fortissimo.setData(createChangeVelocityActionData(TGVelocities.FORTISSIMO));
		this.fortissimo.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		//--FF--
		this.forteFortissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.forteFortissimo.setData(createChangeVelocityActionData(TGVelocities.FORTE_FORTISSIMO));
		this.forteFortissimo.addSelectionListener(new TGActionProcessor(ChangeVelocityAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
		int velocity = ((caret.getSelectedNote() != null)?caret.getSelectedNote().getVelocity():caret.getVelocity());
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		this.pianoPianissimo.setSelection(velocity == TGVelocities.PIANO_PIANISSIMO);
		this.pianoPianissimo.setEnabled( !running );
		this.pianissimo.setSelection(velocity == TGVelocities.PIANISSIMO);
		this.pianissimo.setEnabled( !running );
		this.piano.setSelection(velocity == TGVelocities.PIANO);
		this.piano.setEnabled( !running );
		this.mezzoPiano.setSelection(velocity == TGVelocities.MEZZO_PIANO);
		this.mezzoPiano.setEnabled( !running );
		this.mezzoForte.setSelection(velocity == TGVelocities.MEZZO_FORTE);
		this.mezzoForte.setEnabled( !running );
		this.forte.setSelection(velocity == TGVelocities.FORTE);
		this.forte.setEnabled( !running );
		this.fortissimo.setSelection(velocity == TGVelocities.FORTISSIMO);
		this.fortissimo.setEnabled( !running );
		this.forteFortissimo.setSelection(velocity == TGVelocities.FORTE_FORTISSIMO);
		this.forteFortissimo.setEnabled( !running );
	}
	
	public void loadProperties(){
		this.pianoPianissimo.setToolTipText(TuxGuitar.getProperty("dynamic.piano-pianissimo"));
		this.pianissimo.setToolTipText(TuxGuitar.getProperty("dynamic.pianissimo"));
		this.piano.setToolTipText(TuxGuitar.getProperty("dynamic.piano"));
		this.mezzoPiano.setToolTipText(TuxGuitar.getProperty("dynamic.mezzo-piano"));
		this.mezzoForte.setToolTipText(TuxGuitar.getProperty("dynamic.mezzo-forte"));
		this.forte.setToolTipText(TuxGuitar.getProperty("dynamic.forte"));
		this.fortissimo.setToolTipText(TuxGuitar.getProperty("dynamic.fortissimo"));
		this.forteFortissimo.setToolTipText(TuxGuitar.getProperty("dynamic.forte-fortissimo"));
	}
	
	public void loadIcons(){
		this.pianoPianissimo.setImage(TuxGuitar.instance().getIconManager().getDynamicPPP());
		this.pianissimo.setImage(TuxGuitar.instance().getIconManager().getDynamicPP());
		this.piano.setImage(TuxGuitar.instance().getIconManager().getDynamicP());
		this.mezzoPiano.setImage(TuxGuitar.instance().getIconManager().getDynamicMP());
		this.mezzoForte.setImage(TuxGuitar.instance().getIconManager().getDynamicMF());
		this.forte.setImage(TuxGuitar.instance().getIconManager().getDynamicF());
		this.fortissimo.setImage(TuxGuitar.instance().getIconManager().getDynamicFF());
		this.forteFortissimo.setImage(TuxGuitar.instance().getIconManager().getDynamicFFF());
	}
	
	private Map createChangeVelocityActionData(int velocity){
		Map actionData = new HashMap();
		actionData.put(ChangeVelocityAction.PROPERTY_VELOCITY, new Integer(velocity));
		return actionData;
	}
}
