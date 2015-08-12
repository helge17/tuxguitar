package org.herac.tuxguitar.player.impl.jsa.assistant;

import java.io.File;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortSynthesizer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class SBInstallerGui implements SBInstallerlistener{

	private static final String SB_PATH = ( TGFileUtils.PATH_USER_PLUGINS_CONFIG + File.separator + "tuxguitar-jsa" );
	
	private TGContext context;
	private Shell dialog;
	private Label progressLabel;

	private SBInstaller installer;	
	
	public SBInstallerGui(TGContext context, URL url, MidiPortSynthesizer synthesizer){
		this.context = context;
		this.initInstaller(url, synthesizer);
	}
	
	public void initInstaller(URL url, MidiPortSynthesizer synthesizer){
		File tmpPath = new File(SB_PATH);
		File dstPath = new File(SB_PATH);
		
		if(!tmpPath.exists()){
			tmpPath.mkdirs();
		}
		if(!dstPath.exists()){
			dstPath.mkdirs();
		}
		this.installer = new SBInstaller(this.context, url, tmpPath, dstPath, synthesizer, this);
	}
	
	public void open(){
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout());

		//-----------------------------------------------------
		Composite header = new Composite(this.dialog,SWT.NONE);
		header.setLayout(new GridLayout(2,false));
		header.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));		
        
		Label headerImage = new Label(header, SWT.NONE);
		headerImage.setImage(TuxGuitar.getInstance().getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		headerImage.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		
		Label headerTip = new Label(header, SWT.WRAP);
		headerTip.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		headerTip.setText(TuxGuitar.getProperty("jsa.soundbank-assistant.process.tip"));
		
		FontData[] fontData = headerTip.getFont().getFontData();
		for(int i = 0; i < fontData.length; i ++){
			fontData[i].setStyle(SWT.BOLD);
		}
		final Font font = new Font(headerTip.getDisplay(),fontData);
		headerTip.setFont(font);
		headerTip.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				font.dispose();
			}
		});
			
		
        //------------------PROGRESS--------------------------		
        Composite composite = new Composite(this.dialog,SWT.NONE);            
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));		
	        
		final ProgressBar progressBar = new ProgressBar(composite, SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		this.progressLabel = new Label(composite, SWT.WRAP);
		this.progressLabel.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
        //------------------BUTTONS--------------------------            
        Composite buttons = new Composite(this.dialog, SWT.NONE);
        buttons.setLayout(new GridLayout());
        buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false));    	

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;        

        Button buttonCancel = new Button(buttons, SWT.PUSH);
        buttonCancel.setText(TuxGuitar.getProperty("cancel"));
        buttonCancel.setLayoutData(data);
        buttonCancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                getInstaller().setCancelled(true);
            	getDialog().dispose();
            }
        });

        this.process();
        
        DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}

	private void process(){
		new Thread(new Runnable() {
			public void run() throws TGException {				
				if(!isDisposed()){
					getInstaller().process();
				}
			}
		}).start();
	}
	
	public void notifyProcess(final String process){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						getProgressLabel().setText(process);
					}
				}
			});
		}
	}

	public void notifyFinish(){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						getDialog().dispose();
					}
				}
			});
		}
	}
	
	public void notifyFailed(final Throwable throwable){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						getDialog().dispose();
						
						TGErrorManager.getInstance(SBInstallerGui.this.context).handleError(throwable);
					}
				}
			});
		}
	}	

	public boolean isDisposed(){
		return ( TuxGuitar.isDisposed() || getDialog().isDisposed() );
	}
	
	public Shell getDialog() {
		return this.dialog;
	}
	
	public Label getProgressLabel() {
		return this.progressLabel;
	}
	
	public SBInstaller getInstaller() {
		return this.installer;
	}
}
