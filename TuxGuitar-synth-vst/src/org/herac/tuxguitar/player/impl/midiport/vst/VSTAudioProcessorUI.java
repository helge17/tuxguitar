package org.herac.tuxguitar.player.impl.midiport.vst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserFormat;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

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
		if( this.processor.isOpen() ) {
			vstPlugin = this.processor.getFile().getName();
		} else {
			vstPlugin = TuxGuitar.getProperty("tuxguitar-synth-vst.ui.label.empty");
		}
		return TuxGuitar.getProperty("tuxguitar-synth-vst.ui.label." + vstPluginType, new String[] {vstPlugin});
	}

	public boolean isOpen() {
		if( this.processor.isOpen() ) {
			if( this.processor.getEffect().isEditorAvailable() ) {
				return this.processor.getEffect().isNativeEditorOpen();
			}
			return (this.editor != null && this.editor.isOpen());
		}
		return false;
	}

	public void open(UIWindow parent) {
		if( this.processor.isOpen() ) {
			if( this.processor.getEffect().isEditorAvailable() ) {
				this.processor.getEffect().openNativeEditor();
				this.startCheckForUpdatesThread();
			}
			else {
				if( this.editor == null ) {
					this.editor = new VSTEffectEditor(this.context, this.processor.getEffect(), this.callback);
				}
				this.editor.openInUiThread(parent);
			}
		} else {
			this.choosePluginInUiThread(parent);
		}
	}
	
	public void close() {
		if( this.processor.isOpen() ) {
			if( this.processor.getEffect().isEditorAvailable()) {
				if( this.processor.getEffect().isNativeEditorOpen() ) {
					this.processor.getEffect().closeNativeEditor();
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
		String[] extensions = vstSettings.getPluginExtensions();
		
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UIFileChooser uiFileChooser = uiFactory.createOpenFileChooser(parent);
		
		if( chooserPath != null) {
			uiFileChooser.setDefaultPath(new File(chooserPath));
		}
		if( extensions != null) {
			List<UIFileChooserFormat> uiFileChooserFormats = new ArrayList<UIFileChooserFormat>();
			UIFileChooserFormat uiFileChooserFormat = new UIFileChooserFormat(TuxGuitar.getProperty("tuxguitar-synth-vst.ui.chooser.file-format.name"));
			for(String extension : extensions) {
				uiFileChooserFormat.getExtensions().add(extension);
			}
			uiFileChooserFormats.add(uiFileChooserFormat);
			uiFileChooser.setSupportedFormats(uiFileChooserFormats);
		}
		uiFileChooser.choose(new UIFileChooserHandler() {
			public void onSelectFile(File file) {
				if (file != null) {
					vstSettings.setPluginPath(file.getParentFile().getAbsolutePath());
					vstSettings.save();
					
					VSTAudioProcessorUI.this.openPluginInThread(parent, file);
				}
			}
		});
	}
	
	public void choosePluginInUiThread(final UIWindow parent) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				VSTAudioProcessorUI.this.choosePlugin(parent);
			}
		});
	}
	
	public void openPlugin(UIWindow parent, File file) {
		this.processor.open(file);
		this.open(parent);
		this.callback.onChange(true);
	}
	
	public void openPluginInThread(final UIWindow parent, final File file) {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				VSTAudioProcessorUI.this.openPlugin(parent, file);
			}
		});
	}
	
	public void checkForUpdates() {
		if( this.isOpen() ) {
			if( this.processor.getEffect().isUpdated() ) {
				this.callback.onChange(false);
			}
		}
	}
	
	public void startCheckForUpdatesThread() {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				processCheckForUpdatesLoop();
			}
		});
	}
	
	public void processCheckForUpdatesLoop() {
		TGThreadManager.getInstance(this.context).loop(new TGThreadLoop() {
			public Long process() {
				if( VSTAudioProcessorUI.this.isOpen() ) {
					VSTAudioProcessorUI.this.checkForUpdates();
					
					return 250l;
				}
				return BREAK;
			}
		});
	}
}
