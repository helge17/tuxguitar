package org.herac.tuxguitar.player.impl.midiport.oss;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.system.plugins.TGPluginConfigManager;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class MidiConfigUtils {
	
	public static final String DEVICE_KEY = "oss.device";
	
	public static final String DEVICE_DEFAULT = "/dev/sequencer";
	
	public static TGConfigManager getConfig(){
		TGConfigManager config = new TGPluginConfigManager("tuxguitar-oss");
		config.init();
		return config;
	}
	
	public static String getDevice(){
		return getDevice(getConfig());
	}
	
	public static String getDevice(final TGConfigManager config){
		return config.getStringConfigValue(DEVICE_KEY,DEVICE_DEFAULT);
	}
	
	public static void setupDialog(Shell parent,final MidiOutputPortProviderImpl provider) {
		setupDialog(parent,provider,getConfig());
	}
	
	public static void setupDialog(Shell parent,final MidiOutputPortProviderImpl provider,final TGConfigManager config) {
		final String device = getDevice(config);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText("Configuration");
		
		//------------------DEVICE-----------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText("Device Configuration");
		
		final Label deviceLabel = new Label(group,SWT.LEFT);
		deviceLabel.setText("Device:");
		
		final Text deviceValue = new Text(group,SWT.BORDER);
		deviceValue.setLayoutData(new GridData(250,SWT.DEFAULT));
		deviceValue.setText( (device == null ? new String() : device) );
		
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
				String selection = deviceValue.getText();
				
				String value1 = (device == null ? new String() : device);
				String value2 = (selection == null ? new String() : selection);
				if(!value1.equals(value2)){
					if(selection != null){
						config.setProperty(DEVICE_KEY,selection);
					}else{
						config.removeProperty(DEVICE_KEY);
					}
					config.save();
					provider.updateDevice(selection);
				}
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
}
