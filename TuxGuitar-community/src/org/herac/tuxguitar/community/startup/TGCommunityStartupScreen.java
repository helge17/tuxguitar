package org.herac.tuxguitar.community.startup;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.utils.TGCommunityWeb;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGCommunityStartupScreen {
	
	private static final int MAIN_WIDTH  = 550;
	private static final int MAIN_HEIGHT = SWT.DEFAULT;
	
	public TGCommunityStartupScreen(){
		super();
	}
	
	public void open(){
		try {
			final Shell parent = TuxGuitar.instance().getShell();
			TGSynchronizer.instance().runLater( new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
					open( parent );
				}
			} );
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected void open(Shell parent){
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM );
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.title"));
		
		Composite composite = new Composite( dialog, SWT.NONE );
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(MAIN_WIDTH, MAIN_HEIGHT));
		
		//==============================================================//
		Composite top = new Composite( composite, SWT.NONE );
		top.setLayout(new GridLayout( 2 , false ));
		top.setLayoutData(new GridData(SWT.FILL,SWT.FILL, true,true));
		
		Composite topLeft = new Composite( top , SWT.NONE );
		topLeft.setLayout( new GridLayout() );
		topLeft.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		
		Label image = new Label( topLeft, SWT.NONE );
		image.setImage( TuxGuitar.instance().getIconManager().getAppIcon() );
		
		Composite topRight = new Composite( top , SWT.NONE );
		topRight.setLayout( new GridLayout(2,false) );
		topRight.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		addTitle( topRight , TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.title") );
		
		addTipItem( topRight );
		addComment( topRight , TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.tip-1") );
		
		addTipItem( topRight );
		addComment( topRight , TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.tip-2") );
		
		//==============================================================//
		Composite bottom = new Composite( composite, SWT.NONE );
		bottom.setLayout(new GridLayout());
		bottom.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		addComment( bottom , TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.tip-bottom") );
		
		//==============================================================//
		Composite buttons = new Composite( composite, SWT.NONE );
		buttons.setLayout(new GridLayout( 2 , false ));
		buttons.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		final Button buttonDisabled = new Button( buttons , SWT.CHECK );
		buttonDisabled.setLayoutData( new GridData(SWT.LEFT, SWT.FILL, true, true) );
		buttonDisabled.setText( TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.disable") );
		buttonDisabled.setSelection( this.isDisabled() );
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonOkData());
		buttonOK.setFocus();
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setDisabled( buttonDisabled.getSelection() );
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK );
	}
	
	private GridData getButtonOkData(){
		GridData data = new GridData(SWT.RIGHT, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private void addTitle( Composite parent , String text ){
		Label label = new Label( parent , SWT.LEFT );
		label.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true,2,1));
		label.setText(text);
		
		FontData[] fontDatas = label.getFont().getFontData();
		if(fontDatas.length > 0){
			int fHeight = (fontDatas[0].getHeight() + 2);
			int fStyle = (fontDatas[0].getStyle() | SWT.BOLD);
			final Font font = new Font(label.getDisplay(),fontDatas[0].getName(),fHeight, fStyle);
			label.setFont(font);
			label.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					font.dispose();
				}
			});
		}
	}
	
	private void addTipItem( Composite parent ){
		Label label = new Label( parent , SWT.LEFT );
		label.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,true));
		label.setText("\u066D");
	}
	
	private void addComment( Composite parent , String text ){
		final Link link = new Link( parent , SWT.LEFT );
		link.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		link.setText(text);
		link.addListener(SWT.Selection, new Listener() {
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
	}
	
	public void setDisabled( boolean enabled ){
		TGCommunitySingleton.getInstance().getConfig().setProperty("community.welcome.disabled",enabled);
	}
	
	public boolean isDisabled(){
		return TGCommunitySingleton.getInstance().getConfig().getBooleanConfigValue("community.welcome.disabled");
	}
}