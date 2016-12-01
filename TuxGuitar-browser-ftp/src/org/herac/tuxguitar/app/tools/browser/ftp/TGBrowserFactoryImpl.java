package org.herac.tuxguitar.app.tools.browser.ftp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactoryHandler;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserFactoryImpl implements TGBrowserFactory{
	
	private TGContext context;
	
	public TGBrowserFactoryImpl(TGContext context) {
		this.context = context;
	}
	
	public String getType(){
		return "ftp";
	}
	
	public String getName(){
		return "FTP";
	}
	
	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) {
		handler.onCreateBrowser(new TGBrowserImpl(TGBrowserSettingsModel.createInstance(settings)));
	}
	
	public void createSettings(TGBrowserFactorySettingsHandler handler) {
		new TGBrowserDataDialog(this.context, handler).show();
	}

}

class TGBrowserDataDialog{
	
	private static final Float MINIMUM_LEGEND_WIDTH = 350f;
	
	private TGContext context;
	private TGBrowserFactorySettingsHandler handler;
	
	public TGBrowserDataDialog(TGContext context, TGBrowserFactorySettingsHandler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	public void show() {
		TGBrowserDialog browser = TGBrowserDialog.getInstance(this.context);
		this.show(!browser.isDisposed() ? browser.getWindow() : TGWindow.getInstance(this.context).getWindow());
	}
	
	public void show(final UIWindow parent){
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.title"));
		
		//-------------LIBRARY DATA-----------------------------------------------
		UITableLayout serverLayout = new UITableLayout();
		UILegendPanel server = uiFactory.createLegendPanel(dialog);
		server.setLayout(serverLayout);
		server.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.server.settings"));
		dialogLayout.set(server, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_LEGEND_WIDTH, null, null);
		
		//name
		UILabel nameLabel = uiFactory.createLabel(server);
		nameLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.name"));
		serverLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField nameText = uiFactory.createTextField(server);
		serverLayout.set(nameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//host
		UILabel hostLabel = uiFactory.createLabel(server);
		hostLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.host"));
		serverLayout.set(hostLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField hostText = uiFactory.createTextField(server);
		serverLayout.set(hostText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//path
		UILabel pathLabel = uiFactory.createLabel(server);
		pathLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.path"));
		serverLayout.set(pathLabel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField pathText = uiFactory.createTextField(server);
		serverLayout.set(pathText, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//user
		UILabel userLabel = uiFactory.createLabel(server);
		userLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.username"));
		serverLayout.set(userLabel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField userText = uiFactory.createTextField(server);
		serverLayout.set(userText, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//password
		UILabel passwordLabel = uiFactory.createLabel(server);
		passwordLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.password"));
		serverLayout.set(passwordLabel, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField passwordText = uiFactory.createTextField(server);
		serverLayout.set(passwordText, 5, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		
		UITableLayout proxyLayout = new UITableLayout();
		UILegendPanel proxy = uiFactory.createLegendPanel(dialog);
		proxy.setLayout(proxyLayout);
		proxy.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.proxy.settings"));
		dialogLayout.set(proxy, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_LEGEND_WIDTH, null, null);
		
		// Proxy
		final UICheckBox hasProxy = uiFactory.createCheckBox(proxy);
		hasProxy.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.proxy.enabled"));
		proxyLayout.set(hasProxy, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		
		//proxy host
		final UILabel proxyHostLabel = uiFactory.createLabel(proxy);
		proxyHostLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.proxy.host"));
		proxyLayout.set(proxyHostLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField proxyHostText = uiFactory.createTextField(proxy);
		proxyLayout.set(proxyHostText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//proxy port
		final UILabel proxyPortLabel = uiFactory.createLabel(proxy); 
		proxyPortLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.proxy.port"));
		proxyLayout.set(proxyPortLabel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField proxyPortText = uiFactory.createTextField(proxy);
		proxyPortText.setText("1080");
		proxyLayout.set(proxyPortText, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//proxy user
		final UILabel proxyUserLabel = uiFactory.createLabel(proxy);
		proxyUserLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.proxy.username"));
		proxyLayout.set(proxyUserLabel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField proxyUserText = uiFactory.createTextField(proxy);
		proxyLayout.set(proxyUserText, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//proxy password
		final UILabel proxyPwdLabel = uiFactory.createLabel(proxy);
		proxyPwdLabel.setText(TuxGuitar.getProperty("tuxguitar-browser-ftp.browser-dialog.proxy.password"));
		proxyLayout.set(proxyPwdLabel, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField proxyPwdText = uiFactory.createTextField(proxy);
		proxyLayout.set(proxyPwdText, 5, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		proxyHostText.setEnabled(false);
		proxyPortText.setEnabled(false);
		proxyUserText.setEnabled(false);
		proxyPwdText.setEnabled(false);
		
		hasProxy.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				proxyHostText.setEnabled(hasProxy.isSelected());
				proxyPortText.setEnabled(hasProxy.isSelected());
				proxyUserText.setEnabled(hasProxy.isSelected());
				proxyPwdText.setEnabled(hasProxy.isSelected());
			}
		});
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String name = nameText.getText();
				String host = hostText.getText();
				String path = pathText.getText();
				String user = userText.getText();
				String password = passwordText.getText();
				String proxyHost = proxyHostText.getText();
				String proxyPortStr = proxyPortText.getText();
				String proxyUser = proxyUserText.getText();
				String proxyPwd = proxyPwdText.getText();
				
				List<String> errors = validate(name, host, proxyHost, proxyPortStr, hasProxy.isSelected());
				if( !errors.isEmpty() ){
					StringWriter buffer = new StringWriter();
					PrintWriter writer = new PrintWriter( buffer );
					Iterator<String> it = errors.iterator();
					while( it.hasNext() ){
						writer.println( "*" + (String)it.next() );
					}
					TGMessageDialogUtil.errorMessage(getContext(), parent, buffer.getBuffer().toString() );
				}else{
					int proxyPort = Integer.parseInt( proxyPortStr );
					
					dialog.dispose();
					
					TGBrowserSettings settings = new TGBrowserSettingsModel(name, host, path, user, password, proxyUser, proxyPwd, proxyHost, proxyPort).toBrowserSettings();
					TGBrowserDataDialog.this.handler.onCreateSettings(settings);
				}
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
	
	public List<String> validate(String name, String host, String pHost, String pPort, boolean pEnabled){
		List<String> errors = new ArrayList<String>();
		// Check the Name
		if (name == null || name.trim().length() == 0) {
			errors.add("Please enter the Name");
		}else{
			Iterator<TGBrowserCollection> it = TGBrowserManager.getInstance(getContext()).getCollections();
			while(it.hasNext()){
				TGBrowserCollection collection = (TGBrowserCollection)it.next();
				if(name.equals(collection.getData().getTitle())){
					errors.add("A collection named \"" + name + "\" already exists");
					break;
				}
			}
		}
		if (host == null || host.trim().length() == 0) {
			errors.add("Please enter the Host");
		}
		if( pEnabled ){
			if(pHost == null || pHost.trim().length() == 0){
				errors.add("Please enter Proxy Host");
			}
			if(pPort == null || pPort.trim().length() == 0){
				errors.add("Please enter Proxy Port");
			}else if(!isNumber(pPort)){
				errors.add("Proxy Port should be a valid number");
			}
		}
		
		return errors;
	}
	
	public boolean isNumber( String s ){
		try {
			Integer.parseInt(s);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	public TGContext getContext() {
		return context;
	}
}