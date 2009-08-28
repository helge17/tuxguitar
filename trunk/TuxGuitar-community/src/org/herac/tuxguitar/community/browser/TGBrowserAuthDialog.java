package org.herac.tuxguitar.community.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class TGBrowserAuthDialog {
	
	private boolean accepted;
	
	public TGBrowserAuthDialog(){
		this.accepted = false;
	}
	
	protected void open(Shell shell) {
		this.accepted = false;
		
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.title"));
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(makeGroupLayout(5));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.account"));
		
		//-------USERNAME---------------------------------
		Label usernameLabel = new Label(group, SWT.NULL);
		usernameLabel.setLayoutData(makeLabelData()); 
		usernameLabel.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.account.user") + ":");
		
		final Text usernameText = new Text(group, SWT.BORDER | SWT.READ_ONLY );
		usernameText.setLayoutData(makeUsernameTextData());
		usernameText.setText( TGCommunitySingleton.getInstance().getAuth().getUsername() );
		
		final Button usernameChooser = new Button(group, SWT.PUSH );
		usernameChooser.setText("...");
		usernameChooser.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGCommunityAuthDialog authDialog = new TGCommunityAuthDialog();
				authDialog.open( dialog );
				if( authDialog.isAccepted() ){
					TGCommunitySingleton.getInstance().getAuth().update();
					usernameText.setText( TGCommunitySingleton.getInstance().getAuth().getUsername() );
				}
			}
		} );
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setAccepted();
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	private GridLayout makeGroupLayout(int spacing){
		GridLayout layout = new GridLayout(3,false);
		layout.marginTop = spacing;
		layout.marginBottom = spacing;
		layout.marginLeft = spacing;
		layout.marginRight = spacing;
		layout.verticalSpacing = spacing;
		layout.horizontalSpacing = spacing;
		return layout;
	}
	
	private GridData makeLabelData(){
		return new GridData(SWT.RIGHT,SWT.CENTER,false,true);
	}
	
	private GridData makeUsernameTextData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 250;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void setAccepted(){
		this.accepted = true;
	}
	
	public boolean isAccepted(){
		return this.accepted;
	}
}
