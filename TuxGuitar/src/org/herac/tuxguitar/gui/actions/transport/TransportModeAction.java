/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.transport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.player.base.MidiPlayerMode;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TransportModeAction extends Action {
	public static final String NAME = "action.transport.mode";
		
	protected static final int MIN_SELECTION = 1;
    protected static final int MAX_SELECTION = 500;
    protected static final int[] DEFAULT_PERCENTS = new int[]{25,50,75,100,125,150,175,200};
    
    protected Button single;
    protected Button singleLoop;
    protected Combo singlePercent;
        
    protected Button custom;
    protected Spinner customFrom;
    protected Spinner customTo;
    protected Spinner customIncrement;
    
	public TransportModeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}

	protected int execute(TypedEvent e){
		this.showDialog(e.widget.getDisplay().getActiveShell(), TuxGuitar.instance().getPlayer().getMode());	
		return 0;
	}

	public void showDialog(final Shell parent,final MidiPlayerMode mode) {		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("transport.mode"));

		// ----------------------------------------------------------------------		
		
		Composite radios = new Composite(dialog, SWT.NONE);
		radios.setLayout(new GridLayout());
		radios.setLayoutData(getMainData());
		
        //---Single---
		this.single = new Button(radios, SWT.RADIO);
		this.single.setText(TuxGuitar.getProperty("transport.mode.simple"));
		this.single.setSelection(mode.getType() == MidiPlayerMode.TYPE_SINGLE);		
		RadioSelectionAdapter singleAdapter = new RadioSelectionAdapter(this.single);
		
		Group singleGroup = new Group(radios, SWT.SHADOW_ETCHED_IN);
		singleGroup.setLayout(new GridLayout(2,false));
		singleGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		singleGroup.setText(TuxGuitar.getProperty("transport.mode.simple"));		
		singleAdapter.addControl(singleGroup);		

		singleAdapter.addControl(makeLabel(singleGroup, TuxGuitar.getProperty("transport.mode.simple.tempo-percent"),SWT.LEFT,1));
        this.singlePercent = new Combo(singleGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        this.singlePercent.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        for(int i = 0; i < DEFAULT_PERCENTS.length; i ++){
        	this.singlePercent.add(Integer.toString(DEFAULT_PERCENTS[i]) + "%",i);
        	if(mode.getSinglePercent() == DEFAULT_PERCENTS[i]){
        		this.singlePercent.select(i);
        	}
        }  		
        singleAdapter.addControl(this.singlePercent);	
        
        this.singleLoop = new Button(singleGroup, SWT.CHECK);
        this.singleLoop.setText(TuxGuitar.getProperty("transport.mode.simple.loop"));
        this.singleLoop.setSelection(mode.isLoop());        
        singleAdapter.addControl(this.singleLoop);	
        
        GridData loopedData = new GridData(SWT.FILL,SWT.FILL,true,true);
        loopedData.horizontalSpan = 2;
        this.singleLoop.setLayoutData(loopedData);
		
        //---Trainer---
        this.custom = new Button(radios, SWT.RADIO);
		this.custom.setText(TuxGuitar.getProperty("transport.mode.trainer"));
		this.custom.setSelection(mode.getType() == MidiPlayerMode.TYPE_CUSTOM);
		RadioSelectionAdapter customAdapter = new RadioSelectionAdapter(this.custom);
               	
        Group trainerGroup = new Group(radios, SWT.SHADOW_ETCHED_IN);
        trainerGroup.setLayout(new GridLayout(6,false));
        trainerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        trainerGroup.setText(TuxGuitar.getProperty("transport.mode.trainer"));		
        customAdapter.addControl(trainerGroup);
        
        customAdapter.addControl(makeLabel(trainerGroup, TuxGuitar.getProperty("composition.tempo"),SWT.LEFT,1));
        this.customFrom = new Spinner(trainerGroup,SWT.BORDER);
        this.customFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));        
        this.customFrom.setMinimum(MIN_SELECTION);
        this.customFrom.setMaximum(MAX_SELECTION);
        this.customFrom.setSelection(mode.getCustomPercentFrom());
        customAdapter.addControl(this.customFrom);
        customAdapter.addControl(makeLabel(trainerGroup, "%",SWT.LEFT,1));
        
        customAdapter.addControl(makeLabel(trainerGroup, TuxGuitar.getProperty("edit.to"),SWT.RIGHT,1));
        this.customTo = new Spinner(trainerGroup,SWT.BORDER);
        this.customTo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));        
        this.customTo.setMinimum(MIN_SELECTION);
        this.customTo.setMaximum(MAX_SELECTION);
        this.customTo.setSelection(mode.getCustomPercentTo());
        customAdapter.addControl(this.customTo);
        customAdapter.addControl(makeLabel(trainerGroup, "%",SWT.LEFT,1));
        
        customAdapter.addControl(makeLabel(trainerGroup, TuxGuitar.getProperty("transport.mode.trainer.increment-description"),SWT.LEFT,4));
        this.customIncrement = new Spinner(trainerGroup,SWT.BORDER);
        this.customIncrement.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));        
        this.customIncrement.setMinimum(MIN_SELECTION);
        this.customIncrement.setMaximum(MAX_SELECTION);        
        this.customIncrement.setSelection(mode.getCustomPercentIncrement());
        customAdapter.addControl(this.customIncrement);
        customAdapter.addControl(makeLabel(trainerGroup, "%",SWT.LEFT,1));
                                
        SpinnerSelectionAdapter spinnerAdapter = new SpinnerSelectionAdapter(this.customFrom,this.customTo,this.customIncrement);        
        this.customFrom.addSelectionListener(spinnerAdapter);
        this.customTo.addSelectionListener(spinnerAdapter);
        this.customIncrement.addSelectionListener(spinnerAdapter);
        
        singleAdapter.update();
        customAdapter.update();
		// ------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));

		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateMode(mode);
				dialog.dispose();
			}
		});

		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});

		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}

	private GridData getMainData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 350;
		return data;
	}	
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private Label makeLabel(Composite parent,String text,int aligment,int horizontalSpan){
		Label label = new Label(parent,SWT.CENTER | aligment);
		label.setText(text);
		GridData data = new GridData(SWT.FILL,SWT.CENTER,true,true);		
		data.horizontalSpan = horizontalSpan;
		label.setLayoutData(data);
		return label;
	}

	protected void updateMode(MidiPlayerMode mode){		
		int type = (this.custom.getSelection())?MidiPlayerMode.TYPE_CUSTOM:MidiPlayerMode.TYPE_SINGLE;
		mode.setType(type);	
		mode.setLoop( (type == MidiPlayerMode.TYPE_CUSTOM || (type == MidiPlayerMode.TYPE_SINGLE && this.singleLoop.getSelection())));		
		mode.setSinglePercent( this.singlePercent.getSelectionIndex() >= 0?DEFAULT_PERCENTS[this.singlePercent.getSelectionIndex()]:MidiPlayerMode.DEFAULT_TEMPO_PERCENT);
		mode.setCustomPercentFrom(this.customFrom.getSelection());
		mode.setCustomPercentTo(this.customTo.getSelection());
		mode.setCustomPercentIncrement(this.customIncrement.getSelection());
		mode.reset();
	}
	
	private class RadioSelectionAdapter extends SelectionAdapter{
		private Button control;
		private List controls;
		
		public RadioSelectionAdapter(Button control) {			
			this.controls = new ArrayList();
			this.control = control;
			this.control.addSelectionListener(this);
		}

		public void addControl(Control control){
			this.controls.add(control);
		}
		
		public void update(){
			boolean enabled = this.control.getSelection();
			Iterator it = this.controls.iterator();
			while(it.hasNext()){
				Control control = (Control)it.next();
				control.setEnabled(enabled);
			}
		}		
		
		public void widgetSelected(SelectionEvent e) {
			update();
		}			
		
	}
	
	private class SpinnerSelectionAdapter extends SelectionAdapter{
		private Spinner to;
		private Spinner from;
		private Spinner increment;
		
		public SpinnerSelectionAdapter(Spinner from,Spinner to, Spinner increment) {			
			this.from = from;
			this.to = to;
			this.increment = increment;
		}

		public void widgetSelected(SelectionEvent e) {								
			if(e.widget.equals(this.from)){
				if(this.from.getSelection() < MIN_SELECTION){
					this.from.setSelection(MIN_SELECTION);
				}else if(this.from.getSelection() >= this.to.getSelection()){
					this.from.setSelection(this.to.getSelection() - 1);
				}	
			}else if(e.widget.equals(this.to)){
				if(this.to.getSelection() <= this.from.getSelection()){
					this.to.setSelection(this.from.getSelection() + 1);
				}else if(this.to.getSelection() > MAX_SELECTION){
					this.to.setSelection(MAX_SELECTION);
				}
			}
			if(this.increment.getSelection() > (this.to.getSelection() - this.from.getSelection())){
				this.increment.setSelection(this.to.getSelection() - this.from.getSelection());
			}
		}			
		
	}	
}