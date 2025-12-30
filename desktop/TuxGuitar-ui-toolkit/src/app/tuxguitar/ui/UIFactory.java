package app.tuxguitar.ui;

import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIDirectoryChooser;
import app.tuxguitar.ui.chooser.UIFileChooser;
import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIBrowser;
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

public interface UIFactory extends UIResourceFactory {

	UIWindow createWindow();

	UIWindow createWindow(UIWindow parent, boolean modal, boolean resizable);

	UISplashWindow createSplashWindow();

	UIMenuBar createMenuBar(UIWindow parent);

	UIPopupMenu createPopupMenu(UIWindow parent);

	UIToolBar createHorizontalToolBar(UIContainer parent);

	UIToolBar createVerticalToolBar(UIContainer parent);

	UIPanel createPanel(UIContainer parent, boolean bordered);

	UIScrollBarPanel createScrollBarPanel(UIContainer parent, boolean vScroll, boolean hScroll, boolean bordered);

	UILegendPanel createLegendPanel(UIContainer parent);

	UICanvas createCanvas(UIContainer parent, boolean bordered);

	UILabel createLabel(UIContainer parent);

	UIWrapLabel createWrapLabel(UIContainer parent);

	UILinkLabel createLinkLabel(UIContainer parent);

	UIImageView createImageView(UIContainer parent);

	UISeparator createVerticalSeparator(UIContainer parent);

	UISeparator createHorizontalSeparator(UIContainer parent);

	UIButton createButton(UIContainer parent);

	UIToggleButton createToggleButton(UIContainer parent);

	UICheckBox createCheckBox(UIContainer parent);

	UIRadioButton createRadioButton(UIContainer parent);

	UITextField createTextField(UIContainer parent);

	UIReadOnlyTextField createReadOnlyTextField(UIContainer parent);

	UITextArea createTextArea(UIContainer parent, boolean vScroll, boolean hScroll);

	UIReadOnlyTextBox createReadOnlyTextBox(UIContainer parent, boolean vScroll, boolean hScroll);

	UISpinner createSpinner(UIContainer parent);

	UISlider createHorizontalSlider(UIContainer parent);

	UISlider createVerticalSlider(UIContainer parent);

	UIScale createHorizontalScale(UIContainer parent);

	UIScale createVerticalScale(UIContainer parent);

	UIKnob createKnob(UIContainer parent);

	UIProgressBar createProgressBar(UIContainer parent);

	UIIndeterminateProgressBar createIndeterminateProgressBar(UIContainer parent);

	UITabFolder createTabFolder(UIContainer parent, boolean showClose);

	UIDivider createHorizontalDivider(UIContainer parent);

	UIDivider createVerticalDivider(UIContainer parent);

	<T> UITable<T> createTable(UIContainer parent, boolean headerVisible);

	<T> UICheckTable<T> createCheckTable(UIContainer parent, boolean headerVisible);

	<T> UIDropDownSelect<T> createDropDownSelect(UIContainer parent);

	<T> UIListBoxSelect<T> createListBoxSelect(UIContainer parent);

	UIFontChooser createFontChooser(UIWindow parent);

	UIColorChooser createColorChooser(UIWindow parent);

	UIFileChooser createOpenFileChooser(UIWindow parent);

	UIFileChooser createSaveFileChooser(UIWindow parent);

	UIDirectoryChooser createDirectoryChooser(UIWindow parent);

	UIPrinterChooser createPrinterChooser(UIWindow parent);

	UIBrowser createBrowser(UIWindow parent);
}
