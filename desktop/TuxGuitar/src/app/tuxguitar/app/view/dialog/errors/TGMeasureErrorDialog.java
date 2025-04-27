package app.tuxguitar.app.view.dialog.errors;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGProcess;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventException;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.helpers.TGMeasureError;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UITable;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMeasureErrorDialog implements TGEventListener {

	private UIWindow dialog;
	private TGContext context;
	private TGMeasureError currentError;
	private UILegendPanel curMeasureLegendPanel;
	private UIImageView measureStatusIcon;
	private UILabel voiceStatusLabel;
	private UIButton fixButton;
	private UILegendPanel globalStatusLegendPanel;
	private UIImageView globalStatusIcon;
	private UILabel globalStatusLabel;
	private UILegendPanel errListLegendPanel;
	private UICheckBox showAllTracks;
	private UITable<TGMeasureError> errTable;
	private UIButton closeButton;
	private TGProcess updateItemsProcess;
	private TGProcess loadIconsProcess;
	private TGProcess loadPropertiesProcess;
	private TGMeasureError selectedError;
	// cache locally icons (skin-dependent) and messages (language-dependent)
	// to avoid re-loading them each time the dialog is updated
	// (dialog content depends from skin, language, and current measure/song, and
	// may be updated often)
	private UIImage imageOK;
	private UIImage imageKO;
	private String sMeasureErrors;
	private String sShowAllTracks;
	private String sErrorsList;
	private String sCurrentMeasuresStatus;
	private String sFix;
	private String sSongStatus;
	private String sSongValid;
	private String sSongInvalid;
	private String sVoiceValid;
	private String sVoiceTooLong;
	private String sVoiceTooShort;
	private String sVoiceInvalid;
	private String sClose;

	public TGMeasureErrorDialog(TGContext context) {
		this.context = context;
		this.currentError = null;
		this.selectedError = null;
		this.createSyncProcesses();
	}

	public void show(TGViewContext viewContext) {
		UIFactory uiFactory = this.getUIFactory();
		UIWindow uiParent = viewContext.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		UITableLayout dialogLayout = new UITableLayout();

		this.loadIcons();
		this.loadProperties();

		this.dialog = uiFactory.createWindow(uiParent, false, true);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(sMeasureErrors);

		// ----------------- CURRENT MEASURE ------------------------
		UITableLayout curMeasureLayout = new UITableLayout();
		this.curMeasureLegendPanel = uiFactory.createLegendPanel(dialog);
		curMeasureLegendPanel.setLayout(curMeasureLayout);
		dialogLayout.set(curMeasureLegendPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		measureStatusIcon = uiFactory.createImageView(curMeasureLegendPanel);
		curMeasureLayout.set(measureStatusIcon, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false,
				true);

		voiceStatusLabel = uiFactory.createLabel(curMeasureLegendPanel);
		curMeasureLayout.set(voiceStatusLabel, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);

		fixButton = uiFactory.createButton(curMeasureLegendPanel);
		fixButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				if (TGMeasureErrorDialog.this.currentError != null) {
					TuxGuitar.getInstance().getTablatureEditor().getTablature().getSelector().clearSelection();
					TGActionProcessor actionProcessor = new TGActionProcessor(context, TGFixMeasureVoiceAction.NAME);
					actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE,
							TGMeasureErrorDialog.this.currentError.getMeasure());
					actionProcessor.setAttribute(TGFixMeasureVoiceAction.ATTRIBUTE_VOICE_INDEX,
							TGMeasureErrorDialog.this.currentError.getVoiceIndex());
					actionProcessor.setAttribute(TGFixMeasureVoiceAction.ATTRIBUTE_ERR_CODE,
							TGMeasureErrorDialog.this.currentError.getErrCode());
					actionProcessor.process();
				}
			}
		});
		curMeasureLayout.set(fixButton, 2, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		// ----------------- GLOBAL STATUS ------------------------
		UITableLayout globalStatusLayout = new UITableLayout();
		this.globalStatusLegendPanel = uiFactory.createLegendPanel(dialog);
		this.globalStatusLegendPanel.setLayout(globalStatusLayout);
		dialogLayout.set(globalStatusLegendPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		globalStatusIcon = uiFactory.createImageView(globalStatusLegendPanel);
		globalStatusLayout.set(globalStatusIcon, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false,
				true);

		globalStatusLabel = uiFactory.createLabel(globalStatusLegendPanel);
		globalStatusLayout.set(globalStatusLabel, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true,
				true);

		// ----------------- ERRORS LIST ------------------------
		UITableLayout errListLayout = new UITableLayout();
		this.errListLegendPanel = uiFactory.createLegendPanel(dialog);
		this.errListLegendPanel.setLayout(errListLayout);
		dialogLayout.set(errListLegendPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.showAllTracks = uiFactory.createCheckBox(errListLegendPanel);
		this.showAllTracks.setSelected(true);
		this.showAllTracks.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMeasureErrorDialog.this.updateItemsProcess.process();
			}
		});
		errListLayout.set(showAllTracks, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);

		this.errTable = uiFactory.createTable(errListLegendPanel, false);
		this.errTable.setColumns(1);
		this.errTable.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMeasureError err = TGMeasureErrorDialog.this.errTable.getSelectedValue();
				TGMeasureErrorDialog.this.selectedError = err;
				if (err != null) {
					TGMeasureErrorDialog.this.moveToError(err);
				}
			}
		});
		errListLayout.set(errTable, UITableLayout.PACKED_HEIGHT, 120f);
		errListLayout.set(errTable, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		// ----------------- BUTTON ------------------------
		UITableLayout buttonLayout = new UITableLayout();
		UIPanel buttonPanel = uiFactory.createPanel(dialog, false);
		buttonPanel.setLayout(buttonLayout);
		dialogLayout.set(buttonPanel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.closeButton = uiFactory.createButton(buttonPanel);
		this.closeButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMeasureErrorDialog.this.dispose();
			}
		});
		buttonLayout.set(closeButton, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);

		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
			}
		});

		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_PACK);

	}

	private void addListeners() {
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}

	private void removeListeners() {
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}

	private void moveToError(TGMeasureError err) {
		int caretVoiceIndex = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getVoice();
		if (caretVoiceIndex != err.getVoiceIndex()) {
			new TGActionProcessor(this.context, 
					caretVoiceIndex==0 ? TGSetVoice2Action.NAME : TGSetVoice1Action.NAME).process();
		}
		TGActionProcessor actionProcessor = new TGActionProcessor(this.context, TGMoveToAction.NAME);
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK,
				(TGTrackImpl) err.getMeasure().getTrack());
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, err.getMeasure());
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, err.getMeasure().getBeat(0));
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING,
				err.getMeasure().getTrack().getString(1));
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, Boolean.FALSE);
		actionProcessor.process();
	}

	private void loadProperties() {
		this.sMeasureErrors = TuxGuitar.getProperty("measure-errors");
		this.sShowAllTracks = TuxGuitar.getProperty("measure-errors.show-all-tracks");
		this.sErrorsList = TuxGuitar.getProperty("measure-errors.errors-list");
		this.sCurrentMeasuresStatus = TuxGuitar.getProperty("measure-errors.current-measure-status");
		this.sFix = TuxGuitar.getProperty("measure-errors.fix");
		this.sSongStatus = TuxGuitar.getProperty("measure-errors.song-status");
		this.sSongValid = TuxGuitar.getProperty("measure-errors.song-valid");
		this.sSongInvalid = TuxGuitar.getProperty("measure-errors.song-invalid");
		this.sVoiceValid = TuxGuitar.getProperty("measure-errors.voice-valid");
		this.sVoiceTooLong = TuxGuitar.getProperty("measure-errors.voice-too-long");
		this.sVoiceTooShort = TuxGuitar.getProperty("measure-errors.voice-too-short");
		this.sVoiceInvalid = TuxGuitar.getProperty("measure-errors.voice-invalid");
		this.sClose = TuxGuitar.getProperty("close");
	}

	private void loadIcons() {
		this.imageOK = TuxGuitar.getInstance().getIconManager().getOK();
		this.imageKO = TuxGuitar.getInstance().getIconManager().getKO();
	}

	private void updateItems() {
		if (this.isDisposed()) {
			return;
		}
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGSong song = tablature.getSong();
		TGMeasureImpl measure = tablature.getCaret().getMeasure();
		int voiceIndex = tablature.getCaret().getVoice();
		TGSongManager songManager = tablature.getSongManager();

		// ----------------- ERRORS LIST ------------------------
		this.showAllTracks.setText(sShowAllTracks);
		// build errors list, and create associated user messages to be displayed in
		// table
		List<TGMeasureError> listErrors = songManager.getMeasureErrors(song);
		List<UITableItem<TGMeasureError>> listTableItems = new ArrayList<UITableItem<TGMeasureError>>();
		this.errTable.removeItems();
		this.currentError = null;
		for (TGMeasureError err : listErrors) {
			UITableItem<TGMeasureError> tableItem = new UITableItem<TGMeasureError>(err);
			tableItem.setText(0, this.userMessage(err.getMeasure(), err.getVoiceIndex(), err));
			if (err.isEqualTo(this.selectedError)) {
				this.currentError = err;
			} else if ((this.selectedError == null) && (measure.equals(err.getMeasure()))
				&& ((voiceIndex == err.getVoiceIndex()) || (this.currentError == null)) ){
				this.currentError = err;
			}
			if (this.showAllTracks.isSelected()
					|| (measure.getTrack().getNumber() == err.getMeasure().getTrack().getNumber())) {
				listTableItems.add(tableItem);
			}
		}
		this.selectedError = null;
		// if current measure is valid, insert a first empty line in table
		// (gets selected by default when opening dialog, or after fixing a measure)
		if (this.currentError == null) {
			UITableItem<TGMeasureError> tableItem = new UITableItem<TGMeasureError>(null);
			tableItem.setText(0, "");
			listTableItems.add(0, tableItem);
		}
		// fill the UItable
		this.errTable.setColumnName(0, sErrorsList);
		for (UITableItem<TGMeasureError> tableItem : listTableItems) {
			this.errTable.addItem(tableItem);
			if ((this.currentError == tableItem.getValue())) {
				this.errTable.setSelectedItem(tableItem);
			}
		}

		// ----------------- CURRENT MEASURE ------------------------
		this.curMeasureLegendPanel.setText(sCurrentMeasuresStatus);
		this.measureStatusIcon.setImage(this.currentError == null ? this.imageOK : this.imageKO);
		this.voiceStatusLabel.setText(userMessage(measure, tablature.getCaret().getVoice(), this.currentError));
		this.fixButton.setText(sFix);
		this.fixButton.setEnabled(this.currentError != null && this.currentError.canBeFixed());

		// ----------------- GLOBAL STATUS ------------------------
		this.globalStatusLegendPanel.setText(sSongStatus);
		String songStatusDetailed = new String();
		if (listErrors.size() == 0) {
			globalStatusIcon.setImage(this.imageOK);
			songStatusDetailed = sSongValid;
		} else {
			globalStatusIcon.setImage(this.imageKO);
			songStatusDetailed = sSongInvalid.replace("{0}", String.valueOf(listErrors.size()));
		}
		globalStatusLabel.setText(songStatusDetailed);

		// ----------------- BUTTON ------------------------
		this.closeButton.setText(sClose);

		this.dialog.pack();
	}

	private String userMessage(TGMeasure measure, int voiceIndex, TGMeasureError err) {
		String sVoiceStatus = new String();
		String sVoiceIndex = new String();
		if (err == null) {
			sVoiceStatus = sVoiceValid;
			sVoiceIndex = String.valueOf(voiceIndex + 1);
		} else {
			sVoiceIndex = String.valueOf(err.getVoiceIndex() + 1);
			switch (err.getErrCode()) {
			case TGMeasureManager.VOICE_TOO_LONG:
				sVoiceStatus = sVoiceTooLong;
				break;
			case TGMeasureManager.VOICE_TOO_SHORT:
				sVoiceStatus = sVoiceTooShort;
				break;
			default:
				sVoiceStatus = sVoiceInvalid;
				break;
			}
		}
		sVoiceStatus = sVoiceStatus.replace("{0}", String.valueOf(measure.getTrack().getNumber()));
		sVoiceStatus = sVoiceStatus.replace("{1}", String.valueOf(measure.getNumber()));
		sVoiceStatus = sVoiceStatus.replace("{2}", sVoiceIndex);
		return sVoiceStatus;
	}

	private void createSyncProcesses() {
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});

		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});

		this.loadPropertiesProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});

	}

	@Override
	public void processEvent(TGEvent event) throws TGEventException {
		if (!this.isDisposed()) {
			if (TGUpdateEvent.EVENT_TYPE.equals(event.getEventType())) {
				int type = ((Integer) event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
				if (type == TGUpdateEvent.SELECTION) {
					this.updateItemsProcess.process();
				}
			} else if (TGSkinEvent.EVENT_TYPE.equals(event.getEventType())) {
				this.loadIconsProcess.process();
			} else if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
				this.loadPropertiesProcess.process();
			}
		}
	}

	public static TGMeasureErrorDialog getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMeasureErrorDialog.class.getName(),
				new TGSingletonFactory<TGMeasureErrorDialog>() {
					public TGMeasureErrorDialog createInstance(TGContext context) {
						return new TGMeasureErrorDialog(context);
					}
				});
	}

	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}

	public void dispose() {
		if (!isDisposed()) {
			this.dialog.dispose();
		}
	}

	private UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}

}
