package app.tuxguitar.app.view.dialog.toolbar;

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
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UISeparator;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarTimeCounterDialog {

	private TGContext context;
	private UIWindow parentWindow;
	private UIFontModel selectedFont;
	private boolean displayLoopTimestamp;

	public TGMainToolBarTimeCounterDialog(TGContext context, UIWindow parentWindow) {
		this.context = context;
		this.parentWindow = parentWindow;
	}

	public void show() {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UITableLayout dialogLayout = new UITableLayout();
		UIWindow dialog = uiFactory.createWindow(this.parentWindow, true, false);
		this.selectedFont = TGConfigManager.getInstance(this.context)
				.getFontModelConfigValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP);
		this.displayLoopTimestamp = TGConfigManager.getInstance(this.context)
				.getBooleanValue(TGConfigKeys.TIMECOUNTER_DISPLAY_MODE);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("toolbar.timeCounter.dialogTitle"));

		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(dialog, false);
		panel.setLayout(panelLayout);
		dialogLayout.set(panel, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, true, true);

		UIButton selectButton = uiFactory.createButton(panel);
		selectButton.setText(TuxGuitar.getProperty("toolbar.timeCounter.selectFont"));
		selectButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				openFontChooser(dialog);
			}
		});
		panelLayout.set(selectButton, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		UISeparator separator = uiFactory.createHorizontalSeparator(panel);
		panelLayout.set(separator, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UIRadioButton perLoopRadio = uiFactory.createRadioButton(panel);
		perLoopRadio.setText(TuxGuitar.getProperty("toolbar.timeCounter.loopTimestamp"));
		perLoopRadio.setSelected(this.displayLoopTimestamp);
		perLoopRadio.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarTimeCounterDialog.this.displayLoopTimestamp = true;
			}
		});
		panelLayout.set(perLoopRadio, 3, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		UIRadioButton perSessionRadio = uiFactory.createRadioButton(panel);
		perSessionRadio.setText(TuxGuitar.getProperty("toolbar.timeCounter.perSession"));
		perSessionRadio.setSelected(!this.displayLoopTimestamp);
		perSessionRadio.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarTimeCounterDialog.this.displayLoopTimestamp = false;
			}
		});
		panelLayout.set(perSessionRadio, 4, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		separator = uiFactory.createHorizontalSeparator(panel);
		panelLayout.set(separator, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(panel, false);
		buttons.setLayout(buttonsLayout);
		panelLayout.set(buttons, 6, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, false);

		UIButton cancelButton = uiFactory.createButton(buttons);
		cancelButton.setText(TuxGuitar.getProperty("cancel"));
		cancelButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(cancelButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UIButton okButton = uiFactory.createButton(buttons);
		okButton.setDefaultButton();
		okButton.setText(TuxGuitar.getProperty("ok"));
		okButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGConfigManager config = TGConfigManager.getInstance(TGMainToolBarTimeCounterDialog.this.context);
				config.setValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP, TGMainToolBarTimeCounterDialog.this.selectedFont);
				config.setValue(TGConfigKeys.TIMECOUNTER_DISPLAY_MODE, TGMainToolBarTimeCounterDialog.this.displayLoopTimestamp);
				dialog.dispose();
			}
		});
		buttonsLayout.set(okButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void openFontChooser(UIWindow dialog) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UIFontChooser uiFontChooser = uiFactory.createFontChooser(dialog);
		uiFontChooser.setDefaultModel(this.selectedFont);
		uiFontChooser.choose(new UIFontChooserHandler() {
			public void onSelectFont(UIFontModel selection) {
				if (selection != null) {
					TGMainToolBarTimeCounterDialog.this.selectedFont = selection;
				}
			}
		});
	}
}
