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
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
	protected Combo bufferSizeCombo = null;
	protected Combo FFTSizeCombo = null;
	protected Scale noiseGate = null;
	protected Label noiseGateValue = null;
	protected Text settingsInfo = null;
	protected boolean updated;
	
	public TGTunerSettingsDialog(TGTunerDialog dialog) {
		this.tunerDialog = dialog;
		this.updated=false;
	}

	public void show() {
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(),SWT.DIALOG_TRIM | SWT.RESIZE);
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
		
		Composite sampleComposite = this.createGroup(TuxGuitar.getProperty("tuner.sound-format"), group);

		
		new Label(sampleComposite,SWT.LEFT).setText(TuxGuitar.getProperty("tuner.sample-rate"));
		this.sampleRateCombo = new Combo(sampleComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.sampleRateCombo.add("48000");
		this.sampleRateCombo.add("44100");
		this.sampleRateCombo.add("22050");
		this.sampleRateCombo.add("11025");
		this.sampleRateCombo.add("8000");
		this.sampleRateCombo.addSelectionListener(new UpdatedListener());

		
		new Label(sampleComposite,SWT.LEFT).setText(TuxGuitar.getProperty("tuner.sample-size"));
		this.sampleSizeCombo = new Combo(sampleComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.sampleSizeCombo.add("16");
		this.sampleSizeCombo.add("8");
		this.sampleSizeCombo.addSelectionListener(new UpdatedListener());


		Composite analyzeComposite = this.createGroup(TuxGuitar.getProperty("tuner.sampling-and-analyze"), group);

		// buffer size
		new Label(analyzeComposite,SWT.LEFT).setText(TuxGuitar.getProperty("tuner.sampling-buffer-size"));
		this.bufferSizeCombo = new Combo(analyzeComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.bufferSizeCombo.add(new Integer(512).toString());
		this.bufferSizeCombo.add(new Integer(1024).toString());
		this.bufferSizeCombo.add(new Integer(2048).toString());
		this.bufferSizeCombo.add(new Integer(4096).toString());
		this.bufferSizeCombo.add(new Integer(8192).toString());
		this.bufferSizeCombo.add(new Integer(16348).toString());
		this.bufferSizeCombo.addSelectionListener(new UpdatedListener());

		
		// FFT buffer size
		new Label(analyzeComposite,SWT.LEFT).setText(TuxGuitar.getProperty("tuner.fourier-buffer-size"));
		this.FFTSizeCombo = new Combo(analyzeComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.FFTSizeCombo.add(new Integer(1024).toString());
		this.FFTSizeCombo.add(new Integer(2048).toString());
		this.FFTSizeCombo.add(new Integer(4096).toString());
		this.FFTSizeCombo.add(new Integer(8192).toString());
		this.FFTSizeCombo.add(new Integer(16384).toString());
		this.FFTSizeCombo.add(new Integer(32768).toString());
		this.FFTSizeCombo.addSelectionListener(new UpdatedListener());
		
		Composite noiseGateComposite = this.createGroup(TuxGuitar.getProperty("tuner.noise-gate"), group);
		this.noiseGate = new Scale(noiseGateComposite, SWT.BORDER);
		this.noiseGate.setMaximum(100);
		this.noiseGate.setIncrement(5);
		this.noiseGate.setPageIncrement(10);
		this.noiseGate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGTunerSettingsDialog.this.noiseGateValue.setText(
						new Integer(TGTunerSettingsDialog.this.noiseGate.getSelection()).toString()+"%");
			}
		});
		this.noiseGate.addSelectionListener(new UpdatedListener());
		this.noiseGate.setLayoutData(new GridData(SWT.None, SWT.NONE, true,false,1,1));
		((GridData)this.noiseGate.getLayoutData()).widthHint=270;
		((GridData)this.noiseGate.getLayoutData()).grabExcessHorizontalSpace=true;
		this.noiseGateValue = new Label(noiseGateComposite,SWT.LEFT);
		this.noiseGateValue.setText("                       ");
		
		Composite infoComposite = this.createGroup(TuxGuitar.getProperty("tuner.info"), group);
		
		this.settingsInfo = new Text(infoComposite, SWT.READ_ONLY | SWT.MULTI );
		this.settingsInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,2,1));
		((GridData)this.settingsInfo.getLayoutData()).heightHint=30;
		((GridData)this.settingsInfo.getLayoutData()).grabExcessHorizontalSpace=true;
		((GridData)this.settingsInfo.getLayoutData()).grabExcessVerticalSpace=true;
		((GridData)this.settingsInfo.getLayoutData()).widthHint=300;
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
			while (!found) {
				if ( Integer.parseInt(this.FFTSizeCombo.getItem(i)) == settings.getFFTSize()  ) {
							this.FFTSizeCombo.select(i);
							found=true;
				}
				i++;
			}
			
			this.bufferSizeCombo.setText(new Integer(settings.getBufferSize()).toString());
			this.noiseGate.setSelection((int)Math.round(settings.getTreshold()*100));
			this.noiseGateValue.setText(new Integer(this.noiseGate.getSelection()).toString()+"%");

