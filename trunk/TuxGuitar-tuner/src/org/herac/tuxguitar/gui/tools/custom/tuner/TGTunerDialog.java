package org.herac.tuxguitar.gui.tools.custom.tuner;

import org.eclipse.swt.SWT;
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

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerDialog implements TGTunerListener {
	
	private static final int SHELL_WIDTH = 700;
	protected TGTuner tuner = null;
	protected int[] tuning = null;
	protected Label currentFrequency = null;
	protected Shell dialog = null;
	protected TGTunerRoughWidget roughTuner = null;
	
	TGTunerDialog(int[] tuning) {
		this.tuning = tuning;
	}


	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(),SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		this.dialog.setText(TuxGuitar.getProperty("tuner.instrument-tuner"));
		this.dialog.setMinimumSize(SHELL_WIDTH,SWT.DEFAULT);
		
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);            
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("tuner.tuner"));

		Composite tunComposite = new Composite(group,SWT.NONE);
		tunComposite.setLayout(new GridLayout(1,false));
		tunComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.currentFrequency = new Label(tunComposite,SWT.LEFT);
		this.currentFrequency.setText("temppppppppppppppppppppppppppppppp");		
		
		//////// to delete
		final Label tunLabel = new Label(tunComposite,SWT.LEFT);
		String tunLabelText = new String();
		for (int i=0; i<this.tuning.length; i++) {
			tunLabelText=tunLabelText+this.tuning[i]+" ";
		}
		tunLabel.setText(tunLabelText);
		/////// to delete

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
        
        // TODO: if closed on [X], set this.tuner.setCanceled(true);

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
			TuxGuitar.instance().getDisplay().syncExec(new Runnable() {
				public void run() {
					if (!TGTunerDialog.this.dialog.isDisposed())
						TGTunerDialog.this.currentFrequency.setText(Math.floor(freq)+" Hz");
						TGTunerDialog.this.roughTuner.setCurrentFrequency((int)Math.round(freq));
						TGTunerDialog.this.roughTuner.redraw();
				}
			});
	}

	
	public TGTuner getTuner() {
		return this.tuner;
	}
	
	public int[] getTuning() {
		return this.tuning;
	}


	public void fireException(final Exception ex) {
			TuxGuitar.instance().getDisplay().syncExec(new Runnable() {
				public void run() {
					if (!TGTunerDialog.this.dialog.isDisposed())
						MessageDialog.errorMessage(ex);
				}
			});
	}

	
}
