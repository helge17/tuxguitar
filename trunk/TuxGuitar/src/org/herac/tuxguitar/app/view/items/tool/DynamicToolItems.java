/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.items.ToolItems;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
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
		this.pianoPianissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANO_PIANISSIMO));
		
		//--PP--
		this.pianissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.pianissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANISSIMO));
		
		//--P--
		this.piano = new ToolItem(this.toolBar, SWT.CHECK);
		this.piano.addSelectionListener(this.createChangeVelocityAction(TGVelocities.PIANO));
		
		//--MP--
		this.mezzoPiano = new ToolItem(this.toolBar, SWT.CHECK);
		this.mezzoPiano.addSelectionListener(this.createChangeVelocityAction(TGVelocities.MEZZO_PIANO));
		
		//--MF--
		this.mezzoForte = new ToolItem(this.toolBar, SWT.CHECK);
		this.mezzoForte.addSelectionListener(this.createChangeVelocityAction(TGVelocities.MEZZO_FORTE));
		
		//--F--
		this.forte = new ToolItem(this.toolBar, SWT.CHECK);
		this.forte.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTE));
		
		//--FF--
		this.fortissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.fortissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTISSIMO));
		
		//--FF--
		this.forteFortissimo = new ToolItem(this.toolBar, SWT.CHECK);
		this.forteFortissimo.addSelectionListener(this.createChangeVelocityAction(TGVelocities.FORTE_FORTISSIMO));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		int velocity = ((caret.getSelectedNote() != null)?caret.getSelectedNote().getVelocity():caret.getVelocity());
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
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
		this.pianoPianissimo.setImage(TuxGuitar.getInstance().getIconManager().getDynamicPPP());
		this.pianissimo.setImage(TuxGuitar.getInstance().getIconManager().getDynamicPP());
		this.piano.setImage(TuxGuitar.getInstance().getIconManager().getDynamicP());
		this.mezzoPiano.setImage(TuxGuitar.getInstance().getIconManager().getDynamicMP());
		this.mezzoForte.setImage(TuxGuitar.getInstance().getIconManager().getDynamicMF());
		this.forte.setImage(TuxGuitar.getInstance().getIconManager().getDynamicF());
		this.fortissimo.setImage(TuxGuitar.getInstance().getIconManager().getDynamicFF());
		this.forteFortissimo.setImage(TuxGuitar.getInstance().getIconManager().getDynamicFFF());
	}
	
	public TGActionProcessorListener createChangeVelocityAction(Integer velocity) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGChangeVelocityAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		return tgActionProcessor;
	}
}
