package org.herac.tuxguitar.player.impl.midiport.vst.ui;

import java.io.File;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.player.impl.midiport.vst.VSTAudioProcessor;
import org.herac.tuxguitar.player.impl.midiport.vst.VSTEffectEditor;
import org.herac.tuxguitar.player.impl.midiport.vst.VSTSettings;
import org.herac.tuxguitar.player.impl.midiport.vst.VSTType;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class VSTAudioProcessorUI implements TGAudioProcessorUI {
	
	private TGContext context;
	private VSTType type;
	private VSTAudioProcessor processor;
	private VSTEffectEditor editor;
	private TGAudioProcessorUICallback callback;
	
	public VSTAudioProcessorUI(TGContext context, VSTAudioProcessor processor, VSTType type, TGAudioProcessorUICallback callback) {
		this.context = context;
		this.processor = processor;
		this.type = type;
		this.callback = callback;
	}
	
	public String getLabel() {
		String vstPlugin = null;
		String vstPluginType = this.type.name().toLowerCase();
		if( this.processor.getTarget() != null ) {
			vstPlugin = this.processor.getTarget().getPlugin().getFile().getName();
		} else {
			vstPlugin = TuxGuitar.getProperty("tuxguitar-synth-vst.ui.label.empty");
		}
		return TuxGuitar.getProperty("tuxguitar-synth-vst.ui.label." + vstPluginType, new String[] {vstPlugin});
	}

	public boolean isOpen() {
		if( this.processor.getTarget() != null ) {
			if( this.processor.getTarget().getEffectUI().isEditorAvailable() ) {
				return this.processor.getTarget().getEffectUI().isNativeEditorOpen();
			}
			return (this.editor != null && this.editor.isOpen());
		}
		return false;
	}

	public void open(UIWindow parent) {
		if( this.processor.getTarget() != null ) {
			if( this.processor.getTarget().getEffectUI().isEditorAvailable() ) {
				this.processor.getTarget().getEffectUI().openNativeEditor();
			}
			else {
				if( this.editor == null ) {
					this.editor = new VSTEffectEditor(this.context, this.processor.getTarget());
				}
				this.editor.open(parent);
			}
		} else {
			this.choosePlugin(parent);
		}
	}
	
	public void close() {
		if( this.processor.getTarget() != null ) {
			if( this.processor.getTarget().getEffectUI().isEditorAvailable()) {
				if( this.processor.getTarget().getEffectUI().isNativeEditorOpen() ) {
					this.processor.getTarget().getEffectUI().closeNativeEditor();
				}
			}
			else {
				if (this.editor != null && this.editor.isOpen()) {
					this.editor.close();
				}
			}
		}
	}
	
	public void choosePlugin(final UIWindow parent) {
		final VSTSettings vstSettings = new VSTSettings(this.context);
		String chooserPath = vstSettings.getPluginPath();
		
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UIFileChooser uiFileChooser = uiFactory.createOpenFileChooser(parent);
		if (chooserPath != null) {
			uiFileChooser.setDefaultPath(new File(chooserPath));
		}
		uiFileChooser.choose(new UIFileChooserHandler() {
			public void onSelectFile(File file) {
				if (file != null) {
					VSTAudioProcessorUI.this.processor.open(file.getAbsolutePath());
					VSTAudioProcessorUI.this.open(parent);
					VSTAudioProcessorUI.this.callback.onChange(true);
					
					vstSettings.setPluginPath(file.getParentFile().getAbsolutePath());
					vstSettings.save();
				}
			}
		});
	}
}
