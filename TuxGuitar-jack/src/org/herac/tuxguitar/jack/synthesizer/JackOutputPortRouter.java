package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.gui.system.config.TGConfigManager;

public class JackOutputPortRouter {
	
	public static final int CREATE_UNIQUE_PORT = 0;
	public static final int CREATE_MULTIPLE_PORTS_BY_CHANNEL = 1;
	public static final int CREATE_MULTIPLE_PORTS_BY_PROGRAM = 2;
	
	private static final int PORT_INDEX = 0;
	private static final int CHANNEL_INDEX = 1;
	private static final int PROGRAM_INDEX = 2;
	
	private int ports;
	private int[][] channels;
	private int[][] programs;
	private boolean programRoutingEnabled;
	
	public JackOutputPortRouter(){
		this.ports = 1;
		this.channels = new int[16][3];
		this.programs = new int[127][3];
	}
	
	public int getPortCount(){
		return this.ports;
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
	
	public int getProgramRoute( int channel ){
		if( channel >= 0 && channel < this.channels.length ){
			return this.channels[ channel ][ PROGRAM_INDEX ];
		}
		return 0;
	}
	
	public void setProgram( int channel , int program ){
		if( channel >= 0 && channel < this.channels.length && program >= 0 && program < this.programs.length ){
			if( this.programRoutingEnabled && channel != 9 ){
				int routePort = this.programs[ program ][ PORT_INDEX ];
				int routeChannel = this.programs[ program ][ CHANNEL_INDEX ];
				int routeProgram = this.programs[ program ][ PROGRAM_INDEX ];
				
				this.channels[ channel ][ PORT_INDEX ] = (routePort >= 0 ? routePort : 0 );
				this.channels[ channel ][ CHANNEL_INDEX ] = (routeChannel >= 0 ? routeChannel : channel );
				this.channels[ channel ][ PROGRAM_INDEX ] = (routeProgram >= 0 ? routeProgram : program );
			}else{
				this.channels[ channel ][ PROGRAM_INDEX ] = program;
			}
		}
	}
	
	protected void createUniquePort(){
		this.ports = 1;
		this.programRoutingEnabled = false;
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ CHANNEL_INDEX ] = i;
			this.channels[i][ PROGRAM_INDEX ] = 0;
			this.channels[i][ PORT_INDEX ] = 0;
		}
		for( int i = 0 ; i < this.programs.length ; i ++ ){
			this.programs[i][ PROGRAM_INDEX ] = i;
			this.programs[i][ CHANNEL_INDEX ] = -1;
			this.programs[i][ PORT_INDEX ] = -1;
		}
	}
	
	protected void createMultiplePortsByChannel(){
		this.ports = this.channels.length;
		this.programRoutingEnabled = false;
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ CHANNEL_INDEX ] = i;
			this.channels[i][ PROGRAM_INDEX ] = 0;
			this.channels[i][ PORT_INDEX ] = i;
		}
		for( int i = 0 ; i < this.programs.length ; i ++ ){
			this.programs[i][ PROGRAM_INDEX ] = i;
			this.programs[i][ CHANNEL_INDEX ] = -1;
			this.programs[i][ PORT_INDEX ] = -1;
		}
	}
	
	protected void createMultiplePortsByProgram( boolean[] ports, boolean percussion ){
		this.ports = 1;
		this.programRoutingEnabled = true;
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ CHANNEL_INDEX ] = i;
			this.channels[i][ PROGRAM_INDEX ] = 0;
			this.channels[i][ PORT_INDEX ] = 0;
		}
		for( int i = 0 ; i < this.programs.length ; i ++ ){
			this.programs[i][ PROGRAM_INDEX ] = i;
			this.programs[i][ CHANNEL_INDEX ] = -1;
			this.programs[i][ PORT_INDEX ] = ( ports[ i ] ? this.ports++ : 0 );
		}
		this.channels[9][ PORT_INDEX ] = ( percussion ? this.ports++ : 0 );
	}
	
	public void loadSettings( TGConfigManager config ){
		int type = config.getIntConfigValue("jack.midi.ports.type", CREATE_UNIQUE_PORT );
		if( type == CREATE_MULTIPLE_PORTS_BY_PROGRAM ){
			boolean[] ports = new boolean[this.programs.length];
			boolean percussion = config.getBooleanConfigValue("jack.midi.port.percussion", false);
			
			for( int i = 0 ; i < ports.length ; i ++ ){
				ports[ i ] = config.getBooleanConfigValue("jack.midi.port.program-" + i , false);
			}
			this.createMultiplePortsByProgram(ports, percussion);
			
		}else if( type == CREATE_MULTIPLE_PORTS_BY_CHANNEL ){
			this.createMultiplePortsByChannel();
		}else{
			this.createUniquePort();
		}
	}
}
