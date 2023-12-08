package org.herac.tuxguitar.player.impl.jsa.utils;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGFileChooser;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class MidiConfigUtils {
	
	public static final String SOUNDBANK_KEY = "soundbank.custom.path";
	
	public static TGConfigManager getConfig(TGContext context){
		return new TGConfigManager(context, "tuxguitar-jsa");
	}
	
	public static String getSoundbankPath(TGContext context){
		return getSoundbankPath(getConfig(context));
	}
	
	public static String getSoundbankPath(final TGConfigManager config){
		return config.getStringValue(SOUNDBANK_KEY);
	}
	
	public static void setupDialog(TGContext context, UIWindow parent) {
		setupDialog(context, parent, getConfig(context));
	}
	
	public static void setupDialog(final TGContext context, final UIWindow parent, final TGConfigManager config) {
		final String soundbank = getSoundbankPath(config);
		
		final UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("jsa.settings.title"));
		
		//------------------SOUNDBANK-----------------------
		UITableLayout soundbankLayout = new UITableLayout();
		UILegendPanel soundbankGroup = uiFactory.createLegendPanel(dialog);
		soundbankGroup.setLayout(soundbankLayout);
		soundbankGroup.setText(TuxGuitar.getProperty("jsa.settings.soundbank.tip"));
		dialogLayout.set(soundbankGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, null, null);
		
		final UIRadioButton sbDefault = uiFactory.createRadioButton(soundbankGroup);
		sbDefault.setText(TuxGuitar.getProperty("jsa.settings.soundbank.default"));
		sbDefault.setSelected( (soundbank == null) );
		soundbankLayout.set(sbDefault, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 2);
		soundbankLayout.set(sbDefault, UITableLayout.PACKED_WIDTH, 0f);
		
		final UIRadioButton sbCustom = uiFactory.createRadioButton(soundbankGroup);
		sbCustom.setText(TuxGuitar.getProperty("jsa.settings.soundbank.custom"));
		sbCustom.setSelected( (soundbank != null) );
		soundbankLayout.set(sbCustom, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 2);
		soundbankLayout.set(sbCustom, UITableLayout.PACKED_WIDTH, 0f);
		
		final UITextField sbCustomPath = uiFactory.createTextField(soundbankGroup);
		sbCustomPath.setText((soundbank == null ? new String() : soundbank));
		sbCustomPath.setEnabled( (soundbank != null) );
		soundbankLayout.set(sbCustomPath, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		final UIButton sbCustomChooser = uiFactory.createButton(soundbankGroup);
		sbCustomChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		sbCustomChooser.setEnabled((soundbank != null));
		sbCustomChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGFileChooser.getInstance(context).openChooser(new TGFileChooserHandler() {
					public void updateFileName(String fileName) {
						sbCustomPath.setText(fileName);
					}
				}, TGFileChooser.ALL_FORMATS, TGFileChooserDialog.STYLE_OPEN);
			}
		});
		soundbankLayout.set(sbCustomChooser, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		
		UISelectionListener sbRadioSelectionListener = new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				sbCustomPath.setEnabled(sbCustom.isSelected());
				sbCustomChooser.setEnabled(sbCustom.isSelected());
			}
		};
		sbDefault.addSelectionListener(sbRadioSelectionListener);
		sbCustom.addSelectionListener(sbRadioSelectionListener);
		
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
				String selection = (sbCustom.isSelected() ? sbCustomPath.getText() : null);
				
				boolean changed = false;
				changed = (selection == null && soundbank != null);
				changed = changed || (selection != null && soundbank == null);
				changed = changed || (selection != null && !selection.equals(soundbank) ) ;
				if(changed){
					if(selection != null){
						config.setValue(SOUNDBANK_KEY,selection);
					}else{
						config.remove(SOUNDBANK_KEY);
					}
					config.save();
					
					TGMessageDialogUtil.infoMessage(context, TuxGuitar.getProperty("warning"), TuxGuitar.getProperty("jsa.settings.soundbank-restart-message"));
				}
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
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
}
