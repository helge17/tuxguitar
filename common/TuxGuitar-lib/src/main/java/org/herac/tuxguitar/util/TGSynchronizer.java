package org.herac.tuxguitar.util;

import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSynchronizer {
	
	private TGSynchronizerController controller;
	
	private TGSynchronizer(){
		super();
	}
	
	public void executeLater(Runnable runnable) throws TGException {
		this.controller.executeLater(runnable);
	}
	
	public void setController(TGSynchronizerController controller){
		this.controller = controller;
	}
	
	public static TGSynchronizer getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSynchronizer.class.getName(), new TGSingletonFactory<TGSynchronizer>() {
			public TGSynchronizer createInstance(TGContext context) {
				return new TGSynchronizer();
			}
		});
	}
	
	public static interface TGSynchronizerController {
		
		void executeLater(Runnable runnable);
	}
}
