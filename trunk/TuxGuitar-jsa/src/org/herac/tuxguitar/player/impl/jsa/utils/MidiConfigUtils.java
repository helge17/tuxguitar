package org.herac.tuxguitar.player.impl.jsa.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGFileChooser;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class MidiConfigUtils {
	
	public static final String SOUNDBANK_KEY = "soundbank.custom.path";
	
	public static TGConfigManager getConfig(TGContext context){
		return new TGConfigManager(context, "tuxguitar-jsa");
	}
	
	public static String getSoundbankPath(TGContext context){
		return getSoundbankPath(getConfig(context));
	}
	
	public static String getSoundbankPath(final TGConfigManager config){
		return config.getStringValue(SOUNDBANK_KEY);
	}
	
	public static void setupDialog(TGContext context, Shell parent) {
		setupDialog(context, parent, getConfig(context));
	}
	
	public static void setupDialog(final TGContext context, final Shell parent, final TGConfigManager config) {
		final String soundbank = getSoundbankPath(config);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("jsa.settings.title"));
		
		//------------------SOUNDBANK-----------------------
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("jsa.settings.soundbank.tip"));
		
		final Button sbDefault = new Button(group,SWT.RADIO);
		sbDefault.setText(TuxGuitar.getProperty("jsa.settings.soundbank.default"));
		sbDefault.setSelection( (soundbank == null) );
		
		final Button sbCustom = new Button(group,SWT.RADIO);
		sbCustom.setText(TuxGuitar.getProperty("jsa.settings.soundbank.custom"));
		sbCustom.setSelection( (soundbank != null) );
		
		Composite chooser = new Composite(group,SWT.NONE);
		chooser.setLayout(new GridLayout(2,false));
		
		final Text sbCustomPath = new Text(chooser,SWT.BORDER);
		sbCustomPath.setLayoutData(new GridData(350,SWT.DEFAULT));
		sbCustomPath.setText( (soundbank == null ? new String() : soundbank)  );
		
		final Button sbCustomChooser = new Button(chooser,SWT.PUSH);
		sbCustomChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		sbCustomChooser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGFileChooser.getInstance(context).openChooser(new TGFileChooserHandler() {
					public void updateFileName(String fileName) {
						sbCustomPath.setText(fileName);
					}
				}, TGFileChooser.ALL_FORMATS, TGFileChooserDialog.STYLE_OPEN);
			}
		});
		
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
				String selection = ( sbCustom.getSelection() ? sbCustomPath.getText() : null);
				
				boolean changed = false;
				changed = (selection == null && soundbank != null);
				changed = changed || (selection != null && soundbank == null);
				changed = changed || (selection != null && !selection.equals(soundbank) ) ;
				if(changed){
					if(selection != null){
						config.setValue(SOUNDBANK_KEY,selection);
					}else{
						config.remove(SOUNDBANK_KEY);
					}
					config.save();
					
					TGMessageDialogUtil.infoMessage(context, TuxGuitar.getProperty("warning"), TuxGuitar.getProperty("jsa.settings.soundbank-restart-message"));
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
