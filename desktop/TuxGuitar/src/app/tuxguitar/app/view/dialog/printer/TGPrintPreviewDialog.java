package app.tuxguitar.app.view.dialog.printer;

import java.util.Arrays;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.ui.appearance.UIColorAppearance;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIKeyEvent;
import app.tuxguitar.ui.event.UIKeyReleasedListener;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIKey;
import app.tuxguitar.ui.resource.UIKeyCombination;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIScrollBar;
import app.tuxguitar.ui.widget.UIScrollBarPanel;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;

public class TGPrintPreviewDialog{

	private static final int SCROLL_INCREMENT = 50;

	private static final String COLOR_BACKGROUND = "widget.printPreview.backgroundColor";

	public static final String ATTRIBUTE_PAGES = (TGPrintPreviewDialog.class.getName() + "-pages");
	public static final String ATTRIBUTE_SIZE = (TGPrintPreviewDialog.class.getName() + "-size");

	public static final UIKeyCombination ENTER_KEY_CONVINATION = new UIKeyCombination(Arrays.asList(UIKey.ENTER));

	private TGViewContext context;
	private UIWindow dialog;
	private UIScrollBarPanel previewComposite;
	private UICanvas pageComposite;
	private UITextField currentText;
	private UIButton previous;
	private UIButton next;
	private UISize size;
	private List<UIImage> pages;
	private int currentPage;

	public TGPrintPreviewDialog(TGViewContext context) {
		this.context = context;
		this.pages = context.getAttribute(ATTRIBUTE_PAGES);
		this.size = context.getAttribute(ATTRIBUTE_SIZE);
	}

	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(uiParent, true, true);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("print.preview"));

		this.initToolBar();
		this.initPreviewComposite();
		this.changePage(0);

		UIDisposeListener disposeListener = this.context.getAttribute(TGViewContext.ATTRIBUTE_DISPOSE_LISTENER);
		if( disposeListener != null ) {
			this.dialog.addDisposeListener(disposeListener);
		}

		this.dialog.setBounds(new UIRectangle(new UISize(this.size.getWidth() + 80f, 600f)));
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_LAYOUT);
	}

	private void initToolBar(){
		UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();

		UITableLayout compositeLayout = new UITableLayout(0f);
		UIPanel composite = factory.createPanel(this.dialog,false);
		composite.setLayout(compositeLayout);
		dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);

		this.previous = factory.createButton(composite);
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_LEFT));
		compositeLayout.set(this.previous, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		this.currentText = factory.createTextField(composite);
		compositeLayout.set(this.currentText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		compositeLayout.set(this.currentText, UITableLayout.PACKED_WIDTH, 25f);

		this.next = factory.createButton(composite);
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_RIGHT));
		compositeLayout.set(this.next, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		UILabel maxPages = factory.createLabel(composite);
		compositeLayout.set(maxPages, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		UIButton close = factory.createButton(composite);
		compositeLayout.set(close, 1, 5, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 80f, 25f, null);

		this.currentText.addKeyReleasedListener(new UIKeyReleasedListener() {
			public void onKeyReleased(UIKeyEvent event) {
				if( event.getKeyCombination().equals(ENTER_KEY_CONVINATION)){
					try{
						Integer number = Integer.valueOf(TGPrintPreviewDialog.this.currentText.getText());
						changePage(number.intValue() - 1);
					}catch(NumberFormatException exception){
						changePage(TGPrintPreviewDialog.this.currentPage);
					}
				}
			}
		});
		this.previous.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(TGPrintPreviewDialog.this.currentPage >= 0){
					changePage(TGPrintPreviewDialog.this.currentPage - 1);
				}
			}
		});
		this.next.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(TGPrintPreviewDialog.this.currentPage >= 0){
					changePage(TGPrintPreviewDialog.this.currentPage + 1);
				}
			}
		});
		close.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGPrintPreviewDialog.this.dialog.dispose();
			}
		});
		maxPages.setText(TuxGuitar.getProperty("print.preview.page-of") + " " + this.pages.size());
		close.setText(TuxGuitar.getProperty("close"));
	}

	private void initPreviewComposite() {
		final UIAppearance appearance = this.getUIAppearance();
		final UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		UITableLayout previewLayout = new UITableLayout();

		TGColorManager colorManager = TGColorManager.getInstance(this.context.getContext());
		colorManager.appendSkinnableColors(new TGSkinnableColor[] {
			new TGSkinnableColor(COLOR_BACKGROUND, appearance.getColorModel(UIColorAppearance.WidgetBackground))
		});

		this.previewComposite = factory.createScrollBarPanel(this.dialog, true, false, true);
		this.previewComposite.setLayout(previewLayout);
		this.previewComposite.setBgColor(colorManager.getColor(COLOR_BACKGROUND));
		this.previewComposite.setFocus();
		dialogLayout.set(this.previewComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		dialogLayout.set(this.previewComposite, UITableLayout.PACKED_WIDTH, 0f);
		dialogLayout.set(this.previewComposite, UITableLayout.PACKED_HEIGHT, 0f);

		this.pageComposite = factory.createCanvas(this.previewComposite, true);
		this.pageComposite.setBgColor(colorManager.getColor(TGColorManager.COLOR_WHITE));
		this.pageComposite.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				if(TGPrintPreviewDialog.this.currentPage >= 0){
					updateScroll();

					int vScroll = TGPrintPreviewDialog.this.previewComposite.getVScroll().getValue();

					UIImage page = TGPrintPreviewDialog.this.pages.get(TGPrintPreviewDialog.this.currentPage);
					UIPainter painter = event.getPainter();
					painter.drawImage(page, 0, -vScroll);
				}
			}
		});

		previewLayout.set(this.pageComposite, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, true, true);
		previewLayout.set(this.pageComposite, UITableLayout.PACKED_WIDTH, this.size.getWidth());
		previewLayout.set(this.pageComposite, UITableLayout.PACKED_HEIGHT, this.size.getHeight());

		this.previewComposite.getVScroll().setIncrement(SCROLL_INCREMENT);
		this.previewComposite.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGPrintPreviewDialog.this.pageComposite.redraw();
			}
		});
	}

	protected void updateScroll(){
		UIScrollBar vBar = this.previewComposite.getVScroll();
		UIRectangle client = this.previewComposite.getChildArea();

		vBar.setMaximum(Math.max(Math.round(this.size.getHeight() - client.getHeight()), 0));
		vBar.setThumb(Math.round(client.getHeight()));
	}

	protected void changePage(int index){
		if(!this.pages.isEmpty()){
			int pageCount = this.pages.size();
			if(index >= 0 && index < pageCount){
				this.currentPage = index;
				this.currentText.setText(Integer.toString(index + 1));
				this.pageComposite.redraw();
			}else if(this.currentPage >= 0 && this.currentPage < pageCount){
				this.currentText.setText(Integer.toString(this.currentPage + 1 ));
			}
			this.previous.setEnabled(this.currentPage > 0);
			this.next.setEnabled((this.currentPage + 1) < pageCount);
			this.previewComposite.getVScroll().setValue(0);
			this.previewComposite.setFocus();
		}else{
			this.currentText.setEnabled(false);
			this.previous.setEnabled(false);
			this.next.setEnabled(false);
		}

	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}

	public UIAppearance getUIAppearance() {
		return TGApplication.getInstance(this.context.getContext()).getAppearance();
	}

	public TGViewContext getContext() {
		return context;
	}
}
