package app.tuxguitar.ui.qt;

import java.io.InputStream;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIDirectoryChooser;
import app.tuxguitar.ui.chooser.UIFileChooser;
import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.qt.chooser.QTColorChooser;
import app.tuxguitar.ui.qt.chooser.QTDirectoryChooser;
import app.tuxguitar.ui.qt.chooser.QTFileChooser;
import app.tuxguitar.ui.qt.chooser.QTFontChooser;
import app.tuxguitar.ui.qt.chooser.QTPrinterChooser;
import app.tuxguitar.ui.qt.menu.QTMenuBar;
import app.tuxguitar.ui.qt.menu.QTPopupMenu;
import app.tuxguitar.ui.qt.resource.QTResourceFactory;
import app.tuxguitar.ui.qt.toolbar.QTToolBar;
import app.tuxguitar.ui.qt.widget.QTAbstractWindow;
import app.tuxguitar.ui.qt.widget.QTButton;
import app.tuxguitar.ui.qt.widget.QTCanvas;
import app.tuxguitar.ui.qt.widget.QTCheckBox;
import app.tuxguitar.ui.qt.widget.QTContainer;
import app.tuxguitar.ui.qt.widget.QTDialog;
import app.tuxguitar.ui.qt.widget.QTDivider;
import app.tuxguitar.ui.qt.widget.QTDropDownSelect;
import app.tuxguitar.ui.qt.widget.QTImageView;
import app.tuxguitar.ui.qt.widget.QTIndeterminateProgressBar;
import app.tuxguitar.ui.qt.widget.QTKnob;
import app.tuxguitar.ui.qt.widget.QTLabel;
import app.tuxguitar.ui.qt.widget.QTLegendPanel;
import app.tuxguitar.ui.qt.widget.QTLinkLabel;
import app.tuxguitar.ui.qt.widget.QTListBoxSelect;
import app.tuxguitar.ui.qt.widget.QTPanel;
import app.tuxguitar.ui.qt.widget.QTPasswordField;
import app.tuxguitar.ui.qt.widget.QTProgressBar;
import app.tuxguitar.ui.qt.widget.QTRadioButton;
import app.tuxguitar.ui.qt.widget.QTReadOnlyTextBox;
import app.tuxguitar.ui.qt.widget.QTReadOnlyTextField;
import app.tuxguitar.ui.qt.widget.QTScale;
import app.tuxguitar.ui.qt.widget.QTScrollBarPanel;
import app.tuxguitar.ui.qt.widget.QTSeparator;
import app.tuxguitar.ui.qt.widget.QTSlider;
import app.tuxguitar.ui.qt.widget.QTSpinner;
import app.tuxguitar.ui.qt.widget.QTSplashWindow;
import app.tuxguitar.ui.qt.widget.QTTabFolder;
import app.tuxguitar.ui.qt.widget.QTTable;
import app.tuxguitar.ui.qt.widget.QTTextArea;
import app.tuxguitar.ui.qt.widget.QTTextField;
import app.tuxguitar.ui.qt.widget.QTToggleButton;
import app.tuxguitar.ui.qt.widget.QTWindow;
import app.tuxguitar.ui.qt.widget.QTWrapLabel;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.ui.toolbar.UIToolBar;
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
import org.qtjambi.qt.core.Qt.Orientation;

public class QTFactory implements UIFactory {

	private UIResourceFactory resourceFactory;

	public QTFactory() {
		this.resourceFactory = new QTResourceFactory();
	}

	public UISplashWindow createSplashWindow() {
		return new QTSplashWindow();
	}

	public UIWindow createWindow() {
		return new QTWindow();
	}

	public UIWindow createWindow(UIWindow parent, boolean modal, boolean resizable) {
		return new QTDialog((QTAbstractWindow<?>) parent, modal, resizable);
	}

	public UIPanel createPanel(UIContainer parent, boolean bordered) {
		return new QTPanel((QTContainer) parent, bordered);
	}

	public UIScrollBarPanel createScrollBarPanel(UIContainer parent, boolean vScroll, boolean hScroll, boolean bordered) {
		return new QTScrollBarPanel((QTContainer) parent, vScroll, hScroll, bordered);
	}

	public UILegendPanel createLegendPanel(UIContainer parent) {
		return new QTLegendPanel((QTContainer) parent);
	}

	public UICanvas createCanvas(UIContainer parent, boolean bordered) {
		return new QTCanvas((QTContainer) parent, bordered);
	}

	public UILabel createLabel(UIContainer parent) {
		return new QTLabel((QTContainer) parent);
	}

	public UIWrapLabel createWrapLabel(UIContainer parent) {
		return new QTWrapLabel((QTContainer) parent);
	}

	public UILinkLabel createLinkLabel(UIContainer parent) {
		return new QTLinkLabel((QTContainer) parent);
	}

	public UIImageView createImageView(UIContainer parent) {
		return new QTImageView((QTContainer) parent);
	}

	public UISeparator createVerticalSeparator(UIContainer parent) {
		return new QTSeparator((QTContainer) parent, Orientation.Vertical);
	}

