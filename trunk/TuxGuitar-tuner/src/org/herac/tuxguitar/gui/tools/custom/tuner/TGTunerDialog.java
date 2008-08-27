package org.herac.tuxguitar.gui.tools.custom.tuner;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerDialog implements TGTunerListener {
	
	private static final int SHELL_WIDTH = 400;
	protected TGTuner tuner = null;
	protected int[] tuning = null;
	protected Label currentFrequency = null;
	protected Shell dialog = null;
	protected TGTunerRoughWidget roughTuner = null;
	protected ArrayList allStringButtons = null;
	protected TGTunerFineWidget fineTuner = null;
	
	TGTunerDialog(int[] tuning) {
		this.tuning = tuning;
	}


	public void show() {

		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(),SWT.DIALOG_TRIM | SWT.RESIZE);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		this.dialog.setText(TuxGuitar.getProperty("tuner.instrument-tuner"));
		this.dialog.setMinimumSize(SHELL_WIDTH,SWT.DEFAULT);
		this.dialog.setSize(700, 400);
		
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);            
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("tuner.tuner"));
		
		Composite specialComposite = new Composite(group,SWT.NONE);
		specialComposite.setLayout(new GridLayout(2,false));
		specialComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.allStringButtons = new ArrayList(this.tuning.length);
		
		this.fineTuner = new TGTunerFineWidget(specialComposite);
		
		
		Composite buttonsComposite = new Composite (specialComposite,SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(1,false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		for (int i=0; i<this.tuning.length; i++)
			createTuningString(this.tuning[i],buttonsComposite);

		Composite tunComposite = new Composite(group,SWT.NONE);
		tunComposite.setLayout(new GridLayout(1,false));
		tunComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.currentFrequency = new Label(tunComposite,SWT.LEFT);
		this.currentFrequency.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.roughTuner = new TGTunerRoughWidget(group);
		
		Composite btnComposite = new Composite(group,SWT.NONE);
		btnComposite.setLayout(new GridLayout(2,false));
		btnComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
        final Button buttonSettings = new Button(btnComposite, SWT.PUSH);
        buttonSettings.setText(TuxGuitar.getProperty("settings"));
        buttonSettings.setLayoutData(getGridData(80,25));
        buttonSettings.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	TGTunerDialog.this.getTuner().pause();
            	new TGTunerSettingsDialog(TGTunerDialog.this).show();
            }
        });
        
        final Button buttonExit = new Button(btnComposite, SWT.PUSH);
        buttonExit.setText(TuxGuitar.getProperty("close"));
        buttonExit.setLayoutData(getGridData(80,25));
        buttonExit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	TGTunerDialog.this.getTuner().setCanceled(true);
            	TGTunerDialog.this.dialog.dispose();
            }
        });

        
        // if closed on [X], set this.tuner.setCanceled(true);
        this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
            	TGTunerDialog.this.getTuner().setCanceled(true);
            	TGTunerDialog.this.dialog.dispose();
			}
        });

        DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);

        // start the tuner thread
        this.tuner = new TGTuner(this);
        this.getTuner().start();
        
	}

	static GridData getGridData(int minimumWidth, int minimumHeight){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = minimumWidth;
		data.minimumHeight = minimumHeight;
		return data;
	}

	
	public void fireFrequency(final double freq) {
		if (!this.dialog.isDisposed()) {
			 try {
				 TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {				
					 public void run() {
						if (!TGTunerDialog.this.dialog.isDisposed() && !TGTunerDialog.this.roughTuner.isDisposed()) {
							TGTunerDialog.this.currentFrequency.setText(Math.floor(freq)+" Hz");
							TGTunerDialog.this.roughTuner.setCurrentFrequency(freq);
						}
						if (!TGTunerDialog.this.dialog.isDisposed() && !TGTunerDialog.this.fineTuner.isDisposed())
								TGTunerDialog.this.fineTuner.setCurrentFrequency(freq);
					 }
				 });
			 } catch (Throwable e) {
				 e.printStackTrace();
			 }
		}
	}

	
	public TGTuner getTuner() {
		return this.tuner;
	}
	
	public int[] getTuning() {
		return this.tuning;
	}


	public void fireException(final Exception ex) {
		try {
			TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
				public void run() {
					if (!TGTunerDialog.this.dialog.isDisposed())
						MessageDialog.errorMessage(ex);
				}
			});
		} catch (Throwable e) {
			 e.printStackTrace();
		}
	}

	
	public void fireCurrentString(final int string) {
		this.tuner.pause();
		if (string == 0) { // TODO: it never happens
			this.tuner.setWantedRange();
			this.fineTuner.setEnabled(false);
		}
		else {
			this.tuner.setWantedNote(string);
			this.fineTuner.setWantedTone(string);
		}
		this.tuner.resumeFromPause();
	}
	
	
	
	protected void createTuningString(int midiNote, Composite parent) {
		TGTuningString tempString = new TGTuningString(midiNote,parent,this);
		this.allStringButtons.add(tempString);
		tempString.getStringButton().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				// disable all others
				TGTunerDialog.this.fineTuner.setCurrentFrequency(-1);
				Iterator it = TGTunerDialog.this.allStringButtons.iterator();
				while (it.hasNext()) {
					TGTuningString tmp = (TGTuningString)it.next();
					tmp.getStringButton().setSelection(false);
				}
			}
		});
		tempString.addListener();
		
	}
	
}
