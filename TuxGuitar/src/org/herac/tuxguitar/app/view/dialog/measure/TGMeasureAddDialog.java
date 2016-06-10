package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
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

public class TGMeasureAddDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("measure.add"));
		
		//-----------------COUNT------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("measure.add"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel countLabel = uiFactory.createLabel(group);
		countLabel.setText(TuxGuitar.getProperty("measure.add.count"));
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
		
		final UIRadioButton beforePosition = uiFactory.createRadioButton(options);
		beforePosition.setText(TuxGuitar.getProperty("measure.add-before-current-position"));
		optionsLayout.set(beforePosition, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton afterPosition = uiFactory.createRadioButton(options);
		afterPosition.setText(TuxGuitar.getProperty("measure.add-after-current-position"));
		optionsLayout.set(afterPosition, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton atEnd = uiFactory.createRadioButton(options);
		atEnd.setText(TuxGuitar.getProperty("measure.add-at-end"));
		atEnd.setSelected(true);
		optionsLayout.set(atEnd, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
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
				int number = 0;
				int count = countSpinner.getValue();
				if( beforePosition.isSelected() ){
					number = (header.getNumber());
				}else if( afterPosition.isSelected() ){
					number = (header.getNumber() + 1);
				}else if( atEnd.isSelected() ){
					number = (song.countMeasureHeaders() + 1);
				}
				processAction(context.getContext(), song, number, count);
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
	
	public void processAction(TGContext context, TGSong song, Integer number, Integer count) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGAddMeasureListAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, count);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, number);
		tgActionProcessor.process();
	}
}
