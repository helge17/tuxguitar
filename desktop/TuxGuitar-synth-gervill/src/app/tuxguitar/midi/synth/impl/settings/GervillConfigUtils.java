package app.tuxguitar.midi.synth.impl.settings;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGFileChooser;
import app.tuxguitar.app.util.TGMessageDialogUtil;
import app.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.midi.synth.impl.GervillSettings;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGExpressionResolver;
import app.tuxguitar.util.configuration.TGConfigManager;

public class GervillConfigUtils {

	private static final List<TGFileFormat> soundfontFormats = Arrays.asList(
			new TGFileFormat("SoundFont SF2", "audio/x-sf2", new String[]{"sf2"}),
			new TGFileFormat("Downloadable Sounds DLS", "audio/x-dls", new String[]{"dls"}));


	public static TGConfigManager getConfig(TGContext context){
		return new TGConfigManager(context, "tuxguitar-synth-gervill");
	}

	public static String getSoundbankPath(TGContext context){
		return getSoundbankPath(getConfig(context));
	}

	public static String getSoundbankPath(final TGConfigManager config){
		return config.getStringValue(GervillSettings.GERVILL_SOUNDBANK_PATH);
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
		dialog.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.settings.title"));

		//------------------SOUNDBANK-----------------------
		UITableLayout soundbankLayout = new UITableLayout();
		UILegendPanel soundbankGroup = uiFactory.createLegendPanel(dialog);
		soundbankGroup.setLayout(soundbankLayout);
		soundbankGroup.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.settings.soundbank.tip"));
		dialogLayout.set(soundbankGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, null, null);

		final UIRadioButton sbDefault = uiFactory.createRadioButton(soundbankGroup);
		sbDefault.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.settings.soundbank.default"));
		sbDefault.setSelected( (soundbank == null) );
		soundbankLayout.set(sbDefault, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 2);
		soundbankLayout.set(sbDefault, UITableLayout.PACKED_WIDTH, 0f);

		final UIRadioButton sbCustom = uiFactory.createRadioButton(soundbankGroup);
		sbCustom.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.settings.soundbank.custom"));
		sbCustom.setSelected( (soundbank != null) );
		soundbankLayout.set(sbCustom, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 2);
		soundbankLayout.set(sbCustom, UITableLayout.PACKED_WIDTH, 0f);

		final UITextField sbCustomPath = uiFactory.createTextField(soundbankGroup);
		sbCustomPath.setText((soundbank == null ? new String() : soundbank));
		sbCustomPath.setEnabled( (soundbank != null) );
		soundbankLayout.set(sbCustomPath, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		final UIButton sbCustomChooser = uiFactory.createButton(soundbankGroup);
		sbCustomChooser.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.FILE_OPEN));
		sbCustomChooser.setEnabled((soundbank != null));
		sbCustomChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGFileChooser.getInstance(context).openChooser(new TGFileChooserHandler() {
					public void updateFileName(String fileName) {
						sbCustomPath.setText(fileName);
					}
				}, soundfontFormats, TGFileChooserDialog.STYLE_OPEN);
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
						File soundfont = new File(TGExpressionResolver.getInstance(context).resolve(selection));
						if(!soundfont.isFile()){
							TGMessageDialogUtil.errorMessage(context, dialog, TuxGuitar.getProperty("tuxguitar-synth-gervill.error.soundbank.custom", new String[] {soundfont.getAbsolutePath()}));
							return;
						}
						config.setValue(GervillSettings.GERVILL_SOUNDBANK_PATH,selection);
					}else{
						config.remove(GervillSettings.GERVILL_SOUNDBANK_PATH);
					}
					config.save();

					TGMessageDialogUtil.infoMessage(context, TuxGuitar.getProperty("warning"), TuxGuitar.getProperty("tuxguitar-synth-gervill.settings.soundbank-restart-message"));
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
