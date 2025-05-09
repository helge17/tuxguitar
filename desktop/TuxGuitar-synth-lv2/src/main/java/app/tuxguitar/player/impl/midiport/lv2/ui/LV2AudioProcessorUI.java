package app.tuxguitar.player.impl.midiport.lv2.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import app.tuxguitar.player.impl.midiport.lv2.LV2AudioProcessor.LV2AudioProcessorUpdateCallback;
import app.tuxguitar.player.impl.midiport.lv2.LV2AudioProcessorWrapper;
import app.tuxguitar.player.impl.midiport.lv2.LV2PluginValidator;
import app.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class LV2AudioProcessorUI implements LV2AudioProcessorUpdateCallback, TGAudioProcessorUI {

	private TGContext context;
	private LV2PluginValidator validator;
	private LV2AudioProcessorWrapper processor;
	private LV2AudioProcessorDialog dialog;
	private TGAudioProcessorUICallback callback;

	public LV2AudioProcessorUI(TGContext context, LV2AudioProcessorWrapper processor, LV2PluginValidator validator, TGAudioProcessorUICallback callback) {
		this.context = context;
		this.processor = processor;
		this.validator = validator;
		this.callback = callback;
	}

	public String getLabel() {
		String lv2Plugin = null;
		if( this.processor.getTarget() != null && this.processor.getTarget().isOpen() ) {
			lv2Plugin = this.processor.getTarget().getPlugin().getName();
		} else {
			lv2Plugin = TuxGuitar.getProperty("tuxguitar-synth-lv2.ui.label.empty");
		}
		return TuxGuitar.getProperty("tuxguitar-synth-lv2.ui.label", new String[] {lv2Plugin});
	}

	public boolean isOpen() {
		if( this.processor.getTarget() != null && this.processor.getTarget().isOpen() ) {
			if( this.processor.getTarget().getInstance().isUIAvailable() ) {
				return this.processor.getTarget().getInstance().isUIOpen();
			}
			return (this.dialog != null && this.dialog.isOpen());
		}
		return false;
	}

	public void open(UIWindow parent) {
		if( this.processor.getTarget() != null && this.processor.getTarget().isOpen() ) {
			if( this.processor.getTarget().getInstance().isUIAvailable() ) {
				this.processor.getTarget().getInstance().openUI();
				this.processor.getTarget().setUpdateCallback(this);
			}
			else {
	 			if( this.dialog == null ) {
					this.dialog = new LV2AudioProcessorDialog(this.context, this.processor.getTarget(), this.callback);
				}
				this.dialog.open(parent);
			}
		} else {
			this.choosePlugin(parent);
		}
	}

	public void close() {
		if( this.processor.getTarget() != null && this.processor.getTarget().isOpen() ) {
			if( this.processor.getTarget().getInstance().isUIAvailable() ) {
				this.processor.getTarget().setUpdateCallback(null);
				this.processor.getTarget().getInstance().closeUI();
			}
			else {
				if (this.dialog != null && this.dialog.isOpen()) {
					this.dialog.close();
				}
			}
		}
	}

	public void focus() {
		if( this.processor.getTarget() != null && this.processor.getTarget().isOpen() ) {
			if( this.processor.getTarget().getInstance().isUIAvailable() ) {
				this.processor.getTarget().getInstance().focusUI();
			}
			else {
				if (this.dialog != null && this.dialog.isOpen()) {
					this.dialog.focus();
				}
			}
		}
	}

	public void choosePlugin(final UIWindow parent) {
		LV2AudioProcessorChooser lv2AudioProcessorChooser = new LV2AudioProcessorChooser(this.context, this.processor.getWorld());
		lv2AudioProcessorChooser.choose(parent, this.validator, new LV2AudioProcessorChooser.LV2AudioProcessorChooserHandler() {
			public void onSelectPlugin(LV2Plugin plugin) {
				if( plugin != null ) {
					LV2AudioProcessorUI.this.processor.open(plugin.getUri());
					LV2AudioProcessorUI.this.open(parent);
					LV2AudioProcessorUI.this.callback.onChange(true);
				}
			}
		});
	}

	public void onUpdate() {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				LV2AudioProcessorUI.this.callback.onChange(false);
			}
		});
	}
}
