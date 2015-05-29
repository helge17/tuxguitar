package org.herac.tuxguitar.jack.connection;

import java.util.Iterator;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class JackConnectionConfig {
	
	private static final String JACK_CONFIG_ID = "tuxguitar-jack";
	private static final String JACK_AUTOMATICALLY_RESTORE_CONNECTIONS = "jack.automatically.restore.connections";
	private static final String JACK_CONNECTIONS = "jack.connections";
	private static final String JACK_CONNECTIONS_SEPARATOR = ";";
	private static final String JACK_CONNECTIONS_SRC_DST_SEPARATOR = "<->";
	
	private TGContext context;
	private JackConnectionManager jackConnectionManager;
	
	public JackConnectionConfig(TGContext context, JackConnectionManager jackConnectionManager){
		this.context = context;
		this.jackConnectionManager = jackConnectionManager;
	}
	
	public void save(){
		StringBuffer connectionsBuffer = new StringBuffer();
		
		Iterator<JackConnection> it = this.jackConnectionManager.getJackConnections().iterator();
		while( it.hasNext() ){
			JackConnection jackConnection = (JackConnection) it.next();
			if( connectionsBuffer.length() > 0 ){
				connectionsBuffer.append(JACK_CONNECTIONS_SEPARATOR);
			}
			connectionsBuffer.append(jackConnection.getSrcPortId());
			connectionsBuffer.append(JACK_CONNECTIONS_SRC_DST_SEPARATOR);
			connectionsBuffer.append(jackConnection.getDstPortId());
		}
		
		TGConfigManager tgConfigManager = new TGConfigManager(this.context, JACK_CONFIG_ID);
		tgConfigManager.setValue(JACK_CONNECTIONS, connectionsBuffer.toString());
		tgConfigManager.setValue(JACK_AUTOMATICALLY_RESTORE_CONNECTIONS, this.jackConnectionManager.isAutoConnectPorts());
		tgConfigManager.save();
	}
	
	public void load(){
		TGConfigManager tgConfigManager = new TGConfigManager(this.context, JACK_CONFIG_ID);
		
		this.jackConnectionManager.setAutoConnectPorts(tgConfigManager.getBooleanValue(JACK_AUTOMATICALLY_RESTORE_CONNECTIONS, false));
		this.jackConnectionManager.clearJackConnections();
		
		String jackConnectionsStr = tgConfigManager.getStringValue(JACK_CONNECTIONS);
		if( jackConnectionsStr != null ){
			String[] jackConnectionsArr = jackConnectionsStr.split(JACK_CONNECTIONS_SEPARATOR);
			for( int i = 0 ; i < jackConnectionsArr.length ; i ++ ){
				String[] jackConnectionArr = jackConnectionsArr[i].split(JACK_CONNECTIONS_SRC_DST_SEPARATOR);
				if( jackConnectionArr.length == 2 ){
					long srcPortId = Long.parseLong(jackConnectionArr[0]);
					long dstPortId = Long.parseLong(jackConnectionArr[1]);
					this.jackConnectionManager.addJackConnection(new JackConnection(srcPortId, dstPortId));
				}
			}
		}
	}
}
