package org.herac.tuxguitar.app.view.dialog.printer;

import java.util.Arrays;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.color.TGColorManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListener;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKey;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGPrintPreviewDialog{
	
	private static final int SCROLL_INCREMENT = 50;
	
	public static final String ATTRIBUTE_PAGES = (TGPrintPreviewDialog.class.getName() + "-pages");
	public static final String ATTRIBUTE_SIZE = (TGPrintPreviewDialog.class.getName() + "-size");
	
	public static final UIKeyConvination ENTER_KEY_CONVINATION = new UIKeyConvination(Arrays.asList(UIKey.ENTER));
	
	private TGViewContext context;
	private UIWindow dialog;
	private UIScrollBarPanel previewComposite;
	private UICanvas pageComposite;
	private UITextField currentText;
	private UIButton previous;
	private UIButton next;
	private TGDimension size;
	private List<UIImage> pages;
	private int currentPage;
	
	public TGPrintPreviewDialog(TGViewContext context) {
		this.context = context;
		this.pages = context.getAttribute(ATTRIBUTE_PAGES);
		this.size = context.getAttribute(ATTRIBUTE_SIZE);
	}
	
	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
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
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_MAXIMIZED);
	}
	
	private void initToolBar(){
		UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout compositeLayout = new UITableLayout(0f);
		UIPanel composite = factory.createPanel(this.dialog,false);
		composite.setLayout(compositeLayout);
		dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		
		this.previous = factory.createButton(composite);
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getArrowLeft());
		compositeLayout.set(this.previous, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		this.currentText = factory.createTextField(composite);
		compositeLayout.set(this.currentText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		compositeLayout.set(this.currentText, UITableLayout.PACKED_WIDTH, 25f);
		
		this.next = factory.createButton(composite);
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getArrowRight());
		compositeLayout.set(this.next, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		UILabel maxPages = factory.createLabel(composite);
		compositeLayout.set(maxPages, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		UIButton close = factory.createButton(composite);
		compositeLayout.set(close, 1, 5, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 80f, 25f, null);
		
		this.currentText.addKeyReleasedListener(new UIKeyReleasedListener() {
			public void onKeyReleased(UIKeyEvent event) {
				if( event.getKeyConvination().equals(ENTER_KEY_CONVINATION)){
					try{
						Integer number = new Integer(TGPrintPreviewDialog.this.currentText.getText());
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
		final UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		UITableLayout previewLayout = new UITableLayout();
		
		this.previewComposite = factory.createScrollBarPanel(this.dialog, true, false, true);
		this.previewComposite.setLayout(previewLayout);
		this.previewComposite.setBgColor(TGColorManager.getInstance(this.context.getContext()).getColor(TGColorManager.COLOR_GRAY));
		this.previewComposite.setFocus();
		dialogLayout.set(this.previewComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		dialogLayout.set(this.previewComposite, UITableLayout.PACKED_WIDTH, 0f);
		dialogLayout.set(this.previewComposite, UITableLayout.PACKED_HEIGHT, 0f);
		
		this.pageComposite = factory.createCanvas(this.previewComposite, true);
		this.pageComposite.setBgColor(TGColorManager.getInstance(this.context.getContext()).getColor(TGColorManager.COLOR_WHITE));
		this.pageComposite.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				if(TGPrintPreviewDialog.this.currentPage >= 0){
					updateScroll();
					
					int vScroll = TGPrintPreviewDialog.this.previewComposite.getVScroll().getValue();
					
					TGImage page = new TGImageImpl(factory, TGPrintPreviewDialog.this.pages.get(TGPrintPreviewDialog.this.currentPage));
					TGPainter painter = new TGPainterImpl(factory, event.getPainter());
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
	
	public TGViewContext getContext() {
		return context;
	}
}
