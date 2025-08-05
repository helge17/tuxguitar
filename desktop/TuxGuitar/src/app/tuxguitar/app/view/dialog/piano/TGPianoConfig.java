package app.tuxguitar.app.view.dialog.piano;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigDefaults;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIColorChooserHandler;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILayoutContainer;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;

public class TGPianoConfig {

	private static final float MINIMUM_CONTROL_WIDTH = 180;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;

	private TGContext context;
	private UIColor colorNatural;
	private UIColor colorNotNatural;
	private UIColor colorNote;
	private UIColor colorScale;
	private UIColor colorOctaveNumber;
	private UIFont  font;

	public TGPianoConfig(TGContext context){
		this.context = context;
	}

	public UIColor getColorNatural() {
		return this.colorNatural;
	}

	public UIColor getColorNotNatural() {
		return this.colorNotNatural;
	}

	public UIColor getColorNote() {
		return this.colorNote;
	}

	public UIColor getColorScale() {
		return this.colorScale;
	}

	public UIColor getColorOctaveNumber() {
		return this.colorOctaveNumber;
	}

	public UIFont getFont() {
		return this.font;
	}

	public UIColor createColor(UIFactory factory, UIColorModel cm) {
		return TGApplication.getInstance(this.context).getFactory().createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}

	public UIFont createFont(UIFactory factory, UIFontModel fm) {
		return TGApplication.getInstance(this.context).getFactory().createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}

	public void load(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();

		this.colorNatural = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL));
		this.colorNotNatural = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL));
		this.colorNote = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_NOTE));
		this.colorScale = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_SCALE));
		this.colorOctaveNumber = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_OCTAVE_NUMBER));
		this.font = createFont(factory, config.getFontModelConfigValue(TGConfigKeys.PIANO_FONT));
	}

	public void defaults(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		TGProperties defaults = TGConfigDefaults.createDefaults();
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL, defaults.getValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL));
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL, defaults.getValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL));
		config.setValue(TGConfigKeys.PIANO_COLOR_NOTE, defaults.getValue(TGConfigKeys.PIANO_COLOR_NOTE));
		config.setValue(TGConfigKeys.PIANO_COLOR_SCALE, defaults.getValue(TGConfigKeys.PIANO_COLOR_SCALE));
		config.setValue(TGConfigKeys.PIANO_COLOR_OCTAVE_NUMBER, defaults.getValue(TGConfigKeys.PIANO_COLOR_OCTAVE_NUMBER));
	}

	public void save(UIColorModel rgbNatural, UIColorModel rgbNotNatural, UIColorModel rgbNote, UIColorModel rgbScale, UIColorModel rgbOctaveNumber){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL,rgbNatural);
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL,rgbNotNatural);
		config.setValue(TGConfigKeys.PIANO_COLOR_NOTE,rgbNote);
		config.setValue(TGConfigKeys.PIANO_COLOR_SCALE,rgbScale);
		config.setValue(TGConfigKeys.PIANO_COLOR_OCTAVE_NUMBER, rgbOctaveNumber);
	}

	public void dispose(){
		this.colorNatural.dispose();
		this.colorNotNatural.dispose();
		this.colorNote.dispose();
		this.colorScale.dispose();
		this.colorOctaveNumber.dispose();
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}

	public void configure(UIWindow parent) {
		final UIFactory factory = getUIFactory();
		final UITableLayout windowLayout = new UITableLayout();
		final UIWindow window = factory.createWindow(parent, true, false);
		window.setLayout(windowLayout);
		window.setText(TuxGuitar.getProperty("piano.settings"));

		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = factory.createLegendPanel(window);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("piano.settings"));
		windowLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		int groupRow = 0;

		// Color
		final UIColorModel rgbNatural = getColorChooser(window, group, TuxGuitar.getProperty("piano.natural-key-color") + ":", this.colorNatural, ++groupRow);
		final UIColorModel rgbNotNatural = getColorChooser(window, group, TuxGuitar.getProperty("piano.not-natural-key-color") + ":", this.colorNotNatural, ++groupRow);
		final UIColorModel rgbNote = getColorChooser(window, group, TuxGuitar.getProperty("piano.note-color") + ":", this.colorNote, ++groupRow);
		final UIColorModel rgbScale = getColorChooser(window, group, TuxGuitar.getProperty("piano.scale-note-color") + ":", this.colorScale, ++groupRow);
		final UIColorModel rgbOctaveNumber = getColorChooser(window, group, TuxGuitar.getProperty("piano.octave-number-color") + ":", this.colorOctaveNumber, ++groupRow);

		// ------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = factory.createPanel(window, false);
		buttons.setLayout(buttonsLayout);
		windowLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonDefaults = factory.createButton(buttons);
		buttonDefaults.setText(TuxGuitar.getProperty("defaults"));
		buttonDefaults.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				window.dispose();
				defaults();
				applyChanges();
			}
		});
		buttonsLayout.set(buttonDefaults, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

		final UIButton buttonOK = factory.createButton(buttons);
		buttonOK.setDefaultButton();
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				window.dispose();

				save(rgbNatural, rgbNotNatural,rgbNote, rgbScale, rgbOctaveNumber);
				applyChanges();
			}
		});
		buttonsLayout.set(buttonOK, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

		final UIButton buttonCancel = factory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				window.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		TGDialogUtil.openDialog(window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	protected void applyChanges(){
		this.dispose();
		this.load();

		TGPianoEditor.getInstance(this.context).getPiano().reloadFromConfig();
	}

	private UIColorModel getColorChooser(final UIWindow window, UILayoutContainer parent, String title, UIColor rgb, int row){
		final UIFactory factory = getUIFactory();

		UITableLayout layout = (UITableLayout) parent.getLayout();
		UILabel label = factory.createLabel(parent);
		label.setText(title);
		layout.set(label, row, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);

		ButtonColor button = new ButtonColor(window, parent, TuxGuitar.getProperty("choose"));
		button.loadColor(new UIColorModel(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
		layout.set(button.getControl(), row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);

		return button.getValue();
	}

	private class ButtonColor {

		private UIWindow window;
		private UIButton button;
		private UIColor color;
		private UIColorModel value;

		public ButtonColor(UIWindow window, UIContainer parent, String text){
			this.window = window;
			this.value = new UIColorModel();
			this.button = getUIFactory().createButton(parent);
			this.button.setText(text);
			this.addListeners();
		}

		public void loadColor(UIColorModel cm){
			this.value.setRed(cm.getRed());
			this.value.setGreen(cm.getGreen());
			this.value.setBlue(cm.getBlue());

			UIColor color = getUIFactory().createColor(this.value);
			this.button.setFgColor(color);
			this.disposeColor();
			this.color = color;
		}

		public void disposeColor(){
			if( this.color != null && !this.color.isDisposed()){
				this.color.dispose();
				this.color = null;
			}
		}

		public void addListeners(){
			this.button.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					UIColorChooser dlg = getUIFactory().createColorChooser(ButtonColor.this.window);
					dlg.setDefaultModel(ButtonColor.this.value);
					dlg.setText(TuxGuitar.getProperty("choose-color"));
					dlg.choose(new UIColorChooserHandler() {
						public void onSelectColor(UIColorModel model) {
							if( model != null) {
								ButtonColor.this.loadColor(model);
							}
						}
					});
				}
			});
			this.button.addDisposeListener(new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					ButtonColor.this.disposeColor();
				}
			});
		}

		public UIControl getControl() {
			return this.button;
		}

		public UIColorModel getValue(){
			return this.value;
		}
	}
}
