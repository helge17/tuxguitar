package org.herac.tuxguitar.jack.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPortFlags;
import org.herac.tuxguitar.jack.JackPortTypes;
import org.herac.tuxguitar.jack.provider.JackClientProvider;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class JackConnectionManager {
	
	private JackClient jackClient;
	private JackClientProvider jackClientProvider;
	private JackConnectionListener jackConnectionListener;
	private JackConnectionConfig jackConnectionConfig;
	
	private List jackConnections;
	private boolean autoConnectPorts;
	
	public JackConnectionManager(JackClientProvider jackClientProvider){
		this.jackClientProvider = jackClientProvider;
		this.jackConnectionConfig = new JackConnectionConfig(this);
		this.jackConnectionListener = new JackConnectionListener(this);
		this.jackConnections = new ArrayList();
	}
	
	public void initialize(){
		if( this.jackClient == null ){
			this.jackConnections.clear();
			this.jackClient = this.jackClientProvider.getJackClient();
			this.jackClient.addPortRegisterListener(this.jackConnectionListener);
			this.loadConfig();
		}
	}
	
	public void destroy(){
		if( this.jackClient != null ){
			this.jackConnections.clear();
			this.jackClient.removePortRegisterListener(this.jackConnectionListener);
			this.jackClient = null;
		}
	}
	
	public void loadConfig(){
		try {
			this.jackConnectionConfig.load();
		} catch (Throwable throwable){
			TGErrorManager.getInstance().handleError(throwable);
		}
	}
	
	public void saveConfig(){
		try {
			this.jackConnectionConfig.save();
		} catch (Throwable throwable){
			TGErrorManager.getInstance().handleError(throwable);
		}
	}
	
	public void connectAllPorts(){
		if( this.jackClient != null ){
			Iterator it = this.jackConnections.iterator();
			while( it.hasNext() ){
				JackConnection jackConnection = (JackConnection) it.next();
				
				this.connectPorts(jackConnection);
			}
		}
	}
	
	public void connectPorts(JackConnection jackConnection){
		if( this.jackClient != null ){
			List portNames = this.jackClient.getPortNames(JackPortTypes.JACK_ALL_TYPES, 0);
			String srcPortName = findPortNameById(jackConnection.getSrcPortId(), portNames);
			String dstPortName = findPortNameById(jackConnection.getDstPortId(), portNames);
			if( srcPortName != null && dstPortName != null ){
				this.jackClient.connectPorts(srcPortName, dstPortName);
			}
		}
	}
	
	public void loadExistingConnections(){
		if( this.jackClient != null ){
			List existingConnections = new ArrayList();
			List existingPortNames = this.jackClient.getPortNames(JackPortTypes.JACK_ALL_TYPES, 0);
			
			this.loadExistingConnections(existingConnections);
			this.loadPreviousConnectionsToExistingList(existingConnections, existingPortNames);
			this.jackConnections.clear();
			this.jackConnections.addAll(existingConnections);
		}
	}
	
	public void loadExistingConnections(List existingConnections){
		if( this.jackClient != null ){
			List srcPortNames = this.jackClient.getPortNames(JackPortTypes.JACK_ALL_TYPES, JackPortFlags.JACK_PORT_IS_OUTPUT);
			if( srcPortNames != null ){
				Iterator it = srcPortNames.iterator();
				while( it.hasNext() ){
					this.loadExistingConnections(existingConnections, (String) it.next());
				}
			}
		}
	}
	
	public void loadExistingConnections(List existingConnections, String srcPortName){
		if( this.jackClient != null ){
			List dstPortNames = this.jackClient.getPortConnections(srcPortName);
			if( dstPortNames != null ){
				Iterator it = dstPortNames.iterator();
				while( it.hasNext() ){
					this.loadExistingConnection(existingConnections, srcPortName, (String) it.next());
				}
			}
		}
	}
	
	public void loadExistingConnection(List existingConnections, String srcPortName, String dstPortName){
		if( this.jackClient != null ){
			JackConnection jackConnection = new JackConnection(createPortId(srcPortName), createPortId(dstPortName));
			if(!existingConnections.contains(jackConnection)){
				existingConnections.add(jackConnection);
			}
		}
	}
	
	public void loadPreviousConnectionsToExistingList(List existingConnections, List existingPortNames){
		if( this.jackClient != null ){
			Iterator it = this.jackConnections.iterator();
			while( it.hasNext() ){
				JackConnection jackConnection = (JackConnection) it.next();
				
				String srcPortName = findPortNameById(jackConnection.getSrcPortId(), existingPortNames);
				String dstPortName = findPortNameById(jackConnection.getDstPortId(), existingPortNames);
				// If both ports are not null means "ports were disconnected".
				if( srcPortName == null || dstPortName == null ){
					existingConnections.add( jackConnection );
				}
			}
		}
	}
	
	public String findPortNameById(long portId){
		if( this.jackClient != null ){
			return findPortNameById(portId, this.jackClient.getPortNames(JackPortTypes.JACK_ALL_TYPES, 0));
		}
		return null;
	}
	
	public String findPortNameById(long portId, List portNames){
		if( this.jackClient != null ){
			if( portNames != null ){
				Iterator it = portNames.iterator();
				while( it.hasNext() ){
					String portName = (String) it.next();
					if( portId == createPortId(portName) ){
						return portName;
					}
				}
			}
		}
		return null;
	}
	
	public long createPortId(String portName){
		return portName.hashCode();
	}
	
	public void clearJackConnections(){
		this.jackConnections.clear();
	}
	
	public void addJackConnection(JackConnection jackConnection){
		if(!this.jackConnections.contains(jackConnection)){
			this.jackConnections.add(jackConnection);
		}
	}
	
	public List getJackConnections() {
		return this.jackConnections;
	}
	
	public boolean isAutoConnectPorts() {
		return autoConnectPorts;
	}

	public void setAutoConnectPorts(boolean autoConnectPorts) {
		this.autoConnectPorts = autoConnectPorts;
	}
}
