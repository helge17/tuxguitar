package app.tuxguitar.app.view.dialog.scale;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import app.tuxguitar.app.tools.scale.ScaleManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIListBoxSelect;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGScaleDialog {

	public void show(final TGViewContext context) {
		final ScaleManager scaleManager = ScaleManager.getInstance(context.getContext());
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("scale.list"));

		// ----------------------------------------------------------------------
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(dialog, false);
		composite.setLayout(compositeLayout);
		dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIListBoxSelect<Integer> keys = uiFactory.createListBoxSelect(composite);
		String[] keyNames = scaleManager.getKeyNames();
		for(int i = 0;i < keyNames.length;i ++){
			keys.addItem(new UISelectItem<Integer>(keyNames[i], i));
		}
		keys.setSelectedValue(scaleManager.getSelectionKeyIndex());
		compositeLayout.set(keys, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		compositeLayout.set(keys, UITableLayout.PACKED_HEIGHT, 200f);

		final UIListBoxSelect<Integer> scales = uiFactory.createListBoxSelect(composite);
		scales.addItem(new UISelectItem<Integer>("None", ScaleManager.NONE_SELECTION));
		String[] scaleNames = scaleManager.getScaleNames();
		for(int i = 0;i < scaleNames.length;i ++){
			scales.addItem(new UISelectItem<Integer>(scaleNames[i], i));
		}
		scales.setSelectedValue(scaleManager.getScaleIndex());

		compositeLayout.set(scales, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		compositeLayout.set(scales, UITableLayout.PACKED_HEIGHT, 200f);

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				selectScale(context.getContext(), scales.getSelectedValue(), keys.getSelectedValue());
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

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void selectScale(TGContext context, Integer index, Integer key) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGSelectScaleAction.NAME);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_INDEX, index);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_KEY, key);
		tgActionProcessor.process();
	}
}
