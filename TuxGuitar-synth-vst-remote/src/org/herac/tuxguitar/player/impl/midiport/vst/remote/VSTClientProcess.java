package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class VSTClientProcess {
	
	private static final String VARIABLE_VST_SESSION_ID = "vst.sessionId";
	private static final String VARIABLE_VST_SERVER_PORT = "vst.serverPort";
	private static final String VARIABLE_VST_FILE_NAME = "vst.fileName";
	
	private TGContext context;
	private Process process;
	
	public VSTClientProcess(TGContext context) {
		this.context = context;
	}
	
	public void startSession(Integer sessionId, Integer serverPort, String fileName) throws VSTException {
		this.startProcess(sessionId, serverPort, fileName);
		this.startIOStreamThread();
	}
	
	public void startProcess(Integer sessionId, Integer serverPort, String fileName) throws VSTException {
		try {
			String command = this.findClientCommand(fileName);
			if( command != null ) {
				Map<String, Object> variables = this.createCommandVariables(sessionId, serverPort, fileName);
				TGExpressionResolver expressionResolver = TGExpressionResolver.getInstance(this.context);
				
				String[] cmdarray = command.split(",");
				for(int i = 0 ; i < cmdarray.length; i ++) {
					cmdarray[i] = expressionResolver.resolve(cmdarray[i], variables);
				}
				this.process = Runtime.getRuntime().exec(cmdarray);
			}
		} catch (IOException e) {
			throw new VSTException(e);
		}
	}
	
	public void startIOStreamThread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					startIOStream();
				} catch (VSTException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void startIOStream() throws VSTException {
		try {
			if( this.process != null ) {
				String line = null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
				while(this.isAlive()) {
					if ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				}
			}
		} catch (IOException e) {
			throw new VSTException(e);
		}
	}
	
	public String findClientCommand(String fileName) throws VSTException {
		String pluginType = this.findPluginType(fileName);
		if( pluginType != null ) {
			VSTSettings vstSettings = new VSTSettings(this.context);
			return vstSettings.getPluginClientCommand(pluginType);
		}
		return null;
	}
	
	public String findPluginType(String fileName) throws VSTException {
		if( fileName != null ) {
			int index = fileName.lastIndexOf(".");
			if( index > 0 && (index + 1) < fileName.length()){
				return fileName.substring(index + 1).trim().toLowerCase();
			}
		}
		return null;
	}
	
	public Map<String, Object> createCommandVariables(Integer sessionId, Integer serverPort, String fileName) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VARIABLE_VST_SESSION_ID, sessionId);
		variables.put(VARIABLE_VST_SERVER_PORT, serverPort);
		variables.put(VARIABLE_VST_FILE_NAME, fileName);
		return variables;
	}
	
	public boolean isAlive() {
		return (this.process != null && this.process.isAlive());
	}
}
