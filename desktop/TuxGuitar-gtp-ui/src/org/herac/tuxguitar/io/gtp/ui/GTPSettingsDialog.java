package org.herac.tuxguitar.io.gtp.ui;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.io.gtp.GTPSettingsManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class GTPSettingsDialog {
	
	private TGContext context;
	
	public GTPSettingsDialog(TGContext context){
		this.context = context;
	}
	
	public void configure(UIWindow parent) {
		final List<String> charsets = getAvailableCharsets();
		final GTPSettingsManager settingsUtil = GTPSettingsManager.getInstance(this.context);
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("gtp.settings.title"));
		
		//------------------DEVICE-----------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("gtp.settings.charset.tip"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel label = uiFactory.createLabel(group);
		label.setText(TuxGuitar.getProperty("gtp.settings.charset.select") + ":");		
		groupLayout.set(label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<String> value = uiFactory.createDropDownSelect(group);
		for(String charset : charsets) {
			value.addItem(new UISelectItem<String>(charset, charset));
		}
		value.setSelectedValue(settingsUtil.getSettings().getCharset());
		groupLayout.set(value, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, 250f, null, null);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String charset = value.getSelectedValue();
				if( charset != null ){
					settingsUtil.getSettings().setCharset(charset);
					settingsUtil.saveSettings();
				}
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private List<String> getAvailableCharsets(){
		List<String> charsets = new ArrayList<String>();
		Iterator<Map.Entry<String, Charset>> it = Charset.availableCharsets().entrySet().iterator();
		while( it.hasNext() ){
			Map.Entry<String, Charset> entry = it.next();
			charsets.add(entry.getKey());
		}
		return charsets;
	}
}
