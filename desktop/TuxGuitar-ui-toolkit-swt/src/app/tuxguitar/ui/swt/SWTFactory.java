package app.tuxguitar.ui.swt;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIDirectoryChooser;
import app.tuxguitar.ui.chooser.UIFileChooser;
import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.ui.swt.chooser.SWTColorChooser;
import app.tuxguitar.ui.swt.chooser.SWTDirectoryChooser;
import app.tuxguitar.ui.swt.chooser.SWTFileChooser;
import app.tuxguitar.ui.swt.chooser.SWTFontChooser;
import app.tuxguitar.ui.swt.chooser.SWTPrinterChooser;
import app.tuxguitar.ui.swt.menu.SWTMenuBar;
import app.tuxguitar.ui.swt.menu.SWTPopupMenu;
import app.tuxguitar.ui.swt.resource.SWTResourceFactory;
import app.tuxguitar.ui.swt.toolbar.SWTToolBar;
import app.tuxguitar.ui.swt.widget.SWTBrowser;
import app.tuxguitar.ui.swt.widget.SWTButton;
import app.tuxguitar.ui.swt.widget.SWTCanvas;
import app.tuxguitar.ui.swt.widget.SWTCheckBox;
import app.tuxguitar.ui.swt.widget.SWTCheckTable;
import app.tuxguitar.ui.swt.widget.SWTContainer;
import app.tuxguitar.ui.swt.widget.SWTCustomKnob;
import app.tuxguitar.ui.swt.widget.SWTCustomScale;
import app.tuxguitar.ui.swt.widget.SWTDivider;
import app.tuxguitar.ui.swt.widget.SWTDropDownSelect;
import app.tuxguitar.ui.swt.widget.SWTDropDownSelectCCombo;
import app.tuxguitar.ui.swt.widget.SWTDropDownSelectLight;
import app.tuxguitar.ui.swt.widget.SWTImageView;
import app.tuxguitar.ui.swt.widget.SWTIndeterminateProgressBar;
import app.tuxguitar.ui.swt.widget.SWTLabel;
import app.tuxguitar.ui.swt.widget.SWTLegendPanel;
import app.tuxguitar.ui.swt.widget.SWTLinkLabel;
import app.tuxguitar.ui.swt.widget.SWTListBoxSelect;
import app.tuxguitar.ui.swt.widget.SWTPanel;
import app.tuxguitar.ui.swt.widget.SWTProgressBar;
import app.tuxguitar.ui.swt.widget.SWTRadioButton;
import app.tuxguitar.ui.swt.widget.SWTReadOnlyTextBox;
import app.tuxguitar.ui.swt.widget.SWTReadOnlyTextField;
import app.tuxguitar.ui.swt.widget.SWTScale;
import app.tuxguitar.ui.swt.widget.SWTScaleKnob;
import app.tuxguitar.ui.swt.widget.SWTScrollBarPanel;
import app.tuxguitar.ui.swt.widget.SWTSeparator;
import app.tuxguitar.ui.swt.widget.SWTSlider;
import app.tuxguitar.ui.swt.widget.SWTSpinner;
import app.tuxguitar.ui.swt.widget.SWTSplashWindow;
import app.tuxguitar.ui.swt.widget.SWTTabFolder;
import app.tuxguitar.ui.swt.widget.SWTTable;
import app.tuxguitar.ui.swt.widget.SWTTextArea;
import app.tuxguitar.ui.swt.widget.SWTTextField;
import app.tuxguitar.ui.swt.widget.SWTToggleButton;
import app.tuxguitar.ui.swt.widget.SWTWindow;
import app.tuxguitar.ui.swt.widget.SWTWrapLabel;
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

public class SWTFactory implements UIFactory {

	private Display display;
	private UIResourceFactory resourceFactory;

	public SWTFactory(Display display) {
		this.display = display;
		this.resourceFactory = new SWTResourceFactory(this.display);
	}

	public UIWindow createWindow() {
		return new SWTWindow(this.display);
	}

