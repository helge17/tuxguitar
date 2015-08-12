package org.herac.tuxguitar.app.tools.custom.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.util.TGContext;

public class TGConverterDialog implements TGEventListener{
	
	private static final int SHELL_WIDTH = 500;
	
	private TGContext context;
	
	protected Shell dialog;
	protected Group group;
	protected Label outputFormatLabel;
	protected Label outputFolderLabel;
	protected Label inputFolderLabel;
	protected Button inputFolderChooser;
	protected Button outputFolderChooser;
	protected Button buttonOK;
	protected Button buttonCancel;
	
	protected List<TGConverterFormat> outputFormats;
	
	public TGConverterDialog(TGContext context) {
		this.context = context;
		this.outputFormats = new ArrayList<TGConverterFormat>();
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(),SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setMinimumSize(SHELL_WIDTH,SWT.DEFAULT);
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TuxGuitar.getInstance().getIconManager().removeLoader( TGConverterDialog.this );
				TuxGuitar.getInstance().getLanguageManager().removeLoader( TGConverterDialog.this );
			}
		});
		
		// Settings
		this.group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		this.group.setLayout(new GridLayout());
		this.group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite composite = new Composite(this.group,SWT.NONE);
		composite.setLayout(new GridLayout(3,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.outputFormatLabel = new Label(composite,SWT.LEFT);
		this.outputFormatLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
		
		final Combo outputFormat = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		outputFormat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,2,1));
		addFileFormats(outputFormat);
		
		this.inputFolderLabel = new Label(composite,SWT.LEFT);
		this.inputFolderLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
		
		final Text inputFolder = new Text(composite,SWT.BORDER);
		inputFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		this.inputFolderChooser = new Button(composite,SWT.PUSH);
		this.inputFolderChooser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(TGConverterDialog.this.dialog);
				String selection = directoryDialog.open();
				if(selection != null){
					inputFolder.setText(selection);
				}
			}
		});
		
		this.outputFolderLabel = new Label(composite,SWT.LEFT);
		this.outputFolderLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false));
		
		final Text outputFolder = new Text(composite,SWT.BORDER);
		outputFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		this.outputFolderChooser = new Button(composite,SWT.PUSH);
		this.outputFolderChooser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(TGConverterDialog.this.dialog);
				String selection = directoryDialog.open();
				if(selection != null){
					outputFolder.setText(selection);
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false));
		
		this.buttonOK = new Button(buttons, SWT.PUSH);
		this.buttonOK.setLayoutData(getGridData(80,25));
		this.buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				String inputFolderValue = inputFolder.getText();
				String outputFolderValue = outputFolder.getText();
				TGConverterFormat outputFormatValue = getFileFormat( outputFormat.getSelectionIndex() );
				
				if(inputFolderValue == null || inputFolderValue.trim().length() == 0){
					TGMessageDialogUtil.errorMessage(TGConverterDialog.this.context, TGConverterDialog.this.dialog, TuxGuitar.getProperty("batch.converter.input.folder.invalid"));
				}
				else if(outputFolderValue == null || outputFolderValue.trim().length() == 0){
					TGMessageDialogUtil.errorMessage(TGConverterDialog.this.context, TGConverterDialog.this.dialog, TuxGuitar.getProperty("batch.converter.output.folder.invalid"));
				}
				else if(outputFormatValue == null){
					TGMessageDialogUtil.errorMessage(TGConverterDialog.this.context, TGConverterDialog.this.dialog, TuxGuitar.getProperty("batch.converter.output.format.invalid"));
				}
				else{
					TGConverterProcess process = new TGConverterProcess(getContext());
					process.start(inputFolderValue.trim(), outputFolderValue.trim(), outputFormatValue );
					TGConverterDialog.this.dialog.dispose();
				}
			}
		});
		
		this.buttonCancel = new Button(buttons, SWT.PUSH);
		this.buttonCancel.setLayoutData(getGridData(80,25));
		this.buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGConverterDialog.this.dialog.dispose();
			}
		});
		
		this.dialog.setDefaultButton( this.buttonOK );
		
		this.loadIcons(false);
		this.loadProperties(false);
		
		TuxGuitar.getInstance().getIconManager().addLoader( this );
		TuxGuitar.getInstance().getLanguageManager().addLoader( this );
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	private GridData getGridData(int minimumWidth, int minimumHeight){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = minimumWidth;
		data.minimumHeight = minimumHeight;
		return data;
	}
	
	private void addFileFormats(Combo combo){
		this.outputFormats.clear();
		
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.context);
		
		Iterator<TGOutputStreamBase> outputStreams = fileFormatManager.getOutputStreams();
		while(outputStreams.hasNext()){
			TGOutputStreamBase stream = (TGOutputStreamBase)outputStreams.next();
			addFileFormats(combo, stream.getFileFormat() , stream );
		}
		
		Iterator<TGRawExporter> exporters = fileFormatManager.getExporters();
		while (exporters.hasNext()) {
			TGRawExporter exporter = (TGRawExporter)exporters.next();
			if( exporter instanceof TGLocalFileExporter ){
				addFileFormats(combo, ((TGLocalFileExporter)exporter).getFileFormat() , exporter );
			}
		}
		if(this.outputFormats.size() > 0 ){
			combo.select( 0 );
		}
	}
	
	private void addFileFormats(Combo combo, TGFileFormat format, Object exporter ){
		if(format.getSupportedFormats() != null){
			String[] extensions = format.getSupportedFormats();
			if( extensions != null && extensions.length > 0 ){
				for(int i = 0; i < extensions.length; i ++){
					String exportName = format.getName();
					if( exporter instanceof TGLocalFileExporter ){
						exportName = ( (TGLocalFileExporter) exporter ).getExportName();
					}
					combo.add( exportName + " (*." + extensions[i] + ")");
					this.outputFormats.add(new TGConverterFormat( (extensions[i]).trim() , exporter ));
				}
			}
		}
	}
	
	protected TGConverterFormat getFileFormat(int index){
		if(index >= 0 && index < this.outputFormats.size()){
			return (TGConverterFormat)this.outputFormats.get(index);
		}
		return null;
	}
	
	public void loadProperties(){
		this.loadProperties(true);
	}
	
	public void loadProperties(boolean layout){
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("batch.converter"));
			this.group.setText(TuxGuitar.getProperty("batch.converter.settings"));
			this.inputFolderLabel.setText(TuxGuitar.getProperty("batch.converter.input.folder"));
			this.outputFolderLabel.setText(TuxGuitar.getProperty("batch.converter.output.folder"));
			this.outputFormatLabel.setText(TuxGuitar.getProperty("batch.converter.output.format"));
			this.buttonOK.setText(TuxGuitar.getProperty("ok"));
			this.buttonCancel.setText(TuxGuitar.getProperty("cancel"));
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
			this.inputFolderChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
			this.outputFolderChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
			if(layout){
				this.dialog.layout(true, true);
			}
		}
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