/*			i=0; found=false;
			if (settings.deviceName==null)
				found=true;
			while (!found) {
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
		try {
			if (this.updated & saveWanted) {
				TGTunerSettings settings = new TGTunerSettings();
				settings.setSampleRate(this.getSampleRate());
				settings.setSampleSize(Integer.parseInt(this.sampleSizeCombo.getItem(this.sampleSizeCombo.getSelectionIndex())));
	
				settings.setBufferSize(this.getBufferSize());
				settings.setFFTSize(this.getFFTSize());
				settings.setTreshold((float)this.noiseGate.getSelection()/100);
				settings.setWaitPeriod(100); // TODO: hard coded?
				
				this.checkBufferValues(settings); // check if they are divisable with buffer size
				
	
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
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.errorMessage(dialog,ex.getMessage());
		}
	}
	
	
	
	
	private float getSampleRate() {
		return Float.parseFloat(this.sampleRateCombo.getItem(this.sampleRateCombo.getSelectionIndex()));
	}
	private int getFFTSize() {
		return Integer.parseInt(this.FFTSizeCombo.getItem(this.FFTSizeCombo.getSelectionIndex()));
	}
	private int getBufferSize() {
		return Integer.parseInt(this.bufferSizeCombo.getText());
	}
	
	

	/** adapter class which sets update flag */
	protected class UpdatedListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent arg0) {
        	TGTunerSettingsDialog.this.updated=true;
        	TGTunerSettingsDialog.this.settingsInfo.setText(" Minimal freq diff = "+this.getMinimalFrequencyDiff()+"Hz   \n Time to fill the buffer = "+ this.getTimeToFillBuffer()+" sec");
        }
		
    	private double getMinimalFrequencyDiff() {
    		return ((double) TGTunerSettingsDialog.this.getSampleRate()) / TGTunerSettingsDialog.this.getFFTSize();
    	}
    	
    	private double getTimeToFillBuffer() {
    		return TGTunerSettingsDialog.this.getBufferSize() / TGTunerSettingsDialog.this.getSampleRate();
    	}

	}
	
	
	

	/** because there are many groups */
	protected Composite createGroup(String groupCaption, Composite parent) {
		Group tempGroup = new Group(parent,SWT.SHADOW_ETCHED_IN);            
		tempGroup.setLayout(new GridLayout());
		tempGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		tempGroup.setText(groupCaption);
		Composite groupComposite = new Composite(tempGroup,SWT.NONE);
		groupComposite.setLayout(new GridLayout(2,false));
		groupComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		return groupComposite;
	}
	
	protected void checkBufferValues(TGTunerSettings settings) throws Exception {
		if (settings.bufferSize % settings.sampleSize != 0 ||
			settings.bufferSize > settings.fftSize	)
			throw new Exception("Invalid sampling buffer size");
	}


}
