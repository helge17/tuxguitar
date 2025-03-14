package app.tuxguitar.ui.jfx;

import java.io.InputStream;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIDirectoryChooser;
import app.tuxguitar.ui.chooser.UIFileChooser;
import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.jfx.chooser.AWTPrinterChooser;
import app.tuxguitar.ui.jfx.chooser.JFXColorChooser;
import app.tuxguitar.ui.jfx.chooser.JFXDirectoryChooser;
import app.tuxguitar.ui.jfx.chooser.JFXFileChooser;
import app.tuxguitar.ui.jfx.chooser.JFXFontChooser;
import app.tuxguitar.ui.jfx.menu.JFXMenuBar;
import app.tuxguitar.ui.jfx.menu.JFXPopupMenu;
import app.tuxguitar.ui.jfx.resource.JFXResourceFactory;
import app.tuxguitar.ui.jfx.toolbar.JFXToolBar;
import app.tuxguitar.ui.jfx.widget.JFXBrowser;
import app.tuxguitar.ui.jfx.widget.JFXButton;
import app.tuxguitar.ui.jfx.widget.JFXCanvas;
import app.tuxguitar.ui.jfx.widget.JFXCheckBox;
import app.tuxguitar.ui.jfx.widget.JFXContainer;
import app.tuxguitar.ui.jfx.widget.JFXDivider;
import app.tuxguitar.ui.jfx.widget.JFXDropDownSelect;
import app.tuxguitar.ui.jfx.widget.JFXImageView;
import app.tuxguitar.ui.jfx.widget.JFXIndeterminateProgressBar;
import app.tuxguitar.ui.jfx.widget.JFXKnob;
import app.tuxguitar.ui.jfx.widget.JFXLabel;
import app.tuxguitar.ui.jfx.widget.JFXLegendPanel;
import app.tuxguitar.ui.jfx.widget.JFXLinkLabel;
import app.tuxguitar.ui.jfx.widget.JFXListBoxSelect;
import app.tuxguitar.ui.jfx.widget.JFXPanel;
import app.tuxguitar.ui.jfx.widget.JFXPasswordField;
import app.tuxguitar.ui.jfx.widget.JFXProgressBar;
import app.tuxguitar.ui.jfx.widget.JFXRadioButton;
import app.tuxguitar.ui.jfx.widget.JFXReadOnlyTextBox;
import app.tuxguitar.ui.jfx.widget.JFXReadOnlyTextField;
import app.tuxguitar.ui.jfx.widget.JFXScale;
import app.tuxguitar.ui.jfx.widget.JFXScrollBarPanel;
import app.tuxguitar.ui.jfx.widget.JFXSeparator;
import app.tuxguitar.ui.jfx.widget.JFXSlider;
import app.tuxguitar.ui.jfx.widget.JFXSpinner;
import app.tuxguitar.ui.jfx.widget.JFXSplashWindow;
import app.tuxguitar.ui.jfx.widget.JFXTabFolder;
import app.tuxguitar.ui.jfx.widget.JFXTable;
import app.tuxguitar.ui.jfx.widget.JFXTextArea;
import app.tuxguitar.ui.jfx.widget.JFXTextField;
import app.tuxguitar.ui.jfx.widget.JFXToggleButton;
import app.tuxguitar.ui.jfx.widget.JFXWindow;
import app.tuxguitar.ui.jfx.widget.JFXWrapLabel;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.widget.UIBrowser;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UICheckTable;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIDivider;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UIIndeterminateProgressBar;
import app.tuxguitar.ui.widget.UIKnob;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UILinkLabel;
import app.tuxguitar.ui.widget.UIListBoxSelect;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIPasswordField;
import app.tuxguitar.ui.widget.UIProgressBar;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UIReadOnlyTextBox;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;
import app.tuxguitar.ui.widget.UIScale;
import app.tuxguitar.ui.widget.UIScrollBarPanel;
import app.tuxguitar.ui.widget.UISeparator;
import app.tuxguitar.ui.widget.UISlider;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UISplashWindow;
import app.tuxguitar.ui.widget.UITabFolder;
import app.tuxguitar.ui.widget.UITable;
import app.tuxguitar.ui.widget.UITextArea;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIToggleButton;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.ui.widget.UIWrapLabel;

