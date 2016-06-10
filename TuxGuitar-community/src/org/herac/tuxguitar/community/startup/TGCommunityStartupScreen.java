package org.herac.tuxguitar.community.startup;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.utils.TGCommunityWeb;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UILinkEvent;
import org.herac.tuxguitar.ui.event.UILinkListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILinkLabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGCommunityStartupScreen {
	
	private static final float WRAP_WIDTH = 450;
	
	private TGContext context;
	
	public TGCommunityStartupScreen(TGContext context){
		this.context = context;
	}
	
	public void open(){
		try {
			final UIWindow parent = TGWindow.getInstance(this.context).getWindow();
			TGSynchronizer.getInstance(this.context).executeLater( new Runnable() {
				public void run() throws TGException {
					open(parent);
				}
			} );
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected void open(UIWindow parent){
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.title"));
		
		//==============================================================//
		UITableLayout topLayout = new UITableLayout();
		UIPanel top = uiFactory.createPanel(dialog, false);
		top.setLayout(topLayout);
		dialogLayout.set(top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIImageView image = uiFactory.createImageView(top);
		image.setImage( TuxGuitar.getInstance().getIconManager().getAppIcon() );
		topLayout.set(image, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_TOP, false, false, 3, 1);
		
		addTitle(uiFactory, top, 1, 2, TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.title"));
		addTipComment(uiFactory, top, 2, 2, TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.tip-1"), WRAP_WIDTH);
		addTipComment(uiFactory, top, 3, 2, TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.tip-2"), WRAP_WIDTH);
		
		top.computePackedSize();
		//==============================================================//
		
		UITableLayout bottomLayout = new UITableLayout();
		UIPanel bottom = uiFactory.createPanel(dialog, false);
		bottom.setLayout(bottomLayout);
		dialogLayout.set(bottom, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		addComment(uiFactory, bottom, 1, 1, TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.tip-bottom"), top.getPackedContentSize().getWidth());
		
		//==============================================================//
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		final UICheckBox buttonDisabled = uiFactory.createCheckBox(buttons);
		buttonDisabled.setText( TuxGuitar.getProperty("tuxguitar-community.welcome-dialog.disable") );
		buttonDisabled.setSelected( this.isDisabled() );
		buttonsLayout.set(buttonDisabled, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.setFocus();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				setDisabled( buttonDisabled.isSelected() );
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonOK, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK );
	}
	
	private void addTitle(UIFactory factory, UILayoutContainer parent, Integer row, Integer col, String text){
		UILabel uiLabel = factory.createLabel(parent);
		uiLabel.setText(text);
		this.addLayout(uiLabel, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		
		UIFont defaultFont = uiLabel.getFont();
		if( defaultFont != null ) {
			final UIFont font = factory.createFont(defaultFont.getName(), defaultFont.getHeight() + 2, true, false);
			uiLabel.setFont(font);
			uiLabel.addDisposeListener(new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					font.dispose();
				}
			});
		}
	}
	
	private void addTipItem(UIFactory factory, UIContainer parent, Integer row, Integer col){
		UILabel uiLabel = factory.createLabel(parent);
		uiLabel.setText("\u066D");
		this.addLayout(uiLabel, row, col, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
	}
	
	private void addComment(UIFactory factory, UIContainer parent, Integer row, Integer col, String text, Float wrapWidth){
		final UILinkLabel uiLink = factory.createLinkLabel(parent);
		uiLink.setText(text);
		uiLink.setWrapWidth(wrapWidth);
		uiLink.addLinkListener(new UILinkListener() {
			public void onLinkSelect(final UILinkEvent event) {
				new Thread( new Runnable() {
					public void run() throws TGException {
						TGCommunityWeb.open(getContext(), event.getLink());
					}
				} ).start();
			}
		});
		
		this.addLayout(uiLink, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
	}
	
	private void addTipComment(UIFactory factory, UIContainer parent, Integer row, Integer col, String text, Float wrapWidth){
		UIPanel uiPanel = factory.createPanel(parent, false);
		uiPanel.setLayout(new UITableLayout(0f));
		
		this.addLayout(uiPanel, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		this.addTipItem(factory, uiPanel, 1, 1);
		this.addComment(factory, uiPanel, 1, 2, text, wrapWidth);
	}
	
	private void addLayout(UIControl control, Integer row, Integer col, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY) {
		UILayoutContainer uiParent = (UILayoutContainer) control.getParent();
		UITableLayout uiLayout = (UITableLayout) uiParent.getLayout();
		uiLayout.set(control, row, col, alignX, alignY, fillX, fillY);
	}
	
	public void setDisabled( boolean enabled ){
		TGCommunitySingleton.getInstance(this.context).getConfig().setValue("community.welcome.disabled",enabled);
	}
	
	public boolean isDisabled(){
		return TGCommunitySingleton.getInstance(this.context).getConfig().getBooleanValue("community.welcome.disabled");
	}
	
	public TGContext getContext() {
		return context;
	}
}