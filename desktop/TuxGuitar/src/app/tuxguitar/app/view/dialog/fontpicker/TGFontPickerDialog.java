package app.tuxguitar.app.view.dialog.fontpicker;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIFontChooserHandler;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UISeparator;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGFontPickerDialog {

	private TGContext context;
	private UIWindow parentWindow;

	public TGFontPickerDialog(TGContext context, UIWindow parentWindow) {
		this.context = context;
		this.parentWindow = parentWindow;
	}

	public void show() {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UITableLayout dialogLayout = new UITableLayout();
		UIWindow dialog = uiFactory.createWindow(this.parentWindow, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("toolbar.timeCounter.dialogTitle"));

		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(dialog, false);
		panel.setLayout(panelLayout);
		dialogLayout.set(panel, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, true, true);

		UILabel selectLabel = uiFactory.createLabel(panel);
		selectLabel.setText(TuxGuitar.getProperty("toolbar.timeCounter.selectFont"));
		panelLayout.set(selectLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		UIButton selectButton = uiFactory.createButton(panel);
		selectButton.setText(TuxGuitar.getProperty("toolbar.timeCounter.select"));
		selectButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				openFontChooser(dialog);
			}
		});
		panelLayout.set(selectButton, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		UISeparator separator = uiFactory.createHorizontalSeparator(panel);
		panelLayout.set(separator, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 2, 0f, 0f, null);

		UILabel displayModeLabel = uiFactory.createLabel(panel);
		displayModeLabel.setText(TuxGuitar.getProperty("toolbar.timeCounter.displayMode"));
		panelLayout.set(displayModeLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		String displayMode = TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.TIMECOUNTER_DISPLAY_MODE);

		UIRadioButton perLoopRadio = uiFactory.createRadioButton(panel);
		perLoopRadio.setText(TuxGuitar.getProperty("toolbar.timeCounter.perLoop"));
		perLoopRadio.setSelected("perLoop".equals(displayMode));
		perLoopRadio.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGConfigManager.getInstance(TGFontPickerDialog.this.context).setValue(TGConfigKeys.TIMECOUNTER_DISPLAY_MODE, "perLoop");
			}
		});
		panelLayout.set(perLoopRadio, 3, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		UIRadioButton perSessionRadio = uiFactory.createRadioButton(panel);
		perSessionRadio.setText(TuxGuitar.getProperty("toolbar.timeCounter.perSession"));
		perSessionRadio.setSelected("perSession".equals(displayMode));
		perSessionRadio.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGConfigManager.getInstance(TGFontPickerDialog.this.context).setValue(TGConfigKeys.TIMECOUNTER_DISPLAY_MODE, "perSession");
			}
		});
		panelLayout.set(perSessionRadio, 4, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void openFontChooser(UIWindow dialog) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UIFontModel fontModel = TGConfigManager.getInstance(this.context)
				.getFontModelConfigValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP);
		UIFontChooser uiFontChooser = uiFactory.createFontChooser(dialog);
		uiFontChooser.setDefaultModel(fontModel);
		uiFontChooser.choose(new UIFontChooserHandler() {
			public void onSelectFont(UIFontModel selection) {
				if (selection != null) {
					TGConfigManager.getInstance(TGFontPickerDialog.this.context)
							.setValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP, selection);
				}
			}
		});
	}
}