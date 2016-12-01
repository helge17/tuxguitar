package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class MidiConfigUtils {
	
	public static final String DEVICE_KEY = "oss.device";
	
	public static final String DEVICE_DEFAULT = "/dev/sequencer";
	
	public static TGConfigManager getConfig(TGContext context){
		return new TGConfigManager(context, "tuxguitar-oss");
	}
	
	public static String getDevice(TGContext context){
		return getDevice(getConfig(context));
	}
	
	public static String getDevice(final TGConfigManager config){
		return config.getStringValue(DEVICE_KEY,DEVICE_DEFAULT);
	}
	
	public static void setupDialog(TGContext context, UIWindow parent,final MidiOutputPortProviderImpl provider) {
		setupDialog(context, parent, provider, getConfig(context));
	}
	
	public static void setupDialog(TGContext context, UIWindow parent, final MidiOutputPortProviderImpl provider,final TGConfigManager config) {
		final String device = getDevice(config);
		
		final UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText("Configuration");
		
		//------------------DEVICE-----------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText("Device Configuration");
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);
		
		final UILabel deviceLabel = uiFactory.createLabel(group);
		deviceLabel.setText("Device:");
		groupLayout.set(deviceLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField deviceValue = uiFactory.createTextField(group);
		deviceValue.setText( (device == null ? new String() : device) );
		groupLayout.set(deviceValue, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------BUTTONS--------------------------		
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String selection = deviceValue.getText();
				
				String value1 = (device == null ? new String() : device);
				String value2 = (selection == null ? new String() : selection);
				if(!value1.equals(value2)){
					if(selection != null){
						config.setValue(DEVICE_KEY,selection);
					}else{
						config.remove(DEVICE_KEY);
					}
					config.save();
					provider.updateDevice(selection);
				}
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
}
