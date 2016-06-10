package org.herac.tuxguitar.app.view.dialog.settings.items;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
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
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGSynchronizer;

public class StylesOption extends TGSettingsOption {
	
	private static final float BUTTON_WIDTH = 200;
	
	private boolean initialized;
	private UIFontModel defaultFontData;
	private UIFontModel noteFontData;
	private UIFontModel timeSignatureFontData;
	private UIFontModel textFontData;
	private UIFontModel lyricFontData;
	private UIFontModel printerDefaultFontData;
	private UIFontModel printerNoteFontData;
	private UIFontModel printerTSFontData;
	private UIFontModel printerTextFontData;
	private UIFontModel printerLyricFontData;
	
	private UIButton defaultFontButton;
	private UIButton noteFontButton;
	private UIButton timeSignatureFontButton;
	private UIButton textFontButton;
	private UIButton lyricFontButton;
	
	private UIButton printerDefaultFontButton;
	private UIButton printerNoteFontButton;
	private UIButton printerTSFontButton;
	private UIButton printerTextFontButton;
	private UIButton printerLyricFontButton;
	
	private UIColorButton scoreNoteColorButton;
	private UIColorButton tabNoteColorButton;
	private UIColorButton playNoteColorButton;
	private UIColorButton linesColorButton;
	