import javafx.geometry.Orientation;

public class JFXFactory implements UIFactory {

	private JFXApplication application;
	private UIResourceFactory resourceFactory;

	public JFXFactory(JFXApplication application) {
		this.application = application;
		this.resourceFactory = new JFXResourceFactory();
	}

	public UISplashWindow createSplashWindow() {
		return new JFXSplashWindow(this.application.getControl().getStage());
	}

	public UIWindow createWindow() {
		return new JFXWindow(this.application.getControl().getStage(), null);
	}

	public UIWindow createWindow(UIWindow parent, boolean modal, boolean resizable) {
		return new JFXWindow((JFXWindow) parent, modal, resizable);
	}

	public UIPanel createPanel(UIContainer parent, boolean bordered) {
		return new JFXPanel((JFXContainer<?>) parent, bordered);
	}

	public UIScrollBarPanel createScrollBarPanel(UIContainer parent, boolean vScroll, boolean hScroll, boolean bordered) {
		return new JFXScrollBarPanel((JFXContainer<?>) parent, vScroll, hScroll, bordered);
	}

	public UILegendPanel createLegendPanel(UIContainer parent) {
		return new JFXLegendPanel((JFXContainer<?>) parent);
	}

	public UICanvas createCanvas(UIContainer parent, boolean bordered) {
		return new JFXCanvas((JFXContainer<?>) parent, bordered);
	}

	public UILabel createLabel(UIContainer parent) {
		return new JFXLabel((JFXContainer<?>) parent);
	}

	public UIWrapLabel createWrapLabel(UIContainer parent) {
		return new JFXWrapLabel((JFXContainer<?>) parent);
	}

	public UILinkLabel createLinkLabel(UIContainer parent) {
		return new JFXLinkLabel((JFXContainer<?>) parent);
	}

	public UIImageView createImageView(UIContainer parent) {
		return new JFXImageView((JFXContainer<?>) parent);
	}

	public UISeparator createVerticalSeparator(UIContainer parent) {
		return new JFXSeparator((JFXContainer<?>) parent, Orientation.VERTICAL);
	}

	public UISeparator createHorizontalSeparator(UIContainer parent) {
		return new JFXSeparator((JFXContainer<?>) parent, Orientation.HORIZONTAL);
	}

	public UIButton createButton(UIContainer parent) {
		return new JFXButton((JFXContainer<?>) parent);
	}

	public UIToggleButton createToggleButton(UIContainer parent) {
		return new JFXToggleButton((JFXContainer<?>) parent);
	}

	public UICheckBox createCheckBox(UIContainer parent) {
		return new JFXCheckBox((JFXContainer<?>) parent);
	}

	public UIRadioButton createRadioButton(UIContainer parent) {
		return new JFXRadioButton((JFXContainer<?>) parent);
	}

	public UITextField createTextField(UIContainer parent) {
		return new JFXTextField((JFXContainer<?>) parent);
	}

	public UIPasswordField createPasswordField(UIContainer parent) {
		return new JFXPasswordField((JFXContainer<?>) parent);
	}

	public UIReadOnlyTextField createReadOnlyTextField(UIContainer parent) {
		return new JFXReadOnlyTextField((JFXContainer<?>) parent);
	}

	public UITextArea createTextArea(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new JFXTextArea((JFXContainer<?>) parent, vScroll, hScroll);
	}

	public UIReadOnlyTextBox createReadOnlyTextBox(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new JFXReadOnlyTextBox((JFXContainer<?>) parent, vScroll, hScroll);
	}

	public UISpinner createSpinner(UIContainer parent) {
		return new JFXSpinner((JFXContainer<?>) parent);
	}

	public UISlider createHorizontalSlider(UIContainer parent) {
		return new JFXSlider((JFXContainer<?>) parent, Orientation.HORIZONTAL);
	}

	public UISlider createVerticalSlider(UIContainer parent) {
		return new JFXSlider((JFXContainer<?>) parent, Orientation.VERTICAL);
	}

