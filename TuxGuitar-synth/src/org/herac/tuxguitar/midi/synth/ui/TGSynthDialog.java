package org.herac.tuxguitar.midi.synth.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.TGProgram;
import org.herac.tuxguitar.midi.synth.TGProgramElement;
import org.herac.tuxguitar.midi.synth.TGProgramPropertiesUtil;
import org.herac.tuxguitar.midi.synth.TGSynthChannel;
import org.herac.tuxguitar.midi.synth.TGSynthChannelProperties;
import org.herac.tuxguitar.midi.synth.TGSynthManager;
import org.herac.tuxguitar.midi.synth.TGSynthesizer;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGSynthDialog implements TGChannelSettingsDialog, TGEventListener {
	
	private TGContext context;
	private TGSynthesizer synthesizer;
	private TGChannel channel;
	private TGSong song;
	
	private UIWindow dialog;
	private UIButton buttonReceiverAdd;
	private UIButton buttonReceiverEdit;
	private UIButton buttonOutputAdd;
	private UIButton buttonOutputEdit;
	private UIButton buttonOutputDelete;
	private UIReadOnlyTextField receiver;
	private UITable<TGProgramElement> outputs;
	
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateProcess;
	
	private Map<TGAudioProcessor, TGAudioProcessorUI> processorsUI;
	
	public TGSynthDialog(TGContext context, TGSynthesizer synthesizer, TGChannel channel, TGSong song){
		this.context = context;
		this.synthesizer = synthesizer;
		this.channel = channel;
		this.song = song;
		this.processorsUI = new HashMap<TGAudioProcessor, TGAudioProcessorUI>();
		this.createSyncProcesses();
	}
	
	public void open(final UIWindow parent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);
		
		// ----------------------------------------------------------------------
		UITableLayout receiverLayout = new UITableLayout();
		UIPanel receiverPanel = uiFactory.createPanel(this.dialog, false);
		receiverPanel.setLayout(receiverLayout);
		dialogLayout.set(receiverPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.receiver = uiFactory.createReadOnlyTextField(receiverPanel);
		receiverLayout.set(this.receiver, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		// ----------------------------------------------------------------------
		this.buttonReceiverAdd = uiFactory.createButton(receiverPanel);
		this.buttonReceiverAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onAddReceiver();
			}
		});
		receiverLayout.set(this.buttonReceiverAdd, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		this.buttonReceiverEdit = uiFactory.createButton(receiverPanel);
		this.buttonReceiverEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onEditReceiver();
			}
		});
		receiverLayout.set(this.buttonReceiverEdit, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		// ----------------------------------------------------------------------
		
		UITableLayout outputsLayout = new UITableLayout();
		UIPanel compositeTable = uiFactory.createPanel(this.dialog, false);
		compositeTable.setLayout(outputsLayout);
		dialogLayout.set(compositeTable, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.outputs = uiFactory.createTable(compositeTable, true);
		this.outputs.setColumns(1);
		this.outputs.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				onEditElement(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});
		outputsLayout.set(this.outputs, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		outputsLayout.set(this.outputs, UITableLayout.PACKED_WIDTH, 250f);
		outputsLayout.set(this.outputs, UITableLayout.PACKED_HEIGHT, 200f);
		
		// ------------------BUTTONS--------------------------
		UITableLayout outputsButtonsLayout = new UITableLayout(0f);
		UIPanel compositeButtons = uiFactory.createPanel(compositeTable, false);
		compositeButtons.setLayout(outputsButtonsLayout);
		outputsLayout.set(compositeButtons, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		
		this.buttonOutputAdd = uiFactory.createButton(compositeButtons);
		this.buttonOutputAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onAddOutput();
			}
		});
		
		this.buttonOutputEdit = uiFactory.createButton(compositeButtons);
		this.buttonOutputEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onEditElement(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});

		this.buttonOutputDelete = uiFactory.createButton(compositeButtons);
		this.buttonOutputDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onRemoveOutput(TGSynthDialog.this.outputs.getSelectedValue());
			}
		});
		
		outputsButtonsLayout.set(this.buttonOutputAdd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		outputsButtonsLayout.set(this.buttonOutputDelete, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, false, false);
		outputsButtonsLayout.set(this.buttonOutputEdit, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, false, true);
		
		this.loadIcons();
		this.loadProperties();
		this.updateItems();
		
		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
			}
		});
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void close() {
		if( this.isOpen()) {
			this.dialog.dispose();
		}
	}
	
	public boolean isOpen(){
		return (this.dialog != null && !this.dialog.isDisposed());
	}
	
	public void openReceiverSelectionDialog() {
		final List<String> supportedTypes = TGSynthManager.getInstance(this.context).getAllSupportedMidiTypes();
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(this.dialog, false, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("synth-host.ui.midi.processor.dialog.title"));
		
		// ----------------------------------------------------------------------
		UITableLayout typeGroupLayout = new UITableLayout();
		UILegendPanel typeGroup = uiFactory.createLegendPanel(dialog);
		typeGroup.setLayout(typeGroupLayout);
		typeGroup.setText(TuxGuitar.getProperty("synth-host.ui.midi.processor.tip"));
		dialogLayout.set(typeGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel typeLabel = uiFactory.createLabel(typeGroup);
		typeLabel.setText(TuxGuitar.getProperty("synth-host.ui.midi.processor.type"));
		typeGroupLayout.set(typeLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<String> typeCombo = uiFactory.createDropDownSelect(typeGroup);
		typeGroupLayout.set(typeCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		for(String supportedType : supportedTypes){
			typeCombo.addItem(new UISelectItem<String>(supportedType, supportedType));
		}
		if( supportedTypes.size() > 0 ){
			typeCombo.setSelectedValue(supportedTypes.get(0));
		}
		
		//------------------BUTTONS----------------------------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onReceiverSelected(typeCombo.getSelectedValue());
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
		// ----------------------------------------------------------------------
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void openOutputSelectionDialog() {
		final List<String> supportedTypes = TGSynthManager.getInstance(this.context).getAllSupportedAudioTypes();
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(this.dialog, false, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("synth-host.ui.audio.processor.dialog.title"));
		
		// ----------------------------------------------------------------------
		UITableLayout typeGroupLayout = new UITableLayout();
		UILegendPanel typeGroup = uiFactory.createLegendPanel(dialog);
		typeGroup.setLayout(typeGroupLayout);
		typeGroup.setText(TuxGuitar.getProperty("synth-host.ui.audio.processor.tip"));
		dialogLayout.set(typeGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel typeLabel = uiFactory.createLabel(typeGroup);
		typeLabel.setText(TuxGuitar.getProperty("synth-host.ui.audio.processor.type"));
		typeGroupLayout.set(typeLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<String> typeCombo = uiFactory.createDropDownSelect(typeGroup);
		typeGroupLayout.set(typeCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		for(String supportedType : supportedTypes){
			typeCombo.addItem(new UISelectItem<String>(supportedType, supportedType));
		}
		if( supportedTypes.size() > 0 ){
			typeCombo.setSelectedValue(supportedTypes.get(0));
		}
		
		//------------------BUTTONS----------------------------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onOutputSelected(typeCombo.getSelectedValue());
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
		// ----------------------------------------------------------------------
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void openProcessorUI(TGProgramElement element) {
		TGAudioProcessorUI ui = this.getProcessorUI(element);
		if( ui != null && !ui.isOpen()) {
			ui.open(this.dialog);
		}
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}
	
	public void onAddReceiver() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				openReceiverSelectionDialog();
			}
		});
	}
	
	public void onAddOutput() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				openOutputSelectionDialog();
			}
		});
	}
	
	public void onEditElement(final TGProgramElement element) {
		if( element != null ) {
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					openProcessorUI(element);
				}
			});
		}
	}
	
	public void onRemoveOutput(TGProgramElement output) {
		if( output != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.removeOutput(output);
				
				this.onProgramUpdated(program, false);
			}
		}
	}
	
	public void onOutputSelected(String type) {
		if( type != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {				
				TGProgramElement element = new TGProgramElement();
				element.setId("custom-" + System.currentTimeMillis());
				element.setType(type);
				
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.addOutput(element);
				
				this.onProgramUpdated(program, false);
			}
		}
	}
	
	public void onReceiverSelected(String type) {
		if( type != null ) {
			TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
			if( channel != null ) {
				TGProgramElement element = null;
				if( type != null ) {
					element = new TGProgramElement();
					element.setId("custom-" + System.currentTimeMillis());
					element.setType(type);
				}
				
				TGProgram program = new TGProgram();
				program.copyFrom(channel.getProgram());
				program.setReceiver(element);
				
				this.onProgramUpdated(program, false);
			}
		}
	}
	
	public void onEditReceiver() {
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null && channel.getProgram().getReceiver() != null ) {
			this.onEditElement(channel.getProgram().getReceiver());
		}
	}
	
	public void onProgramUpdated(TGProgram program, boolean appliedChanges) {
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		TGProgramPropertiesUtil.setProgram(properties, TGSynthChannel.CUSTOM_PROGRAM_PREFIX, program);
		TGFactory factory = TGDocumentManager.getInstance(TGSynthDialog.this.context).getSongManager().getFactory();
		
		List<TGChannelParameter> parameters = new ArrayList<TGChannelParameter>();
		for(Entry<String, String> entry : properties.getProperties().entrySet()) {
			TGChannelParameter parameter = factory.newChannelParameter();
			parameter.setKey(entry.getKey());
			parameter.setValue(entry.getValue());
			parameters.add(parameter);
		}
		this.callUpdateChannelParametersAction(parameters, appliedChanges);
	}
	
	public void onProcessorUpdated(TGProgramElement source, TGAudioProcessor processor, boolean appliedChanges) {
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null ) {
			TGProgram program = new TGProgram();
			program.copyFrom(channel.getProgram());
			
			TGProgramElement element = null;
			if( program.getReceiver() != null && program.getReceiver().equals(source) ) {
				element = program.getReceiver();
			} else {
				int outputCount = program.countOutputs();
				for(int i = 0 ; i < outputCount; i ++) {
					TGProgramElement output = program.getOutput(i);
					if( output.equals(source) ) {
						element = output;
					}
				}
			}
			
			if( element != null ) {
				processor.storeParameters(element.getParameters());
				
				this.onProgramUpdated(program, appliedChanges);
				
				if( appliedChanges ) {
					channel.getProgram().copyFrom(program);
				}
			}
		}
	}
	
	public String getProcessorLabel(TGProgramElement element) {
		TGAudioProcessorUI ui = this.getProcessorUI(element);
		if( ui != null ) {
			String label = ui.getLabel();
			if( label != null ) {
				return label;
			}
		}
		return element.getType();
	}
	
	public TGAudioProcessorUI getProcessorUI(TGProgramElement element) {
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null && channel.getProcessor() != null ) {
			TGAudioProcessor processor = channel.getProcessor().getAudioProcessor(element);
			if( processor != null ) {
				return this.getProcessorUI(element, processor);
			}
		}
		return null;
	}
	
	public TGAudioProcessorUI getProcessorUI(TGProgramElement element, TGAudioProcessor processor) {
		if( this.processorsUI.containsKey(processor) ) {
			return this.processorsUI.get(processor);
		}
		
		TGAudioProcessorUICallback callback = this.createProcessorCallback(element, processor);
		TGAudioProcessorUI ui = TGAudioProcessorUIManager.getInstance(this.context).createUI(element.getType(), processor, callback);
		if( ui != null ) {
			this.processorsUI.put(processor, ui);
		}
		return ui;
	}
	
	public TGAudioProcessorUICallback createProcessorCallback(final TGProgramElement element, final TGAudioProcessor processor) {
		return new TGAudioProcessorUICallback() {
			public void onChange(boolean appliedChanges) {
				onProcessorUpdated(element, processor, appliedChanges);
			}
		};
	}
	
	public void loadMidiReceiver(TGProgram program) {
		this.receiver.setText(program != null && program.getReceiver() != null ? this.getProcessorLabel(program.getReceiver()) : "");
	}
	
	public void loadOutputsItems(TGProgram program) {
		TGProgramElement selection = this.outputs.getSelectedValue();
		this.outputs.setIgnoreEvents(true);
		this.outputs.removeItems();
		
		if( program != null ) {
			int outputCount = program.countOutputs();
			for(int i = 0 ; i < outputCount; i ++) {
				TGProgramElement output = program.getOutput(i);
				UITableItem<TGProgramElement> item = new UITableItem<TGProgramElement>(output);
				item.setText(0, this.getProcessorLabel(output));
				this.outputs.addItem(item);
			}
		}
		
		if( selection != null ) {
			this.outputs.setSelectedValue(selection);
		}
		this.outputs.setIgnoreEvents(false);
	}
	
	public void loadChannel() {
		TGProgram program = null;
		TGSynthChannel channel = this.synthesizer.getChannelById(this.channel.getChannelId());
		if( channel != null ) {
			program = channel.getProgram();
		}
		this.loadMidiReceiver(program);
		this.loadOutputsItems(program);
	}
	
	public void loadProperties(){
		if( this.isOpen()){
			this.dialog.setText(TuxGuitar.getProperty("synth-host.ui.dialog.title"));
			
			this.buttonReceiverAdd.setToolTipText(TuxGuitar.getProperty("synth-host.ui.midi.receiver.add"));
			this.buttonReceiverEdit.setToolTipText(TuxGuitar.getProperty("synth-host.ui.midi.receiver.edit"));
			this.buttonOutputAdd.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.add"));
			this.buttonOutputEdit.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.edit"));
			this.buttonOutputDelete.setToolTipText(TuxGuitar.getProperty("synth-host.ui.audio.processor.remove"));
		}
	}
	
	public void loadIcons(){
		if( this.isOpen()){
			this.buttonReceiverAdd.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListAdd());
			this.buttonReceiverEdit.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListEdit());
			this.buttonOutputAdd.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListAdd());
			this.buttonOutputEdit.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListEdit());
			this.buttonOutputDelete.setImage(TGIconManager.getInstance(this.synthesizer.getContext()).getListRemove());
		}
	}
	
	public void updateItems() {
		if( this.isOpen()){
			this.loadChannel();
		}
	}
	
	public void createSyncProcesses() {
		this.updateProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
		this.loadIconsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateProcess.process();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
	}
	
	public void callUpdateChannelParametersAction(List<TGChannelParameter> parameters, boolean appliedChanges) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGUpdateChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel);
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PARAMETERS, parameters);
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_APPLIED_CHANGES, appliedChanges);
		tgActionProcessor.process();
	}
}
