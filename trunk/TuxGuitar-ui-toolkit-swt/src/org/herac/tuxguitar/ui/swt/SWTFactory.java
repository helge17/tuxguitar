package org.herac.tuxguitar.ui.swt;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.ui.swt.chooser.SWTColorChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTDirectoryChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTFileChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTFontChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTPrinterChooser;
import org.herac.tuxguitar.ui.swt.menu.SWTMenuBar;
import org.herac.tuxguitar.ui.swt.menu.SWTPopupMenu;
import org.herac.tuxguitar.ui.swt.resource.SWTResourceFactory;
import org.herac.tuxguitar.ui.swt.toolbar.SWTToolBar;
import org.herac.tuxguitar.ui.swt.widget.SWTButton;
import org.herac.tuxguitar.ui.swt.widget.SWTCanvas;
import org.herac.tuxguitar.ui.swt.widget.SWTCheckBox;
import org.herac.tuxguitar.ui.swt.widget.SWTCheckTable;
import org.herac.tuxguitar.ui.swt.widget.SWTContainer;
import org.herac.tuxguitar.ui.swt.widget.SWTDivider;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelectLight;
import org.herac.tuxguitar.ui.swt.widget.SWTImageView;
import org.herac.tuxguitar.ui.swt.widget.SWTIndeterminateProgressBar;
import org.herac.tuxguitar.ui.swt.widget.SWTKnob;
import org.herac.tuxguitar.ui.swt.widget.SWTLabel;
import org.herac.tuxguitar.ui.swt.widget.SWTLegendPanel;
import org.herac.tuxguitar.ui.swt.widget.SWTLinkLabel;
import org.herac.tuxguitar.ui.swt.widget.SWTListBoxSelect;
import org.herac.tuxguitar.ui.swt.widget.SWTPanel;
import org.herac.tuxguitar.ui.swt.widget.SWTPasswordField;
import org.herac.tuxguitar.ui.swt.widget.SWTProgressBar;
import org.herac.tuxguitar.ui.swt.widget.SWTRadioButton;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelect;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelectCCombo;
import org.herac.tuxguitar.ui.swt.widget.SWTReadOnlyTextBox;
import org.herac.tuxguitar.ui.swt.widget.SWTReadOnlyTextField;
import org.herac.tuxguitar.ui.swt.widget.SWTScale;
import org.herac.tuxguitar.ui.swt.widget.SWTScrollBarPanel;
import org.herac.tuxguitar.ui.swt.widget.SWTSeparator;
import org.herac.tuxguitar.ui.swt.widget.SWTSlider;
import org.herac.tuxguitar.ui.swt.widget.SWTSpinner;
import org.herac.tuxguitar.ui.swt.widget.SWTSplashWindow;
import org.herac.tuxguitar.ui.swt.widget.SWTTabFolder;
import org.herac.tuxguitar.ui.swt.widget.SWTTable;
import org.herac.tuxguitar.ui.swt.widget.SWTTextArea;
import org.herac.tuxguitar.ui.swt.widget.SWTTextField;
import org.herac.tuxguitar.ui.swt.widget.SWTToggleButton;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.ui.swt.widget.SWTWrapLabel;
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
	public UIPasswordField createPasswordField(UIContainer parent) {
		return new SWTPasswordField((SWTContainer<Composite>) parent);
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
		return new SWTScale((SWTContainer<Composite>) parent, SWT.HORIZONTAL);
	}
	
	@SuppressWarnings("unchecked")
	public UIScale createVerticalScale(UIContainer parent) {
		return new SWTScale((SWTContainer<Composite>) parent, SWT.VERTICAL);
	}
	
	@SuppressWarnings("unchecked")
	public UIKnob createKnob(UIContainer parent) {
		return new SWTKnob((SWTContainer<Composite>) parent);
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
}