package org.herac.tuxguitar.io.midi;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class MidiSettingsDialog {
	
	public static final int MAX_TRANSPOSE = 24;
	
	public static final int MIN_TRANSPOSE = -24;
	
	private static final int STATUS_NONE = 0;
	
	private static final int STATUS_CANCELLED = 1;
	
	private static final int STATUS_ACCEPTED = 2;
	
	protected int status;
	
	protected MidiSettings settings;
	
	public MidiSettingsDialog(){
		this.settings = new MidiSettings();
	}
	
	public MidiSettings open() {
		this.status = STATUS_NONE;
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText("Options");
		
		//------------------TRACK SELECTION------------------
		Group trackGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		trackGroup.setLayout(new GridLayout(2,false));
		trackGroup.setLayoutData(getGroupData());
		trackGroup.setText("Transpose notes");
		
		//------------------TRANSPOSE----------------------
		Label transposeLabel = new Label(trackGroup, SWT.NONE);
		transposeLabel.setText("Transpose:");
		transposeLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Combo transposeCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		transposeCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		for(int i = MIN_TRANSPOSE;i <= MAX_TRANSPOSE;i ++){
			transposeCombo.add(Integer.toString(i));
		}
		transposeCombo.select(-MIN_TRANSPOSE);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				MidiSettingsDialog.this.status = STATUS_ACCEPTED;
				MidiSettingsDialog.this.settings.setTranspose( (MIN_TRANSPOSE + transposeCombo.getSelectionIndex()) );
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				MidiSettingsDialog.this.status = STATUS_CANCELLED;
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return ((this.status == STATUS_ACCEPTED)?MidiSettingsDialog.this.settings:null);
	}
	
	private GridData getGroupData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 300;
		return data;
	}
}