	public UIWindow createWindow(UIWindow parent, boolean modal, boolean resizable) {
		return new SWTWindow((SWTWindow) parent, modal, resizable);
	}

	public UISplashWindow createSplashWindow() {
		return new SWTSplashWindow(this.display);
	}

	@SuppressWarnings("unchecked")
	public UIPanel createPanel(UIContainer parent, boolean bordered) {
		return new SWTPanel((SWTContainer<Composite>) parent, bordered);
	}

	@SuppressWarnings("unchecked")
	public UIScrollBarPanel createScrollBarPanel(UIContainer parent, boolean vScroll, boolean hScroll, boolean bordered) {
		return new SWTScrollBarPanel((SWTContainer<Composite>) parent, vScroll, hScroll, bordered);
	}

	@SuppressWarnings("unchecked")
	public UILegendPanel createLegendPanel(UIContainer parent) {
		return new SWTLegendPanel((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UICanvas createCanvas(UIContainer parent, boolean bordered) {
		return new SWTCanvas((SWTContainer<Composite>) parent, bordered);
	}

	@SuppressWarnings("unchecked")
	public UILabel createLabel(UIContainer parent) {
		return new SWTLabel((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIWrapLabel createWrapLabel(UIContainer parent) {
		return new SWTWrapLabel((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UILinkLabel createLinkLabel(UIContainer parent) {
		return new SWTLinkLabel((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIImageView createImageView(UIContainer parent) {
		return new SWTImageView((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UISeparator createVerticalSeparator(UIContainer parent) {
		return new SWTSeparator((SWTContainer<Composite>) parent, SWT.VERTICAL);
	}

	@SuppressWarnings("unchecked")
	public UISeparator createHorizontalSeparator(UIContainer parent) {
		return new SWTSeparator((SWTContainer<Composite>) parent, SWT.HORIZONTAL);
	}

	@SuppressWarnings("unchecked")
	public UIButton createButton(UIContainer parent) {
		return new SWTButton((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIToggleButton createToggleButton(UIContainer parent) {
		return new SWTToggleButton((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UICheckBox createCheckBox(UIContainer parent) {
		return new SWTCheckBox((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIRadioButton createRadioButton(UIContainer parent) {
		return new SWTRadioButton((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UITextField createTextField(UIContainer parent) {
		return new SWTTextField((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIReadOnlyTextField createReadOnlyTextField(UIContainer parent) {
		return new SWTReadOnlyTextField((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UITextArea createTextArea(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new SWTTextArea((SWTContainer<Composite>) parent, vScroll, hScroll);
	}

	@SuppressWarnings("unchecked")
	public UIReadOnlyTextBox createReadOnlyTextBox(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new SWTReadOnlyTextBox((SWTContainer<Composite>) parent, vScroll, hScroll);
	}

	@SuppressWarnings("unchecked")
	public UISpinner createSpinner(UIContainer parent) {
		return new SWTSpinner((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UISlider createHorizontalSlider(UIContainer parent) {
		return new SWTSlider((SWTContainer<Composite>) parent, SWT.HORIZONTAL);
	}

	@SuppressWarnings("unchecked")
	public UISlider createVerticalSlider(UIContainer parent) {
		return new SWTSlider((SWTContainer<Composite>) parent, SWT.VERTICAL);
	}

	@SuppressWarnings("unchecked")
	public UIScale createHorizontalScale(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getHorizontalScaleAlternative();
		if( SWTCustomScale.class.getName().equals(alternative) ) {
			return new SWTCustomScale((SWTContainer<Composite>) parent, true);
		}
		return new SWTScale((SWTContainer<Composite>) parent, SWT.HORIZONTAL);
	}

	@SuppressWarnings("unchecked")
	public UIScale createVerticalScale(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getVerticalScaleAlternative();
		if( SWTCustomScale.class.getName().equals(alternative) ) {
			return new SWTCustomScale((SWTContainer<Composite>) parent, false);
		}
		return new SWTScale((SWTContainer<Composite>) parent, SWT.VERTICAL);
	}

	@SuppressWarnings("unchecked")
	public UIKnob createKnob(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getKnobAlternative();
		if( SWTCustomScale.class.getName().equals(alternative) ) {
			return new SWTScaleKnob((SWTContainer<Composite>) parent);
		}
		return new SWTCustomKnob((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIProgressBar createProgressBar(UIContainer parent) {
		return new SWTProgressBar((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIIndeterminateProgressBar createIndeterminateProgressBar(UIContainer parent) {
		return new SWTIndeterminateProgressBar((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public <T> UITable<T> createTable(UIContainer parent, boolean headerVisible) {
		return new SWTTable<T>((SWTContainer<Composite>) parent, headerVisible);
	}

	@SuppressWarnings("unchecked")
	public <T> UICheckTable<T> createCheckTable(UIContainer parent, boolean headerVisible) {
		return new SWTCheckTable<T>((SWTContainer<Composite>) parent, headerVisible);
	}

	@SuppressWarnings("unchecked")
	public <T> UIDropDownSelect<T> createDropDownSelect(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getDropDownSelectAlternative();
		if( SWTDropDownSelectLight.class.getName().equals(alternative) ) {
			return new SWTDropDownSelectLight<T>((SWTContainer<Composite>) parent);
		}
		if( SWTDropDownSelectCCombo.class.getName().equals(alternative) ) {
			return new SWTDropDownSelectCCombo<T>((SWTContainer<Composite>) parent);
		}
		return new SWTDropDownSelect<T>((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public <T> UIListBoxSelect<T> createListBoxSelect(UIContainer parent) {
		return new SWTListBoxSelect<T>((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIToolBar createHorizontalToolBar(UIContainer parent) {
		return new SWTToolBar((SWTContainer<Composite>) parent, SWT.HORIZONTAL);
	}

	@SuppressWarnings("unchecked")
	public UIToolBar createVerticalToolBar(UIContainer parent) {
		return new SWTToolBar((SWTContainer<Composite>) parent, SWT.VERTICAL);
	}

	public UIMenuBar createMenuBar(UIWindow parent) {
		return new SWTMenuBar((SWTWindow) parent);
	}

	public UIPopupMenu createPopupMenu(UIWindow parent) {
		return new SWTPopupMenu((SWTWindow) parent);
	}

	@SuppressWarnings("unchecked")
	public UITabFolder createTabFolder(UIContainer parent, boolean showClose) {
		return new SWTTabFolder((SWTContainer<Composite>) parent, showClose);
	}

	@SuppressWarnings("unchecked")
	public UIDivider createHorizontalDivider(UIContainer parent) {
		return new SWTDivider((SWTContainer<Composite>) parent);
	}

	@SuppressWarnings("unchecked")
	public UIDivider createVerticalDivider(UIContainer parent) {
		return new SWTDivider((SWTContainer<Composite>) parent);
	}

	public UIFontChooser createFontChooser(UIWindow parent) {
		return new SWTFontChooser((SWTWindow) parent);
	}

	public UIColorChooser createColorChooser(UIWindow parent) {
		return new SWTColorChooser((SWTWindow) parent);
	}

	public UIFileChooser createOpenFileChooser(UIWindow parent) {
		return new SWTFileChooser((SWTWindow) parent, SWT.OPEN);
	}

	public UIFileChooser createSaveFileChooser(UIWindow parent) {
		return new SWTFileChooser((SWTWindow) parent, SWT.SAVE);
	}

	public UIDirectoryChooser createDirectoryChooser(UIWindow parent) {
		return new SWTDirectoryChooser((SWTWindow) parent);
	}

	public UIPrinterChooser createPrinterChooser(UIWindow parent) {
		return new SWTPrinterChooser((SWTWindow) parent);
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

	@SuppressWarnings("unchecked")
	public UIBrowser createBrowser(UIWindow parent) {
		return new SWTBrowser((SWTContainer<Composite>) parent);
	}
}