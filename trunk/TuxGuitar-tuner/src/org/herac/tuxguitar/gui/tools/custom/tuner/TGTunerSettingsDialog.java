package org.herac.tuxguitar.gui.tools.custom.tuner;


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
import org.herac.tuxguitar.gui.util.MessageDialog;


/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerSettingsDialog {

	private static final int SHELL_WIDTH = 350;
	protected TGTunerDialog tunerDialog = null;
	// protected Combo deviceCombo = null;
	protected Combo sampleRateCombo = null;
	protected Combo sampleSizeCombo = null;
	protected boolean updated;
	
	public TGTunerSettingsDialog(TGTunerDialog dialog) {
		this.tunerDialog = dialog;
		this.updated=false;
	}

	public void show() {
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(),SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout());
		dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuner.settings"));
		dialog.setMinimumSize(SHELL_WIDTH,SWT.DEFAULT);
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);            
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("tuner.device-settings"));
		
/*		this.deviceCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.deviceCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,2,1));
		this.fillDeviceCombo(dialog);
		this.deviceCombo.addSelectionListener(new UpdatedListener());
*/
		
		Group formatGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);            
		formatGroup.setLayout(new GridLayout());
		formatGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		formatGroup.setText(TuxGuitar.getProperty("tuner.sound-format"));
		
		
		Composite sampleComposite = new Composite(formatGroup,SWT.NONE);
		sampleComposite.setLayout(new GridLayout(2,false));
		sampleComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		
		new Label(sampleComposite,SWT.LEFT).setText(TuxGuitar.getProperty("tuner.sample-rate"));
		this.sampleRateCombo = new Combo(sampleComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.sampleRateCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,2,1));
		this.sampleRateCombo.add("48000");
		this.sampleRateCombo.add("44100");
		this.sampleRateCombo.add("22050");
		this.sampleRateCombo.add("11025");
		this.sampleRateCombo.add("8000");
		this.sampleRateCombo.addSelectionListener(new UpdatedListener());

		
		new Label(sampleComposite,SWT.LEFT).setText(TuxGuitar.getProperty("tuner.sample-size"));
		this.sampleSizeCombo = new Combo(sampleComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.sampleSizeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,2,1));
		this.sampleSizeCombo.add("16");
		this.sampleSizeCombo.add("8");
		this.sampleSizeCombo.addSelectionListener(new UpdatedListener());
		

		//// buttons ok/cancel
		Composite btnComposite = new Composite(dialog,SWT.NONE);
		btnComposite.setLayout(new GridLayout(2,false));
		btnComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(btnComposite, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(TGTunerDialog.getGridData(80,25));
		buttonOK.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	TGTunerSettingsDialog.this.dispose(dialog,true);
            }
        });
		final Button buttonExit = new Button(btnComposite, SWT.PUSH);
        buttonExit.setText(TuxGuitar.getProperty("close"));
        buttonExit.setLayoutData(TGTunerDialog.getGridData(80,25));
        buttonExit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	TGTunerSettingsDialog.this.dispose(dialog,false);
            }
        });
        
        this.loadSettings(this.tunerDialog.getTuner().getSettings(), dialog);
        
        DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}

	
	

	protected void loadSettings(TGTunerSettings settings, Shell dialog) {
		
		// TODO: this is no good! vvvvvvvvv
		
		boolean loadedDefaults=false;
		if (settings==null) {
			settings = TGTunerSettings.getDefaults();
			loadedDefaults=true;
		}
		// TODO: this is no good! ^^^^^^^^^
		
		boolean found = false;
		int i = 0;
		
		try {
			while (!found) {
				if ( Float.parseFloat(this.sampleRateCombo.getItem(i)) == settings.getSampleRate()  ) {
						this.sampleRateCombo.select(i);
						found=true;
				}
				i++;
			}
			i=0; found=false;
		
			while (!found) {
				if ( Integer.parseInt(this.sampleSizeCombo.getItem(i)) == settings.getSampleSize()  ) {
							this.sampleSizeCombo.select(i);
							found=true;
				}
				i++;
			}
			i=0; found=false;
			if (settings.deviceName==null)
				found=true;
/*			while (!found) {
				if ( this.deviceCombo.getItem(i).equals(settings.getDeviceName())  ) {
							this.deviceCombo.select(i);
							found=true;
				}
				i++;
			}
*/		} catch (Exception ex) {
			if (!loadedDefaults) {
				MessageDialog.errorMessage(dialog,"Failed to load TuxGuitar settings.\nLoading defaults.");
				loadSettings(TGTunerSettings.getDefaults(),dialog);
			}
		}
		
	}

	
/*	
	*//** cycles through available SourceData audio devices *//*
	private void fillDeviceCombo(Shell dialog) {
		Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			Mixer mixer = AudioSystem.getMixer(aInfos[i]);
			Line.Info lineInfo = new Line.Info(SourceDataLine.class);
			if (mixer.isLineSupported(lineInfo))
			{
				this.deviceCombo.add(aInfos[i].getName());
			}
		}
		if (aInfos.length == 0)
		{
			MessageDialog.errorMessage(dialog,"No input sound devices available.");
		}
	}

*/	
	protected void dispose(Shell dialog, boolean saveWanted) {
		if (updated & saveWanted) {
			TGTunerSettings settings = new TGTunerSettings();
			settings.setBufferSize(TGTunerSettings.DEFAULT_BUFFER_SIZE);
			settings.setSampleRate(Float.parseFloat(this.sampleRateCombo.getItem(this.sampleRateCombo.getSelectionIndex())));
			settings.setSampleSize(Integer.parseInt(this.sampleSizeCombo.getItem(this.sampleSizeCombo.getSelectionIndex())));
			
/*			if (this.deviceCombo.getSelectionIndex()<0) {
				MessageDialog.errorMessage(dialog,"You didn't set the input device.");
				return;
			}
			settings.setDeviceName(this.deviceCombo.getItem(this.deviceCombo.getSelectionIndex()));
*/			
			this.tunerDialog.getTuner().setSettings(settings);
			// TODO: save the settings in the system
		}
		
    	this.tunerDialog.getTuner().resumeFromPause();
		dialog.dispose();
	}
	
	
	

	/** adapter class which sets update flag */
	protected class UpdatedListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent arg0) {
        	TGTunerSettingsDialog.this.updated=true;;
        }
	}

}
