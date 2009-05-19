package org.herac.tuxguitar.gui.editors.effects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;

public class GraceEditor extends SelectionAdapter{
	
	public static final int WIDTH = 400;
	
	public static final int HEIGHT = 0;
	
	private Spinner fretSpinner;
	private Button deadButton;
	private Button beforeBeatButton;
	private Button onBeatButton;
	private Button durationButton1;
	private Button durationButton2;
	private Button durationButton3;
	private Button pppButton;
	private Button ppButton;
	private Button pButton;
	private Button mpButton;
	private Button mfButton;
	private Button fButton;
	private Button ffButton;
	private Button fffButton;
	private Button noneButton;
	private Button slideButton;
	private Button bendButton;
	private Button hammerButton;
	
	protected TGEffectGrace result;
	
	public GraceEditor(){
		super();
	}
	
	private static final int LAYOUT_COLUMNS = 2;
	
	public TGEffectGrace show(final TGNote note){
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("effects.grace-editor"));
		dialog.setMinimumSize(360,360);
		
		Composite composite = new Composite(dialog,SWT.NONE);
		composite.setLayout(new GridLayout(LAYOUT_COLUMNS,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		int horizontalSpan = 2;
		
		//-----defaults-------------------------------------------------
		boolean dead = false;
		boolean onBeat = false;
		int fret = note.getValue();
		int duration = 1;
		int dynamic = TGVelocities.DEFAULT;
		int transition = TGEffectGrace.TRANSITION_NONE;
		if(note.getEffect().isGrace()){
			dead = note.getEffect().getGrace().isDead();
			fret = note.getEffect().getGrace().getFret();
			onBeat = note.getEffect().getGrace().isOnBeat();
			duration = note.getEffect().getGrace().getDuration();
			dynamic = note.getEffect().getGrace().getDynamic();
			transition = note.getEffect().getGrace().getTransition();
		}
		//---------------------------------------------------
		//------------------NOTE-----------------------------
		//---------------------------------------------------
		Group noteGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("note"));
		noteGroup.setLayout(new GridLayout(2,false));
		
		Label fretLabel = new Label(noteGroup,SWT.NONE);
		
		fretLabel.setText(TuxGuitar.getProperty("fret") + ": ");
		
		this.fretSpinner = new Spinner(noteGroup,SWT.BORDER);
		this.fretSpinner.setLayoutData(makeGridData(1));
		this.fretSpinner.setSelection(fret);
		
		this.deadButton = new Button(noteGroup,SWT.CHECK);
		this.deadButton.setText(TuxGuitar.getProperty("note.deadnote"));
		this.deadButton.setLayoutData(makeGridData(2));
		this.deadButton.setSelection(dead);
		//---------------------------------------------------
		//------------------POSITION-------------------------
		//---------------------------------------------------
		Group positionGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("position"));
		positionGroup.setLayout(new GridLayout());
		
		this.beforeBeatButton = new Button(positionGroup,SWT.RADIO);
		this.beforeBeatButton.setText(TuxGuitar.getProperty("effects.grace.before-beat"));
		this.beforeBeatButton.setLayoutData(makeGridData(1));
		this.beforeBeatButton.setSelection(!onBeat);
		
