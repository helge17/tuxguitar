package org.herac.tuxguitar.player.impl.midiport.lv2.ui;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.player.impl.midiport.lv2.LV2AudioProcessor;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2ControlPortInfo;
import org.herac.tuxguitar.ui.UIFactory;
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

public class LV2AudioProcessorDialog {
	
	private LV2AudioProcessor processor;
	private TGContext context;
	private UIWindow dialog;
	
	public LV2AudioProcessorDialog(TGContext context, LV2AudioProcessor processor) {
		this.context = context;
		this.processor = processor;
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
		
		List<Integer> portIndices = this.processor.getInstance().getControlPortIndices();
		for(int i = 0 ; i < portIndices.size(); i ++ ) {
			final int index = portIndices.get(i);
			final float value = this.processor.getInstance().getControlPortValue(index);
			final LV2ControlPortInfo info = this.processor.getInstance().getControlPortInfo(index);
			
			final UILabel labelParameterName = uiFactory.createLabel(panel);
			labelParameterName.setText( (info.getName() != null ? info.getName() : ("") ) );
			panelLayout.set(labelParameterName, (1 + i), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			final UIScale scaleParameterValue = uiFactory.createHorizontalScale(panel);
			scaleParameterValue.setMaximum(100);
			scaleParameterValue.setMinimum(0);
			scaleParameterValue.setIncrement(1);
			scaleParameterValue.setValue(Math.round((int) ((value - info.getMinimumValue()) * 100) / (info.getMaximumValue() - info.getMinimumValue())));
			panelLayout.set(scaleParameterValue, (1 + i), 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
			
			final UILabel labelParameterValue = uiFactory.createLabel(panel);
			labelParameterValue.setText( Float.toString(value) );
			panelLayout.set(labelParameterValue, (1 + i), 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			scaleParameterValue.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					float selection = info.getMinimumValue() + ((scaleParameterValue.getValue() * (info.getMaximumValue() - info.getMinimumValue())) / 100f);
					labelParameterValue.setText(Float.toString(selection));
					
					LV2AudioProcessorDialog.this.processor.getInstance().setControlPortValue(index, selection);
				}
			} );
		}
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
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
}
