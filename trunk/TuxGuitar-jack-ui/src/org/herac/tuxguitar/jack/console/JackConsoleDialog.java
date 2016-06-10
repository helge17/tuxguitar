package org.herac.tuxguitar.jack.console;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.jack.connection.JackConnectionManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class JackConsoleDialog implements TGEventListener {
	
	private TGContext context;
	private JackConnectionManager jackConnectionManager;
	
	private UIWindow dialog;
	private UILegendPanel groupOptions;

	private UICheckBox buttonAutoConnectPorts;
	private UIButton buttonStoreConnections;
	private UIButton buttonRestoreConnections;
	
	public JackConsoleDialog(TGContext context, JackConnectionManager jackConnectionManager) {
		this.context = context;
		this.jackConnectionManager = jackConnectionManager;
	}
	
	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TuxGuitar.getInstance().getIconManager().removeLoader( JackConsoleDialog.this );
				TuxGuitar.getInstance().getLanguageManager().removeLoader( JackConsoleDialog.this );
			}
		});
		
		// Options
		UITableLayout groupLayout = new UITableLayout();
		this.groupOptions = uiFactory.createLegendPanel(this.dialog);
		this.groupOptions.setLayout(groupLayout);
		dialogLayout.set(this.groupOptions, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 350f, null, null);
		
		this.buttonAutoConnectPorts = uiFactory.createCheckBox(this.groupOptions);
		this.buttonAutoConnectPorts.setSelected(this.jackConnectionManager.isAutoConnectPorts());
		this.buttonAutoConnectPorts.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				final boolean selection = JackConsoleDialog.this.buttonAutoConnectPorts.isSelected();
				new Thread(new Runnable() {
					public void run() {
						updateAutoConnectPorts(selection);
					}
				}).start();
			}
		});
		groupLayout.set(this.buttonAutoConnectPorts, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.buttonStoreConnections = uiFactory.createButton(buttons);
		this.buttonStoreConnections.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				new Thread(new Runnable() {
					public void run() {
						storeConnections();
					}
				}).start();
			}
		});
		buttonsLayout.set(this.buttonStoreConnections, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, null, null, 0f);
		
		this.buttonRestoreConnections = uiFactory.createButton(buttons);
		this.buttonRestoreConnections.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				new Thread(new Runnable() {
					public void run() {
						restoreConnections();
					}
				}).start();
			}
		});
		buttonsLayout.set(this.buttonRestoreConnections, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, null, null, 0f);
		
		this.loadIcons(false);
		this.loadProperties(false);
		
		TuxGuitar.getInstance().getIconManager().addLoader( this );
		TuxGuitar.getInstance().getLanguageManager().addLoader( this );
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
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
			if( layout ){
				this.dialog.layout();
			}
		}
	}
	
	public void loadIcons() {
		this.loadIcons(true);
	}
	
	public void loadIcons(boolean layout){
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			if( layout ){
				this.dialog.layout();
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
