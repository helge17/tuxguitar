package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserAuthDialog {
	
	private TGContext context;
	private boolean accepted;
	
	public TGBrowserAuthDialog(TGContext context){
		this.context = context;
		this.accepted = false;
	}
	
	public void open(final UIWindow parent) {
		this.accepted = false;
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.title"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.account"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-------USERNAME---------------------------------
		UILabel usernameLabel = uiFactory.createLabel(group);
		usernameLabel.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.account.user") + ":");
		groupLayout.set(usernameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIReadOnlyTextField usernameText = uiFactory.createReadOnlyTextField(group);
		usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
		groupLayout.set(usernameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 250f, null, null);
		
		final UIButton usernameChooser = uiFactory.createButton(group);
		usernameChooser.setText("...");
		usernameChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGCommunityAuthDialog authDialog = new TGCommunityAuthDialog(getContext());
				authDialog.open( dialog );
				if( authDialog.isAccepted() ){
					TGCommunitySingleton.getInstance(getContext()).getAuth().update();
					usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
				}
			}
		});
		groupLayout.set(usernameChooser, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGBrowserAuthDialog.this.setAccepted();
				dialog.dispose();
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
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK | TGDialogUtil.OPEN_STYLE_WAIT);
	}
	
	private void setAccepted(){
		this.accepted = true;
	}
	
	public boolean isAccepted(){
		return this.accepted;
	}
	
	public TGContext getContext() {
		return context;
	}
}
