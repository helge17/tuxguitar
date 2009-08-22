package org.herac.tuxguitar.jack.synthesizer;

public class JackOutputPortRouter {
	
	private static final int PORT_INDEX = 0;
	private static final int CHANNEL_INDEX = 1;
	
	private int ports;
	private int[][] channels;
	
	public JackOutputPortRouter(){
		this.ports = 1;
		this.channels = new int[16][2];
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ PORT_INDEX ] = 0;
			this.channels[i][ CHANNEL_INDEX ] = i;
		}
	}
	
	public int getChannelRoute( int channel ){
		if( channel >= 0 && channel < this.channels.length ){
			return this.channels[ channel ][ CHANNEL_INDEX ];
		}
		return 0;
	}
	
	public int getPortRoute( int channel ){
		if( channel >= 0 && channel < this.channels.length ){
			return this.channels[ channel ][ PORT_INDEX ];
		}
		return 0;
	}
	
	public int getPorts(){
		return this.ports;
	}
}
