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
	
	public JackOutputPortRouter(){
		this.ports = 1;
		this.channels = new int[16][3];
		this.programs = new int[128][3];
	}
	
	public int getPortCount(){
		return this.ports;
	}
	
	public int getPortRoute( int channel ){
		if( channel >= 0 && channel < this.channels.length ){
			int route = this.channels[ channel ][ PORT_INDEX ];
			return ( route >= 0 ? route : 0 );
		}
		return 0;
	}
	
	public int getChannelRoute( int channel ){
		if( channel >= 0 && channel < this.channels.length ){
			int route = this.channels[ channel ][ CHANNEL_INDEX ];
			return ( route >= 0 ? route : channel );
		}
		return 0;
	}
	
	public int getProgramRoute( int channel , int program ){
		if( channel >= 0 && channel < this.channels.length ){
			int route = this.channels[ channel ][ PROGRAM_INDEX ];
			return ( route >= 0 ? route : program );
		}
		return 0;
	}
	
	public void setProgram( int channel , int program ){
		if( channel >= 0 && channel < this.channels.length && program >= 0 && program < this.programs.length ){
			if( this.channels[ channel ][ PROGRAM_INDEX ] >= 0 ){
				if( channel != 9 ){
					int routePort = this.programs[ program ][ PORT_INDEX ];
					int routeChannel = this.programs[ program ][ CHANNEL_INDEX ];
					int routeProgram = this.programs[ program ][ PROGRAM_INDEX ];
					if( routePort >= 0 ){
						this.channels[ channel ][ PORT_INDEX ] = routePort;
					}
					if( routeChannel >= 0 ){
						this.channels[ channel ][ CHANNEL_INDEX ] = routeChannel;
					}
					if( routeProgram >= 0 ){
						this.channels[ channel ][ PROGRAM_INDEX ] = routeProgram;
					}
				}
			}
		}
	}
	
	protected void createUniquePort(){
		this.ports = 1;
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ PORT_INDEX ] = 0;
			this.channels[i][ CHANNEL_INDEX ] = i;
			this.channels[i][ PROGRAM_INDEX ] = -1;
		}
		for( int i = 0 ; i < this.programs.length ; i ++ ){
			this.programs[i][ PORT_INDEX ] = -1;
			this.programs[i][ PROGRAM_INDEX ] = -1;
			this.programs[i][ CHANNEL_INDEX ] = -1;
		}
	}
	
	protected void createMultiplePortsByChannel( int[][] routing ){
		this.ports = this.channels.length;
		
		// Initialize default channel values
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ PORT_INDEX ] = i;
			this.channels[i][ CHANNEL_INDEX ] = i;
			this.channels[i][ PROGRAM_INDEX ] = -1;
		}
		// Disable program routing
		for( int i = 0 ; i < this.programs.length ; i ++ ){
			this.programs[i][ PORT_INDEX ] = -1;
			this.programs[i][ PROGRAM_INDEX ] = -1;
			this.programs[i][ CHANNEL_INDEX ] = -1;
		}
		
		// load new routing
		for( int i = 0 ; i < routing.length ; i ++ ){
			if( routing[i].length == 3 ){
				int channel = routing[i][0];
				if( channel >= 0 && channel < this.channels.length ){
					this.channels[channel][ CHANNEL_INDEX ] = routing[i][1];
					this.channels[channel][ PROGRAM_INDEX ] = routing[i][2];
				}
			}
		}
	}
	
	protected void createMultiplePortsByProgram( int[][] routing ){
		this.ports = 1;
		
		// Initialize default channel values
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ PORT_INDEX ] = -1;
			this.channels[i][ CHANNEL_INDEX ] = -1;
			this.channels[i][ PROGRAM_INDEX ] = 0;
		}
		
		// Initialize default program values
		for( int i = 0 ; i < this.programs.length ; i ++ ){
			this.programs[i][ PORT_INDEX ] = 0;
			this.programs[i][ PROGRAM_INDEX ] = i;
			this.programs[i][ CHANNEL_INDEX ] = -1;
		}
		
		// load new routing
		for( int i = 0 ; i < routing.length ; i ++ ){
			if( routing[i].length == 4 ){
				int program = routing[i][0];
				if( program >= 0 && program < this.programs.length ){
					if( routing[i][1] > 0 ){
						// Create a dedicated port
						this.programs[program][ PORT_INDEX ] = this.ports ++ ;
					}
					this.programs[program][ CHANNEL_INDEX ] = routing[i][2];
					this.programs[program][ PROGRAM_INDEX ] = (routing[i][3] >= 0 ? routing[i][3] : program );
				}
				// Percussion Kit
				else if( program == 128 ){
					if( routing[i][1] > 0 ){
						// Create a dedicated port
						this.channels[9][ PORT_INDEX ] = this.ports ++ ;
					}
					this.channels[9][ CHANNEL_INDEX ] = routing[i][2];
					this.channels[9][ PROGRAM_INDEX ] = routing[i][3];
				}
			}
		}
	}
	
	public void loadSettings( TGConfigManager config ){
		int type = config.getIntConfigValue("jack.midi.ports.type", CREATE_UNIQUE_PORT );
		if( type == CREATE_MULTIPLE_PORTS_BY_PROGRAM ){
			// 128 instruments + 1 percussion kit
			int[][] routing = new int[129][];
			for( int i = 0 ; i < routing.length ; i ++ ){
				routing[i] = new int[4];
				routing[i][0] = i;
				routing[i][1] = config.getIntConfigValue("jack.midi.port.program-routing.port-" + i , 0 );
				routing[i][2] = config.getIntConfigValue("jack.midi.port.program-routing.to-channel-" + i , -1 );
				routing[i][3] = config.getIntConfigValue("jack.midi.port.program-routing.to-program-" + i , -1 );
			}
			this.createMultiplePortsByProgram( routing );
		}else if( type == CREATE_MULTIPLE_PORTS_BY_CHANNEL ){
			// 16 channels
			int[][] routing = new int[16][];
			for( int i = 0 ; i < routing.length ; i ++ ){
				routing[i] = new int[3];
				routing[i][0] = i;
				routing[i][1] = config.getIntConfigValue("jack.midi.port.channel-routing.to-channel-" + i , -1 );
				routing[i][2] = config.getIntConfigValue("jack.midi.port.channel-routing.to-program-" + i , -1 );
			}
			this.createMultiplePortsByChannel( routing );
		}else{
			this.createUniquePort();
		}
	}
}