	public UISeparator createHorizontalSeparator(UIContainer parent) {
		return new QTSeparator((QTContainer) parent, Orientation.Horizontal);
	}

	public UIButton createButton(UIContainer parent) {
		return new QTButton((QTContainer) parent);
	}

	public UIToggleButton createToggleButton(UIContainer parent) {
		return new QTToggleButton((QTContainer) parent);
	}

	public UICheckBox createCheckBox(UIContainer parent) {
		return new QTCheckBox((QTContainer) parent);
	}

	public UIRadioButton createRadioButton(UIContainer parent) {
		return new QTRadioButton((QTContainer) parent);
	}

	public UITextField createTextField(UIContainer parent) {
		return new QTTextField((QTContainer) parent);
	}

	public UIPasswordField createPasswordField(UIContainer parent) {
		return new QTPasswordField((QTContainer) parent);
	}

	public UIReadOnlyTextField createReadOnlyTextField(UIContainer parent) {
		return new QTReadOnlyTextField((QTContainer) parent);
	}

	public UITextArea createTextArea(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new QTTextArea((QTContainer) parent, vScroll, hScroll);
	}

	public UIReadOnlyTextBox createReadOnlyTextBox(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new QTReadOnlyTextBox((QTContainer) parent, vScroll, hScroll);
	}

	public UISpinner createSpinner(UIContainer parent) {
		return new QTSpinner((QTContainer) parent);
	}

	public UISlider createHorizontalSlider(UIContainer parent) {
		return new QTSlider((QTContainer) parent, Orientation.Horizontal);
	}

	public UISlider createVerticalSlider(UIContainer parent) {
		return new QTSlider((QTContainer) parent, Orientation.Vertical);
	}

	public UIScale createHorizontalScale(UIContainer parent) {
		return new QTScale((QTContainer) parent, Orientation.Horizontal);
	}

	public UIScale createVerticalScale(UIContainer parent) {
		return new QTScale((QTContainer) parent, Orientation.Vertical);
	}

	public UIKnob createKnob(UIContainer parent) {
		return new QTKnob((QTContainer) parent);
	}

	public UIProgressBar createProgressBar(UIContainer parent) {
		return new QTProgressBar((QTContainer) parent);
	}

	public UIIndeterminateProgressBar createIndeterminateProgressBar(UIContainer parent) {
		return new QTIndeterminateProgressBar((QTContainer) parent);
	}

	public <T> UITable<T> createTable(UIContainer parent, boolean headerVisible) {
		return new QTTable<T>((QTContainer) parent, headerVisible, false);
	}

	public <T> UICheckTable<T> createCheckTable(UIContainer parent, boolean headerVisible) {
		return new QTTable<T>((QTContainer) parent, headerVisible, true);
	}

	public <T> UIDropDownSelect<T> createDropDownSelect(UIContainer parent) {
		return new QTDropDownSelect<T>((QTContainer) parent);
	}

	public <T> UIListBoxSelect<T> createListBoxSelect(UIContainer parent) {
		return new QTListBoxSelect<T>((QTContainer) parent);
	}

	public UIToolBar createHorizontalToolBar(UIContainer parent) {
		return new QTToolBar((QTContainer) parent, Orientation.Horizontal);
	}

	public UIToolBar createVerticalToolBar(UIContainer parent) {
		return new QTToolBar((QTContainer) parent, Orientation.Vertical);
	}

	public UIMenuBar createMenuBar(UIWindow parent) {
		return new QTMenuBar((QTAbstractWindow<?>) parent);
	}

	public UIPopupMenu createPopupMenu(UIWindow parent) {
		return new QTPopupMenu();
	}

	public UITabFolder createTabFolder(UIContainer parent, boolean showClose) {
		return new QTTabFolder((QTContainer) parent, showClose);
	}

	public UIDivider createHorizontalDivider(UIContainer parent) {
		return new QTDivider((QTContainer) parent);
	}

	public UIDivider createVerticalDivider(UIContainer parent) {
		return new QTDivider((QTContainer) parent);
	}

	public UIFontChooser createFontChooser(UIWindow parent) {
		return new QTFontChooser((QTAbstractWindow<?>) parent);
	}

	public UIColorChooser createColorChooser(UIWindow parent) {
		return new QTColorChooser((QTAbstractWindow<?>) parent);
	}

	public UIFileChooser createOpenFileChooser(UIWindow parent) {
		return new QTFileChooser((QTAbstractWindow<?>) parent, QTFileChooser.STYLE_OPEN);
	}

	public UIFileChooser createSaveFileChooser(UIWindow parent) {
		return new QTFileChooser((QTAbstractWindow<?>) parent, QTFileChooser.STYLE_SAVE);
	}

	public UIDirectoryChooser createDirectoryChooser(UIWindow parent) {
		return new QTDirectoryChooser((QTAbstractWindow<?>) parent);
	}

	public UIPrinterChooser createPrinterChooser(UIWindow parent) {
		return new QTPrinterChooser((QTAbstractWindow<?>) parent);
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