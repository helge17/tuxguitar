package org.herac.tuxguitar.gui.printer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingConstants;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class PrintPreview{
	private static final int SCROLL_INCREMENT = 50;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_BOTTOM = 40;
	private static final int MARGIN_LEFT = 50;
	private static final int MARGIN_RIGHT = 20;
	
	protected Shell dialog;
	protected Composite previewComposite;
	protected Composite pageComposite;
	protected Text currentText;
	protected Button previous;
	protected Button next;
	protected Rectangle bounds;
	protected List pages;
	protected int currentPage;
	
	public PrintPreview(List pages,Rectangle bounds){
		this.pages = pages;
		this.bounds = bounds;
	}
	
	public void showPreview(Shell parent){
		this.dialog = DialogUtils.newDialog(parent,SWT.SHELL_TRIM | SWT.APPLICATION_MODAL );
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("print.preview"));
		
		this.initToolBar();
		this.initPreviewComposite();
		this.changePage(0);
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_MAXIMIZED | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	private void initToolBar(){
		Composite composite = new Composite(this.dialog,SWT.NONE);
		composite.setLayout(new GridLayout(5,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		
		this.previous = new Button(composite,SWT.ARROW | SWT.LEFT);
		this.currentText = new Text(composite,SWT.BORDER);
		this.currentText.setLayoutData(new GridData(25,SWT.DEFAULT));
		this.next = new Button(composite,SWT.ARROW | SWT.RIGHT);
		Label maxPages = new Label(composite,SWT.NONE);
		
		Button close = new Button(composite,SWT.PUSH);
		close.setLayoutData(getButtonData());
		
		this.currentText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == KeyBindingConstants.ENTER){
					try{
						Integer number = new Integer(PrintPreview.this.currentText.getText());
						changePage(number.intValue() - 1);
					}catch(NumberFormatException exception){
						changePage(PrintPreview.this.currentPage);
					}
				}
			}
		});
		this.previous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(PrintPreview.this.currentPage >= 0){
					changePage(PrintPreview.this.currentPage - 1);
				}
			}
		});
		this.next.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(PrintPreview.this.currentPage >= 0){
					changePage(PrintPreview.this.currentPage + 1);
				}
			}
		});
		close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PrintPreview.this.dialog.dispose();
			}
		});
		maxPages.setText(TuxGuitar.getProperty("print.preview.page-of") + " " + this.pages.size());
		close.setText(TuxGuitar.getProperty("close"));
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.RIGHT, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private void initPreviewComposite(){
		this.previewComposite = new Composite(this.dialog,SWT.BORDER | SWT.V_SCROLL);
		this.previewComposite.setLayout(new GridLayout());
		this.previewComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.previewComposite.setBackground(this.previewComposite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		this.previewComposite.setFocus();
		this.pageComposite = new Composite(this.previewComposite,SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.pageComposite.setLayout(new GridLayout());
		this.pageComposite.setBackground(this.previewComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.pageComposite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(PrintPreview.this.currentPage >= 0){
					updateScroll();
					
					int vScroll = PrintPreview.this.previewComposite.getVerticalBar().getSelection();
					
					TGPainter painter = new TGPainter(e.gc);
					painter.drawImage((Image)PrintPreview.this.pages.get(PrintPreview.this.currentPage),MARGIN_LEFT,MARGIN_TOP - vScroll);
				}
			}
		});
		GridData pageData = new GridData();
		pageData.horizontalAlignment = SWT.CENTER;
		pageData.verticalAlignment = SWT.CENTER;
		pageData.grabExcessHorizontalSpace = true;
		pageData.grabExcessVerticalSpace = true;
		pageData.widthHint = (this.bounds.width - this.bounds.x) + (MARGIN_LEFT + MARGIN_RIGHT);
		pageData.heightHint = (this.bounds.height - this.bounds.y) + (MARGIN_TOP + MARGIN_BOTTOM);
		this.pageComposite.setLayoutData(pageData);
		this.previewComposite.getVerticalBar().setIncrement(SCROLL_INCREMENT);
		this.previewComposite.getVerticalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				PrintPreview.this.pageComposite.redraw();
			}
		});
	}
	
	protected void updateScroll(){
		ScrollBar vBar = this.previewComposite.getVerticalBar();
		Rectangle client = this.pageComposite.getClientArea();
		vBar.setMaximum((this.bounds.height - this.bounds.y) + (MARGIN_TOP + MARGIN_BOTTOM));
		vBar.setThumb(Math.min((this.bounds.height - this.bounds.y) + (MARGIN_TOP + MARGIN_BOTTOM), client.height));
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
			this.previewComposite.getVerticalBar().setSelection(0);
			this.previewComposite.setFocus();
		}else{
			this.currentText.setEnabled(false);
			this.previous.setEnabled(false);
			this.next.setEnabled(false);
		}
		
	}
	
}
