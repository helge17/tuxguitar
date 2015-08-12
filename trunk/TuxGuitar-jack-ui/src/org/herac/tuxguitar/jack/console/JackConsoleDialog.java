package org.herac.tuxguitar.jack.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.jack.connection.JackConnectionManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class JackConsoleDialog implements TGEventListener {
	
	private static final int SHELL_WIDTH = 350;
	
	private TGContext context;
	private JackConnectionManager jackConnectionManager;
	
	private Shell dialog;
	private Group groupOptions;

	private Button buttonAutoConnectPorts;
	private Button buttonStoreConnections;
	private Button buttonRestoreConnections;
	
	public JackConsoleDialog(TGContext context, JackConnectionManager jackConnectionManager) {
		this.context = context;
		this.jackConnectionManager = jackConnectionManager;
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setMinimumSize(SHELL_WIDTH,SWT.DEFAULT);
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TuxGuitar.getInstance().getIconManager().removeLoader( JackConsoleDialog.this );
				TuxGuitar.getInstance().getLanguageManager().removeLoader( JackConsoleDialog.this );
			}
		});
		
		// Options
		this.groupOptions = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		this.groupOptions.setLayout(new GridLayout());
		this.groupOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite composite = new Composite(this.groupOptions, SWT.NONE);
		composite.setLayout(new GridLayout(1,false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.buttonAutoConnectPorts = new Button(composite,SWT.CHECK);
		this.buttonAutoConnectPorts.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
		this.buttonAutoConnectPorts.setSelection(this.jackConnectionManager.isAutoConnectPorts());
		this.buttonAutoConnectPorts.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final boolean selection = ((Button) e.widget).getSelection();
				new Thread(new Runnable() {
					public void run() {
						updateAutoConnectPorts(selection);
					}
				}).start();
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(1,false));
		buttons.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		
		this.buttonStoreConnections = new Button(buttons, SWT.PUSH);
		this.buttonStoreConnections.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.buttonStoreConnections.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						storeConnections();
					}
				}).start();
			}
		});
		
		this.buttonRestoreConnections = new Button(buttons, SWT.PUSH);
		this.buttonRestoreConnections.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.buttonRestoreConnections.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						restoreConnections();
					}
				}).start();
			}
		});
		
		this.loadIcons(false);
		this.loadProperties(false);
		
		TuxGuitar.getInstance().getIconManager().addLoader( this );
		TuxGuitar.getInstance().getLanguageManager().addLoader( this );
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void loadProperties(){
		this.loadProperties(true);
	}
	
	public void loadProperties(boolean layout){
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("jack.console.title"));
			this.groupOptions.setText(TuxGuitar.getProperty("jack.console.options"));
			this.buttonAutoConnectPorts.setText(TuxGuitar.getProperty("jack.console.autoconnect.ports"));
			this.buttonStoreConnections.setText(TuxGuitar.getProperty("jack.console.store.connections"));
			this.buttonRestoreConnections.setText(TuxGuitar.getProperty("jack.console.restore.connections"));
			if(layout){
				this.dialog.layout(true, true);
			}
		}
	}
	
	public void loadIcons() {
		this.loadIcons(true);
	}
	
	public void loadIcons(boolean layout){
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			if(layout){
				this.dialog.layout(true, true);
			}
		}
	}
	
	public void updateAutoConnectPorts( boolean autoConnectPorts ){
		try {
			this.jackConnectionManager.setAutoConnectPorts(autoConnectPorts);
			this.jackConnectionManager.saveConfig();
		} catch (Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public void storeConnections(){
		try {
			this.jackConnectionManager.loadExistingConnections();
			this.jackConnectionManager.saveConfig();
			this.showInfoMessage("jack.console.info.message.title", "jack.console.store.connections.success");
		} catch (Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public void restoreConnections(){
		try {
			this.jackConnectionManager.connectAllPorts();
			this.showInfoMessage("jack.console.info.message.title", "jack.console.restore.connections.success");
		} catch (Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public void showInfoMessage(String title, String message){
		TGMessageDialogUtil.infoMessage(this.context, this.dialog, TuxGuitar.getProperty(title), TuxGuitar.getProperty(message));
	}

	public void processEvent(TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
	}
}
