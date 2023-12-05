package org.herac.tuxguitar.player.impl.jsa.assistant;

import java.io.File;
import java.net.URL;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortSynthesizer;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UIIndeterminateProgressBar;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class SBInstallerGui implements SBInstallerlistener{

	private static final String SB_PATH = ( TGFileUtils.PATH_USER_PLUGINS_CONFIG + File.separator + "tuxguitar-jsa" );
	
	private TGContext context;
	private UIWindow dialog;
	private UILabel progressLabel;

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
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, true, false);
		this.dialog.setLayout(dialogLayout);
		
		//-----------------------------------------------------
		UITableLayout headerLayout = new UITableLayout();
		UIPanel header = uiFactory.createPanel(this.dialog, false);
		header.setLayout(headerLayout);
		dialogLayout.set(header, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false);		
        
		UIImageView headerImage = uiFactory.createImageView(header);
		headerImage.setImage(TGIconManager.getInstance(this.context).getStatusInfo());
		headerLayout.set(headerImage, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_TOP, false, false);
		
		UIWrapLabel headerTip = uiFactory.createWrapLabel(header);
		headerTip.setText(TuxGuitar.getProperty("jsa.soundbank-assistant.process.tip"));
		headerLayout.set(headerTip, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		UIFont defaultFont = headerTip.getFont();
		if( defaultFont != null ) {
			final UIFont font = uiFactory.createFont(defaultFont.getName(), defaultFont.getHeight(), true, false);
			headerTip.setFont(font);
			headerTip.addDisposeListener(new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					font.dispose();
				}
			});
		}
		
        //------------------PROGRESS--------------------------		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(this.dialog, false);
		composite.setLayout(compositeLayout);
		dialogLayout.set(composite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//SWT.INDETERMINATE
		final UIIndeterminateProgressBar progressBar = uiFactory.createIndeterminateProgressBar(composite);
		compositeLayout.set(progressBar, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.progressLabel = uiFactory.createLabel(composite);
		compositeLayout.set(this.progressLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
        //------------------BUTTONS--------------------------            
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
                getInstaller().setCancelled(true);
            	getDialog().dispose();
            }
        });
        buttonsLayout.set(buttonCancel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
        
        this.process();
        
        TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
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
		return ( TuxGuitar.getInstance().isDisposed() || getDialog().isDisposed() );
	}
	
	public UIWindow getDialog() {
		return this.dialog;
	}
	
	public UILabel getProgressLabel() {
		return this.progressLabel;
	}
	
	public SBInstaller getInstaller() {
		return this.installer;
	}
}
