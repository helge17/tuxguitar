package org.herac.tuxguitar.gui.editors.effects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;

public class HarmonicEditor extends SelectionAdapter{
	
	public static final int WIDTH = 400;
	public static final int HEIGHT = 0;
	
	protected Combo harmonicType;
	protected Combo harmonicDataCombo;
	protected TGEffectHarmonic result;
	
	public HarmonicEditor(){
		super();
	}
	
	protected Button[] typeButtons;
	
	public TGEffectHarmonic show(final TGNote note){
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("effects.harmonic-editor"));
		
		//---------------------------------------------------------------------
		//------------HARMONIC-------------------------------------------------
		//---------------------------------------------------------------------
		Group group = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(resizeData(new GridData(SWT.FILL,SWT.FILL,true,true),WIDTH));
		group.setText(TuxGuitar.getProperty("effects.harmonic.type-of-harmonic"));
		
		this.typeButtons = new Button[5];
		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				update(note,getSelectedType());
			}
		};
		
		// Natural
		String label = "[" + TGEffectHarmonic.KEY_NATURAL + "] " + TuxGuitar.getProperty("effects.harmonic.natural");
		initButton(group,listener,0,TGEffectHarmonic.TYPE_NATURAL,label);
		
		// Artificial
		label = ("[" + TGEffectHarmonic.KEY_ARTIFICIAL + "] " + TuxGuitar.getProperty("effects.harmonic.artificial"));
		initButton(group,listener,1,TGEffectHarmonic.TYPE_ARTIFICIAL,label);
		
		// Tapped
		label = ("[" + TGEffectHarmonic.KEY_TAPPED + "] " + TuxGuitar.getProperty("effects.harmonic.tapped"));
		initButton(group,listener,2,TGEffectHarmonic.TYPE_TAPPED,label);
		
		// Pinch
		label = ("[" + TGEffectHarmonic.KEY_PINCH + "] " + TuxGuitar.getProperty("effects.harmonic.pinch"));
		initButton(group,listener,3,TGEffectHarmonic.TYPE_PINCH,label);
		
		// Semi
		label = ("[" + TGEffectHarmonic.KEY_SEMI + "] " + TuxGuitar.getProperty("effects.harmonic.semi"));
		initButton(group,listener,4,TGEffectHarmonic.TYPE_SEMI,label);
		
		this.harmonicDataCombo = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.harmonicDataCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		//---------------------------------------------------
		//------------------BUTTONS--------------------------
		//---------------------------------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(3,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.BOTTOM,true,true));
		
		Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				HarmonicEditor.this.result = getHarmonic();
				dialog.dispose();
			}
		});
		
		Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.setEnabled( note.getEffect().isHarmonic());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				HarmonicEditor.this.result = null;
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				HarmonicEditor.this.result = note.getEffect().getHarmonic();
				dialog.dispose();
			}
		});
		
		this.initDefaults(note);
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		return this.result;
	}
	
	private GridData resizeData(GridData data,int minWidth){
		data.minimumWidth = minWidth;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private void initButton(Composite parent,SelectionListener listener,int index, int type, String label){
		this.typeButtons[index] = new Button(parent,SWT.RADIO);
		this.typeButtons[index].setText(label);
		this.typeButtons[index].setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.typeButtons[index].setData(new Integer(type));
		this.typeButtons[index].addSelectionListener(listener);
	}
	
	protected void initDefaults(TGNote note){
		int type = TGEffectHarmonic.TYPE_NATURAL;
		if(note.getEffect().isHarmonic()){
			type = note.getEffect().getHarmonic().getType();
		}
		else{
			boolean naturalValid = false;
			for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
				if((note.getValue() % 12) == (TGEffectHarmonic.NATURAL_FREQUENCIES[i][0] % 12)  ){
					naturalValid = true;
					break;
				}
			}
			if(!naturalValid){
				this.typeButtons[0].setEnabled(false);
				type = TGEffectHarmonic.TYPE_ARTIFICIAL;
			}
			
		}
		for(int i = 0; i < this.typeButtons.length; i ++){
			int data = ((Integer)this.typeButtons[i].getData()).intValue();
			this.typeButtons[i].setSelection((data == type));
		}
		update(note,type);
	}
	
	protected int getSelectedType(){
		for(int i = 0; i < this.typeButtons.length; i ++){
			if(this.typeButtons[i].getSelection()){
				return ((Integer)this.typeButtons[i].getData()).intValue();
			}
		}
		return 0;
	}
	
	protected void update(TGNote note,int type){
		TGEffectHarmonic h = note.getEffect().getHarmonic();
		this.harmonicDataCombo.removeAll();
		this.harmonicDataCombo.setEnabled(type != TGEffectHarmonic.TYPE_NATURAL);
		if(type != TGEffectHarmonic.TYPE_NATURAL){
			String label = getTypeLabel(type);
			for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
				this.harmonicDataCombo.add(label + "(" + Integer.toString(TGEffectHarmonic.NATURAL_FREQUENCIES[i][0]) + ")" );
			}
			this.harmonicDataCombo.select((h != null && h.getType() == type)?h.getData():0);
		}
	}
	
	private String getTypeLabel(int type){
		if(type == TGEffectHarmonic.TYPE_NATURAL){
			return TGEffectHarmonic.KEY_NATURAL;
		}
		if(type == TGEffectHarmonic.TYPE_ARTIFICIAL){
			return TGEffectHarmonic.KEY_ARTIFICIAL;
		}
		if(type == TGEffectHarmonic.TYPE_TAPPED){
			return TGEffectHarmonic.KEY_TAPPED;
		}
		if(type == TGEffectHarmonic.TYPE_PINCH){
			return TGEffectHarmonic.KEY_PINCH;
		}
		if(type == TGEffectHarmonic.TYPE_SEMI){
			return TGEffectHarmonic.KEY_SEMI;
		}
		return new String();
	}
	
	public TGEffectHarmonic getHarmonic(){
		int type = getSelectedType();
		if(type > 0){
			TGEffectHarmonic effect = TuxGuitar.instance().getSongManager().getFactory().newEffectHarmonic();
			effect.setType(type);
			effect.setData(this.harmonicDataCombo.getSelectionIndex());
			return effect;
		}
		return null;
	}
}
