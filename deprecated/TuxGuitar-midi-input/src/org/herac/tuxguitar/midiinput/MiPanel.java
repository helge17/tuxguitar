package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

class MiPanel
{
	private			UIWindow					f_Dialog = null;
	private			UIDropDownSelect<Integer>	f_CmbMode;
	private			UIButton					f_BtnConfig;
	private			UIButton					f_BtnRecord;
	private			UIButton					f_BtnStop;
	static private	MiPanel						s_Instance;


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


	void	showDialog(final TGContext context, UIWindow parent)
	{
	if( f_Dialog != null && !f_Dialog.isDisposed() )
		f_Dialog.moveToTop();
	else
		{
		try {
			final UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
			final UITableLayout dialogLayout = new UITableLayout();
			
			f_Dialog = uiFactory.createWindow(parent, true, false);
			f_Dialog.setLayout(dialogLayout);
			f_Dialog.setText(TuxGuitar.getProperty("midiinput.panel.title"));

			// MODE
			UITableLayout groupModeLayout = new UITableLayout();
			UILegendPanel groupMode = uiFactory.createLegendPanel(f_Dialog);
			groupMode.setLayout(groupModeLayout);
			groupMode.setText(TuxGuitar.getProperty("midiinput.panel.label.group.mode"));
			dialogLayout.set(groupMode, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			// MODE combo
			UILabel lblMode = uiFactory.createLabel(groupMode);
			lblMode.setText(TuxGuitar.getProperty("midiinput.panel.label.mode") + ":");
			groupModeLayout.set(lblMode, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
			
			f_CmbMode = uiFactory.createDropDownSelect(groupMode);
			f_CmbMode.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("midiinput.mode.echo"), MiConfig.MODE_FRETBOARD_ECHO));
			f_CmbMode.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("midiinput.mode.chords"), MiConfig.MODE_CHORDS_RECORDING));
			f_CmbMode.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("midiinput.mode.scales"), MiConfig.MODE_SCALES_RECOGNITION));
			f_CmbMode.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("midiinput.mode.record"), MiConfig.MODE_SONG_RECORDING));
			f_CmbMode.setSelectedValue(MiConfig.instance().getMode());

			f_CmbMode.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					Integer mode = f_CmbMode.getSelectedValue();

					if(mode != null && mode != MiConfig.instance().getMode())
						{
						MiConfig.getConfig().setValue(MiConfig.KEY_MODE, mode);
						MiConfig.getConfig().save();

						MiProvider.instance().setMode(mode);
						updateControls();
						}
				}
			});
			groupModeLayout.set(f_CmbMode, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
			
			// CONFIGURE button
			f_BtnConfig = uiFactory.createButton(groupMode);
			f_BtnConfig.setText(TuxGuitar.getProperty("midiinput.panel.button.config"));
			f_BtnConfig.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					MiConfig.instance().showDialog(f_Dialog);
				}
			});
			groupModeLayout.set(f_BtnConfig, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 1, 80f, 25f, null);
			
		///* RECORDING
			// Recording
			UITableLayout groupRecLayout = new UITableLayout();
			UILegendPanel groupRec = uiFactory.createLegendPanel(f_Dialog);
			groupRec.setLayout(groupRecLayout);
			groupRec.setText(TuxGuitar.getProperty("midiinput.panel.label.group.rec"));
			dialogLayout.set(groupRec, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			// START button
			f_BtnRecord = uiFactory.createButton(groupRec);
			f_BtnRecord.setText(TuxGuitar.getProperty("midiinput.panel.button.start"));
			f_BtnRecord.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					MiRecorder.instance().start();
					updateControls();
				}
			});
			groupRecLayout.set(f_BtnRecord, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 80f, 25f, null);
			
			// STOP button
			f_BtnStop = uiFactory.createButton(groupRec);
			f_BtnStop.setText(TuxGuitar.getProperty("midiinput.panel.button.stop"));
			f_BtnStop.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					MiRecorder.instance().stop();
					updateControls();
				}
			});
			groupRecLayout.set(f_BtnStop, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 80f, 25f, null);
		//*/

			updateControls();
			TGDialogUtil.openDialog(f_Dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
			}
		catch(Exception e)
			{
			TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(e);
			}
		}
	}
}
