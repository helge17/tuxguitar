package app.tuxguitar.app.view.dialog.about;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIReadOnlyTextBox;
import app.tuxguitar.ui.widget.UITabFolder;
import app.tuxguitar.ui.widget.UITabItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGVersion;

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
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
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

				UIPainter tgPainter = event.getPainter();
				tgPainter.drawImage(TGAboutDialog.this.image, ((IMAGE_WIDTH - width) / 2f),((IMAGE_HEIGHT - height) / 2f));
			}
		});
		headerLayout.set(this.imageComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, IMAGE_WIDTH, IMAGE_HEIGHT, null);

		final UIColor titleColor = uiFactory.createColor(0xc0, 0xc0, 0xc0);
		final UIFont titleFont = uiFactory.createFont(configManager.getFontModelConfigValue(TGConfigKeys.FONT_ABOUT_DIALOG_TITLE));
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
		tabsLayout.set(tabFolder, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, TAB_ITEM_WIDTH, TAB_ITEM_HEIGHT, null);

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
		UITabItem uiTabItem = tabFolder.createTab();
		uiTabItem.setText(TuxGuitar.getProperty(PROPERTY_PREFIX + itemName));

		UITableLayout controlLayout = new UITableLayout();
		UIPanel control = uiFactory.createPanel(uiTabItem, false);
		control.setLayout(controlLayout);

		UIReadOnlyTextBox text = uiFactory.createReadOnlyTextBox(control, true, false);
		text.setText(itemText);

		controlLayout.set(text, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}
}
