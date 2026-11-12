package app.tuxguitar.midi.synth;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;

/**
 * A module's configuration, read the first time it is asked for.
 */
public final class TGLazyConfig {

	private final TGContext context;
	private final String module;
	private TGConfigManager config;

	public TGLazyConfig(TGContext context, String module) {
		this.context = context;
		this.module = module;
	}

	public TGConfigManager get() {
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, this.module);
		}
		return this.config;
	}
}
