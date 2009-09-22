package org.herac.tuxguitar.gui.editors.effects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGStroke;

public class StrokeEditor extends SelectionAdapter{
	
	public static final int WIDTH = 400;
	
	public static final int HEIGHT = 0;
	
	public static final int STATUS_OK = 1;
	public static final int STATUS_CLEAN = 2;
	public static final int STATUS_CANCEL = 3;
	
	private Button duration4;
	private Button duration8;
	private Button duration16;
	private Button duration32;
	private Button duration64;
	
	private int status;
	private int value;
	
	public StrokeEditor(){
		super();
	}
	
	public void open(final TGBeat beat){
		this.status = STATUS_CANCEL;
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("beat.stroke"));
		
		//-----defaults-------------------------------------------------
		int duration = TGDuration.SIXTEENTH;
		if( beat.getStroke().getDirection() != TGStroke.STROKE_NONE ){
			duration = beat.getStroke().getValue();
		}
		
		//---------------------------------------------------
		//------------------DURATION-------------------------
		//---------------------------------------------------
		Group group = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setLayout(new GridLayout(5,false));
		group.setText( TuxGuitar.getProperty("duration") );
		
		this.duration64 = new Button(group,SWT.RADIO);
		this.duration64.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.duration64.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.duration64.setSelection(duration == TGDuration.SIXTY_FOURTH);
		
		this.duration32 = new Button(group,SWT.RADIO);
		this.duration32.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.duration32.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.duration32.setSelection(duration == TGDuration.THIRTY_SECOND);
		
		this.duration16 = new Button(group,SWT.RADIO);
		this.duration16.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.duration16.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.duration16.setSelection(duration == TGDuration.SIXTEENTH);
		
		this.duration8 = new Button(group,SWT.RADIO);
		this.duration8.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.EIGHTH));
		this.duration8.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.duration8.setSelection(duration == TGDuration.EIGHTH);
		
		this.duration4 = new Button(group,SWT.RADIO);
		this.duration4.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.QUARTER));
		this.duration4.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.duration4.setSelection(duration == TGDuration.QUARTER);
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
				setStatus( STATUS_OK );
				setValue( getSelection() );
				dialog.dispose();
			}
		});
		
		Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setStatus( STATUS_CLEAN );
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setStatus( STATUS_CANCEL );
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected int getSelection(){
		if( this.duration4.getSelection() ){
			return TGDuration.QUARTER;
		}
		if( this.duration8.getSelection() ){
			return TGDuration.EIGHTH;
		}
		if( this.duration16.getSelection() ){
			return TGDuration.SIXTEENTH;
		}
		if( this.duration32.getSelection() ){
			return TGDuration.THIRTY_SECOND;
		}
		if( this.duration64.getSelection() ){
			return TGDuration.SIXTY_FOURTH;
		}
		return 0;
	}
	
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
