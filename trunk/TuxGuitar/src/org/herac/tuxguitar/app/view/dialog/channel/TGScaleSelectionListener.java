package org.herac.tuxguitar.app.view.dialog.channel;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGScaleSelectionListener extends SelectionAdapter implements Runnable{
	
	private static final long MAXIMUM_TIME = 500;
	
	private long time;
	private boolean running;
	private TGChannelItem handle;
	
	private Object mutex = new Object();
	
	public TGScaleSelectionListener(TGChannelItem handle){
		this.handle = handle;
	}
	
	public void widgetSelected(SelectionEvent e) {
		this.process();
	}
	
	public void process(){
		synchronized (this.mutex) {
			
			if(!this.running){
				this.running = true;
				
				// Start the thread.
				Thread thread = new Thread(this);
				thread.start();
			}
			
			this.time = System.currentTimeMillis();
			
			this.mutex.notifyAll();
		}
	}
	
	public void run(){
		try {
			long timeToWait = MAXIMUM_TIME;
			
			while( this.running ){
				synchronized (this.mutex) {
					timeToWait = ((this.time + MAXIMUM_TIME) - System.currentTimeMillis());
					
					this.running = ( timeToWait > 0 );
					
					if( this.running ){
						this.mutex.wait(timeToWait);
					}
				}
			}
			
			this.doActionSynchronized();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void doActionSynchronized() throws Throwable {
		TGSynchronizer.getInstance(this.handle.getContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				doAction();		
			}
		});
	}
	
	public void doAction(){
		this.handle.updateChannel(false);
	}
}
