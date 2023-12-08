package org.herac.tuxguitar.app.tools.custom.tuner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerDialog implements TGTunerListener {
	
	private TGContext context;
	private TGTuner tuner = null;
	private int[] tuning = null;
	private UILabel currentFrequency = null;
	private UIWindow dialog = null;
	private TGTunerRoughWidget roughTuner = null;
	private List<TGTuningString> allStringButtons = null;
	private TGTunerFineWidget fineTuner = null;
	
	public TGTunerDialog(TGContext context, int[] tuning) {
		this.context = context;
		this.tuning = tuning;
	}
	
	public void show() {
		final UIFactory uiFactory = this.getUIFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setText(TuxGuitar.getProperty("tuner.instrument-tuner"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(this.dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tuner.tuner"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, null, null);
		
		UITableLayout specialLayout = new UITableLayout(0f);
		UIPanel specialComposite = uiFactory.createPanel(group, false);
		specialComposite.setLayout(specialLayout);
		groupLayout.set(specialComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		this.allStringButtons = new ArrayList<TGTuningString>(this.tuning.length);
		
		this.fineTuner = new TGTunerFineWidget(this.context, uiFactory, specialComposite);
		specialLayout.set(this.fineTuner.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		
		UITableLayout stringButtonsLayout = new UITableLayout();
		UIPanel stringButtonsComposite = uiFactory.createPanel(specialComposite, false);
		stringButtonsComposite.setLayout(stringButtonsLayout);
		specialLayout.set(stringButtonsComposite, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);
		
		for (int i=0; i<this.tuning.length; i++) {
			createTuningString(uiFactory, stringButtonsComposite, i);
		}
		
		UITableLayout tunLayout = new UITableLayout();
		UIPanel tunComposite = uiFactory.createPanel(group, false);
		tunComposite.setLayout(tunLayout);
		groupLayout.set(tunComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.currentFrequency = uiFactory.createLabel(tunComposite);
		tunLayout.set(this.currentFrequency, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.roughTuner = new TGTunerRoughWidget(this.context, uiFactory, group);
		groupLayout.set(this.roughTuner.getControl(), 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 600f, null, null);
		
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
        UIButton buttonSettings = uiFactory.createButton(buttons);
        buttonSettings.setText(TuxGuitar.getProperty("settings"));
        buttonSettings.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
            	TGTunerDialog.this.getTuner().pause();
            	new TGTunerSettingsDialog(TGTunerDialog.this).show();
            }
        });
        buttonsLayout.set(buttonSettings, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
        
        UIButton buttonExit = uiFactory.createButton(buttons);
        buttonExit.setText(TuxGuitar.getProperty("close"));
        buttonExit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
            	TGTunerDialog.this.getTuner().setCanceled(true);
            	TGTunerDialog.this.dialog.dispose();
            }
        });
		buttonsLayout.set(buttonExit, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonExit, UITableLayout.MARGIN_RIGHT, 0f);
        
        // if closed on [X], set this.tuner.setCanceled(true);
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
            	TGTunerDialog.this.getTuner().setCanceled(true);
            	TGTunerDialog.this.dialog.dispose();
			}
        });

        TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);

        // start the tuner thread
        this.tuner = new TGTuner(this);
        this.getTuner().start();
        
	}
	
	public void fireFrequency(final double freq) {
		if (!this.dialog.isDisposed()) {
			 TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {				
				 public void run() {
					if (!TGTunerDialog.this.dialog.isDisposed() && !TGTunerDialog.this.roughTuner.isDisposed()) {
						TGTunerDialog.this.currentFrequency.setText(Math.floor(freq)+" Hz");
						TGTunerDialog.this.roughTuner.setCurrentFrequency(freq);
					}
					if (!TGTunerDialog.this.dialog.isDisposed() && !TGTunerDialog.this.fineTuner.isDisposed()) {
						TGTunerDialog.this.fineTuner.setCurrentFrequency(freq);
					}
				 }
			 });
		}
	}

	
	public TGTuner getTuner() {
		return this.tuner;
	}
	
	public int[] getTuning() {
		return this.tuning;
	}


	public void fireException(final Exception ex) {
		TGErrorManager.getInstance(this.context).handleError(ex);
	}
	
	public void fireCurrentString(final int string) {
		this.tuner.pause();
		if (string == 0) { // TODO: it never happens
			this.tuner.setWantedRange();
			this.fineTuner.getControl().setEnabled(false);
		}
		else {
			this.tuner.setWantedNote(string);
			this.fineTuner.setWantedTone(string);
		}
		this.tuner.resumeFromPause();
	}
	
	private void createTuningString(UIFactory factory, UILayoutContainer parent, int index) {
		TGTuningString tempString = new TGTuningString(factory, parent, this, this.tuning[index]);
		this.allStringButtons.add(tempString);
		tempString.getStringButton().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				// disable all others
				TGTunerDialog.this.fineTuner.setCurrentFrequency(-1);
				Iterator<TGTuningString> it = TGTunerDialog.this.allStringButtons.iterator();
				while (it.hasNext()) {
					TGTuningString tmp = (TGTuningString)it.next();
					tmp.getStringButton().setSelected(false);
				}
			}
		});
		tempString.addListener();
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(tempString.getStringButton(), (index + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public UIWindow getWindow() {
		return this.dialog;
	}
	
	public TGContext getContext() {
		return context;
	}
}
