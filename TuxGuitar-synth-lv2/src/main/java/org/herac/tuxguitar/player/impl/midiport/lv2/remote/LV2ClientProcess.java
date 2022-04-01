package org.herac.tuxguitar.player.impl.midiport.lv2.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.player.impl.midiport.lv2.LV2Settings;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class LV2ClientProcess {
	
	private static final String VARIABLE_LV2_SESSION_ID = "lv2.sessionId";
	private static final String VARIABLE_LV2_SERVER_PORT = "lv2.serverPort";
	private static final String VARIABLE_LV2_BUFFER_SIZE = "lv2.bufferSize";
	private static final String VARIABLE_LV2_PLUGIN_URI = "lv2.pluginUri";
	
	private TGContext context;
	private Process process;
	
	public LV2ClientProcess(TGContext context) {
		this.context = context;
	}
	
	public void startSession(Integer sessionId, Integer serverPort, LV2Plugin plugin, int bufferSize) throws LV2RemoteException {
		this.startProcess(sessionId, serverPort, plugin, bufferSize);
		this.startIOStreamThread();
	}
	
	public void startProcess(Integer sessionId, Integer serverPort, LV2Plugin plugin, int bufferSize) throws LV2RemoteException {
		try {
			String command = this.findClientCommand();
			if( command != null ) {
				Map<String, Object> variables = this.createCommandVariables(sessionId, serverPort,  plugin.getUri(), bufferSize);
				TGExpressionResolver expressionResolver = TGExpressionResolver.getInstance(this.context);
				
				String[] cmdarray = command.split(",");
				for(int i = 0 ; i < cmdarray.length; i ++) {
					cmdarray[i] = expressionResolver.resolve(cmdarray[i], variables);
				}
				this.process = Runtime.getRuntime().exec(cmdarray);
			}
		} catch (IOException e) {
			throw new LV2RemoteException(e);
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
					if( LV2ClientProcess.this.isAlive() ) {
						printIOStream(System.out, LV2ClientProcess.this.process.getInputStream());
						printIOStream(System.err, LV2ClientProcess.this.process.getErrorStream());
						
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
	
	public String findClientCommand() throws LV2RemoteException {
		return new LV2Settings(this.context).getUIClientCommand();
	}
	
	public Map<String, Object> createCommandVariables(Integer sessionId, Integer serverPort, String pluginUri, int bufferSize) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VARIABLE_LV2_SESSION_ID, sessionId);
		variables.put(VARIABLE_LV2_SERVER_PORT, serverPort);
		variables.put(VARIABLE_LV2_BUFFER_SIZE, bufferSize);
		variables.put(VARIABLE_LV2_PLUGIN_URI, pluginUri);
		return variables;
	}
	
	public boolean isAlive() {
		return (this.process != null && this.process.isAlive());
	}
}
