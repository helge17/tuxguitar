package app.tuxguitar.thread;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGThreadManager implements TGThreadHandler {

	private TGThreadHandler handler;

	private TGThreadManager(){
		super();
	}

	public void start(Runnable runnable) {
		if( this.handler != null ) {
			this.handler.start(TGThreadPriority.NORMAL, runnable);
		}
	}

	public void start(TGThreadPriority priority, Runnable runnable) {
		if( this.handler != null ) {
			this.handler.start(priority, runnable);
		}
	}

	public void loop(TGThreadLoop loop) {
		if( this.handler != null ) {
			this.handler.loop(loop);
		}
	}

	public void yield() {
		if( this.handler != null ) {
			this.handler.yield();
		}
	}

	public void dispose() {
		if( this.handler != null ) {
			this.handler.dispose();
		}
	}

	public Object getThreadId() {
		if( this.handler != null ) {
			return this.handler.getThreadId();
		}
		return null;
	}

	public TGThreadHandler getThreadHandler() {
		return handler;
	}

	public void setThreadHandler(TGThreadHandler handler) {
		this.handler = handler;
	}

	public static TGThreadManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGThreadManager.class.getName(), new TGSingletonFactory<TGThreadManager>() {
			public TGThreadManager createInstance(TGContext context) {
				return new TGThreadManager();
			}
		});
	}
}