		this.onBeatButton = new Button(positionGroup,SWT.RADIO);
		this.onBeatButton.setText(TuxGuitar.getProperty("effects.grace.on-beat"));
		this.onBeatButton.setLayoutData(makeGridData(1));
		this.onBeatButton.setSelection(onBeat);
		//---------------------------------------------------
		//------------------DURATION-------------------------
		//---------------------------------------------------
		Group durationGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("duration"));
		durationGroup.setLayout(new GridLayout(3,false));
		
		this.durationButton1 = new Button(durationGroup,SWT.RADIO);
		this.durationButton1.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.durationButton1.setLayoutData(makeGridData(1));
		this.durationButton1.setSelection(duration == 1);
		
		this.durationButton2 = new Button(durationGroup,SWT.RADIO);
		this.durationButton2.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.durationButton2.setLayoutData(makeGridData(1));
		this.durationButton2.setSelection(duration == 2);
		
		this.durationButton3 = new Button(durationGroup,SWT.RADIO);
		this.durationButton3.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.durationButton3.setLayoutData(makeGridData(1));
		this.durationButton3.setSelection(duration == 3);
		
		horizontalSpan = 1;
		//---------------------------------------------------
		//------------------DYNAMIC--------------------------
		//---------------------------------------------------
		Group dynamicGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("dynamic"));
		dynamicGroup.setLayout(new GridLayout(2,false));
		
		this.pppButton = new Button(dynamicGroup,SWT.RADIO);
		this.pppButton.setImage(TuxGuitar.instance().getIconManager().getDynamicPPP());
		this.pppButton.setLayoutData(makeGridData(1));
		this.pppButton.setSelection(dynamic == TGVelocities.PIANO_PIANISSIMO);
		
		this.mfButton = new Button(dynamicGroup,SWT.RADIO);
		this.mfButton.setImage(TuxGuitar.instance().getIconManager().getDynamicMF());
		this.mfButton.setLayoutData(makeGridData(1));
		this.mfButton.setSelection(dynamic == TGVelocities.MEZZO_FORTE);
		
		this.ppButton = new Button(dynamicGroup,SWT.RADIO);
		this.ppButton.setImage(TuxGuitar.instance().getIconManager().getDynamicPP());
		this.ppButton.setLayoutData(makeGridData(1));
		this.ppButton.setSelection(dynamic == TGVelocities.PIANISSIMO);
		
		this.fButton = new Button(dynamicGroup,SWT.RADIO);
		this.fButton.setImage(TuxGuitar.instance().getIconManager().getDynamicF());
		this.fButton.setLayoutData(makeGridData(1));
		this.fButton.setSelection(dynamic == TGVelocities.FORTE);
		
		this.pButton = new Button(dynamicGroup,SWT.RADIO);
		this.pButton.setImage(TuxGuitar.instance().getIconManager().getDynamicP());
		this.pButton.setLayoutData(makeGridData(1));
		this.pButton.setSelection(dynamic == TGVelocities.PIANO);
		
		this.ffButton = new Button(dynamicGroup,SWT.RADIO);
		this.ffButton.setImage(TuxGuitar.instance().getIconManager().getDynamicFF());
		this.ffButton.setLayoutData(makeGridData(1));
		this.ffButton.setSelection(dynamic == TGVelocities.FORTISSIMO);
		
		this.mpButton = new Button(dynamicGroup,SWT.RADIO);
		this.mpButton.setImage(TuxGuitar.instance().getIconManager().getDynamicMP());
		this.mpButton.setLayoutData(makeGridData(1));
		this.mpButton.setSelection(dynamic == TGVelocities.MEZZO_PIANO);
		
		this.fffButton = new Button(dynamicGroup,SWT.RADIO);
		this.fffButton.setImage(TuxGuitar.instance().getIconManager().getDynamicFFF());
		this.fffButton.setLayoutData(makeGridData(1));
		this.fffButton.setSelection(dynamic == TGVelocities.FORTE_FORTISSIMO);
		//---------------------------------------------------
		//------------------TRANSITION-----------------------
		//---------------------------------------------------
		Group transitionGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("effects.grace.transition"));
		transitionGroup.setLayout(new GridLayout());
		
		this.noneButton = new Button(transitionGroup,SWT.RADIO);
		this.noneButton.setText(TuxGuitar.getProperty("effects.grace.transition-none"));
		this.noneButton.setLayoutData(makeGridData(1));
		this.noneButton.setSelection(transition == TGEffectGrace.TRANSITION_NONE);
		
		this.bendButton = new Button(transitionGroup,SWT.RADIO);
		this.bendButton.setText(TuxGuitar.getProperty("effects.grace.transition-bend"));
		this.bendButton.setLayoutData(makeGridData(1));
		this.bendButton.setSelection(transition == TGEffectGrace.TRANSITION_BEND);
		
		this.slideButton = new Button(transitionGroup,SWT.RADIO);
		this.slideButton.setText(TuxGuitar.getProperty("effects.grace.transition-slide"));
		this.slideButton.setLayoutData(makeGridData(1));
		this.slideButton.setSelection(transition == TGEffectGrace.TRANSITION_SLIDE);
		
		this.hammerButton = new Button(transitionGroup,SWT.RADIO);
		this.hammerButton.setText(TuxGuitar.getProperty("effects.grace.transition-hammer"));
		this.hammerButton.setLayoutData(makeGridData(1));
		this.hammerButton.setSelection(transition == TGEffectGrace.TRANSITION_HAMMER);
		//---------------------------------------------------
		//------------------BUTTONS--------------------------
		//---------------------------------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(3,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.BOTTOM,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				GraceEditor.this.result = getGrace();
				dialog.dispose();
			}
		});
		
		Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				GraceEditor.this.result = null;
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				GraceEditor.this.result = note.getEffect().getGrace();
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		return this.result;
	}
	
	private Group makeGroup(Composite parent,int horizontalSpan,String text){
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(makeGridData(horizontalSpan));
		group.setText(text);
		
		return group;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private GridData makeGridData(int horizontalSpan){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.horizontalSpan = horizontalSpan;
		return data;
	}
	
	public TGEffectGrace getGrace(){
		TGEffectGrace effect = TuxGuitar.instance().getSongManager().getFactory().newEffectGrace();
		
		effect.setFret(this.fretSpinner.getSelection());
		effect.setDead(this.deadButton.getSelection());
		effect.setOnBeat(this.onBeatButton.getSelection());
		
		//duration
		if(this.durationButton1.getSelection()){
			effect.setDuration(1);
		}else if(this.durationButton2.getSelection()){
			effect.setDuration(2);
		}else if(this.durationButton3.getSelection()){
			effect.setDuration(3);
		}
		//velocity
		if(this.pppButton.getSelection()){
			effect.setDynamic(TGVelocities.PIANO_PIANISSIMO);
		}else if(this.ppButton.getSelection()){
			effect.setDynamic(TGVelocities.PIANISSIMO);
		}else if(this.pButton.getSelection()){
			effect.setDynamic(TGVelocities.PIANO);
		}else if(this.mpButton.getSelection()){
			effect.setDynamic(TGVelocities.MEZZO_PIANO);
		}else if(this.mfButton.getSelection()){
			effect.setDynamic(TGVelocities.MEZZO_FORTE);
		}else if(this.fButton.getSelection()){
			effect.setDynamic(TGVelocities.FORTE);
		}else if(this.ffButton.getSelection()){
			effect.setDynamic(TGVelocities.FORTISSIMO);
		}else if(this.fffButton.getSelection()){
			effect.setDynamic(TGVelocities.FORTE_FORTISSIMO);
		}
		
		//transition
		if(this.noneButton.getSelection()){
			effect.setTransition(TGEffectGrace.TRANSITION_NONE);
		}else if(this.slideButton.getSelection()){
			effect.setTransition(TGEffectGrace.TRANSITION_SLIDE);
		}else if(this.bendButton.getSelection()){
			effect.setTransition(TGEffectGrace.TRANSITION_BEND);
		}else if(this.hammerButton.getSelection()){
			effect.setTransition(TGEffectGrace.TRANSITION_HAMMER);
		}
		
		return effect;
	}
}
