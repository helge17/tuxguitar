package org.herac.tuxguitar.midiinput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.util.error.TGErrorManager;

class MiPanel
{
	private			Shell		f_Dialog = null;
	private			Combo		f_CmbMode;
	private			Button		f_BtnConfig;
	private			Button		f_BtnRecord;
	private			Button		f_BtnStop;
	static private	MiPanel		s_Instance;


	static MiPanel	instance()
	{
	if(s_Instance == null)
		s_Instance = new MiPanel();

	return s_Instance;
	}


	void	updateControls()
	{
	f_CmbMode.setEnabled(!MiRecorder.instance().isRecording());
	f_BtnConfig.setEnabled(!MiRecorder.instance().isRecording());

///* RECORDING
	if(MiProvider.instance().getMode() != MiConfig.MODE_SONG_RECORDING)
		{
		f_BtnRecord.setEnabled(false);
		f_BtnStop.setEnabled(false);
		}
	else
		{
		f_BtnRecord.setEnabled(!MiRecorder.instance().isRecording());
		f_BtnStop.setEnabled(MiRecorder.instance().isRecording());
		}
//*/
	}


	void	showDialog(Shell parent)
	{
	if(f_Dialog != null)
		f_Dialog.forceActive();
	else
		{
		try {
			f_Dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM);
			f_Dialog.setLayout(new GridLayout());
			f_Dialog.setText(TuxGuitar.getProperty("midiinput.panel.title"));

			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.minimumWidth = 80;
			data.minimumHeight = 25;

			// MODE
			Group	groupMode = new Group(f_Dialog, SWT.SHADOW_ETCHED_IN);
			groupMode.setLayout(new GridLayout(3, false));
			groupMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			groupMode.setText(TuxGuitar.getProperty("midiinput.panel.label.group.mode"));

			// MODE combo
			Label	lblMode = new Label(groupMode, SWT.LEFT);
			lblMode.setText(TuxGuitar.getProperty("midiinput.panel.label.mode") + ":");

			f_CmbMode = new Combo(groupMode, SWT.DROP_DOWN | SWT.READ_ONLY);
			f_CmbMode.setLayoutData(new GridData(130, SWT.DEFAULT));

			f_CmbMode.add(TuxGuitar.getProperty("midiinput.mode.echo"));
			f_CmbMode.add(TuxGuitar.getProperty("midiinput.mode.chords"));
			f_CmbMode.add(TuxGuitar.getProperty("midiinput.mode.scales"));
		///* RECORDING
			f_CmbMode.add(TuxGuitar.getProperty("midiinput.mode.record"));
		//*/
			f_CmbMode.select(MiConfig.instance().getMode());

			f_CmbMode.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					int		mode = f_CmbMode.getSelectionIndex();

					if(mode != MiConfig.instance().getMode())
						{
						MiConfig.getConfig().setValue(MiConfig.KEY_MODE, mode);
						MiConfig.getConfig().save();

						MiProvider.instance().setMode(mode);
						updateControls();
						}
				}
			});

			// CONFIGURE button
			f_BtnConfig = new Button(groupMode, SWT.PUSH);
			f_BtnConfig.setLayoutData(data);

			f_BtnConfig.setText(TuxGuitar.getProperty("midiinput.panel.button.config"));
			f_BtnConfig.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					MiConfig.instance().showDialog(f_Dialog);
				}
			});

		///* RECORDING
			// Recording
			Group	groupRec = new Group(f_Dialog, SWT.SHADOW_ETCHED_IN);
			groupRec.setLayout(new GridLayout(2, false));
			groupRec.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			groupRec.setText(TuxGuitar.getProperty("midiinput.panel.label.group.rec"));

			// START button
			f_BtnRecord = new Button(groupRec, SWT.PUSH);
			f_BtnRecord.setLayoutData(data);

			f_BtnRecord.setText(TuxGuitar.getProperty("midiinput.panel.button.start"));
			f_BtnRecord.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					MiRecorder.instance().start();
					updateControls();
				}
			});

			// STOP button
			f_BtnStop = new Button(groupRec, SWT.PUSH);
			f_BtnStop.setLayoutData(data);

			f_BtnStop.setText(TuxGuitar.getProperty("midiinput.panel.button.stop"));
			f_BtnStop.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					MiRecorder.instance().stop();
					updateControls();
				}
			});
		//*/

			updateControls();
			DialogUtils.openDialog(f_Dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
			f_Dialog = null;
			}
		catch(Exception e)
			{
			TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(e);
			}
		}
	}
}
