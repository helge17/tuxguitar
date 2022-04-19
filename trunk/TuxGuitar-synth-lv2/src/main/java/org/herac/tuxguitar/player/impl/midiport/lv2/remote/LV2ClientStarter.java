package org.herac.tuxguitar.player.impl.midiport.lv2.remote;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.remote.TGClientStarter;
import org.herac.tuxguitar.player.impl.midiport.lv2.LV2Settings;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class LV2ClientStarter implements TGClientStarter {
	
	private static final String VARIABLE_LV2_SESSION_ID = "lv2.sessionId";
	private static final String VARIABLE_LV2_SERVER_PORT = "lv2.serverPort";
	private static final String VARIABLE_LV2_BUFFER_SIZE = "lv2.bufferSize";
	private static final String VARIABLE_LV2_PLUGIN_URI = "lv2.pluginUri";
	
	private TGContext context;
	private LV2Plugin plugin;
	private Integer bufferSize;
	
	public LV2ClientStarter(TGContext context, LV2Plugin plugin, Integer bufferSize) {
		this.context = context;
		this.plugin = plugin;
		this.bufferSize = bufferSize;
	}

	@Override
	public String[] createClientCommand(Integer sessionId, Integer serverPort) {
		try {
			String command = this.findClientCommand();
			if( command != null ) {
				Map<String, Object> variables = this.createCommandVariables(sessionId, serverPort);
				TGExpressionResolver expressionResolver = TGExpressionResolver.getInstance(this.context);
				
				String[] cmdarray = command.split(",");
				for(int i = 0 ; i < cmdarray.length; i ++) {
					cmdarray[i] = expressionResolver.resolve(cmdarray[i], variables);
				}
				return cmdarray;
			}
			return null;
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public String findClientCommand() {
		return new LV2Settings(this.context).getClientCommand();
	}
	
	public Map<String, Object> createCommandVariables(Integer sessionId, Integer serverPort) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VARIABLE_LV2_SESSION_ID, sessionId);
		variables.put(VARIABLE_LV2_SERVER_PORT, serverPort);
		variables.put(VARIABLE_LV2_BUFFER_SIZE, this.bufferSize);
		variables.put(VARIABLE_LV2_PLUGIN_URI, this.plugin.getUri());
		return variables;
	}
}
