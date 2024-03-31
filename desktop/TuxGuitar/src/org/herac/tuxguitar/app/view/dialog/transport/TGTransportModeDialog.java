package org.herac.tuxguitar.app.view.dialog.transport;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportModeAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.player.base.MidiPlayerLoop;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGBeatRange;

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
	
	protected UIDropDownSelect<Integer> loopSHeader;
	protected UIDropDownSelect<Integer> loopEHeader;

	protected UIDropDownSelect<MidiPlayerLoop> presetSelect;
	protected String presetSetName;
	
	public TGTransportModeDialog(TGViewContext context){
		this.context = context;
	}
	
	public void show(){
		final MidiPlayerMode mode = MidiPlayer.getInstance(this.context.getContext()).getMode();
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		final TGBeatRange beats = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		// final MidiPlayerLoop[] loops = new MidiPlayerLoop[5];


		// for (int i=0; i < loops.length; i++) {
		// 	loops[i] = new MidiPlayerLoop();
		// }

		boolean isSelectionActive = Boolean.TRUE.equals(this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE));
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("transport.mode"));
		
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


		UILabel presetLabel = uiFactory.createLabel(rangeGroup);
		presetLabel.setText(TuxGuitar.getProperty("transport.mode.preset") + ":");
		rangeLayout.set(presetLabel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		mHeaderRangeStatus.addControl(presetLabel);
		

		UILabel presetNameLabel = uiFactory.createLabel(rangeGroup);
		presetNameLabel.setText("name label" + ":");
		rangeLayout.set(presetNameLabel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		mHeaderRangeStatus.addControl(presetNameLabel);
		
		UITextField presetNameText = uiFactory.createTextField(rangeGroup);
		presetNameText.setText("");
		rangeLayout.set(presetNameText, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);




		this.presetSelect = uiFactory.createDropDownSelect(rangeGroup);
		rangeLayout.set(this.presetSelect, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		mHeaderRangeStatus.addControl(this.presetSelect);
		// this.presetSelect.addSelectionListener(new UISelectionListener() {
		// 	public void onSelect(UISelectionEvent event) {
		// 		// load beginning and end
		// 		System.out.print("\n\nselected" + mode.getLoopSHeader() + " " + mode.getLoopEHeader());
				
		// 		System.out.print("\n\nselected" + presetSelect.getSelectedValue().getTitle());
		// 	}
		// });




		UIButton buttonLoad = uiFactory.createButton(rangeGroup);
		buttonLoad.setText(TuxGuitar.getProperty("transport.mode.load"));
		buttonLoad.setDefaultButton();


		UIButton buttonSave = uiFactory.createButton(rangeGroup);
		buttonSave.setText(TuxGuitar.getProperty("transport.mode.save"));
		buttonSave.setDefaultButton();
		buttonSave.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				System.out.print("\n\nsaving beginning and endsss" + presetNameText.getText() + "" +  loopSHeader.getSelectedValue() + " " + loopSHeader.getSelectedValue());

				MidiPlayerLoop port = new MidiPlayerLoop();
				port.setTitle(presetNameText.getText());
				port.setStart(loopSHeader.getSelectedValue());
				port.setEnd(loopEHeader.getSelectedValue());

				UISelectItem<MidiPlayerLoop> item = new UISelectItem<MidiPlayerLoop>(port.getTitle(), port);

				presetSelect.addItem(item);

			}
		});
		rangeLayout.set(buttonSave, 5, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		mHeaderRangeStatus.addControl(buttonSave);
		
		if (isSelectionActive && beats!= null && !beats.isEmpty()) {
			mode.setLoopSHeader(beats.firstMeasure().getNumber());
			mode.setLoopEHeader(beats.lastMeasure().getNumber());
		}
		
		MHeaderComboController mHeaderController = new MHeaderComboController(this.loopSHeader, this.loopEHeader);
		mHeaderController.updateLoopSHeader( mode.getLoopSHeader() );
		mHeaderController.updateLoopEHeader( mode.getLoopSHeader() , mode.getLoopEHeader() );
		mHeaderController.appendListener();

		buttonLoad.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				// load beginning and end
				System.out.print("\n\nloading beginning and end" + mode.getLoopSHeader() + " " + mode.getLoopEHeader());

				System.out.print("\n\nloading saved val" + presetSelect.getSelectedItem().getValue().getLoopSHeader() + " " + presetSelect.getSelectedItem().getValue().getLoopEHeader());
				mHeaderController.updateLoopSHeader( presetSelect.getSelectedItem().getValue().getLoopSHeader() );
				mHeaderController.updateLoopEHeader( presetSelect.getSelectedItem().getValue().getLoopSHeader(), presetSelect.getSelectedItem().getValue().getLoopEHeader() );
			}
		});
		rangeLayout.set(buttonLoad, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		mHeaderRangeStatus.addControl(buttonLoad);
		
		simpleAdapter.update();
		customAdapter.update();
		mHeaderRangeStatus.update();
		
		// ------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 6, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				changeTransportMode();
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
		buttonsLayout.set(buttonCancel, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void changeTransportMode() {
		Integer type = (this.custom.isSelected() ? MidiPlayerMode.TYPE_CUSTOM : MidiPlayerMode.TYPE_SIMPLE );
		Boolean loop = (type == MidiPlayerMode.TYPE_CUSTOM || (type == MidiPlayerMode.TYPE_SIMPLE && this.simpleLoop.isSelected()));
		Integer simplePercent = this.simplePercent.getValue();
		Integer loopSHeader = this.loopSHeader.getSelectedValue();
		Integer loopEHeader = this.loopEHeader.getSelectedValue();
		
		// move caret to loop start if loop defined
		if (loop && loopSHeader != null && loopSHeader>0) {
			TGTrack track = (TGTrack) TGTransportModeDialog.this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			TGBeat beat = track.getMeasure(loopSHeader-1).getBeat(0);
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGMoveToAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT,beat);
			tgActionProcessor.process();
		}
		
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGTransportModeAction.NAME);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_TYPE, type);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_LOOP, loop);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_SIMPLE_PERCENT, (simplePercent != null ? simplePercent : MidiPlayerMode.DEFAULT_TEMPO_PERCENT));
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_CUSTOM_PERCENT_FROM, this.customFrom.getValue());
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_CUSTOM_PERCENT_TO, this.customTo.getValue());
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_CUSTOM_PERCENT_INCREMENT, this.customIncrement.getValue());
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_LOOP_S_HEADER, (loop && loopSHeader != null ? loopSHeader : -1 ));
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_LOOP_E_HEADER, (loop && loopEHeader != null ? loopEHeader : -1 ));
		tgActionProcessor.process();
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
			if( event.getComponent().equals(this.from)){
				if( this.from.getValue() < MIN_SELECTION){
					this.from.setValue(MIN_SELECTION);
				}else if(this.from.getValue() >= this.to.getValue()){
					this.from.setValue(this.to.getValue() - 1);
				}
			}else if(event.getComponent().equals(this.to)){
				if( this.to.getValue() <= this.from.getValue()){
					this.to.setValue(this.from.getValue() + 1);
				}else if(this.to.getValue() > MAX_SELECTION){
					this.to.setValue(MAX_SELECTION);
				}
			}
			if( this.increment.getValue() > (this.to.getValue() - this.from.getValue())){
				this.increment.setValue(this.to.getValue() - this.from.getValue());
			}
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
			System.out.print("\n\n\n\nprintinggg " + this.customLoop.isSelected());

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

	private class MHeaderPresetsController {
		protected UIDropDownSelect<MidiPlayerLoop> presetSelect;
		protected MidiPlayerLoop[] presetList;

		public MHeaderPresetsController(UIDropDownSelect<MidiPlayerLoop> presetSelect){
			this.presetSelect = presetSelect;

			this.presetList = new MidiPlayerLoop[5];

			for (int i=0; i < presetList.length; i++) {
				this.presetList[i] = new MidiPlayerLoop();
			}

			this.presetSelect.removeItems();

			for (int i=0; i < presetList.length; i++) {
				this.presetSelect.addItem(new UISelectItem<MidiPlayerLoop>(presetList[i].getTitle()));
			}
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
			if( eHeader != null && sHeader != null && sHeader > eHeader ){
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
