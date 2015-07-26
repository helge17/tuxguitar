package org.herac.tuxguitar.util;

public class TGSynchronizer {
	
	private static TGSynchronizer instance;
	
	private TGSynchronizerController controller;
	
	private TGSynchronizer(){
		super();
	}
	
	public static TGSynchronizer instance(){
		synchronized (TGSynchronizer.class) {
			if( instance == null ) {
				instance = new TGSynchronizer();
			}
		}
		return instance;
	}
	
	public void executeLater(Runnable runnable) throws TGException {
		this.controller.executeLater(runnable);
	}
	
	public void setController(TGSynchronizerController controller){
		this.controller = controller;
	}
	
	public static interface TGSynchronizerController {
		
		public void executeLater(Runnable runnable);
	}
}
