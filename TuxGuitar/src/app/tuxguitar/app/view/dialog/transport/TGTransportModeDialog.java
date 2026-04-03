package app.tuxguitar.app.view.dialog.transport;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGBeatRange;

public class TGTransportModeDialog {

	protected static final int MIN_SELECTION = 1;
	protected static final int MAX_SELECTION = 500;

	private TGViewContext context;
	protected UIRadioButton simple;
	protected UICheckBox simpleLoop;
	protected UISpinner simplePercent;

	protected UIRadioButton custom;
	protected UISpinner customFrom;
	protected UISpinner customTo;
	protected UISpinner customIncrement;
	
	protected UIButton buttonOK;
	protected UIColorModel colorModelErr;
	protected UIColor colorErr;
	protected UIColor colorOK;

	protected UIDropDownSelect<Integer> loopSHeader;
	protected UIDropDownSelect<Integer> loopEHeader;

	public TGTransportModeDialog(TGViewContext context){
		this.context = context;
		this.colorModelErr = TGConfigManager.getInstance(context.getContext()).getColorModelConfigValue(TGConfigKeys.COLOR_INVALID_ENTRY);
	}

	public void show(){
		final MidiPlayerMode mode = MidiPlayer.getInstance(this.context.getContext()).getMode();
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		final TGBeatRange beats = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		boolean isSelectionActive = Boolean.TRUE.equals(this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE));
		this.colorErr = uiFactory.createColor(this.colorModelErr);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("transport.mode"));
		dialog.addDisposeListener(new UIDisposeListener() {
			@Override
			public void onDispose(UIDisposeEvent event) {
				if ((TGTransportModeDialog.this.colorErr != null) && !TGTransportModeDialog.this.colorErr.isDisposed()) {
					TGTransportModeDialog.this.colorErr.dispose();
				}
			}
		});

		// ----------------------------------------------------------------------

		//---Simple---
		this.simple = uiFactory.createRadioButton(dialog);
		this.simple.setText(TuxGuitar.getProperty("transport.mode.simple"));
		this.simple.setSelected(mode.getType() == MidiPlayerMode.TYPE_SIMPLE);
		this.simple.setFocus();
		dialogLayout.set(this.simple, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		RadioSelectionAdapter simpleAdapter = new RadioSelectionAdapter(this.simple);

		UITableLayout simpleLayout = new UITableLayout();
		UILegendPanel simpleGroup = uiFactory.createLegendPanel(dialog);
		simpleGroup.setLayout(simpleLayout);
		simpleGroup.setText(TuxGuitar.getProperty("transport.mode.simple"));
		simpleAdapter.addControl(simpleGroup);
		dialogLayout.set(simpleGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel simplePercentLabel = uiFactory.createLabel(simpleGroup);
		simplePercentLabel.setText(TuxGuitar.getProperty("transport.mode.simple.tempo-percent") + ":");
		simpleLayout.set(simplePercentLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		simpleAdapter.addControl(simplePercentLabel);

		this.simplePercent = uiFactory.createSpinner(simpleGroup);
		this.simplePercent.setMinimum(MIN_SELECTION);
		this.simplePercent.setMaximum(MAX_SELECTION);
		this.simplePercent.setValue(mode.getSimplePercent());
		simpleLayout.set(this.simplePercent, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		simpleAdapter.addControl(this.simplePercent);

		UILabel tempoSimplePercentLabel = uiFactory.createLabel(simpleGroup);
		tempoSimplePercentLabel.setText("%");
		simpleLayout.set(tempoSimplePercentLabel, 1, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		simpleAdapter.addControl(tempoSimplePercentLabel);

		this.simpleLoop = uiFactory.createCheckBox(simpleGroup);
		this.simpleLoop.setText(TuxGuitar.getProperty("transport.mode.simple.loop"));
		this.simpleLoop.setSelected(mode.isLoop());
		simpleLayout.set(this.simpleLoop, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);
		simpleAdapter.addControl(this.simpleLoop);

		//---Trainer---
		this.custom = uiFactory.createRadioButton(dialog);
		this.custom.setText(TuxGuitar.getProperty("transport.mode.trainer"));
		this.custom.setSelected(mode.getType() == MidiPlayerMode.TYPE_CUSTOM);
		dialogLayout.set(this.custom, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		RadioSelectionAdapter customAdapter = new RadioSelectionAdapter(this.custom);

		UITableLayout customLayout = new UITableLayout();
		UILegendPanel customGroup = uiFactory.createLegendPanel(dialog);
		customGroup.setLayout(customLayout);
		customGroup.setText(TuxGuitar.getProperty("transport.mode.trainer"));
		customAdapter.addControl(customGroup);
		dialogLayout.set(customGroup, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel tempoLabel = uiFactory.createLabel(customGroup);
		tempoLabel.setText(TuxGuitar.getProperty("composition.tempo") + ":");
		customLayout.set(tempoLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		customAdapter.addControl(tempoLabel);

		this.customFrom = uiFactory.createSpinner(customGroup);
		this.customFrom.setMinimum(MIN_SELECTION);
		this.customFrom.setMaximum(MAX_SELECTION);
		this.customFrom.setValue(mode.getCustomPercentFrom());
		customAdapter.addControl(this.customFrom);
		customLayout.set(this.customFrom, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel tempoPercentLabel = uiFactory.createLabel(customGroup);
		tempoPercentLabel.setText("%");
		customLayout.set(tempoPercentLabel, 1, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		customAdapter.addControl(tempoPercentLabel);

		UILabel tempoToLabel = uiFactory.createLabel(customGroup);
		tempoToLabel.setText(TuxGuitar.getProperty("edit.to"));
		customLayout.set(tempoToLabel, 1, 4, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);
		customAdapter.addControl(tempoToLabel);

		this.customTo = uiFactory.createSpinner(customGroup);
		this.customTo.setMinimum(MIN_SELECTION);
		this.customTo.setMaximum(MAX_SELECTION);
		this.customTo.setValue(mode.getCustomPercentTo());
		customLayout.set(this.customTo, 1, 5, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		customAdapter.addControl(this.customTo);

		UILabel tempoToPercentLabel = uiFactory.createLabel(customGroup);
		tempoToPercentLabel.setText("%");
		customLayout.set(tempoToPercentLabel, 1, 6, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		customAdapter.addControl(tempoToPercentLabel);

		UILabel incrementLabel = uiFactory.createLabel(customGroup);
		incrementLabel.setText(TuxGuitar.getProperty("transport.mode.trainer.increment-description") + ":");
		customLayout.set(incrementLabel, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true, 1, 4);
		customAdapter.addControl(incrementLabel);

		this.customIncrement = uiFactory.createSpinner(customGroup);
		this.customIncrement.setMinimum(MIN_SELECTION);
		this.customIncrement.setMaximum(MAX_SELECTION);
		this.customIncrement.setValue(mode.getCustomPercentIncrement());
		customLayout.set(this.customIncrement, 2, 5, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		customAdapter.addControl(this.customIncrement);

		UILabel incrementPercentLabel = uiFactory.createLabel(customGroup);
		incrementPercentLabel.setText("%");
		customLayout.set(incrementPercentLabel, 2, 6, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		customAdapter.addControl(incrementPercentLabel);

		SpinnerSelectionAdapter spinnerAdapter = new SpinnerSelectionAdapter(this.customFrom, this.customTo, this.customIncrement);
		this.customFrom.addSelectionListener(spinnerAdapter);
		this.customTo.addSelectionListener(spinnerAdapter);
		this.customIncrement.addSelectionListener(spinnerAdapter);

		//--- Loop Range ---
		MHeaderRangeStatus mHeaderRangeStatus = new MHeaderRangeStatus(this.simple, this.simpleLoop, this.custom);

		UITableLayout rangeLayout = new UITableLayout();
		UILegendPanel rangeGroup = uiFactory.createLegendPanel(dialog);
		rangeGroup.setLayout(rangeLayout);
		rangeGroup.setText(TuxGuitar.getProperty("transport.mode.loop-range"));
		dialogLayout.set(rangeGroup, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		mHeaderRangeStatus.addControl(rangeGroup);

		UILabel loopSLabel = uiFactory.createLabel(rangeGroup);
		loopSLabel.setText(TuxGuitar.getProperty("transport.mode.loop-range.from") + ":");
		rangeLayout.set(loopSLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		mHeaderRangeStatus.addControl(loopSLabel);

		this.loopSHeader = uiFactory.createDropDownSelect(rangeGroup);
		rangeLayout.set(this.loopSHeader, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		mHeaderRangeStatus.addControl(this.loopSHeader);

		UILabel loopELabel = uiFactory.createLabel(rangeGroup);
		loopELabel.setText(TuxGuitar.getProperty("transport.mode.loop-range.to") + ":");
		rangeLayout.set(loopELabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		mHeaderRangeStatus.addControl(loopELabel);

		this.loopEHeader = uiFactory.createDropDownSelect(rangeGroup);
		rangeLayout.set(this.loopEHeader, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		mHeaderRangeStatus.addControl(this.loopEHeader);

		if (isSelectionActive && beats!= null && !beats.isEmpty()) {
			mode.setLoopSHeader(beats.firstMeasure().getNumber());
			mode.setLoopEHeader(beats.lastMeasure().getNumber());
		}

		MHeaderComboController mHeaderController = new MHeaderComboController(this.loopSHeader, this.loopEHeader);
		mHeaderController.updateLoopSHeader( mode.getLoopSHeader() );
		mHeaderController.updateLoopEHeader( mode.getLoopSHeader() , mode.getLoopEHeader() );
		mHeaderController.appendListener();

		simpleAdapter.update();
		customAdapter.update();
		mHeaderRangeStatus.update();

		// ------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 6, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		this.buttonOK = uiFactory.createButton(buttons);
		this.buttonOK.setText(TuxGuitar.getProperty("ok"));
		this.buttonOK.setDefaultButton();
		this.buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				changeTransportMode();
				dialog.dispose();
			}
		});
		buttonsLayout.set(this.buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		this.colorOK = this.customFrom.getBgColor();
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void changeTransportMode() {
		Integer type = (this.custom.isSelected() ? MidiPlayerMode.TYPE_CUSTOM : MidiPlayerMode.TYPE_SIMPLE );
		Boolean loop = (type == MidiPlayerMode.TYPE_CUSTOM || (type == MidiPlayerMode.TYPE_SIMPLE && this.simpleLoop.isSelected()));
		Integer simplePercent = this.simplePercent.getValue();
		Integer loopSHeader = this.loopSHeader.getSelectedValue();
		Integer loopEHeader = this.loopEHeader.getSelectedValue();

		// move caret to loop start if loop defined
		if (loop && loopSHeader != null) {
			TGTrack track = (TGTrack) TGTransportModeDialog.this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			TGBeat beat = track.getMeasure(loopSHeader > 0 ? loopSHeader-1 : 0).getBeat(0);
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGMoveToAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT,beat);
			tgActionProcessor.process();
		}

		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGTransportModeAction.NAME);
		MidiPlayerMode mode = new MidiPlayerMode();
		mode.setType(type);
		mode.setLoop(loop);
		mode.setSimplePercent(simplePercent != null ? simplePercent : MidiPlayerMode.SIMPLE_DEFAULT_TEMPO_PERCENT);
		mode.setCustomPercentFrom(this.customFrom.getValue());
		mode.setCustomPercentTo(this.customTo.getValue());
		mode.setCustomPercentIncrement(this.customIncrement.getValue());
		mode.setLoopSHeader(loop && loopSHeader != null ? loopSHeader : -1 );
		mode.setLoopEHeader(loop && loopEHeader != null ? loopEHeader : -1 );
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_PLAYER_MODE, mode);
		tgActionProcessor.process();

		TGDocument document = TGDocumentListManager.getInstance(context.getContext()).findCurrentDocument();
		if (document != null) {
			document.setMidiPlayerMode(mode);
		}
	}

	private class RadioSelectionAdapter implements UISelectionListener {
		private UIRadioButton control;
		private List<UIControl> controls;

		public RadioSelectionAdapter(UIRadioButton control) {
			this.controls = new ArrayList<UIControl>();
			this.control = control;
			this.control.addSelectionListener(this);
		}

		public void addControl(UIControl control){
			this.controls.add(control);
		}

		public void update(){
			boolean enabled = this.control.isSelected();

			for(UIControl control : this.controls) {
				control.setEnabled(enabled);
			}
		}

		public void onSelect(UISelectionEvent event) {
			this.update();
		}
	}

	private class SpinnerSelectionAdapter implements UISelectionListener {

		private UISpinner to;
		private UISpinner from;
		private UISpinner increment;

		public SpinnerSelectionAdapter(UISpinner from, UISpinner to, UISpinner increment) {
			this.from = from;
			this.to = to;
			this.increment = increment;
		}

		public void onSelect(UISelectionEvent event) {
			boolean ok = true;
			if (event.getComponent().equals(this.from)) {
				if( this.from.getValue() < MIN_SELECTION){
					this.from.setValue(MIN_SELECTION);
				}
				if (this.from.getValue() >= this.to.getValue()) {
					// invalid
					ok = false;
					this.from.setBgColor(TGTransportModeDialog.this.colorErr);
					this.to.setBgColor(TGTransportModeDialog.this.colorOK);
				}
			} else if (event.getComponent().equals(this.to)) {
				if (this.to.getValue() > MAX_SELECTION){
					this.to.setValue(MAX_SELECTION);
				}
				if(this.to.getValue() <= this.from.getValue()) {
					// invalid
					ok = false;
					this.to.setBgColor(TGTransportModeDialog.this.colorErr);
					this.from.setBgColor(TGTransportModeDialog.this.colorOK);
				}
			}
			if (ok) {
				this.from.setBgColor(TGTransportModeDialog.this.colorOK);
				this.to.setBgColor(TGTransportModeDialog.this.colorOK);
				if (this.increment.getValue() > (this.to.getValue() - this.from.getValue())) {
					ok = false;
					this.increment.setBgColor(colorErr);
				}
				else {
					this.increment.setBgColor(colorOK);
				}
			}
			TGTransportModeDialog.this.buttonOK.setEnabled(ok);
		}
	}

	private class MHeaderRangeStatus implements UISelectionListener {

		private List<UIControl> controls;
		private boolean enabled;

		private UIRadioButton simpleMode;
		private UICheckBox simpleLoop;
		private UIRadioButton customLoop;

		public MHeaderRangeStatus(UIRadioButton simpleMode, UICheckBox simpleLoop, UIRadioButton customLoop) {
			this.controls = new ArrayList<UIControl>();
			this.enabled = false;
			this.simpleMode = simpleMode;
			this.simpleLoop = simpleLoop;
			this.customLoop = customLoop;
			this.simpleMode.addSelectionListener(this);
			this.simpleLoop.addSelectionListener(this);
			this.customLoop.addSelectionListener(this);
		}

		public void addControl(UIControl control){
			this.controls.add(control);
		}

		public void update(){
			// Check enabled
			this.enabled = this.customLoop.isSelected();
			if( !this.enabled ){
				if( this.simpleMode.isSelected() ){
					this.enabled = this.simpleLoop.isSelected();
				}
			}

			// Update controls
			for(UIControl uiControl : this.controls) {
				uiControl.setEnabled( this.enabled );
			}
		}

		public void onSelect(UISelectionEvent event) {
			this.update();
		}
	}

	private class MHeaderComboController {

		protected UIDropDownSelect<Integer> loopSHeader;
		protected UIDropDownSelect<Integer> loopEHeader;

		public MHeaderComboController(UIDropDownSelect<Integer> loopSHeader, UIDropDownSelect<Integer> loopEHeader){
			this.loopSHeader = loopSHeader;
			this.loopEHeader = loopEHeader;
		}

		public void updateLoopSHeader( int sHeader ){
			TGSong song = TGTransportModeDialog.this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			this.loopSHeader.removeItems();
			this.loopSHeader.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("transport.mode.loop-range.from-default"), -1));
			for(int i = 0; i < song.countMeasureHeaders() ; i ++){
				TGMeasureHeader header = song.getMeasureHeader( i );
				this.loopSHeader.addItem(new UISelectItem<Integer>(this.getItemText(header), header.getNumber()));
			}
			this.loopSHeader.setSelectedValue(sHeader);
		}

		public void updateLoopEHeader(Integer sHeader, Integer eHeader){
			TGSong song = TGTransportModeDialog.this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			this.loopEHeader.removeItems();
			this.loopEHeader.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("transport.mode.loop-range.to-default"), -1));
			for(int i = 0; i < song.countMeasureHeaders() ; i ++){
				TGMeasureHeader header = song.getMeasureHeader( i );
				if( sHeader == null || header.getNumber() >= sHeader ){
					this.loopEHeader.addItem(new UISelectItem<Integer>(this.getItemText(header), header.getNumber()));
				}
			}
			this.loopEHeader.setSelectedValue(eHeader);
		}

		public void updateLoopEHeader(){
			Integer sHeader = this.loopSHeader.getSelectedValue();
			Integer eHeader = this.loopEHeader.getSelectedValue();
			if( eHeader != null && sHeader != null && sHeader > eHeader && eHeader != -1 ){
				eHeader = sHeader;
			}
			this.updateLoopEHeader(sHeader , eHeader);
		}

		public String getItemText(TGMeasureHeader header){
			String text = ("#" + header.getNumber());
			if( header.hasMarker() ){
				text += (" (" + header.getMarker().getTitle() + ")");
			}
			return text;
		}

		public void appendListener(){
			this.loopSHeader.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					updateLoopEHeader();
				}
			});
		}
	}
}
