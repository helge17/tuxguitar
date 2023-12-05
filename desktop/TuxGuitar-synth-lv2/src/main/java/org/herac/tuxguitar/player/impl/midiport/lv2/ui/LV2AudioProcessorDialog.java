package org.herac.tuxguitar.player.impl.midiport.lv2.ui;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.player.impl.midiport.lv2.LV2AudioProcessor;
import org.herac.tuxguitar.player.impl.midiport.lv2.LV2ParamsEvent;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2ControlPortInfo;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class LV2AudioProcessorDialog implements TGEventListener {
	
	private LV2AudioProcessor processor;
	private TGAudioProcessorUICallback callback;
	private TGContext context;
	private UIWindow dialog;
	private UIScale[] scaleParameterValue;
	private UILabel[] labelParameterValue; 
	private List<Integer> portIndices;
	
	public LV2AudioProcessorDialog(TGContext context, LV2AudioProcessor processor, TGAudioProcessorUICallback callback) {
		this.context = context;
		this.processor = processor;
		this.callback = callback;
	}
	
	public void open(final UIWindow parent) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				LV2AudioProcessorDialog.this.openInCurrentThread(parent);
			}
		});
	}
	
	public void openInCurrentThread(UIWindow parent) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("tuxguitar-synth-lv2.dialog.title", new String[] {this.processor.getPlugin().getName()}));
		
		//-------------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(this.dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tuxguitar-synth-lv2.dialog.params.tip"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-------------------------------------------------------------------------
		final UIScrollBarPanel scrollBarPanel = uiFactory.createScrollBarPanel(group, true, false, false);
		scrollBarPanel.setLayout(new UIScrollBarPanelLayout(false, true, true, true, false, true));
		scrollBarPanel.getVScroll().setIncrement(10);
		scrollBarPanel.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				scrollBarPanel.layout();
			}
		});
		
		groupLayout.set(scrollBarPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, 400f, null);
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(scrollBarPanel, false);
		panel.setLayout(panelLayout);
		
		this.portIndices = this.processor.getPlugin().getControlPortIndices();
		this.scaleParameterValue = new UIScale[this.portIndices.size()];
		this.labelParameterValue = new UILabel[this.portIndices.size()];
		for(int i = 0 ; i < this.portIndices.size(); i ++ ) {
			final int index = i;
			final int portIndex = this.portIndices.get(index);
			final LV2ControlPortInfo info = this.processor.getPlugin().getControlPortInfo(portIndex);
			
			UILabel labelParameterName = uiFactory.createLabel(panel);
			labelParameterName.setText( (info.getName() != null ? info.getName() : ("") ) );
			panelLayout.set(labelParameterName, (1 + index), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			this.scaleParameterValue[index] = uiFactory.createHorizontalScale(panel);
			this.scaleParameterValue[index].setMaximum(100);
			this.scaleParameterValue[index].setMinimum(0);
			this.scaleParameterValue[index].setIncrement(1);
			panelLayout.set(this.scaleParameterValue[index], (1 + index), 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
			
			this.labelParameterValue[index] = uiFactory.createLabel(panel);
			panelLayout.set(this.labelParameterValue[index], (1 + index), 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			this.scaleParameterValue[index].addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					UIScale scale = LV2AudioProcessorDialog.this.scaleParameterValue[index];
					Float value = info.getMinimumValue() + ((scale.getValue() * (info.getMaximumValue() - info.getMinimumValue())) / 100f);
					
					LV2AudioProcessorDialog.this.labelParameterValue[index].setText(Float.toString(value));
					LV2AudioProcessorDialog.this.processor.getInstance().setControlPortValue(portIndex, value);
					LV2AudioProcessorDialog.this.callback.onChange(false);
				}
			});
		}
		
		this.updateItems();
		this.addEventListeners();
		
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				LV2AudioProcessorDialog.this.removeEventListeners();
			}
		});
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void addEventListeners() {
		TGEventManager.getInstance(this.context).addListener(LV2ParamsEvent.EVENT_TYPE, this);
	}
	
	public void removeEventListeners() {
		TGEventManager.getInstance(this.context).removeListener(LV2ParamsEvent.EVENT_TYPE, this);
	}
	
	public void focus() {
		if( this.isOpen() ){
			this.dialog.moveToTop();
		}
	}
	
	public void close() {
		if( this.isOpen() ){
			this.dialog.dispose();
			this.dialog = null;
		}
	}
	
	public boolean isOpen() {
		return (this.dialog != null && !this.dialog.isDisposed());
	}
	
	public void updateItems() {
		for(int i = 0 ; i < this.portIndices.size(); i ++ ) {
			int index = this.portIndices.get(i);
			float value = this.processor.getInstance().getControlPortValue(index);
			LV2ControlPortInfo info = this.processor.getPlugin().getControlPortInfo(index);
			
			this.scaleParameterValue[i].setIgnoreEvents(true);
			this.scaleParameterValue[i].setValue(Math.round((int) ((value - info.getMinimumValue()) * 100) / (info.getMaximumValue() - info.getMinimumValue())));
			this.scaleParameterValue[i].setIgnoreEvents(false);
			this.labelParameterValue[i].setText(Float.toString(value));
		}
	}
	
	public void updateItemsUiThread() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				LV2AudioProcessorDialog.this.updateItems();
			}
		});
	}
	
	public void processEvent(TGEvent event) {
		if( LV2ParamsEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			LV2AudioProcessor processor = event.getAttribute(LV2ParamsEvent.PROPERTY_PROCESSOR);
			if( LV2ParamsEvent.ACTION_RESTORE.equals(event.getAttribute(LV2ParamsEvent.PROPERTY_ACTION))) {
				if( this.processor.isOpen() && this.processor.getInstance().equals(processor.getInstance())) {
					this.updateItemsUiThread();
				}
			}
		}
	}
}
