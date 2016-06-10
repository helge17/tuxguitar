package org.herac.tuxguitar.app.view.dialog.about;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITabItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGVersion;

public class TGAboutDialog {
	
	private static final String RELEASE_NAME = (TGApplication.NAME + " " + TGVersion.CURRENT.getVersion());
	private static final String PROPERTY_PREFIX = ("help.about.");
	
	private static final float IMAGE_WIDTH = 100;
	private static final float IMAGE_HEIGHT = 100;
	
	private static final float TAB_ITEM_WIDTH = 660;
	private static final float TAB_ITEM_HEIGHT = 300;
	
	protected UICanvas imageComposite;
	protected UIImage image;
	
	public TGAboutDialog() {
		super();
	}
	
	public void show(final TGViewContext context){
		final TGConfigManager configManager = TGConfigManager.getInstance(context.getContext());
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("help.about"));
		
		//--------------------HEADER----------------------------------
		UITableLayout headerLayout = new UITableLayout();
		UIPanel header = uiFactory.createPanel(dialog, false);
		header.setLayout(headerLayout);
		dialogLayout.set(header, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.image = TuxGuitar.getInstance().getIconManager().getAboutDescription();
		
		this.imageComposite = uiFactory.createCanvas(header, false);
		this.imageComposite.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				float width = TGAboutDialog.this.image.getWidth();
				float height = TGAboutDialog.this.image.getHeight();
				
				TGPainter tgPainter = new TGPainterImpl(uiFactory, event.getPainter());
				tgPainter.drawImage(new TGImageImpl(uiFactory, TGAboutDialog.this.image), ((IMAGE_WIDTH - width) / 2f),((IMAGE_HEIGHT - height) / 2f));
			}
		});
		headerLayout.set(this.imageComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, IMAGE_WIDTH, IMAGE_HEIGHT, null);
		
		final UIColor titleColor = uiFactory.createColor(0xc0, 0xc0, 0xc0);
		final UIFont titleFont = uiFactory.createFont(configManager.getUIFontModelConfigValue(TGConfigKeys.FONT_ABOUT_DIALOG_TITLE));
		UILabel title = uiFactory.createLabel(header);
		title.setFont(titleFont);
		title.setFgColor(titleColor);
		title.setText(RELEASE_NAME);
		title.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				titleFont.dispose();
				titleColor.dispose();
			}
		});
		headerLayout.set(title, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//-------------------TABS-----------------------
		UITableLayout tabsLayout = new UITableLayout();
		UIPanel tabs = uiFactory.createPanel(dialog, false);
		tabs.setLayout(tabsLayout);
		dialogLayout.set(tabs, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UITabFolder tabFolder = uiFactory.createTabFolder(tabs, false);
		tabsLayout.set(tabFolder, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		tabsLayout.set(tabFolder, UITableLayout.PACKED_WIDTH, TAB_ITEM_WIDTH);
		tabsLayout.set(tabFolder, UITableLayout.PACKED_HEIGHT, TAB_ITEM_HEIGHT);
		
		TGAboutContentReader docReader = new TGAboutContentReader(context.getContext());
		
		createTabItem(uiFactory, tabFolder, TGAboutContentReader.DESCRIPTION, docReader.read(TGAboutContentReader.DESCRIPTION).toString());
		createTabItem(uiFactory, tabFolder, TGAboutContentReader.AUTHORS, docReader.read(TGAboutContentReader.AUTHORS).toString());
		createTabItem(uiFactory, tabFolder, TGAboutContentReader.LICENSE, docReader.read(TGAboutContentReader.LICENSE).toString());
		
		tabFolder.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int selectedIndex = tabFolder.getSelectedIndex();
				if( selectedIndex == 0 ){
					TGAboutDialog.this.image = TuxGuitar.getInstance().getIconManager().getAboutDescription();
				}else if( selectedIndex == 1 ){
					TGAboutDialog.this.image = TuxGuitar.getInstance().getIconManager().getAboutAuthors();
				}else if( selectedIndex == 2 ){
					TGAboutDialog.this.image = TuxGuitar.getInstance().getIconManager().getAboutLicense();
				}
				TGAboutDialog.this.imageComposite.redraw();
			}
		});
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonClose = uiFactory.createButton(buttons);
		buttonClose.setDefaultButton();
		buttonClose.setText(TuxGuitar.getProperty("close"));
		buttonClose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonClose, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		tabFolder.setSelectedIndex(0);
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void createTabItem(UIFactory uiFactory, UITabFolder tabFolder, String itemName, String itemText){
		final UIColor white = uiFactory.createColor(0xff, 0xff, 0xff);
		
		UITabItem uiTabItem = tabFolder.createTab();
		uiTabItem.setText(TuxGuitar.getProperty(PROPERTY_PREFIX + itemName));
		
		UITableLayout controlLayout = new UITableLayout();
		UIPanel control = uiFactory.createPanel(uiTabItem, false);
		control.setLayout(controlLayout);
		
		UIReadOnlyTextBox text = uiFactory.createReadOnlyTextBox(control, true, false);
		text.setBgColor(white);
		text.setText(itemText);
		text.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				white.dispose();
			}
		});
		
		controlLayout.set(text, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}
}
