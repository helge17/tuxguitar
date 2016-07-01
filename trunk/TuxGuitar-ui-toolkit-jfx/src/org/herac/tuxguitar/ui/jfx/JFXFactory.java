package org.herac.tuxguitar.ui.jfx;

import java.io.InputStream;

import javafx.geometry.Orientation;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.jfx.chooser.JFXColorChooser;
import org.herac.tuxguitar.ui.jfx.chooser.JFXDirectoryChooser;
import org.herac.tuxguitar.ui.jfx.chooser.JFXFileChooser;
import org.herac.tuxguitar.ui.jfx.chooser.JFXFontChooser;
import org.herac.tuxguitar.ui.jfx.chooser.JFXPrinterChooser;
import org.herac.tuxguitar.ui.jfx.menu.JFXMenuBar;
import org.herac.tuxguitar.ui.jfx.menu.JFXPopupMenu;
import org.herac.tuxguitar.ui.jfx.resource.JFXResourceFactory;
import org.herac.tuxguitar.ui.jfx.toolbar.JFXToolBar;
import org.herac.tuxguitar.ui.jfx.widget.JFXButton;
import org.herac.tuxguitar.ui.jfx.widget.JFXCanvas;
import org.herac.tuxguitar.ui.jfx.widget.JFXCheckBox;
import org.herac.tuxguitar.ui.jfx.widget.JFXContainer;
import org.herac.tuxguitar.ui.jfx.widget.JFXDivider;
import org.herac.tuxguitar.ui.jfx.widget.JFXDropDownSelect;
import org.herac.tuxguitar.ui.jfx.widget.JFXImageView;
import org.herac.tuxguitar.ui.jfx.widget.JFXIndeterminateProgressBar;
import org.herac.tuxguitar.ui.jfx.widget.JFXKnob;
import org.herac.tuxguitar.ui.jfx.widget.JFXLabel;
import org.herac.tuxguitar.ui.jfx.widget.JFXLegendPanel;
import org.herac.tuxguitar.ui.jfx.widget.JFXLinkLabel;
import org.herac.tuxguitar.ui.jfx.widget.JFXListBoxSelect;
import org.herac.tuxguitar.ui.jfx.widget.JFXPanel;
import org.herac.tuxguitar.ui.jfx.widget.JFXPasswordField;
import org.herac.tuxguitar.ui.jfx.widget.JFXProgressBar;
import org.herac.tuxguitar.ui.jfx.widget.JFXRadioButton;
import org.herac.tuxguitar.ui.jfx.widget.JFXReadOnlyTextBox;
import org.herac.tuxguitar.ui.jfx.widget.JFXReadOnlyTextField;
import org.herac.tuxguitar.ui.jfx.widget.JFXScale;
import org.herac.tuxguitar.ui.jfx.widget.JFXScrollBarPanel;
import org.herac.tuxguitar.ui.jfx.widget.JFXSeparator;
import org.herac.tuxguitar.ui.jfx.widget.JFXSlider;
import org.herac.tuxguitar.ui.jfx.widget.JFXSpinner;
import org.herac.tuxguitar.ui.jfx.widget.JFXSplashWindow;
import org.herac.tuxguitar.ui.jfx.widget.JFXTabFolder;
import org.herac.tuxguitar.ui.jfx.widget.JFXTable;
import org.herac.tuxguitar.ui.jfx.widget.JFXTextArea;
import org.herac.tuxguitar.ui.jfx.widget.JFXTextField;
import org.herac.tuxguitar.ui.jfx.widget.JFXToggleButton;
import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;
import org.herac.tuxguitar.ui.jfx.widget.JFXWrapLabel;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UICheckTable;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UIIndeterminateProgressBar;
import org.herac.tuxguitar.ui.widget.UIKnob;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UILinkLabel;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIPasswordField;
import org.herac.tuxguitar.ui.widget.UIProgressBar;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UISlider;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UISplashWindow;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITextArea;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;

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
		return new JFXMenuBar();
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
		return new JFXPrinterChooser((JFXWindow) parent);
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
}