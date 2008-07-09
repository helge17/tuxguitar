package org.herac.tuxguitar.io.gtp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.system.plugins.TGPluginConfigManager;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class GTPSettingsUtil {
	
	private static final String KEY_CHARSET = "charset";
	
	private static GTPSettingsUtil instance;
	
	private TGConfigManager config;
	
	private GTPSettings settings;
	
	private GTPSettingsUtil(){
		this.settings = new GTPSettings();
	}
	
	public static GTPSettingsUtil instance(){
		if( instance == null ){
			instance = new GTPSettingsUtil();
		}
		return instance;
	}
	
	public GTPSettings getSettings(){
		return this.settings;
	}
	
	public TGConfigManager getConfig(){
		if(this.config == null){ 
			this.config = new TGPluginConfigManager("tuxguitar-gtp");
			this.config.init();
		}
		return this.config;
	}
	
	public void load(){
		String charsetDefault = System.getProperty("file.encoding");
		if( charsetDefault == null ){
			charsetDefault = GTPSettings.DEFAULT_CHARSET;
		}
		this.settings.setCharset( getConfig().getStringConfigValue(KEY_CHARSET, charsetDefault) );
	}
	
	public void configure(Shell parent) {
		final List charsets = getAvailableCharsets();
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("gtp.settings.title"));
		
		//------------------DEVICE-----------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("gtp.settings.charset.tip"));
		
		final Label label = new Label(group,SWT.LEFT);
		label.setText(TuxGuitar.getProperty("gtp.settings.charset.select") + ":");
		
		final Combo value = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		value.setLayoutData(new GridData(250,SWT.DEFAULT));
		for(int i = 0 ; i < charsets.size(); i ++){
			String charset = (String)charsets.get(i);
			value.add( charset );
			if(charset.equals(this.settings.getCharset())){
				value.select( i );
			}
		}
		
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
				int selection = value.getSelectionIndex();
				if(selection >= 0 && selection < charsets.size() ){
					TGConfigManager config = getConfig();
					config.setProperty(KEY_CHARSET, (String)charsets.get(selection));
					config.save();
					load();
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
	
	private List getAvailableCharsets(){
		List charsets = new ArrayList();
		Iterator it = Charset.availableCharsets().entrySet().iterator();
		while( it.hasNext() ){
			Map.Entry entry = (Map.Entry) it.next();
			charsets.add(entry.getKey());
		}
		return charsets;
	}
}
