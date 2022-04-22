package org.herac.tuxguitar.player.impl.midiport.vst;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.remote.TGClientStarter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class VSTClientStarter implements TGClientStarter {
	
	private static final String VARIABLE_VST_SESSION_ID = "vst.sessionId";
	private static final String VARIABLE_VST_SERVER_PORT = "vst.serverPort";
	private static final String VARIABLE_VST_FILE_NAME = "vst.fileName";
	
	private TGContext context;
	private VSTSettings settings;
	private String fileName;
	
	public VSTClientStarter(TGContext context, String fileName) {
		this.context = context;
		this.fileName = fileName;
		this.settings = new VSTSettings(this.context);
	}
	
	@Override
	public String[] createClientCommand(Integer sessionId, Integer serverPort) {
		try {
			String command = this.findClientCommand();
			if( command != null ) {
				Map<String, Object> variables = this.createCommandVariables(sessionId, serverPort, fileName);
				TGExpressionResolver expressionResolver = TGExpressionResolver.getInstance(this.context);
				
				String[] cmdarray = command.split(",");
				for(int i = 0 ; i < cmdarray.length; i ++) {
					cmdarray[i] = expressionResolver.resolve(cmdarray[i], variables);
				}
				return cmdarray;
			}
			return null;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public String findClientCommand() throws VSTException {
		String pluginType = this.findPluginType(this.fileName);
		if( pluginType != null ) {
			return this.settings.getPluginClientCommand(pluginType);
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
	
	public String getWorkingDir() {
		String workingDir = this.settings.getWorkingDir();
		if( workingDir != null ) {
			return TGExpressionResolver.getInstance(this.context).resolve(workingDir);
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
}
