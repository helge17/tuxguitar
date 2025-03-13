package org.herac.tuxguitar.io.pdf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class PDFSettingsDialog {

	private TGContext context;
	private PDFSettings settings;
	private int nbSettings;
	private Map<String,UISpinner> spinnerMap;

	public PDFSettingsDialog(TGContext context){
		this.context = context;
		this.settings = PDFSettingsManager.getInstance(context).getSettings();
		this.spinnerMap = new HashMap<String, UISpinner>();
	}

	public void configure(UIWindow parent) {
		if (this.settings != null) {
			final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(parent, true, false);

			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("pdf.advanced-settings-dialog"));

			// --- SETTINGS ----
			UITableLayout settingsLayout = new UITableLayout();
			UILegendPanel group = uiFactory.createLegendPanel(dialog);
			group.setLayout(settingsLayout);
			group.setText(TuxGuitar.getProperty("pdf.settings"));
			dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.nbSettings = 0;
			List<String> keys = settings.getOrderedKeys();
			for (String key : keys) {
				String name = TuxGuitar.getProperty(key);
				if (key != name) {
					this.addSetting(key, name, settingsLayout, uiFactory, group);
				}
			}
			this.populateValues();

			// --- BUTTONS ----
			UITableLayout buttonsLayout = new UITableLayout(0f);
			UIPanel buttons = uiFactory.createPanel(dialog, false);
			buttons.setLayout(buttonsLayout);
			dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

			UIButton buttonDefault = uiFactory.createButton(buttons);
			buttonDefault.setText(TuxGuitar.getProperty("defaults"));
			buttonDefault.addSelectionListener(new UISelectionListener() {
				@Override
				public void onSelect(UISelectionEvent event) {
					settings = new PDFSettings();
					populateValues();
				}
			});
			buttonsLayout.set(buttonDefault, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

			UIButton buttonOK = uiFactory.createButton(buttons);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setDefaultButton();
			buttonOK.addSelectionListener(new UISelectionListener() {
				@Override
				public void onSelect(UISelectionEvent event) {
					saveSettings();
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonOK, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

			UIButton buttonCancel = uiFactory.createButton(buttons);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
			buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

			TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}

	private void addSetting(String key, String settingName, UITableLayout layout, UIFactory uiFactory, UILegendPanel panel) {
		nbSettings++;
		UILabel label = uiFactory.createLabel(panel);
		label.setText(settingName + ":");
		layout.set(label, nbSettings, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		UISpinner spinner = uiFactory.createSpinner(panel);
		spinner.setMinimum(0);
		layout.set(spinner, nbSettings, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.spinnerMap.put(key, spinner);
	}

	private void populateValues() {
		Map<String, Float> configMap = this.settings.getSettingsMap();
		for (String key : spinnerMap.keySet()) {
			spinnerMap.get(key).setValue(Math.round(configMap.get(key)));
		}
	}

	private void saveSettings() {
		PDFSettingsManager mgr = PDFSettingsManager.getInstance(context);
		for (String key : spinnerMap.keySet()) {
			mgr.setSetting(key, (float)spinnerMap.get(key).getValue());
		}
		mgr.saveSettings();
	}
}
