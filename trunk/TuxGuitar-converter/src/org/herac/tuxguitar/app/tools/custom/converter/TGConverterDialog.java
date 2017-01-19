package org.herac.tuxguitar.app.tools.custom.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooserHandler;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGConverterDialog implements TGEventListener{
	
	private TGContext context;
	
	private UIWindow dialog;
	private UILegendPanel group;
	private UILabel outputFormatLabel;
	private UILabel outputFolderLabel;
	private UILabel inputFolderLabel;
	private UIButton inputFolderChooser;
	private UIButton outputFolderChooser;
	private UIButton buttonOK;
	private UIButton buttonCancel;
	
	public TGConverterDialog(TGContext context) {
		this.context = context;
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TuxGuitar.getInstance().getIconManager().removeLoader( TGConverterDialog.this );
				TuxGuitar.getInstance().getLanguageManager().removeLoader( TGConverterDialog.this );
			}
		});
		
		// Settings
		UITableLayout groupLayout = new UITableLayout();
		this.group = uiFactory.createLegendPanel(this.dialog);
		this.group.setLayout(groupLayout);
		dialogLayout.set(this.group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 500f, null, null);
		
		this.outputFormatLabel = uiFactory.createLabel(this.group);
		groupLayout.set(this.outputFormatLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<TGConverterFormat> outputFormat = uiFactory.createDropDownSelect(this.group);
		groupLayout.set(outputFormat, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		groupLayout.set(outputFormat, UITableLayout.PACKED_WIDTH, 0f);
		addFileFormats(outputFormat);
		
		this.inputFolderLabel = uiFactory.createLabel(this.group);
		groupLayout.set(this.inputFolderLabel, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField inputFolder = uiFactory.createTextField(this.group);
		groupLayout.set(inputFolder, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.inputFolderChooser = uiFactory.createButton(this.group);
		this.inputFolderChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String defaultPath = inputFolder.getText();
				UIDirectoryChooser chooser = uiFactory.createDirectoryChooser(TGConverterDialog.this.dialog);
				chooser.setDefaultPath(defaultPath != null ? new File(defaultPath) : null);
				chooser.choose(new UIDirectoryChooserHandler() {
					public void onSelectDirectory(File file) {
						if( file != null ){
							inputFolder.setText(file.getAbsolutePath());
						}
					}
				});
			}
		});
		groupLayout.set(this.inputFolderChooser, 2, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		this.outputFolderLabel = uiFactory.createLabel(this.group);
		groupLayout.set(this.outputFolderLabel, 3, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField outputFolder = uiFactory.createTextField(this.group);
		groupLayout.set(outputFolder, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.outputFolderChooser = uiFactory.createButton(this.group);
		this.outputFolderChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String defaultPath = outputFolder.getText();
				UIDirectoryChooser chooser = uiFactory.createDirectoryChooser(TGConverterDialog.this.dialog);
				chooser.setDefaultPath(defaultPath != null ? new File(defaultPath) : null);
				chooser.choose(new UIDirectoryChooserHandler() {
					public void onSelectDirectory(File file) {
						if( file != null ){
							outputFolder.setText(file.getAbsolutePath());
						}
					}
				});
			}
		});
		groupLayout.set(this.outputFolderChooser, 3, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		this.buttonOK = uiFactory.createButton(buttons);
		this.buttonOK.setDefaultButton();
		this.buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String inputFolderValue = inputFolder.getText();
				String outputFolderValue = outputFolder.getText();
				TGConverterFormat outputFormatValue = outputFormat.getSelectedValue();
				
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
		buttonsLayout.set(this.buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		this.buttonCancel = uiFactory.createButton(buttons);
		this.buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGConverterDialog.this.dialog.dispose();
			}
		});
		buttonsLayout.set(this.buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(this.buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		this.loadIcons(false);
		this.loadProperties(false);
		
		TuxGuitar.getInstance().getIconManager().addLoader( this );
		TuxGuitar.getInstance().getLanguageManager().addLoader( this );
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	private void addFileFormats(UIDropDownSelect<TGConverterFormat> combo){
		List<UISelectItem<TGConverterFormat>> selectItems = new ArrayList<UISelectItem<TGConverterFormat>>();
		
		List<TGFileFormat> fileFormats = TGFileFormatManager.getInstance(this.context).findWriteFileFormats(null);
		for(TGFileFormat fileFormat : fileFormats) {
			addFileFormats(selectItems, fileFormat);
		}
		
		for(UISelectItem<TGConverterFormat> selectItem : selectItems) {
			combo.addItem(selectItem);
		}
		
		if( selectItems.size() > 0 ){
			combo.setSelectedItem(selectItems.get(0));
		}
	}
	
	private void addFileFormats(List<UISelectItem<TGConverterFormat>> items, TGFileFormat format){
		if( format.getSupportedFormats() != null){
			String[] extensions = format.getSupportedFormats();
			if( extensions != null && extensions.length > 0 ){
				for(int i = 0; i < extensions.length; i ++){
					String label = (format.getName() + " (*." + extensions[i] + ")");		
					items.add(new UISelectItem<TGConverterFormat>(label, new TGConverterFormat(format, extensions[i].trim())));
				}
			}
		}
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
			this.inputFolderChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
			this.outputFolderChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
			if( layout ){
				this.dialog.layout();
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
