package org.herac.tuxguitar.ui;

import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
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
	
	UIPasswordField createPasswordField(UIContainer parent);
	
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
}
