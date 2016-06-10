package org.herac.tuxguitar.app.view.dialog.plugin;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.plugin.TGPluginInfo;

public class TGPluginInfoDialog {
	
	public static String ATTRIBUTE_MODULE_ID = "moduleId";
	
	public void show(final TGViewContext context) {
		final String moduleId = context.getAttribute(ATTRIBUTE_MODULE_ID);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		TGPluginInfo pluginInfo = new TGPluginInfo(context.getContext(), moduleId);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("plugins"));
		
		UIPanel info = uiFactory.createPanel(dialog,false);
		info.setLayout(new UITableLayout());
		dialogLayout.set(info, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		showInfoString(uiFactory, info, TuxGuitar.getProperty("name") + ":", pluginInfo.getName(), 1);
		showInfoString(uiFactory, info, TuxGuitar.getProperty("version") + ":", pluginInfo.getVersion(), 2);
		showInfoString(uiFactory, info, TuxGuitar.getProperty("author") + ":", pluginInfo.getAuthor(), 3);
		showInfoString(uiFactory, info, TuxGuitar.getProperty("description") + ":", pluginInfo.getDescription(), 4);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonExit = uiFactory.createButton(buttons);
		buttonExit.setDefaultButton();
		buttonExit.setText(TuxGuitar.getProperty("exit"));
		buttonExit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonExit, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void showInfoString(UIFactory factory, UIPanel parent, String key, String value, int row){
		UILabel labelKey = factory.createLabel(parent);
		UILabel labelValue = factory.createLabel(parent);
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(labelKey, row, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_TOP, false, true);
		uiLayout.set(labelValue, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		setBold(factory, labelKey);
		
		labelKey.setText(key);
		labelValue.setText( (value != null && value.length() > 0) ? value : TuxGuitar.getProperty("plugin.unknown-value"));
	}
	
	private void setBold(UIFactory factory, UILabel label){
		UIFont defaultFont = label.getFont();
		if( defaultFont != null ) {
			final UIFont font = factory.createFont(defaultFont.getName(), defaultFont.getHeight(), true, defaultFont.isItalic());
			
			label.setFont(font);
			label.addDisposeListener(new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					font.dispose();
				}
			});
		}
	}
}
