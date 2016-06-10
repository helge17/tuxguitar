package org.herac.tuxguitar.app.tools.custom.converter;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGConverterProcess implements TGConverterListener, TGEventListener{
	
	private static final float SHELL_WIDTH = 650f;
	private static final float SHELL_HEIGHT = 350f;
	
	private static final String EOL = ("\n");
	
	private TGContext context;
	private UIWindow dialog;
	private UIReadOnlyTextBox output;
	private UIButton buttonCancel;
	private UIButton buttonClose;
	private TGConverter converter;
	private boolean finished;
	
	public TGConverterProcess(TGContext context) {
		this.context = context;
	}
	
	public void start(String initFolder, String destFolder, TGConverterFormat format ){
		this.converter = new TGConverter(this.context, initFolder, destFolder);
		this.converter.setFormat(format);
		this.converter.setListener(this);
		
		this.showProcess();
		
		new Thread(new Runnable() {
			public void run() throws TGException {
				TGConverterProcess.this.converter.process();
			}
		}).start();
	}
	
	protected void showProcess() {
		this.finished = false;
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, false, true);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setBounds(new UIRectangle(0, 0, SHELL_WIDTH, SHELL_HEIGHT));
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TuxGuitar.getInstance().getIconManager().removeLoader( TGConverterProcess.this );
				TuxGuitar.getInstance().getLanguageManager().removeLoader( TGConverterProcess.this );
			}
		});
		this.dialog.addCloseListener(new UICloseListener() {
			public void onClose(UICloseEvent event) {
				if( TGConverterProcess.this.finished ) {
					TGConverterProcess.this.dialog.dispose();
				}
			}
		});
		
		this.output = uiFactory.createReadOnlyTextBox(this.dialog, true, true);
		dialogLayout.set(this.output, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_BOTTOM, true, false);
		
		this.buttonCancel = uiFactory.createButton(buttons);
		this.buttonCancel.setEnabled( false );
		this.buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGConverterProcess.this.converter.setCancelled( true );
			}
		});
		buttonsLayout.set(this.buttonCancel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		this.buttonClose = uiFactory.createButton(buttons);
		this.buttonClose.setEnabled( false );
		this.buttonClose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGConverterProcess.this.dialog.dispose();
			}
		});
		buttonsLayout.set(this.buttonClose, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(this.buttonClose, UITableLayout.MARGIN_RIGHT, 0f);
		
		this.loadIcons(false);
		this.loadProperties(false);
		
		TuxGuitar.getInstance().getIconManager().addLoader( this );
		TuxGuitar.getInstance().getLanguageManager().addLoader( this );
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed() );
	}
	
	public void loadProperties(){
		this.loadProperties(true);
	}
	
	public void loadProperties(boolean layout){
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("batch.converter"));
			this.buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			this.buttonClose.setText(TuxGuitar.getProperty("close"));
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
	
	//------------------------------------------------------------------------------------------------//
	//---TGConverterListener Implementation ----------------------------------------------------------//
	//------------------------------------------------------------------------------------------------//
	
	public void notifyFileProcess(final String filename) {
		if(!isDisposed() ){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() throws TGException {
					if(!isDisposed() ){
						TGConverterProcess.this.output.append(TuxGuitar.getProperty("batch.converter.messages.converting", new String[] {filename}));
					}
				}
			});
		}
	}
	
	public void notifyFileResult(final String filename, final int result) {
		if(!isDisposed() ){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() throws TGException {
					if(!isDisposed() ){
						TGConverterProcess.this.appendLogMessage(result, filename);
					}
				}
			});
		}
	}
	
	public void notifyStart() {
		if(!isDisposed() ){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() throws TGException {
					if(!isDisposed() ){
						TGConverterProcess.this.finished = false;
						TGConverterProcess.this.buttonClose.setEnabled( TGConverterProcess.this.finished );
						TGConverterProcess.this.buttonCancel.setEnabled( !TGConverterProcess.this.finished );
						TGConverterProcess.this.output.setCursor(UICursor.WAIT);
					}
				}
			});
		}
	}
	
	public void notifyFinish() {
		if(!isDisposed() ){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() throws TGException {
					if(!isDisposed() ){
						TGConverterProcess.this.finished = true;
						TGConverterProcess.this.buttonClose.setEnabled( TGConverterProcess.this.finished );
						TGConverterProcess.this.buttonCancel.setEnabled( !TGConverterProcess.this.finished );
						TGConverterProcess.this.output.setCursor(UICursor.NORMAL);
					}
				}
			});
		}
	}
	
	public void appendLogMessage(int result, String fileName) {
		String message = (TuxGuitar.getProperty( "batch.converter.messages." + (result == TGConverter.FILE_OK ? "ok" : "failed") ) + EOL);
		
		switch (result) {
			case TGConverter.FILE_COULDNT_WRITE :
				message += ( TuxGuitar.getProperty("batch.converter.messages.couldnt-write", new String[] {fileName}) + EOL );
				break;
			case TGConverter.FILE_BAD :
				message += ( TuxGuitar.getProperty("batch.converter.messages.bad-file", new String[] {fileName}) + EOL );
				break;
			case TGConverter.FILE_NOT_FOUND :
				message += ( TuxGuitar.getProperty("batch.converter.messages.file-not-found", new String[] {fileName}) + EOL );
				break;
			case TGConverter.OUT_OF_MEMORY :
				message += ( TuxGuitar.getProperty("batch.converter.messages.out-of-memory", new String[] {fileName}) + EOL );
				break;
			case TGConverter.EXPORTER_NOT_FOUND :
				message += ( TuxGuitar.getProperty("batch.converter.messages.exporter-not-found", new String[] {fileName}) + EOL );
				break;
			case TGConverter.UNKNOWN_ERROR :
				message += ( TuxGuitar.getProperty("batch.converter.messages.unknown-error", new String[] {fileName}) + EOL );
				break;
		}
		
		TGConverterProcess.this.output.append( message );
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