	public StylesOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.styles"));
		this.initialized = false;
		this.defaultFontData = new UIFontModel();
		this.noteFontData = new UIFontModel();
		this.timeSignatureFontData = new UIFontModel();
		this.textFontData = new UIFontModel();
		this.lyricFontData = new UIFontModel();
		this.printerDefaultFontData = new UIFontModel();
		this.printerNoteFontData = new UIFontModel();
		this.printerTSFontData = new UIFontModel();
		this.printerTextFontData = new UIFontModel();
		this.printerLyricFontData = new UIFontModel();
	}
	
	public void createOption(){
		UIFactory uiFactory = this.getUIFactory();
		
		getToolItem().setText(TuxGuitar.getProperty("settings.config.styles"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionStyle());
		getToolItem().addSelectionListener(this);
		
		//=================================================== EDITOR STYLES ===================================================//
		showLabel(getPanel(), TuxGuitar.getProperty("settings.config.styles.general"), true, 1, 1);
		
		UIPanel mainSection = uiFactory.createPanel(getPanel(), false);
		mainSection.setLayout(new UITableLayout());
		this.indent(mainSection, 2, 1);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.font.default"), false, 1, 1);
		this.defaultFontButton = this.createFontButton(mainSection, this.defaultFontData, 1, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.font.note"), false, 2, 1);
		this.noteFontButton = this.createFontButton(mainSection, this.noteFontData, 2, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.font.lyric"), false, 3, 1);
		this.lyricFontButton = this.createFontButton(mainSection, this.lyricFontData, 3, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.font.text"), false, 4, 1);
		this.textFontButton = this.createFontButton(mainSection, this.textFontData, 4, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.font.time-signature"), false, 5, 1);
		this.timeSignatureFontButton = this.createFontButton(mainSection, this.timeSignatureFontData, 5, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.color.score-note"), false, 6, 1);
		this.scoreNoteColorButton = this.createColorButton(mainSection, TuxGuitar.getProperty("choose"), 6, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.color.tab-note"), false, 7, 1);
		this.tabNoteColorButton = this.createColorButton(mainSection, TuxGuitar.getProperty("choose"), 7, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.color.play-note"), false, 8, 1);
		this.playNoteColorButton = this.createColorButton(mainSection, TuxGuitar.getProperty("choose"), 8, 2);
		
		showLabel(mainSection, TuxGuitar.getProperty("settings.config.styles.color.lines"), false, 9, 1);
		this.linesColorButton = this.createColorButton(mainSection, TuxGuitar.getProperty("choose"), 9, 2);
		
		//=================================================== PRINTER STYLES ===================================================//
		showLabel(getPanel(), TuxGuitar.getProperty("settings.config.styles.printer"), true, 3, 1);
		
		UIPanel printerSection = uiFactory.createPanel(getPanel(), false);
		printerSection.setLayout(new UITableLayout());
		this.indent(printerSection, 4, 1);
		
		showLabel(printerSection, TuxGuitar.getProperty("settings.config.styles.font.default"), false, 1, 1);
		this.printerDefaultFontButton = this.createFontButton(printerSection, this.printerDefaultFontData, 1, 2);
		
		showLabel(printerSection, TuxGuitar.getProperty("settings.config.styles.font.note"), false, 2, 1);
		this.printerNoteFontButton = this.createFontButton(printerSection, this.printerNoteFontData, 2, 2);
		
		showLabel(printerSection, TuxGuitar.getProperty("settings.config.styles.font.lyric"), false, 3, 1);
		this.printerLyricFontButton = this.createFontButton(printerSection, this.printerLyricFontData, 3, 2);
		
		showLabel(printerSection, TuxGuitar.getProperty("settings.config.styles.font.text"), false, 4, 1);
		this.printerTextFontButton = this.createFontButton(printerSection, this.printerTextFontData, 4, 2);
		
		showLabel(printerSection, TuxGuitar.getProperty("settings.config.styles.font.time-signature"), false, 5, 1);
		this.printerTSFontButton = this.createFontButton(printerSection, this.printerTSFontData, 5, 2);
		
		this.loadConfig();
	}
	
	public UIColorButton createColorButton(UILayoutContainer parent, String text, int row, int col) {
		UIColorButton button = new UIColorButton(getWindow(), parent, text);
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(button.getControl(), row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, BUTTON_WIDTH, null, null);
		
		return button;
	}
	
	public UIButton createFontButton(UILayoutContainer parent, UIFontModel fontModel, int row, int col) {
		UIButton uiButton = getUIFactory().createButton(parent);
		uiButton.setText("-");
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(uiButton, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, BUTTON_WIDTH, null, null);
		
		this.addFontButtonListeners(uiButton, fontModel);
		
		return uiButton;
	}
	
	private void addFontButtonListeners(final UIButton button, final UIFontModel fontModel){
		button.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( StylesOption.this.initialized ) {
					UIFontModel defaultModel = new UIFontModel();
					copyFontData(fontModel, defaultModel);
					
					UIFontChooser uiFontChooser = getUIFactory().createFontChooser(getWindow());
					uiFontChooser.setDefaultModel(defaultModel);
					uiFontChooser.choose(new UIFontChooserHandler() {
						public void onSelectFont(UIFontModel selection) {
							if( selection != null ) {
								loadFontData(selection, fontModel, button);
							}
						}
					});
				}
			}
		});
	}
	
	protected void loadFontData(UIFontModel src, UIFontModel dst, UIButton button){
		copyFontData(src, dst);
		setButtonFontData(button, dst);
	}
	
	protected void loadColor(UIColorButton button, UIColorModel rgb){
		button.loadColor(rgb);
	}
	
	protected void setButtonFontData(UIButton button, UIFontModel fontModel) {
		String text = fontModel.getName();
		if( fontModel.isBold()){
			text += " Bold";
		}
		if( fontModel.isItalic()){
			text += " Italic";
		}
		text += (" " + fontModel.getHeight());
		button.setText(text);
	}
	
	protected void copyFontData(UIFontModel src, UIFontModel dst){
		dst.setName( src.getName() );
		dst.setHeight( src.getHeight() );
		dst.setBold( src.isBold() );
		dst.setItalic( src.isItalic() );
	}
	
	protected void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				final UIFontModel defaultFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_DEFAULT);
				final UIFontModel noteFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_NOTE);
				final UIFontModel timeSignatureFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_TIME_SIGNATURE);
				final UIFontModel textFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_TEXT);
				final UIFontModel lyricFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_LYRIC);
				final UIFontModel printerDefaultFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_PRINTER_DEFAULT);
				final UIFontModel printerNoteFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_PRINTER_NOTE);
				final UIFontModel printerTSFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE);
				final UIFontModel printerTextFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TEXT);
				final UIFontModel printerLyricFontData = getConfig().getUIFontModelConfigValue(TGConfigKeys.FONT_PRINTER_LYRIC);
				final UIColorModel scoreNoteRGB  = getConfig().getUIColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE);
				final UIColorModel tabNoteRGB  = getConfig().getUIColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE);
				final UIColorModel playNoteRGB  = getConfig().getUIColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE);
				final UIColorModel linesRGB  = getConfig().getUIColorModelConfigValue(TGConfigKeys.COLOR_LINE);
				TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if(!isDisposed()){
							loadFontData(defaultFontData, StylesOption.this.defaultFontData, StylesOption.this.defaultFontButton);
							loadFontData(noteFontData, StylesOption.this.noteFontData, StylesOption.this.noteFontButton);
							loadFontData(timeSignatureFontData, StylesOption.this.timeSignatureFontData, StylesOption.this.timeSignatureFontButton);
							loadFontData(textFontData, StylesOption.this.textFontData, StylesOption.this.textFontButton);
							loadFontData(lyricFontData, StylesOption.this.lyricFontData, StylesOption.this.lyricFontButton);
							loadFontData(printerDefaultFontData, StylesOption.this.printerDefaultFontData, StylesOption.this.printerDefaultFontButton);
							loadFontData(printerNoteFontData, StylesOption.this.printerNoteFontData, StylesOption.this.printerNoteFontButton);
							loadFontData(printerTSFontData, StylesOption.this.printerTSFontData, StylesOption.this.printerTSFontButton);
							loadFontData(printerTextFontData, StylesOption.this.printerTextFontData, StylesOption.this.printerTextFontButton);
							loadFontData(printerLyricFontData, StylesOption.this.printerLyricFontData, StylesOption.this.printerLyricFontButton);
							StylesOption.this.scoreNoteColorButton.loadColor(scoreNoteRGB);
							StylesOption.this.tabNoteColorButton.loadColor(tabNoteRGB);
							StylesOption.this.playNoteColorButton.loadColor(playNoteRGB);
							StylesOption.this.linesColorButton.loadColor(linesRGB);
							StylesOption.this.initialized = true;
							StylesOption.this.pack();
						}
					}
				});
			}
		}).start();
	}
	
	public void updateConfig(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.FONT_DEFAULT,this.defaultFontData);
			getConfig().setValue(TGConfigKeys.FONT_NOTE,this.noteFontData);
			getConfig().setValue(TGConfigKeys.FONT_TIME_SIGNATURE,this.timeSignatureFontData);
			getConfig().setValue(TGConfigKeys.FONT_TEXT,this.textFontData);
			getConfig().setValue(TGConfigKeys.FONT_LYRIC,this.lyricFontData);
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_DEFAULT,this.printerDefaultFontData);
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_NOTE,this.printerNoteFontData);
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE,this.printerTSFontData);
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_TEXT,this.printerTextFontData);
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_LYRIC,this.printerLyricFontData);
			getConfig().setValue(TGConfigKeys.COLOR_SCORE_NOTE,this.scoreNoteColorButton.getValue());
			getConfig().setValue(TGConfigKeys.COLOR_TAB_NOTE,this.tabNoteColorButton.getValue());
			getConfig().setValue(TGConfigKeys.COLOR_PLAY_NOTE,this.playNoteColorButton.getValue());
			getConfig().setValue(TGConfigKeys.COLOR_LINE,this.linesColorButton.getValue());
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.FONT_DEFAULT, getDefaults().getValue(TGConfigKeys.FONT_DEFAULT));
			getConfig().setValue(TGConfigKeys.FONT_NOTE, getDefaults().getValue(TGConfigKeys.FONT_NOTE));
			getConfig().setValue(TGConfigKeys.FONT_TIME_SIGNATURE, getDefaults().getValue(TGConfigKeys.FONT_TIME_SIGNATURE));
			getConfig().setValue(TGConfigKeys.FONT_TEXT, getDefaults().getValue(TGConfigKeys.FONT_TEXT));
			getConfig().setValue(TGConfigKeys.FONT_LYRIC, getDefaults().getValue(TGConfigKeys.FONT_LYRIC));
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_DEFAULT, getDefaults().getValue(TGConfigKeys.FONT_PRINTER_DEFAULT));
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_NOTE, getDefaults().getValue(TGConfigKeys.FONT_PRINTER_NOTE));
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE, getDefaults().getValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE));
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_TEXT, getDefaults().getValue(TGConfigKeys.FONT_PRINTER_TEXT));
			getConfig().setValue(TGConfigKeys.FONT_PRINTER_LYRIC, getDefaults().getValue(TGConfigKeys.FONT_PRINTER_LYRIC));
			getConfig().setValue(TGConfigKeys.COLOR_SCORE_NOTE, getDefaults().getValue(TGConfigKeys.COLOR_SCORE_NOTE));
			getConfig().setValue(TGConfigKeys.COLOR_TAB_NOTE, getDefaults().getValue(TGConfigKeys.COLOR_TAB_NOTE));
			getConfig().setValue(TGConfigKeys.COLOR_PLAY_NOTE, getDefaults().getValue(TGConfigKeys.COLOR_PLAY_NOTE));
			getConfig().setValue(TGConfigKeys.COLOR_LINE, getDefaults().getValue(TGConfigKeys.COLOR_LINE));
		}
	}
	
	private class UIColorButton {
		
		private UIWindow window;
		private UIButton button;
		private UIColor color;
		private UIColorModel value;
		
		public UIColorButton(UIWindow window, UIContainer parent, String text){
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
					UIColorChooser dlg = getUIFactory().createColorChooser(UIColorButton.this.window);
					dlg.setDefaultModel(UIColorButton.this.value);
					dlg.setText(TuxGuitar.getProperty("choose-color"));
					dlg.choose(new UIColorChooserHandler() {
						public void onSelectColor(UIColorModel model) {
							if( model != null) {
								UIColorButton.this.loadColor(model);
							}
						}
					});
				}
			});
			this.button.addDisposeListener(new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					UIColorButton.this.disposeColor();
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