	public UIScale createHorizontalScale(UIContainer parent) {
		return new JFXScale((JFXContainer<?>) parent, Orientation.HORIZONTAL);
	}

	public UIScale createVerticalScale(UIContainer parent) {
		return new JFXScale((JFXContainer<?>) parent, Orientation.VERTICAL);
	}

	public UIKnob createKnob(UIContainer parent) {
		return new JFXKnob((JFXContainer<?>) parent);
	}

	public UIProgressBar createProgressBar(UIContainer parent) {
		return new JFXProgressBar((JFXContainer<?>) parent);
	}

	public UIIndeterminateProgressBar createIndeterminateProgressBar(UIContainer parent) {
		return new JFXIndeterminateProgressBar((JFXContainer<?>) parent);
	}

	public <T> UITable<T> createTable(UIContainer parent, boolean headerVisible) {
		return new JFXTable<T>((JFXContainer<?>) parent, headerVisible, false);
	}

	public <T> UICheckTable<T> createCheckTable(UIContainer parent, boolean headerVisible) {
		return new JFXTable<T>((JFXContainer<?>) parent, headerVisible, true);
	}

	public <T> UIDropDownSelect<T> createDropDownSelect(UIContainer parent) {
		return new JFXDropDownSelect<T>((JFXContainer<?>) parent);
	}

	public <T> UIListBoxSelect<T> createListBoxSelect(UIContainer parent) {
		return new JFXListBoxSelect<T>((JFXContainer<?>) parent);
	}

	public UIToolBar createHorizontalToolBar(UIContainer parent) {
		return new JFXToolBar((JFXContainer<?>) parent, Orientation.HORIZONTAL);
	}

	public UIToolBar createVerticalToolBar(UIContainer parent) {
		return new JFXToolBar((JFXContainer<?>) parent, Orientation.VERTICAL);
	}

	public UIMenuBar createMenuBar(UIWindow parent) {
		return new JFXMenuBar((JFXWindow) parent);
	}

	public UIPopupMenu createPopupMenu(UIWindow parent) {
		return new JFXPopupMenu((JFXWindow) parent);
	}

	public UITabFolder createTabFolder(UIContainer parent, boolean showClose) {
		return new JFXTabFolder((JFXContainer<?>) parent, showClose);
	}

	public UIDivider createHorizontalDivider(UIContainer parent) {
		return new JFXDivider((JFXContainer<?>) parent);
	}

	public UIDivider createVerticalDivider(UIContainer parent) {
		return new JFXDivider((JFXContainer<?>) parent);
	}

	public UIFontChooser createFontChooser(UIWindow parent) {
		return new JFXFontChooser(this, (JFXWindow) parent);
	}

	public UIColorChooser createColorChooser(UIWindow parent) {
		return new JFXColorChooser(this, (JFXWindow) parent);
	}

	public UIFileChooser createOpenFileChooser(UIWindow parent) {
		return new JFXFileChooser((JFXWindow) parent, JFXFileChooser.STYLE_OPEN);
	}

	public UIFileChooser createSaveFileChooser(UIWindow parent) {
		return new JFXFileChooser((JFXWindow) parent, JFXFileChooser.STYLE_SAVE);
	}

	public UIDirectoryChooser createDirectoryChooser(UIWindow parent) {
		return new JFXDirectoryChooser((JFXWindow) parent);
	}

	public UIPrinterChooser createPrinterChooser(UIWindow parent) {
		return new AWTPrinterChooser();
	}

	public UIColor createColor(int red, int green, int blue) {
		return this.resourceFactory.createColor(red, green, blue);
	}

	public UIColor createColor(UIColorModel model) {
		return this.resourceFactory.createColor(model);
	}

	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return this.resourceFactory.createFont(name, height, bold, italic);
	}

	public UIFont createFont(UIFontModel model) {
		return this.resourceFactory.createFont(model);
	}

	public UIImage createImage(float width, float height) {
		return this.resourceFactory.createImage(width, height);
	}

	public UIImage createImage(InputStream inputStream) {
		return this.resourceFactory.createImage(inputStream);
	}

	public UIBrowser createBrowser(UIWindow parent) {
		return new JFXBrowser((JFXWindow) parent);
	}
}