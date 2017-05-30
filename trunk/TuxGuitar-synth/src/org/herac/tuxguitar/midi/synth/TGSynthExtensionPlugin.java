package org.herac.tuxguitar.midi.synth;

import java.util.List;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSynthExtensionPlugin implements TGPlugin{
	
	private List<TGSynthExtension<?>> extensions;
	
	public TGSynthExtensionPlugin() {
		super();
	}
	
	public abstract List<TGSynthExtension<?>> createExtensions(TGContext context) throws TGPluginException;
	
	@SuppressWarnings("unchecked")
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.extensions == null ) {
				this.extensions = this.createExtensions(context);
				
				TGSynthManager synthManager = TGSynthManager.getInstance(context);
				for(TGSynthExtension<?> extension : this.extensions) {
					synthManager.addExtension((Class<Object>) extension.getExtensionClass(), extension.getExtension());
				}
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.extensions != null ) {
				TGSynthManager synthManager = TGSynthManager.getInstance(context);
				for(TGSynthExtension<?> extension : this.extensions) {
					synthManager.removeExtension((Class<Object>) extension.getExtensionClass(), extension.getExtension());
				}
				
				this.extensions = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}
