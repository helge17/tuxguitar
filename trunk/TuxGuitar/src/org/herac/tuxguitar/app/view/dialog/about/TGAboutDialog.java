package org.herac.tuxguitar.app.view.dialog.about;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.util.TGVersion;

public class TGAboutDialog {
	
	private static final String RELEASE_NAME = (TuxGuitar.APPLICATION_NAME + " " + TGVersion.CURRENT.getVersion());
	private static final String PROPERTY_PREFIX = ("help.about.");
	
	private static final int IMAGE_WIDTH = 100;
	private static final int IMAGE_HEIGHT = 100;
	
	private static final int TAB_ITEM_WIDTH = 660;
	private static final int TAB_ITEM_HEIGHT = 300;
	
	protected Composite imageComposite;
	protected Image image;
	
	public TGAboutDialog() {
		super();
	}
	
	public void show(final TGViewContext context){
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("help.about"));
		
		//--------------------HEADER----------------------------------
		Composite header = new Composite(dialog,SWT.NONE);
		header.setLayout(new GridLayout(2,false));
		header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true ,true));
		
		this.image = TuxGuitar.getInstance().getIconManager().getAboutDescription();
		
		this.imageComposite = new Composite(header,SWT.NONE);
		this.imageComposite.setLayoutData(new GridData(IMAGE_WIDTH,IMAGE_HEIGHT));
		this.imageComposite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle bounds = TGAboutDialog.this.image.getBounds();
				TGPainterImpl painter = new TGPainterImpl(e.gc);
				painter.drawImage(new TGImageImpl(TGAboutDialog.this.image),((IMAGE_WIDTH - bounds.width) /2),((IMAGE_HEIGHT - bounds.height) /2));
			}
		});
		
		final Font titleFont = new Font(dialog.getDisplay(),TuxGuitar.getInstance().getConfig().getFontDataConfigValue(TGConfigKeys.FONT_ABOUT_DIALOG_TITLE));
		Label title = new Label(header,SWT.NONE);
		title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true ,true));
		title.setFont(titleFont);
		title.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		title.setText(RELEASE_NAME);
		title.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				titleFont.dispose();
			}
		});
		
		//-------------------TABS-----------------------
		Composite tabs = new Composite(dialog, SWT.NONE);
		tabs.setLayout(new GridLayout());
		tabs.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		final TabFolder tabFolder = new TabFolder(tabs, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		tabFolder.setLayout(new FormLayout());
		
		TGAboutContentReader docReader = new TGAboutContentReader(context.getContext());
		
		makeTabItem(tabFolder,TGAboutContentReader.DESCRIPTION,docReader.read(TGAboutContentReader.DESCRIPTION).toString());
		makeTabItem(tabFolder,TGAboutContentReader.AUTHORS,docReader.read(TGAboutContentReader.AUTHORS).toString());
		makeTabItem(tabFolder,TGAboutContentReader.LICENSE,docReader.read(TGAboutContentReader.LICENSE).toString());
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tabFolder.getSelectionIndex() == 0){
					TGAboutDialog.this.image = TuxGuitar.getInstance().getIconManager().getAboutDescription();
				}else if(tabFolder.getSelectionIndex() == 1){
					TGAboutDialog.this.image = TuxGuitar.getInstance().getIconManager().getAboutAuthors();
				}else if(tabFolder.getSelectionIndex() == 2){
					TGAboutDialog.this.image = TuxGuitar.getInstance().getIconManager().getAboutLicense();
				}
				TGAboutDialog.this.imageComposite.redraw();
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout());
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		Button buttonClose = new Button(buttons, SWT.PUSH);
		buttonClose.setLayoutData(getButtonData());
		buttonClose.setText(TuxGuitar.getProperty("close"));
		buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		tabFolder.setSelection(0);
		
		dialog.setDefaultButton( buttonClose );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private void makeTabItem(TabFolder tabFolder,String itemName,String itemText){
		Composite control = new Composite(tabFolder, SWT.NONE);
		control.setLayout(new GridLayout());
		control.setLayoutData(new FormData(TAB_ITEM_WIDTH,TAB_ITEM_HEIGHT));
		
		Text text = new Text(control,SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		text.setBackground(TuxGuitar.getInstance().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.setEditable(false);
		text.append(itemText);
		text.setSelection(0);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(TuxGuitar.getProperty(PROPERTY_PREFIX + itemName));
		tabItem.setControl(control);
	}
}
