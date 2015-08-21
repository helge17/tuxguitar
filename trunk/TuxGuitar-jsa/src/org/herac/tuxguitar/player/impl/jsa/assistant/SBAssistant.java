package org.herac.tuxguitar.player.impl.jsa.assistant;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortSynthesizer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class SBAssistant {

	public static final SBUrl[] URLS = new SBUrl[]{
		new SBUrl(toURL("http://www.oracle.com/technetwork/java/soundbank-min-150078.zip"),TuxGuitar.getProperty("jsa.soundbank-assistant.minimal")),
		new SBUrl(toURL("http://www.oracle.com/technetwork/java/soundbank-mid-149984.zip"),TuxGuitar.getProperty("jsa.soundbank-assistant.medium")),
		new SBUrl(toURL("http://www.oracle.com/technetwork/java/soundbank-deluxe-150042.zip"),TuxGuitar.getProperty("jsa.soundbank-assistant.deluxe")),
	};
	
	private TGContext context;
	private MidiPortSynthesizer synthesizer;
	
	public SBAssistant(TGContext context, MidiPortSynthesizer synthesizer){
		this.context = context;
		this.synthesizer = synthesizer;
	}
	
	public void process(){
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("jsa.soundbank-assistant.confirm-message"));
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, new Runnable() {
			public void run() {
				TGSynchronizer.getInstance(SBAssistant.this.context).executeLater(new Runnable() {
					public void run() {
						open();
					}
				});
			}
		});
		tgActionProcessor.process();
	}
	
	protected void open(){		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		
		//------------------------------------------------------------------------------
        Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
        group.setLayout(new GridLayout());
        group.setLayoutData(getGroupData());   
        group.setText(TuxGuitar.getProperty("jsa.soundbank-assistant.select"));
        
        
        final Button urls[] = new Button[ URLS.length ];
        for(int i = 0; i < URLS.length ; i ++){
	        urls[i] = new Button(group, SWT.RADIO);
	        urls[i].setText(URLS[i].getName());
	        urls[i].setData(URLS[i]);
	        urls[i].setSelection(i == 0);
        }
		
        //------------------BUTTONS--------------------------            
        Composite buttons = new Composite(dialog, SWT.NONE);
        buttons.setLayout(new GridLayout(2,false));
        buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false));    	

        Button buttonOk = new Button(buttons, SWT.PUSH);
        buttonOk.setText(TuxGuitar.getProperty("ok"));
        buttonOk.setLayoutData(getButtonsData());
        buttonOk.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	URL url = getSelection(urls);
            	
            	dialog.dispose();
            	
            	if(url != null ){
            		install(url);
            	}
            }
        });		
		
        Button buttonCancel = new Button(buttons, SWT.PUSH);
        buttonCancel.setText(TuxGuitar.getProperty("cancel"));
        buttonCancel.setLayoutData(getButtonsData());
        buttonCancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                dialog.dispose();
            }
        });
		
        dialog.setDefaultButton( buttonOk );
        
        DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	protected GridData getGroupData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 250;
		return data;
	}	
	
	protected GridData getButtonsData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected URL getSelection(Button[] buttons){
    	for(int i = 0; i < buttons.length ; i ++){
    		if( buttons[i].getSelection() && buttons[i].getData() instanceof SBUrl ){
    			return ((SBUrl)buttons[i].getData()).getUrl();
    		}
    	}
    	return null;
	}
	
	protected void install(URL url ){
		new SBInstallerGui(this.context , url, this.synthesizer).open();
	}

	private static URL toURL(String s){
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
