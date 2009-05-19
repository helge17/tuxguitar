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
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

public class TrillEditor extends SelectionAdapter{
	
	public static final int WIDTH = 400;
	
	public static final int HEIGHT = 0;
	
	private Spinner fretSpinner;
	private Button sixtyFourthButton;
	private Button thirtySecondButton;
	private Button sixTeenthButton;
	
	protected TGEffectTrill result;
	
	public TrillEditor(){
		super();
	}
	
	public TGEffectTrill show(final TGNote note){
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("effects.trill-editor"));
		
		
		Composite composite = new Composite(dialog,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		int horizontalSpan = 2;
		
		//-----defaults-------------------------------------------------
		int fret = note.getValue();
		int duration = TGDuration.SIXTEENTH;
		if(note.getEffect().isTrill()){
			fret = note.getEffect().getTrill().getFret();
			duration = note.getEffect().getTrill().getDuration().getValue();
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
		
		//---------------------------------------------------
		//------------------DURATION-------------------------
		//---------------------------------------------------
		Group durationGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("duration"));
		durationGroup.setLayout(new GridLayout(3,false));
		
		this.sixtyFourthButton = new Button(durationGroup,SWT.RADIO);
		this.sixtyFourthButton.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.sixtyFourthButton.setLayoutData(makeGridData(1));
		this.sixtyFourthButton.setSelection(duration == TGDuration.SIXTY_FOURTH);
		
		this.thirtySecondButton = new Button(durationGroup,SWT.RADIO);
		this.thirtySecondButton.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.thirtySecondButton.setLayoutData(makeGridData(1));
		this.thirtySecondButton.setSelection(duration == TGDuration.THIRTY_SECOND);
		
		this.sixTeenthButton = new Button(durationGroup,SWT.RADIO);
		this.sixTeenthButton.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.sixTeenthButton.setLayoutData(makeGridData(1));
		this.sixTeenthButton.setSelection(duration == TGDuration.SIXTEENTH);
		
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
				TrillEditor.this.result = getTrill();
				dialog.dispose();
			}
		});
		
		Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TrillEditor.this.result = null;
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TrillEditor.this.result = note.getEffect().getTrill();
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
	
	public TGEffectTrill getTrill(){
		TGEffectTrill effect = TuxGuitar.instance().getSongManager().getFactory().newEffectTrill();
		effect.setFret(this.fretSpinner.getSelection());
		if(this.sixtyFourthButton.getSelection()){
			effect.getDuration().setValue(TGDuration.SIXTY_FOURTH);
		}else if(this.thirtySecondButton.getSelection()){
			effect.getDuration().setValue(TGDuration.THIRTY_SECOND);
		}else if(this.sixTeenthButton.getSelection()){
			effect.getDuration().setValue(TGDuration.SIXTEENTH);
		}else{
			return null;
		}
		return effect;
	}
}
