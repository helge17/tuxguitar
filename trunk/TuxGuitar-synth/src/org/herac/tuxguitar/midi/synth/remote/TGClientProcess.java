package org.herac.tuxguitar.midi.synth.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.util.TGContext;

public class TGClientProcess {
	
	private TGContext context;
	private Process process;
	
	public TGClientProcess(TGContext context) {
		this.context = context;
	}
	
	public void startSession(TGClientStarter starter, Integer sessionId, Integer serverPort) throws TGRemoteException {
		this.startProcess(starter, sessionId, serverPort);
		this.startIOStreamThread();
	}
	
	public void startProcess(TGClientStarter starter, Integer sessionId, Integer serverPort) throws TGRemoteException {
		try {
			String[] cmdarray = starter.createClientCommand(sessionId, serverPort);
			if( cmdarray != null && cmdarray.length > 0 ) {
				this.process = Runtime.getRuntime().exec(cmdarray);
			}
		} catch (IOException e) {
			throw new TGRemoteException(e);
		}
	}
	
	public void startIOStreamThread() {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				startIOStreamLoop();
			}
		});
	}
	
	public void startIOStreamLoop() {
		TGThreadManager.getInstance(this.context).loop(new TGThreadLoop() {
			public Long process() {
				try {
					if( TGClientProcess.this.isAlive() ) {
						printIOStream(System.out, TGClientProcess.this.process.getInputStream());
						printIOStream(System.err, TGClientProcess.this.process.getErrorStream());
						
						return 250l;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return BREAK;
			}
		});
	}
	
	public void printIOStream(PrintStream printStream, InputStream inputStream) {
		try {
			String line = null;
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			while(reader.ready() && (line = reader.readLine()) != null) {
				printStream.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace(printStream);
		}
	}
	
	public boolean isAlive() {
		return (this.process != null && this.process.isAlive());
	}
}
