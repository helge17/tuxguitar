package org.herac.tuxguitar.community.auth;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.utils.TGCommunityWeb;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UILinkLabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIPasswordField;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;

public class TGCommunityAuthDialog {
	
	private TGContext context;
	private TGCommunityAuth auth;
	private Runnable onSuccess;
	private Runnable onCancel;
	
	public TGCommunityAuthDialog(TGContext context, Runnable onSuccess, Runnable onCancel){
		this.context = context;
		this.onSuccess = onSuccess;
		this.onCancel = onCancel;
		this.auth = TGCommunitySingleton.getInstance(this.context).getAuth();
	}
	
	public void open() {
		this.open(TGWindow.getInstance(this.context).getWindow());
	}
	
	public void open(UIWindow parent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.title"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signin"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-------USERNAME------------------------------------
		UILabel usernameLabel = uiFactory.createLabel(group);
		usernameLabel.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signin.username") + ":");
		groupLayout.set(usernameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField usernameText = uiFactory.createTextField(group);
		usernameText.setText( this.auth.getUsername() );
		groupLayout.set(usernameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//-------PASSWORD------------------------------------
		UILabel passwordLabel = uiFactory.createLabel(group);
		passwordLabel.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signin.password") + ":");
		groupLayout.set(passwordLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIPasswordField passwordText = uiFactory.createPasswordField(group);
		passwordText.setText( this.auth.getPassword() );
		groupLayout.set(passwordText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//-------JOIN------------------------------------
		UITableLayout joinLayout = new UITableLayout();
		UILegendPanel join = uiFactory.createLegendPanel(dialog);
		join.setLayout(joinLayout);
		join.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signup"));
		dialogLayout.set(join, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UILinkLabel joinLink = uiFactory.createLinkLabel(join);
		joinLink.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signup.tip"));
		joinLink.setWrapWidth(320f);
		joinLink.addLinkListener(new UILinkListener() {
			public void onLinkSelect(final UILinkEvent event) {
				new Thread( new Runnable() {
					public void run() throws TGException {
						TGCommunityWeb.open(getContext(), event.getLink());
					}
				} ).start();
			}
		});
		joinLayout.set(joinLink, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				update(usernameText.getText(), passwordText.getText());
				
				onFinish(dialog, TGCommunityAuthDialog.this.onSuccess);
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onFinish(dialog, TGCommunityAuthDialog.this.onCancel);
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void onFinish(UIWindow dialog, Runnable runnable) {
		dialog.dispose();
		if( runnable != null ) {
			runnable.run();
		}
	}
	
	public void update( String username, String password){
		this.auth.setUsername(username);
		this.auth.setPassword(password);
	}
	
	public TGContext getContext() {
		return context;
	}
}
