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
	
	public void addRunnable(TGRunnable runnable) throws Throwable {
		TGSynchronizerTask task = new TGSynchronizerTask(runnable);
		this.controller.excecute(task);
		if(task.getThrowable() != null){
			throw task.getThrowable();
		}
	}
	
	public void setController(TGSynchronizerController controller){
		this.controller = controller;
	}
	
	public class TGSynchronizerTask{
		private long threadId;
		private Throwable throwable;
		private TGRunnable runnable;
		
		public TGSynchronizerTask(TGRunnable runnable){
			this.threadId = Thread.currentThread().getId();
			this.runnable = runnable;
			this.throwable = null;
		}
		
		public Throwable getThrowable(){
			return this.throwable;
		}
		
		public long getThreadId(){
			return this.threadId;
		}
		
		public void run(){
			try{
				this.runnable.run();
			}catch(Throwable throwable){
				this.throwable = throwable;
			}
		}
	}
	
	public interface TGRunnable {
		public void run() throws Throwable;
	}
	
	public interface TGSynchronizerController{
		public void excecute(TGSynchronizerTask task);
	}
}
