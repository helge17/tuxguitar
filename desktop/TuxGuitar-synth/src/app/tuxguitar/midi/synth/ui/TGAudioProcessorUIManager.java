package app.tuxguitar.midi.synth.ui;

import java.util.List;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGAudioProcessorUIManager {

	private TGContext context;

	private TGAudioProcessorUIManager(TGContext context){
		this.context = context;
	}

	public TGAudioProcessorUIFactory findFactory(String type){
		List<TGAudioProcessorUIFactory> factories = TGSynthManager.getInstance(this.context).getExtensions(TGAudioProcessorUIFactory.class);
		for(TGAudioProcessorUIFactory factory : factories) {
			if( factory.getType().equals(type) ) {
				return factory;
			}
		}
		return null;
	}

	public TGAudioProcessorUI createUI(String type, TGAudioProcessor processor, TGAudioProcessorUICallback callback){
		TGAudioProcessorUIFactory factory = this.findFactory(type);
		if( factory != null ) {
			return factory.create(processor, callback);
		}
		return null;
	}

	public static TGAudioProcessorUIManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGAudioProcessorUIManager.class.getName(), new TGSingletonFactory<TGAudioProcessorUIManager>() {
			public TGAudioProcessorUIManager createInstance(TGContext context) {
				return new TGAudioProcessorUIManager(context);
			}
		});
	}
}
