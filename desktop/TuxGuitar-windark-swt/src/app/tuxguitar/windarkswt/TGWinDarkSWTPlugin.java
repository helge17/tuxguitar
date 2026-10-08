package app.tuxguitar.windarkswt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.ui.appearance.UIColorAppearance;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.swt.SWTApplication;
import app.tuxguitar.ui.swt.appearance.SWTAppearance;
import app.tuxguitar.ui.swt.widget.SWTControl;
import app.tuxguitar.ui.swt.widget.SWTControlCustomizer;

import app.tuxguitar.util.configuration.TGConfigManager;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGExpressionResolver;
import app.tuxguitar.util.plugin.TGEarlyInitPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGWinDarkSWTPlugin implements TGEarlyInitPlugin {

	public static final String MODULE_ID = "tuxguitar-windark-swt";
	// specific configuration parameters for Windows dark mode in SWT
	// As of SWT 4.41, description of the configuration keys used in this class is described here:
	// https://github.com/eclipse-platform/eclipse.platform.swt/blob/3e3b24b4691c4f18a7d5e22653fe28d4c47385d7/bundles/org.eclipse.swt/Eclipse%20SWT/win32/org/eclipse/swt/widgets/Display.java#L164	
	private static final String KEY_USE_DARKMODE_EXPLORER_THEME = "org.eclipse.swt.internal.win32.useDarkModeExplorerTheme";
	private static final String KEY_USE_SHELL_TITLE_COLORING = "org.eclipse.swt.internal.win32.useShellTitleColoring";
	private static final String KEY_MENUBAR_FOREGROUND_COLOR = "org.eclipse.swt.internal.win32.menuBarForegroundColor";
	private static final String KEY_MENUBAR_BACKGROUND_COLOR = "org.eclipse.swt.internal.win32.menuBarBackgroundColor";
	private static final String KEY_ALL_USE_WS_BORDER = "org.eclipse.swt.internal.win32.all.use_WS_BORDER";
	private static final String KEY_LABEL_DISABLED_FOREGROUND_COLOR = "org.eclipse.swt.internal.win32.Label.disabledForegroundColor";
	private static final String KEY_COMBO_USE_DARK_THEME = "org.eclipse.swt.internal.win32.Combo.useDarkTheme";
	private static final String KEY_PROGRESSBAR_USE_COLORS = "org.eclipse.swt.internal.win32.ProgressBar.useColors";
	// colors for Windows dark mode
	private Map<String, UIColorModel> colorMap;

	private static final String WIN_DARK_INPUT_BACKGROUND = "input.background";
	private static final String WIN_DARK_INPUT_FOREGROUND = "input.foreground";
	private static final String WIN_DARK_DISABLED_FOREGROUND = "disabled.foreground";
	private static final String WIN_DARK_WIDGET_BACKGROUND = "widget.background";
	private static final String WIN_DARK_WIDGET_FOREGROUND = "widget.foreground";
	private static final String WIN_DARK_WIDGET_LIGHT_BACKGROUND = "widget.light.background";
	private static final String WIN_DARK_WIDGET_LIGHT_FOREGROUND = "widget.light.foreground";
	private static final String WIN_DARK_WIDGET_HIGHLIGHT_BACKGROUND = "widget.highlight.background";
	private static final String WIN_DARK_WIDGET_HIGHLIGHT_FOREGROUND = "widget.highlight.foreground";
	private static final String WIN_DARK_WIDGET_SELECTED_BACKGROUND = "widget.selected.background";
	private static final String WIN_DARK_INPUT_SELECTED_BACKGROUND = "input.selected.background";
	private static final String WIN_DARK_INPUT_SELECTED_FOREGROUND = "input.selected.foreground";

	private TGConfigManager config;
	private TGContext context;

	@Override
	public String getModuleId() {
		return MODULE_ID;
	}

	@Override
	public void earlyInit(TGContext context) throws TGPluginException {

		this.context = context;
		this.colorMap = configureDarkColors();

		Display display = ((SWTApplication) TGApplication.getInstance(context).getApplication()).getDisplay();

		if (Display.isSystemDarkTheme()) {
			configureDarkDisplay(display);
			setColorPalette(context);
			configureControlsColors();
		}
	}

	// default values of configurable parameters
	private Map<String, UIColorModel> configureDarkColors() {
		TGConfigManager cfg = getConfig();
		HashMap<String, UIColorModel> map = new HashMap<String, UIColorModel>();
		addColor(cfg, map, WIN_DARK_INPUT_BACKGROUND, new UIColorModel(0x24, 0x24, 0x24));
		addColor(cfg, map, WIN_DARK_INPUT_FOREGROUND, new UIColorModel(0xD0, 0xD0, 0xD0));
		addColor(cfg, map, WIN_DARK_DISABLED_FOREGROUND, new UIColorModel(0x80, 0x80, 0x80));
		addColor(cfg, map, WIN_DARK_WIDGET_BACKGROUND, new UIColorModel(0x30, 0x30, 0x30));
		addColor(cfg, map, WIN_DARK_WIDGET_FOREGROUND, new UIColorModel(0xD0, 0xD0, 0xD0));
		addColor(cfg, map, WIN_DARK_WIDGET_LIGHT_BACKGROUND, new UIColorModel(0x3A, 0x3A, 0x3A));
		addColor(cfg, map, WIN_DARK_WIDGET_LIGHT_FOREGROUND, new UIColorModel(0xD0, 0xD0, 0xD0));
		addColor(cfg, map, WIN_DARK_WIDGET_HIGHLIGHT_BACKGROUND, new UIColorModel(0x45, 0x45, 0x45));
		addColor(cfg, map, WIN_DARK_WIDGET_HIGHLIGHT_FOREGROUND, new UIColorModel(0xD0, 0xD0, 0xD0));
		addColor(cfg, map, WIN_DARK_WIDGET_SELECTED_BACKGROUND, new UIColorModel(0x50, 0x50, 0x50));
		addColor(cfg, map, WIN_DARK_INPUT_SELECTED_BACKGROUND, new UIColorModel(0x26, 0x4F, 0x78));
		addColor(cfg, map, WIN_DARK_INPUT_SELECTED_FOREGROUND, new UIColorModel(0xFF, 0xFF, 0xFF));

		return map;
	}

	private void addColor(TGConfigManager cfg, Map<String, UIColorModel> map, String key, UIColorModel defaultValue) {
		map.put(key, getColorModelConfigValue(cfg, key, defaultValue));
	}

	@Override
	public void connect(TGContext context) throws TGPluginException {
		// do nothing, everything is set up at early init
	}

	@Override
	public void disconnect(TGContext context) throws TGPluginException {
		// nothing to do
	}

	// As of SWT 4.41, SWT documentation explicitly states that Color instances do not need to be disposed
	// so it's OK to just create a new instance when needed.
	// https://help.eclipse.org/latest/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/graphics/Color.html
	private Color toColor(UIColorModel colorModel) {
		return new Color(colorModel.getRed(), colorModel.getGreen(), colorModel.getBlue());
	}

	private void configureDarkDisplay(Display display) {
		display.setData(KEY_USE_DARKMODE_EXPLORER_THEME, Boolean.TRUE);
		display.setData(KEY_USE_SHELL_TITLE_COLORING, Boolean.TRUE);
		display.setData(KEY_MENUBAR_FOREGROUND_COLOR, toColor(this.colorMap.get(WIN_DARK_WIDGET_FOREGROUND)));
		display.setData(KEY_MENUBAR_BACKGROUND_COLOR, toColor(this.colorMap.get(WIN_DARK_WIDGET_BACKGROUND)));
		display.setData(KEY_ALL_USE_WS_BORDER, Boolean.TRUE);
		display.setData(KEY_LABEL_DISABLED_FOREGROUND_COLOR, toColor(this.colorMap.get(WIN_DARK_DISABLED_FOREGROUND)));
		display.setData(KEY_COMBO_USE_DARK_THEME, Boolean.TRUE);
		display.setData(KEY_PROGRESSBAR_USE_COLORS, Boolean.TRUE);
	}

	private void setColorPalette(TGContext context) {
		SWTAppearance appearance = (SWTAppearance) ((SWTApplication) TGApplication.getInstance(context)
				.getApplication()).getAppearance();
		HashMap<UIColorAppearance, UIColorModel> colorMap = new HashMap<UIColorAppearance, UIColorModel>();

		colorMap.put(UIColorAppearance.WidgetBackground, this.colorMap.get(WIN_DARK_WIDGET_BACKGROUND));
		colorMap.put(UIColorAppearance.WidgetForeground, this.colorMap.get(WIN_DARK_WIDGET_FOREGROUND));
		colorMap.put(UIColorAppearance.WidgetLightBackground, this.colorMap.get(WIN_DARK_WIDGET_LIGHT_BACKGROUND));
		colorMap.put(UIColorAppearance.WidgetLightForeground, this.colorMap.get(WIN_DARK_WIDGET_LIGHT_FOREGROUND));
		colorMap.put(UIColorAppearance.WidgetHighlightBackground, this.colorMap.get(WIN_DARK_WIDGET_HIGHLIGHT_BACKGROUND));
		colorMap.put(UIColorAppearance.WidgetHighlightForeground, this.colorMap.get(WIN_DARK_WIDGET_HIGHLIGHT_FOREGROUND));
		colorMap.put(UIColorAppearance.WidgetSelectedBackground, this.colorMap.get(WIN_DARK_WIDGET_SELECTED_BACKGROUND));
		colorMap.put(UIColorAppearance.InputBackground, this.colorMap.get(WIN_DARK_INPUT_BACKGROUND));
		colorMap.put(UIColorAppearance.InputForeground, this.colorMap.get(WIN_DARK_INPUT_FOREGROUND));
		colorMap.put(UIColorAppearance.InputSelectedBackground, this.colorMap.get(WIN_DARK_INPUT_SELECTED_BACKGROUND));
		colorMap.put(UIColorAppearance.InputSelectedForeground, this.colorMap.get(WIN_DARK_INPUT_SELECTED_FOREGROUND));

		appearance.setColorMap(colorMap);
	}

	private void configureControlsColors() {
		SWTControlCustomizer customizer = new SWTControlCustomizer() {
			@Override
			public void customize(SWTControl<?> swtControl) {
				Color defaultBgColor = TGWinDarkSWTPlugin.this
						.toColor(TGWinDarkSWTPlugin.this.isInputControl(swtControl.getControl())
								? TGWinDarkSWTPlugin.this.colorMap.get(WIN_DARK_INPUT_BACKGROUND)
								: TGWinDarkSWTPlugin.this.colorMap.get(WIN_DARK_WIDGET_BACKGROUND));
				swtControl.setDefaultBgColor(defaultBgColor);
				swtControl.getControl().setBackground(defaultBgColor);

				Color defaultFgColor = TGWinDarkSWTPlugin.this
						.toColor(TGWinDarkSWTPlugin.this.isInputControl(swtControl.getControl())
								? TGWinDarkSWTPlugin.this.colorMap.get(WIN_DARK_INPUT_FOREGROUND)
								: TGWinDarkSWTPlugin.this.colorMap.get(WIN_DARK_WIDGET_FOREGROUND));
				swtControl.setDefaultFgColor(defaultFgColor);
				swtControl.getControl().setForeground(defaultFgColor);
			}
		};
		SWTControl.setControlsCustomizer(customizer);
	}

	private boolean isInputControl(Control control) {
		return (control instanceof Text || control instanceof StyledText || control instanceof Combo
				|| control instanceof CCombo || control instanceof List || control instanceof Table
				|| control instanceof Tree || control instanceof Spinner || control instanceof DateTime);
	}

	private TGConfigManager getConfig() {
		if (this.config == null) {
			this.config = new TGConfigManager(this.context, MODULE_ID);
		}
		return this.config;
	}

	private UIColorModel getColorModelConfigValue(TGConfigManager cfg, String key, UIColorModel defaultValue) {
		String value = cfg.getStringValue(key);
		if (value == null) {
			return defaultValue;
		}
		value = TGExpressionResolver.getInstance(context).resolve(value);
		String[] values = value.trim().split(",");
		if (values != null && values.length == 3) {
			try {
				int red = Integer.parseInt(values[0].trim());
				int green = Integer.parseInt(values[1].trim());
				int blue = Integer.parseInt(values[2].trim());

				return new UIColorModel(red, green, blue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}
}
