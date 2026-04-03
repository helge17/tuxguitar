package app.tuxguitar.app.view.dialog.measure;

import java.util.Iterator;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGMeasureCopyDialog {
	// to memorize user preferences (used each time dialog is opened)
	static boolean initCopyAllTracks = true;
	static boolean initCopyMarkers = false;

	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("edit.copy"));

		//----------------------------------------------------------------------
		UITableLayout rangeLayout = new UITableLayout();
		UILegendPanel range = uiFactory.createLegendPanel(dialog);
		range.setLayout(rangeLayout);
		range.setText(TuxGuitar.getProperty("edit.copy"));
		dialogLayout.set(range, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		int measureCount = song.countMeasureHeaders();

		UILabel fromLabel = uiFactory.createLabel(range);
		fromLabel.setText(TuxGuitar.getProperty("edit.from") + ":");
		rangeLayout.set(fromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		int from;
		int to;
		if (beats!=null && !beats.isEmpty()) {
			from = beats.firstMeasure().getNumber();
			to = beats.lastMeasure().getNumber();
		} else {
			from = header.getNumber();
			to = header.getNumber();
		}

		final UISpinner fromSpinner = uiFactory.createSpinner(range);
		fromSpinner.setMinimum(1);
		fromSpinner.setMaximum(measureCount);
		fromSpinner.setValue(from);
		rangeLayout.set(fromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);

		UILabel toLabel = uiFactory.createLabel(range);
		toLabel.setText(TuxGuitar.getProperty("edit.to") + ":");
		rangeLayout.set(toLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		final UISpinner toSpinner = uiFactory.createSpinner(range);
		toSpinner.setMinimum(1);
		toSpinner.setMaximum(measureCount);
		toSpinner.setValue(to);
		rangeLayout.set(toSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);

		final int minSelection = 1;
		final int maxSelection = track.countMeasures();


		//------------ OPTIONS -----------------------------------------------
		UICheckBox allTracks = null;
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		int rowCheckBox = 1;
		if( song.countTracks() > 1 ){
			allTracks = uiFactory.createCheckBox(options);
			allTracks.setText(TuxGuitar.getProperty("edit.all-tracks"));
			allTracks.setSelected(initCopyAllTracks);
			optionsLayout.set(allTracks, rowCheckBox, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			rowCheckBox++;
		}
		final UICheckBox allTracksFinal = allTracks;
		final UICheckBox copyMarkers = uiFactory.createCheckBox(options);
		copyMarkers.setText(TuxGuitar.getProperty("edit.copy-markers"));
		copyMarkers.setSelected(initCopyMarkers);
		optionsLayout.set(copyMarkers, rowCheckBox, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		fromSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int fromSelection = fromSpinner.getValue();
				int toSelection = toSpinner.getValue();

				if(fromSelection < minSelection){
					fromSpinner.setValue(minSelection);
					fromSelection = minSelection;
				}else if(fromSelection > toSelection){
					fromSpinner.setValue(toSelection);
					fromSelection = toSelection;
				}
				updateCopyMarkers(song, fromSelection, toSelection, copyMarkers);
			}
		});
		toSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int toSelection = toSpinner.getValue();
				int fromSelection = fromSpinner.getValue();
				if(toSelection < fromSelection){
					toSpinner.setValue(fromSelection);
					toSelection = fromSelection;
				}else if(toSelection > maxSelection){
					toSpinner.setValue(maxSelection);
					toSelection = maxSelection;
				}
				updateCopyMarkers(song, fromSelection, toSelection, copyMarkers);
			}
		});
		updateCopyMarkers(song, fromSpinner.getValue(), toSpinner.getValue(), copyMarkers);

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
				if (allTracksFinal!=null) {
					initCopyAllTracks = allTracksFinal.isSelected();
				}
				initCopyMarkers = copyMarkers.isSelected();
				processAction(context.getContext(), fromSpinner.getValue(), toSpinner.getValue(),
						(allTracksFinal != null ? allTracksFinal.isSelected() : false),
						copyMarkers.isSelected());
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

	public void processAction(TGContext context, Integer measure1, Integer measure2, Boolean copyAllTracks, Boolean copyMarkers) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGCopyMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, copyAllTracks);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_COPY_MARKERS, copyMarkers);
		tgActionProcessor.process();
	}

	private void updateCopyMarkers(TGSong song, int from, int to, UICheckBox copyMarkers) {
		boolean atLeastOneMarker = false;
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		while (headers.hasNext() && !atLeastOneMarker) {
			TGMeasureHeader header = headers.next();
			int measureNb = header.getNumber();
			atLeastOneMarker = (measureNb>=from && measureNb<=to && header.getMarker() != null);
		}
		copyMarkers.setEnabled(atLeastOneMarker);
		if (!atLeastOneMarker) {
			copyMarkers.setSelected(false);
		}
	}
}
