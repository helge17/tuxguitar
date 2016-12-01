package org.herac.tuxguitar.app.tools.browser.filesystem;

import java.io.File;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooserHandler;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserDataDialog {
	
	private TGContext context;
	private TGBrowserFactorySettingsHandler handler;
	
	public TGBrowserDataDialog(TGContext context, TGBrowserFactorySettingsHandler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	public void open() {
		TGBrowserDialog browser = TGBrowserDialog.getInstance(this.context);
		this.open(!browser.isDisposed() ? browser.getWindow() : TGWindow.getInstance(this.context).getWindow());
	}
	
	public void open(UIWindow parent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("browser.collection.fs.editor-title"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("browser.collection.fs.editor-tip"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 420f, null, null);
		
		final UILabel titleLabel = uiFactory.createLabel(group);
		titleLabel.setText(TuxGuitar.getProperty("browser.collection.fs.name"));
		groupLayout.set(titleLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField titleValue = uiFactory.createTextField(group);
		groupLayout.set(titleValue, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		
		final UILabel pathLabel = uiFactory.createLabel(group);
		pathLabel.setText(TuxGuitar.getProperty("browser.collection.fs.path"));
		groupLayout.set(pathLabel, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField pathValue = uiFactory.createTextField(group);
		groupLayout.set(pathValue, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UIButton pathChooser = uiFactory.createButton(group);
		pathChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		pathChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String defaultPath = pathValue.getText();
				UIDirectoryChooser chooser = uiFactory.createDirectoryChooser(dialog);
				chooser.setDefaultPath(defaultPath != null ? new File(defaultPath) : null);
				chooser.choose(new UIDirectoryChooserHandler() {
					public void onSelectDirectory(File file) {
						if( file != null ){
							pathValue.setText(file.getAbsolutePath());
						}
					}
				});
			}
		});
		groupLayout.set(pathChooser, 2, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
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
				String selectedTitle = titleValue.getText();
				String selectedPath = pathValue.getText();
				if(!isValidPath(selectedPath)){
					TGMessageDialogUtil.errorMessage(TGBrowserDataDialog.this.context, dialog, TuxGuitar.getProperty("browser.collection.fs.invalid-path"));
					return;
				}
				if(isBlank(selectedTitle)){
					selectedTitle = selectedPath;
				}
				
				dialog.dispose();
				
				TGBrowserDataDialog.this.handler.onCreateSettings(new TGBrowserSettingsModel(selectedTitle, selectedPath).toBrowserSettings());
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
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public boolean isBlank(String s){
		return (s == null || s.length() == 0);
	}
	
	public boolean isValidPath(String path){
		if(!isBlank(path)){
			File file = new File(path);
			return (file.exists() && file.isDirectory());
		}
		return false;
	}
}
