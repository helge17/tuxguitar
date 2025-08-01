package app.tuxguitar.midi.synth.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGFileChooser;
import app.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIModifyEvent;
import app.tuxguitar.ui.event.UIModifyListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class GervillProcessorUI implements TGAudioProcessorUI, UIModifyListener, UISelectionListener {

	private static final List<TGFileFormat> soundbankFormats = Arrays.asList(
		new TGFileFormat("SoundFont SF2", "audio/x-sf2", new String[]{"sf2"}),
		new TGFileFormat("Downloadable Sounds DLS", "audio/x-dls", new String[]{"dls"}));

	private static final String DATA_SOUNDBANK_PATH = "soundbankPath";

	private TGContext context;
	private TGAudioProcessorUICallback callback;
	private GervillProcessor processor;

	private UIWindow dialog;
	private UIReadOnlyTextField customSoundbankName;
	private UIRadioButton channelModeSingle;
	private UIRadioButton channelModeBend;
	private UIRadioButton channelModeVoice;
	private UIDropDownSelect<Integer> bank;
	private UIDropDownSelect<Integer> program;

	public GervillProcessorUI(TGContext context, GervillProcessor processor, TGAudioProcessorUICallback callback) {
		this.context = context;
		this.processor = processor;
		this.callback = callback;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void open(final UIWindow parent) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.dialog.title"));

		//-------------------------------------------------------------------------
		UITableLayout soundbankLayout = new UITableLayout();
		UILegendPanel soundbankGroup = uiFactory.createLegendPanel(this.dialog);
		soundbankGroup.setLayout(soundbankLayout);
		soundbankGroup.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.soundbank.tip"));
		dialogLayout.set(soundbankGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.customSoundbankName = uiFactory.createReadOnlyTextField(soundbankGroup);
		soundbankLayout.set(this.customSoundbankName, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, 300f, null, null);

		final UIButton sbDefault = uiFactory.createButton(soundbankGroup);
		sbDefault.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LIST_REMOVE));
		sbDefault.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				GervillProcessorUI.this.updateSoundbank(null);
			}
		});
		soundbankLayout.set(sbDefault, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

		final UIButton sbCustomChooser = uiFactory.createButton(soundbankGroup);
		sbCustomChooser.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.FILE_OPEN));
		sbCustomChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				chooseSoundbank(parent);
			}
		});
		soundbankLayout.set(sbCustomChooser, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

		//-------------------------------------------------------------------------
		UITableLayout programLayout = new UITableLayout();
		UILegendPanel programGroup = uiFactory.createLegendPanel(this.dialog);
		programGroup.setLayout(programLayout);
		programGroup.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.program.tip"));
		dialogLayout.set(programGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel bankLabel = uiFactory.createLabel(programGroup);
		bankLabel.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.program.bank") + ":");
		programLayout.set(bankLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		String bankPrefix = TuxGuitar.getProperty("instrument.bank");
		this.bank = uiFactory.createDropDownSelect(programGroup);
		for(int i = 0; i < 129; i++) {
			this.bank.addItem(new UISelectItem<Integer>((bankPrefix + " #" + i), i));
		}
		this.bank.addSelectionListener(this);

		programLayout.set(this.bank, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UILabel programLabel = uiFactory.createLabel(programGroup);
		programLabel.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.program.program") + ":");
		programLayout.set(programLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		String programPrefix = TuxGuitar.getProperty("instrument.program");
		this.program = uiFactory.createDropDownSelect(programGroup);
		for(int i = 0; i < 128; i++) {
			this.program.addItem(new UISelectItem<Integer>((programPrefix + " #" + i), i));
		}
		this.program.addSelectionListener(this);

		programLayout.set(this.program, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		//-------------------------------------------------------------------------
		UITableLayout channelLayout = new UITableLayout();
		UILegendPanel channelGroup = uiFactory.createLegendPanel(this.dialog);
		channelGroup.setLayout(channelLayout);
		channelGroup.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.channel.mode.tip"));
		dialogLayout.set(channelGroup, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.channelModeSingle = uiFactory.createRadioButton(channelGroup);
		this.channelModeSingle.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.channel.mode.single"));
		this.channelModeSingle.addSelectionListener(this);
		channelLayout.set(this.channelModeSingle, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.channelModeBend = uiFactory.createRadioButton(channelGroup);
		this.channelModeBend.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.channel.mode.bend"));
		this.channelModeBend.addSelectionListener(this);
		channelLayout.set(this.channelModeBend, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.channelModeVoice = uiFactory.createRadioButton(channelGroup);
		this.channelModeVoice.setText(TuxGuitar.getProperty("tuxguitar-synth-gervill.ui.channel.mode.voice"));
		this.channelModeVoice.addSelectionListener(this);
		channelLayout.set(this.channelModeVoice, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//-------------------------------------------------------------------------

		this.updateItems();

		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void chooseSoundbank(final UIWindow parent) {

		final GervillSettings gervillSettings = new GervillSettings(this.context);
		String chooserPath = gervillSettings.getSoundbankFolder();

		TGFileChooser.getInstance(context).openChooser(new TGFileChooserHandler() {
			public void updateFileName(String file) {
				GervillProcessorUI.this.updateSoundbank(file);

				gervillSettings.setSoundbankFolder(new File(file).getParentFile().getAbsolutePath());
				gervillSettings.save();
			}
		}, soundbankFormats, TGFileChooserDialog.STYLE_OPEN);

	}

	public void updateItem(UIDropDownSelect<Integer> select, Integer value) {
		if( select.getSelectedValue() != value ) {
			select.setIgnoreEvents(true);
			select.setSelectedValue(value);
			select.setIgnoreEvents(false);
		}
	}

	public void updateItem(UIRadioButton button, boolean selected) {
		if( button.isSelected() != selected) {
			button.setIgnoreEvents(true);
			button.setSelected(selected);
			button.setIgnoreEvents(false);
		}
	}

	public void updateSoundbankItem(UIReadOnlyTextField text, String value) {
		String notNullValue = (value != null ? new File(value).getName() : "");
		if(!notNullValue.equals(text.getText())) {
			text.setIgnoreEvents(true);
			text.setText(notNullValue);
			text.setIgnoreEvents(false);
		}
		text.setData(DATA_SOUNDBANK_PATH, value);
	}

	public void updateItems() {
		this.updateItem(this.bank, this.processor.getProgram().getBank());
		this.updateItem(this.program, this.processor.getProgram().getProgram());
		this.updateItem(this.channelModeSingle, this.processor.getProgram().getChannelMode() == GervillProgram.CHANNEL_MODE_SINGLE);
		this.updateItem(this.channelModeBend, this.processor.getProgram().getChannelMode() == GervillProgram.CHANNEL_MODE_BEND);
		this.updateItem(this.channelModeVoice, this.processor.getProgram().getChannelMode() == GervillProgram.CHANNEL_MODE_VOICE);
		this.updateSoundbankItem(this.customSoundbankName, this.processor.getProgram().getSoundbankPath());
	}

	public void updateSoundbank(String soundbankPath) {
		this.customSoundbankName.setData(DATA_SOUNDBANK_PATH, soundbankPath);
		this.updateProgram();
	}

	public void updateProgram() {
		GervillProgram gervillProgram = new GervillProgram();
		gervillProgram.copyFrom(this.processor.getProgram());

		gervillProgram.setSoundbankPath((String) this.customSoundbankName.getData(DATA_SOUNDBANK_PATH));

		Integer bank = this.bank.getSelectedValue();
		if( bank != null ) {
			gervillProgram.setBank(bank);
		}

		Integer program = this.program.getSelectedValue();
		if( program != null ) {
			gervillProgram.setProgram(program);
		}

		if( this.channelModeSingle.isSelected() ) {
			gervillProgram.setChannelMode(GervillProgram.CHANNEL_MODE_SINGLE);
		}
		else if( this.channelModeBend.isSelected() ) {
			gervillProgram.setChannelMode(GervillProgram.CHANNEL_MODE_BEND);
		}
		else if( this.channelModeVoice.isSelected() ) {
			gervillProgram.setChannelMode(GervillProgram.CHANNEL_MODE_VOICE);
		}

		this.processor.loadProgram(gervillProgram);
		this.updateItems();
		this.callback.onChange(false);
	}

	@Override
	public void close() {
		if( this.isOpen() ) {
			this.dialog.dispose();
			this.dialog = null;
		}
	}

	@Override
	public void focus() {
		if( this.isOpen() ) {
			this.dialog.moveToTop();
		}
	}

	@Override
	public boolean isOpen() {
		return (this.dialog != null && !this.dialog.isDisposed());
	}

	@Override
	public void onSelect(UISelectionEvent event) {
		this.updateProgram();
	}

	@Override
	public void onModify(UIModifyEvent event) {
		this.updateProgram();
	}
}
