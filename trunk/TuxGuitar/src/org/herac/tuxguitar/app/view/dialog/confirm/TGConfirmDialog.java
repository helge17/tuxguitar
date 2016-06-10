package org.herac.tuxguitar.app.view.dialog.confirm;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGConfirmDialog {

	public static final String ATTRIBUTE_MESSAGE = "message";
	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_DEFAULT_BUTTON = "defaultButton";
	public static final String ATTRIBUTE_RUNNABLE_YES = "yesRunnable";
	public static final String ATTRIBUTE_RUNNABLE_NO = "noRunnable";
	public static final String ATTRIBUTE_RUNNABLE_CANCEL = "cancelRunnable";
	
	public static int BUTTON_CANCEL = 0x01;
	public static int BUTTON_YES = 0x02;
	public static int BUTTON_NO = 0x04;
	
	public void show(final TGViewContext context) {
		final String message = context.getAttribute(ATTRIBUTE_MESSAGE);
		final Integer style = context.getAttribute(ATTRIBUTE_STYLE);
		final Integer defaultButton = context.getAttribute(ATTRIBUTE_DEFAULT_BUTTON);
		final Runnable yesRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_YES);
		final Runnable noRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_NO);
		final Runnable cancelRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_CANCEL);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("confirm"));
		
		//========================================================================
		UITableLayout panelLayout = new UITableLayout();
		UIPanel uiPanel = uiFactory.createPanel(dialog, false);
		uiPanel.setLayout(panelLayout);
		dialogLayout.set(uiPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIImageView uiIcon = uiFactory.createImageView(uiPanel);
		uiIcon.setImage(TGIconManager.getInstance(context.getContext()).getStatusQuestion());
		panelLayout.set(uiIcon, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		UILabel uiMessage = uiFactory.createLabel(uiPanel);
		uiMessage.setText(message);
		panelLayout.set(uiMessage, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		//========================================================================
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		int columns = 0;
		if((style & BUTTON_YES) != 0){
			addCloseButton(uiFactory, dialog, buttons, TuxGuitar.getProperty("yes"), yesRunnable, (defaultButton == BUTTON_YES), ++columns);
		}
		if((style & BUTTON_NO) != 0){
			addCloseButton(uiFactory, dialog, buttons, TuxGuitar.getProperty("no"), noRunnable, (defaultButton == BUTTON_NO), ++columns);
		}
		if((style & BUTTON_CANCEL) != 0){
			addCloseButton(uiFactory, dialog, buttons, TuxGuitar.getProperty("cancel"), cancelRunnable, (defaultButton == BUTTON_CANCEL), ++columns);
		}
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void addCloseButton(final UIFactory factory, final UIWindow dialog, UILayoutContainer parent, String text, final Runnable runnable, boolean defaultButton, int column){
		UIButton uiButton = factory.createButton(parent);
		uiButton.setText(text);
		uiButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
				if( runnable != null ) {
					runnable.run();
				}
			}
		});
		if( defaultButton ){
			uiButton.setDefaultButton();
		}
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(uiButton, 1, column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
	}
}
