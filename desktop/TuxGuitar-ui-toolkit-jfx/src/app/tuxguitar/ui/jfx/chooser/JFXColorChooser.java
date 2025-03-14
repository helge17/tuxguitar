package app.tuxguitar.ui.jfx.chooser;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIColorChooserHandler;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.jfx.resource.JFXColor;
import app.tuxguitar.ui.jfx.widget.JFXColorPicker;
import app.tuxguitar.ui.jfx.widget.JFXContainer;
import app.tuxguitar.ui.jfx.widget.JFXWindow;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;

public class JFXColorChooser implements UIColorChooser {

	private UIFactory uiFactory;
	private JFXWindow window;
	private String text;
	private UIColorModel defaultModel;

	public JFXColorChooser(UIFactory uiFactory, JFXWindow window) {
		this.uiFactory = uiFactory;
		this.window = window;
	}

	public void choose(final UIColorChooserHandler selectionHandler) {
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = this.uiFactory.createWindow(this.window, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(this.text != null ? this.text : "Color Chooser");

		final JFXColorPicker colorPicker = new JFXColorPicker((JFXContainer<?>) dialog);
		dialogLayout.set(colorPicker, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, 400f, null, null);

		if( this.defaultModel != null ) {
			colorPicker.setValue(new JFXColor(this.defaultModel));
		}

		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = this.uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_BOTTOM, true, false);

		final UIButton buttonOK = this.uiFactory.createButton(buttons);
		buttonOK.setText("Ok");
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				handleSelection(selectionHandler, colorPicker.getValue());
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = this.uiFactory.createButton(buttons);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				handleSelection(selectionHandler, null);
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		dialog.pack();

		UIRectangle parentBounds = this.window.getBounds();
		UIRectangle dialogBounds = dialog.getBounds();
		dialogBounds.getPosition().setX(Math.max(0, parentBounds.getX() + (parentBounds.getWidth() - dialogBounds.getWidth()) / 2f));
		dialogBounds.getPosition().setY(Math.max(0, parentBounds.getY() + (parentBounds.getHeight() - dialogBounds.getHeight()) / 2f));
		dialog.setBounds(dialogBounds);

		dialog.open();
	}

	public void handleSelection(UIColorChooserHandler selectionHandler, UIColor value) {
		selectionHandler.onSelectColor(value != null ? ((JFXColor) value).getControl() : null);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIColorModel defaultModel) {
		this.defaultModel = defaultModel;
	}
}
