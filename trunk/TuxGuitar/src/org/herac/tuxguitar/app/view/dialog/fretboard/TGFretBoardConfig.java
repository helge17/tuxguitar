package org.herac.tuxguitar.app.view.dialog.fretboard;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIColorChooserHandler;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooserHandler;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGFretBoardConfig {
	
	private static final float MINIMUM_CONTROL_WIDTH = 180f;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;
	
	public static final int DISPLAY_TEXT_NOTE = 0x01;
	public static final int DISPLAY_TEXT_SCALE = 0x02;
	public static final int DIRECTION_RIGHT = 0;
	public static final int DIRECTION_LEFT = 1;
	
	private TGContext context;
	private int style;
	private int direction;
	private UIFont font;
	private UIColor colorBackground;
	private UIColor colorString;
	private UIColor colorFretPoint;
	private UIColor colorNote;
	private UIColor colorScale;
	private UIColor colorKeyTextBackground;
	
	public TGFretBoardConfig(TGContext context){
		this.context = context;
	}
	
	public int getStyle() {
		return this.style;
	}
	
	public UIFont getFont() {
		return this.font;
	}
	
	public UIColor getColorBackground() {
		return this.colorBackground;
	}
	
	public UIColor getColorString() {
		return this.colorString;
	}
	
	public UIColor getColorFretPoint() {
		return this.colorFretPoint;
	}
	
	public UIColor getColorNote() {
		return this.colorNote;
	}
	
	public UIColor getColorScale() {
		return this.colorScale;
	}
	
	public UIColor getColorKeyTextBackground() {
		return colorKeyTextBackground;
	}

	public int getDirection(){
		return this.direction;
	}
	
	public UIFont createFont(UIFactory factory, TGFontModel fm) {
		return TGApplication.getInstance(this.context).getFactory().createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
	
	public UIColor createColor(UIFactory factory, TGColorModel cm) {
		return TGApplication.getInstance(this.context).getFactory().createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public void load(){
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		this.style = config.getIntegerValue(TGConfigKeys.FRETBOARD_STYLE);
		this.direction = config.getIntegerValue(TGConfigKeys.FRETBOARD_DIRECTION, DIRECTION_RIGHT );
		this.font = createFont(factory, config.getFontModelConfigValue(TGConfigKeys.FRETBOARD_FONT));
		this.colorBackground = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND));
		this.colorString = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.FRETBOARD_COLOR_STRING));
		this.colorFretPoint = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT));
		this.colorNote = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.FRETBOARD_COLOR_NOTE));
		this.colorScale = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.FRETBOARD_COLOR_SCALE));
		this.colorKeyTextBackground = createColor(factory, new TGColorModel(0xff, 0xff, 0xff));
	}
	
	public void defaults(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		TGProperties defaults = TGConfigDefaults.createDefaults();
		config.setValue(TGConfigKeys.FRETBOARD_STYLE,defaults.getValue(TGConfigKeys.FRETBOARD_STYLE));
		config.setValue(TGConfigKeys.FRETBOARD_DIRECTION,defaults.getValue(TGConfigKeys.FRETBOARD_DIRECTION));
		config.setValue(TGConfigKeys.FRETBOARD_FONT,defaults.getValue(TGConfigKeys.FRETBOARD_FONT));
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,defaults.getValue(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND));
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_STRING,defaults.getValue(TGConfigKeys.FRETBOARD_COLOR_STRING));
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,defaults.getValue(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT));
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_NOTE,defaults.getValue(TGConfigKeys.FRETBOARD_COLOR_NOTE));
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_SCALE,defaults.getValue(TGConfigKeys.FRETBOARD_COLOR_SCALE));
	}
	
	public void save(int style, int direction, UIFontModel fm, UIColorModel rgbBackground, UIColorModel rgbString, UIColorModel rgbFretPoint, UIColorModel rgbNote, UIColorModel rgbScale){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		config.setValue(TGConfigKeys.FRETBOARD_STYLE,style);
		config.setValue(TGConfigKeys.FRETBOARD_DIRECTION,direction);
		config.setValue(TGConfigKeys.FRETBOARD_FONT,fm);
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,rgbBackground);
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_STRING,rgbString);
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,rgbFretPoint);
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_NOTE,rgbNote);
		config.setValue(TGConfigKeys.FRETBOARD_COLOR_SCALE,rgbScale);
	}
	
	public void saveDirection( int direction ){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		config.setValue(TGConfigKeys.FRETBOARD_DIRECTION,direction);
		
		this.direction = direction;
	}
	
	public void dispose(){
		this.font.dispose();
		this.colorBackground.dispose();
		this.colorString.dispose();
		this.colorFretPoint.dispose();
		this.colorNote.dispose();
		this.colorScale.dispose();
		this.colorKeyTextBackground.dispose();
	}
	
	public void configure(UIWindow parent) {
		final UIFactory factory = getUIFactory();
		final UITableLayout windowLayout = new UITableLayout();
		final UIWindow window = factory.createWindow(parent, true, false);
		window.setLayout(windowLayout);
		window.setText(TuxGuitar.getProperty("fretboard.settings"));
		
		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = factory.createLegendPanel(window);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("fretboard.settings"));
		windowLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		int groupRow = 0;
		
		final UIFontModel fontData = getFontChooser(window, group, TuxGuitar.getProperty("fretboard.font"), this.font, ++groupRow);
		
		// Color
		final UIColorModel rgbBackground = getColorChooser(window, group, TuxGuitar.getProperty("fretboard.background-color"), this.colorBackground, ++groupRow);
		final UIColorModel rgbString = getColorChooser(window, group, TuxGuitar.getProperty("fretboard.string-color"), this.colorString, ++groupRow);
		final UIColorModel rgbFretPoint = getColorChooser(window, group, TuxGuitar.getProperty("fretboard.fretpoint-color"), this.colorFretPoint, ++groupRow);
		final UIColorModel rgbNote = getColorChooser(window, group, TuxGuitar.getProperty("fretboard.note-color"), this.colorNote, ++groupRow);
		final UIColorModel rgbScale = getColorChooser(window, group, TuxGuitar.getProperty("fretboard.scale-note-color"), this.colorScale, ++groupRow);
		
		
		UILabel directionLabel = factory.createLabel(group);
		directionLabel.setText(TuxGuitar.getProperty("fretboard.direction"));
		groupLayout.set(directionLabel, ++groupRow, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		final UIDropDownSelect<Integer> directionCombo = factory.createDropDownSelect(group);
		directionCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("fretboard.right-mode"), DIRECTION_RIGHT));
		directionCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("fretboard.left-mode"), DIRECTION_LEFT));
		directionCombo.setSelectedItem(new UISelectItem<Integer>(null, this.direction));
		groupLayout.set(directionCombo, groupRow, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		// ----------------------------------------------------------------------
		groupLayout = new UITableLayout();
		group = factory.createLegendPanel(window);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("fretboard.settings.options"));
		windowLayout.set(group, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox displayTextNote = factory.createCheckBox(group);
		displayTextNote.setText(TuxGuitar.getProperty("fretboard.display-note-text"));
		displayTextNote.setSelected( (this.style & DISPLAY_TEXT_NOTE) != 0 );
		groupLayout.set(displayTextNote, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox displayTextScale = factory.createCheckBox(group);
		displayTextScale.setText(TuxGuitar.getProperty("fretboard.display-scale-text"));
		displayTextScale.setSelected( (this.style & DISPLAY_TEXT_SCALE) != 0 );
		groupLayout.set(displayTextScale, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
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
				int style = 0;
				style |= (displayTextNote.isSelected() ? DISPLAY_TEXT_NOTE : 0 );
				style |= (displayTextScale.isSelected() ? DISPLAY_TEXT_SCALE : 0 );
				
				Integer direction = directionCombo.getSelectedValue();
				if( direction == null ) {
					direction = DIRECTION_RIGHT;
				}
				
				window.dispose();
				
				save(style, direction, fontData, rgbBackground, rgbString, rgbFretPoint, rgbNote, rgbScale);
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
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	protected void applyChanges(){
		this.dispose();
		this.load();
		
		TGFretBoardEditor.getInstance(this.context).getFretBoard().reloadFromConfig();
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
	
	private UIFontModel getFontChooser(final UIWindow window, UILayoutContainer parent, String title, UIFont font, int row) {
		final UIFactory factory = getUIFactory();
		final UIFontModel selection = new UIFontModel(font.getName(), font.getHeight(), font.isBold(), font.isItalic());
		
		UITableLayout layout = (UITableLayout) parent.getLayout();
		UILabel label = factory.createLabel(parent);
		label.setText(title);
		layout.set(label, row, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		UIButton button = factory.createButton(parent);
		button.setText(TuxGuitar.getProperty("choose"));
		button.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIFontChooser uiFontChooser = factory.createFontChooser(window);
				uiFontChooser.setDefaultModel(selection);
				uiFontChooser.choose(new UIFontChooserHandler() {
					public void onSelectFont(UIFontModel model) {
						if( model != null ){
							selection.setName(model.getName());
							selection.setHeight(model.getHeight());
							selection.setBold(model.isBold());
							selection.setItalic(model.isItalic());
						}
					}
				});
			}
		});
		layout.set(button, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		return selection;
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
