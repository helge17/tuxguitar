package org.herac.tuxguitar.community.auth;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.utils.TGCommunityWeb;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGCommunityAuthDialog {
	
	private boolean accepted;
	private TGCommunityAuth auth;
	
	public TGCommunityAuthDialog(){
		this.auth = TGCommunitySingleton.getInstance().getAuth();
		this.accepted = false;
	}
	
	public void open() {
		this.open( TuxGuitar.instance().getShell() );
	}
	
	public void open(final Shell shell) {
		try {
			if( shell != null && !shell.isDisposed() ){
				TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
					public void run() throws Throwable {
						if( !shell.isDisposed() ){
							doOpen( shell );
						}
					}
				});
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	protected void doOpen(Shell shell) {
		this.accepted = false;
		
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.title"));
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(makeGroupLayout(2,5));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signin"));
		
		//-------USERNAME------------------------------------
		Label usernameLabel = new Label(group, SWT.NULL);
		usernameLabel.setLayoutData(makeLabelData()); 
		usernameLabel.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signin.username") + ":");
		
		final Text usernameText = new Text(group, SWT.BORDER);
		usernameText.setLayoutData(makeTextData());
		usernameText.setText( this.auth.getUsername() );
		
		//-------PASSWORD------------------------------------
		Label passwordLabel = new Label(group, SWT.NULL);
		passwordLabel.setLayoutData(makeLabelData());
		passwordLabel.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signin.password") + ":");
		
		final Text passwordText = new Text(group, SWT.BORDER | SWT.PASSWORD );
		passwordText.setLayoutData(makeTextData());
		passwordText.setText( this.auth.getPassword() );
		
		//-------JOIN------------------------------------
		Group join = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		join.setLayout(makeGroupLayout(1,5));
		//join.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		join.setLayoutData(new GridData(group.computeSize(SWT.DEFAULT, SWT.DEFAULT).x , SWT.DEFAULT));
		join.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signup"));
		
		final Link joinLink = new Link( join, SWT.LEFT );
		joinLink.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, true, true ));
		joinLink.setText(TuxGuitar.getProperty("tuxguitar-community.auth-dialog.signup.tip"));
		joinLink.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				final String href = event.text;
				if( href != null ){
					new Thread( new Runnable() {
						public void run() {
							TGCommunityWeb.open( href );
						}
					} ).start();
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				update(usernameText.getText(), passwordText.getText());
				
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
	
	private GridLayout makeGroupLayout(int columns, int spacing){
		GridLayout layout = new GridLayout(columns,false);
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
	
	private GridData makeTextData(){
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
	
	protected void update( String username, String password){
		this.auth.setUsername(username);
		this.auth.setPassword(password);
		this.accepted = true;
	}
	
	public boolean isAccepted(){
		return this.accepted;
	}
}
