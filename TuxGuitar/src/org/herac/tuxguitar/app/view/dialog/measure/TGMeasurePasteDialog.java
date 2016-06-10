package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.measure.TGPasteMeasureAction;
import org.herac.tuxguitar.app.clipboard.MeasureTransferable;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasurePasteDialog {
	
	public void show(final TGViewContext context) {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("edit.paste"));
		
		//-----------------COUNT------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("edit.paste"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel countLabel = uiFactory.createLabel(group);
		countLabel.setText(TuxGuitar.getProperty("edit.paste.count"));
		groupLayout.set(countLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner countSpinner = uiFactory.createSpinner(group);
		countSpinner.setMinimum( 1 );
		countSpinner.setMaximum( 100 );
		countSpinner.setValue( 1 );
		groupLayout.set(countSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//----------------------------------------------------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton replace = uiFactory.createRadioButton(options);
		replace.setText(TuxGuitar.getProperty("edit.paste.replace-mode"));
		replace.setSelected(true);
		optionsLayout.set(replace, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton insert = uiFactory.createRadioButton(options);
		insert.setText(TuxGuitar.getProperty("edit.paste.insert-mode"));
		optionsLayout.set(insert, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int pasteMode = 0;
				int pasteCount = countSpinner.getValue();
				if( replace.isSelected() ){
					pasteMode = MeasureTransferable.TRANSFER_TYPE_REPLACE;
				}else if(insert.isSelected()){
					pasteMode = MeasureTransferable.TRANSFER_TYPE_INSERT;
				}
				processAction(context.getContext(), pasteMode, pasteCount);
				
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
	
	public void processAction(TGContext context, Integer pasteMode, Integer pasteCount) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGPasteMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_MODE, pasteMode);
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_COUNT, pasteCount);
		tgActionProcessor.process();
	}
}
