package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.main.TGWindow;
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
import org.herac.tuxguitar.ui.widget.UITextArea;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGShareFileDialog {
	
	private TGContext context;
	private TGShareFile file;
	private Runnable onSuccess;
	private String errors;
	
	public TGShareFileDialog(TGContext context, TGShareFile file, String errors, Runnable onSuccess){
		this.context = context;
		this.file = file;
		this.errors = errors;
		this.onSuccess = onSuccess;
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
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.share-dialog.title"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tuxguitar-community.share-dialog.details"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 350f, null, null);
		
		//-------USERNAME---------------------------------
		UILabel usernameLabel = uiFactory.createLabel(group);
		usernameLabel.setText(TuxGuitar.getProperty("tuxguitar-community.share-dialog.details.user") + ":");
		groupLayout.set(usernameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIReadOnlyTextField usernameText = uiFactory.createReadOnlyTextField(group);
		usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
		usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
		groupLayout.set(usernameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UIButton usernameChooser = uiFactory.createButton(group);
		usernameChooser.setText("...");
		usernameChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				new TGCommunityAuthDialog(getContext(), new Runnable() {
					public void run() {
						TGCommunitySingleton.getInstance(getContext()).getAuth().update();
						usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
					}
				}, null).open(dialog);
			}
		});
		groupLayout.set(usernameChooser, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		//-------TITLE------------------------------------
		UILabel titleLabel = uiFactory.createLabel(group);
		titleLabel.setText(TuxGuitar.getProperty("tuxguitar-community.share-dialog.details.title") + ":");
		groupLayout.set(titleLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField titleText = uiFactory.createTextField(group);
		titleText.setText( this.file.getTitle() );
		groupLayout.set(titleText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		groupLayout.set(titleText, UITableLayout.PACKED_WIDTH, 0f);
		
		//-------TAGKEYS------------------------------------
		UILabel tagkeysLabel = uiFactory.createLabel(group);
		tagkeysLabel.setText(TuxGuitar.getProperty("tuxguitar-community.share-dialog.details.tagkeys") + ":");
		groupLayout.set(tagkeysLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField tagkeysText = uiFactory.createTextField(group);
		tagkeysText.setText( this.file.getTagkeys() );
		groupLayout.set(tagkeysText, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		groupLayout.set(tagkeysText, UITableLayout.PACKED_WIDTH, 0f);
		
		//-------DESCRIPTION------------------------------------
		UILabel descriptionLabel = uiFactory.createLabel(group);
		descriptionLabel.setText(TuxGuitar.getProperty("tuxguitar-community.share-dialog.details.description") + ":");
		groupLayout.set(descriptionLabel, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextArea descriptionText = uiFactory.createTextArea(group, true, false);
		descriptionText.setText( this.file.getDescription() );
		groupLayout.set(descriptionText,4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2, null, 100f, null);
		groupLayout.set(descriptionText, UITableLayout.PACKED_WIDTH, 0f);
		
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
				update(titleText.getText(), tagkeysText.getText() , descriptionText.getText() );
				
				onFinish(dialog, TGShareFileDialog.this.onSuccess);
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onFinish(dialog, null);
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		if( this.errors != null ){
			TGMessageDialogUtil.errorMessage(this.context, dialog, this.errors);
		}
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void onFinish(UIWindow dialog, Runnable runnable) {
		dialog.dispose();
		if( runnable != null ) {
			runnable.run();
		}
	}
	
	public void update( String title, String tagkeys, String description ){
		this.file.setTitle( title );
		this.file.setTagkeys( tagkeys );
		this.file.setDescription( description );
	}
	
	public TGContext getContext() {
		return context;
	}
}
