package org.herac.tuxguitar.util;


public class TGSynchronizer {
	
	private static TGSynchronizer instance;
	
	private TGSynchronizerController controller;
	
	private TGSynchronizer(){
		super();
	}
	
	public static TGSynchronizer instance(){
		if (instance == null) {
			synchronized (TGSynchronizer.class) {
				instance = new TGSynchronizer();
			}
		}
		return instance;
	}
	
	public void execute(TGRunnable runnable) throws TGException {
		TGSynchronizerTask task = new TGSynchronizerTask(runnable);
		this.controller.execute(task);
		if( task.getThrowable() != null ){
			throw task.getThrowable();
		}
	}
	
	public void executeLater(TGRunnable runnable) throws TGException {
		TGSynchronizerTask task = new TGSynchronizerTask(runnable);
		this.controller.executeLater(task);
		if( task.getThrowable() != null ){
			throw task.getThrowable();
		}
	}
	
	public void setController(TGSynchronizerController controller){
		this.controller = controller;
	}
	
	public class TGSynchronizerTask{
		private TGRunnable runnable;
		private TGException throwable;
		
		public TGSynchronizerTask(TGRunnable runnable){
			this.runnable = runnable;
			this.throwable = null;
		}
		
		public TGException getThrowable(){
			return this.throwable;
		}
		
		public void run(){
			try{
				this.runnable.run();
			} catch(TGException tgException){
				this.throwable = tgException;
			} catch(Throwable throwable){
				this.throwable = new TGException(throwable);
			}
		}
	}
	
	public interface TGRunnable {
		public void run() throws TGException;
	}
	
	public interface TGSynchronizerController{
		public void execute(TGSynchronizerTask task);
		public void executeLater(TGSynchronizerTask task);
	}
}
