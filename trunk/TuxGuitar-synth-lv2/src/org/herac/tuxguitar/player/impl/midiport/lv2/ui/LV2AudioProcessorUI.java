package org.herac.tuxguitar.player.impl.midiport.lv2.ui;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.player.impl.midiport.lv2.LV2AudioProcessorWrapper;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class LV2AudioProcessorUI implements TGAudioProcessorUI {
	
	private TGContext context;
	private LV2AudioProcessorWrapper processor;
	private LV2AudioProcessorDialog dialog;
	private TGAudioProcessorUICallback callback;
	
	public LV2AudioProcessorUI(TGContext context, LV2AudioProcessorWrapper processor, TGAudioProcessorUICallback callback) {
		this.context = context;
		this.processor = processor;
		this.callback = callback;
	}
	
	public String getLabel() {
		String lv2Plugin = null;
		if( this.processor.getTarget() != null ) {
			lv2Plugin = this.processor.getTarget().getPlugin().getName();
		} else {
			lv2Plugin = TuxGuitar.getProperty("tuxguitar-synth-lv2.ui.label.empty");
		}
		return TuxGuitar.getProperty("tuxguitar-synth-lv2.ui.label", new String[] {lv2Plugin});
	}

	public boolean isOpen() {
		if( this.processor.getTarget() != null ) {
			return (this.dialog != null && this.dialog.isOpen());
		}
		return false;
	}

	public void open(UIWindow parent) {
		if( this.processor.getTarget() != null ) {
			if( this.dialog == null ) {
				this.dialog = new LV2AudioProcessorDialog(this.context, this.processor.getTarget());
			}
			this.dialog.open(parent);
		} else {
			this.choosePlugin(parent);
		}
	}
	
	public void close() {
		if( this.processor.getTarget() != null ) {
			if (this.dialog != null && this.dialog.isOpen()) {
				this.dialog.close();
			}
		}
	}
	
	public void choosePlugin(final UIWindow parent) {
		LV2AudioProcessorChooser lv2AudioProcessorChooser = new LV2AudioProcessorChooser(this.context, this.processor.getWorld());
		lv2AudioProcessorChooser.choose(parent, new LV2AudioProcessorChooser.LV2AudioProcessorChooserHandler() {
			public void onSelectPlugin(LV2Plugin plugin) {
				if( plugin != null ) {
					LV2AudioProcessorUI.this.processor.open(plugin.getUri());
					LV2AudioProcessorUI.this.open(parent);
					LV2AudioProcessorUI.this.callback.onChange();
				}
			}
		});
	}
}
