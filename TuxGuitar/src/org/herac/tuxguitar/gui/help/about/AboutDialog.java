package org.herac.tuxguitar.gui.help.about;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.util.TGVersion;

public class AboutDialog {
	
	private static final String RELEASE_NAME = (TuxGuitar.APPLICATION_NAME + " " + TGVersion.CURRENT.getVersion());
	private static final String PROPERTY_PREFIX = ("help.about.");
	
	private static final int IMAGE_WIDTH = 100;
	private static final int IMAGE_HEIGHT = 100;
	
	private static final int TAB_ITEM_WIDTH = 660;
	private static final int TAB_ITEM_HEIGHT = 300;
	
	protected Composite imageComposite;
	protected Image image;
	
	public AboutDialog() {
		super();
	}
	
	public void open(Shell shell) {
		final Shell dialog = DialogUtils.newDialog(shell,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("help.about"));
		
		//--------------------HEADER----------------------------------
		Composite header = new Composite(dialog,SWT.NONE);
		header.setLayout(new GridLayout(2,false));
		header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true ,true));
		
		this.image = TuxGuitar.instance().getIconManager().getAboutDescription();
		
		this.imageComposite = new Composite(header,SWT.NONE);
		this.imageComposite.setLayoutData(new GridData(IMAGE_WIDTH,IMAGE_HEIGHT));
		this.imageComposite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle bounds = AboutDialog.this.image.getBounds();
				TGPainter painter = new TGPainter(e.gc);
				painter.drawImage(AboutDialog.this.image,((IMAGE_WIDTH - bounds.width) /2),((IMAGE_HEIGHT - bounds.height) /2));
			}
		});
		
		final Font titleFont = new Font(dialog.getDisplay(),TuxGuitar.instance().getConfig().getFontDataConfigValue(TGConfigKeys.FONT_ABOUT_DIALOG_TITLE));
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
		
		AboutContentReader docReader = new AboutContentReader();
		
		makeTabItem(tabFolder,AboutContentReader.DESCRIPTION,docReader.read(AboutContentReader.DESCRIPTION).toString());
		makeTabItem(tabFolder,AboutContentReader.AUTHORS,docReader.read(AboutContentReader.AUTHORS).toString());
		makeTabItem(tabFolder,AboutContentReader.LICENSE,docReader.read(AboutContentReader.LICENSE).toString());
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tabFolder.getSelectionIndex() == 0){
					AboutDialog.this.image = TuxGuitar.instance().getIconManager().getAboutDescription();
				}else if(tabFolder.getSelectionIndex() == 1){
					AboutDialog.this.image = TuxGuitar.instance().getIconManager().getAboutAuthors();
				}else if(tabFolder.getSelectionIndex() == 2){
					AboutDialog.this.image = TuxGuitar.instance().getIconManager().getAboutLicense();
				}
				AboutDialog.this.imageComposite.redraw();
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
		text.setBackground(TuxGuitar.instance().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.setEditable(false);
		text.append(itemText);
		text.setSelection(0);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(TuxGuitar.getProperty(PROPERTY_PREFIX + itemName));
		tabItem.setControl(control);
	}
}
