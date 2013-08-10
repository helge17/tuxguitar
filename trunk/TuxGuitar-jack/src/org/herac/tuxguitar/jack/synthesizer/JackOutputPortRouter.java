package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class JackOutputPortRouter {
	
	public static final int CREATE_UNIQUE_PORT = 0;
	public static final int CREATE_MULTIPLE_PORTS_BY_CHANNEL = 1;
	public static final int CREATE_MULTIPLE_PORTS_BY_PROGRAM = 2;
	
	private static final int PORT_INDEX = 0;
	private static final int CHANNEL_INDEX = 1;
	private static final int PROGRAM_INDEX = 2;
	private static final int BANK_INDEX = 3;
	
	private int ports;
	private int[] banks;
	private int[][] channels;
	private int[][][] programs;
	
	public JackOutputPortRouter(){
		this.ports = 1;
		this.banks = new int[16];
		this.channels = new int[16][4];
		this.programs = new int[129][128][4];
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
	
	public int getBankRoute( int channel ){
		if( channel >= 0 && channel < this.channels.length ){
			int route = this.channels[ channel ][ BANK_INDEX ];
			return ( route >= 0 ? route : this.getBank(channel) );
		}
		return 0;
	}
	
	public void setProgram( int channel , int program ){
		if( channel >= 0 && channel < this.channels.length && program >= 0 && program < this.programs.length ){
			if( this.channels[ channel ][ PROGRAM_INDEX ] >= 0 ){
				int bank = this.getBank(channel);
				int routePort = this.programs[ bank ][ program ][ PORT_INDEX ];
				int routeChannel = this.programs[ bank ][ program ][ CHANNEL_INDEX ];
				int routeProgram = this.programs[ bank ][ program ][ PROGRAM_INDEX ];
				int routeBank = this.programs[ bank ][ program ][ BANK_INDEX ];
				if( routePort >= 0 ){
					this.channels[ channel ][ PORT_INDEX ] = routePort;
				}
				if( routeChannel >= 0 ){
					this.channels[ channel ][ CHANNEL_INDEX ] = routeChannel;
				}
				if( routeProgram >= 0 ){
					this.channels[ channel ][ PROGRAM_INDEX ] = routeProgram;
				}
				if( routeBank >= 0 ){
					this.channels[ channel ][ BANK_INDEX ] = routeBank;
				}
			}
		}
	}
	
	public void setBank( int channel , int bank ){
		if( channel != 9 && channel >= 0 && channel < this.banks.length ){
			this.banks[channel] = bank;
		}
	}
	
	public int getBank( int channel ){
		if( channel >= 0 && channel < this.banks.length ){
			return (channel == 9 ? 128 : this.banks[channel]);
		}
		return 0;
	}
	
	protected void createUniquePort(){
		this.ports = 1;
		for( int channel = 0 ; channel < this.channels.length ; channel ++ ){
			this.channels[channel][ PORT_INDEX ] = 0;
			this.channels[channel][ CHANNEL_INDEX ] = channel;
			this.channels[channel][ PROGRAM_INDEX ] = -1;
			this.channels[channel][ BANK_INDEX ] = -1;
		}
		for( int bank = 0 ; bank < this.programs.length ; bank ++ ){
			for( int program = 0 ; program < this.programs[bank].length ; program ++ ){
				this.programs[bank][program][ PORT_INDEX ] = -1;
				this.programs[bank][program][ PROGRAM_INDEX ] = -1;
				this.programs[bank][program][ CHANNEL_INDEX ] = -1;
				this.programs[bank][program][ BANK_INDEX ] = -1;
			}
		}
	}
	
	protected void createMultiplePortsByChannel( int[][] routing ){
		this.ports = this.channels.length;
		
		// Initialize default channel values
		for( int channel = 0 ; channel < this.channels.length ; channel ++ ){
			this.channels[channel][ PORT_INDEX ] = channel;
			this.channels[channel][ CHANNEL_INDEX ] = channel;
			this.channels[channel][ PROGRAM_INDEX ] = -1;
			this.channels[channel][ BANK_INDEX ] = -1;
		}
		// Disable program routing
		for( int bank = 0 ; bank < this.programs.length ; bank ++ ){
			for( int program = 0 ; program < this.programs[bank].length ; program ++ ){
				this.programs[bank][program][ PORT_INDEX ] = -1;
				this.programs[bank][program][ PROGRAM_INDEX ] = -1;
				this.programs[bank][program][ CHANNEL_INDEX ] = -1;
				this.programs[bank][program][ BANK_INDEX ] = -1;
			}
		}
		
		// load new routing
		for( int i = 0 ; i < routing.length ; i ++ ){
			if( routing[i].length == 4 ){
				int channel = routing[i][0];
				if( channel >= 0 && channel < this.channels.length ){
					this.channels[channel][ CHANNEL_INDEX ] = routing[i][1];
					this.channels[channel][ PROGRAM_INDEX ] = routing[i][2];
					this.channels[channel][ BANK_INDEX ] = routing[i][3];
				}
			}
		}
	}
	
	protected void createMultiplePortsByProgram( int[][][] routing ){
		this.ports = 1;
		
		// Initialize default channel values
		for( int i = 0 ; i < this.channels.length ; i ++ ){
			this.channels[i][ PORT_INDEX ] = -1;
			this.channels[i][ CHANNEL_INDEX ] = -1;
			this.channels[i][ PROGRAM_INDEX ] = 0;
			this.channels[i][ BANK_INDEX ] = 0;
		}
		
		// Initialize default program values
		for( int bank = 0 ; bank < this.programs.length ; bank ++ ){
			for( int program = 0 ; program < this.programs[bank].length ; program ++ ){
				this.programs[bank][program][ PORT_INDEX ] = 0;
				this.programs[bank][program][ BANK_INDEX ] = bank;
				this.programs[bank][program][ PROGRAM_INDEX ] = program;
				this.programs[bank][program][ CHANNEL_INDEX ] = -1;
			}
		}
		
		// load new routing
		for( int bank = 0 ; bank < routing.length ; bank ++ ){
			for( int prg = 0 ; prg < routing[bank].length ; prg ++ ){
				if( routing[bank][prg].length == 5 ){
					int program = routing[bank][prg][0];
					if( routing[bank][prg][1] > 0 ){
						// Create a dedicated port
						this.programs[bank][program][ PORT_INDEX ] = this.ports ++ ;
					}
					this.programs[bank][program][ CHANNEL_INDEX ] = routing[bank][prg][2];
					this.programs[bank][program][ PROGRAM_INDEX ] = (routing[bank][prg][3] >= 0 ? routing[bank][prg][3] : program );
					this.programs[bank][program][ BANK_INDEX ] = (routing[bank][prg][4] >= 0 ? routing[bank][prg][4] : bank );
				}
			}
		}
	}
	
	public void loadSettings( TGConfigManager config ){
		int type = config.getIntegerValue("jack.midi.ports.type", CREATE_UNIQUE_PORT );
		if( type == CREATE_MULTIPLE_PORTS_BY_PROGRAM ){
			// 128 instruments bank + 1 percussion kit bank
			int[][][] routing = new int[129][128][];
			for( int bank = 0 ; bank < routing.length ; bank ++ ){
				for( int prg = 0 ; prg < routing[bank].length ; prg ++ ){
					String portId = (bank + "-" + prg);
					routing[bank][prg] = new int[5];
					routing[bank][prg][0] = prg;
					routing[bank][prg][1] = config.getIntegerValue("jack.midi.port.program-routing.port-" + portId , 0 );
					routing[bank][prg][2] = config.getIntegerValue("jack.midi.port.program-routing.to-channel-" + portId , -1 );
					routing[bank][prg][3] = config.getIntegerValue("jack.midi.port.program-routing.to-program-" + portId , -1 );
					routing[bank][prg][4] = config.getIntegerValue("jack.midi.port.program-routing.to-bank-" + portId , -1 );
				}
			}
			this.createMultiplePortsByProgram( routing );
		}else if( type == CREATE_MULTIPLE_PORTS_BY_CHANNEL ){
			// 16 channels
			int[][] routing = new int[16][];
			for( int i = 0 ; i < routing.length ; i ++ ){
				routing[i] = new int[4];
				routing[i][0] = i;
				routing[i][1] = config.getIntegerValue("jack.midi.port.channel-routing.to-channel-" + i , -1 );
				routing[i][2] = config.getIntegerValue("jack.midi.port.channel-routing.to-program-" + i , -1 );
				routing[i][3] = config.getIntegerValue("jack.midi.port.channel-routing.to-bank-" + i , -1 );
			}
			this.createMultiplePortsByChannel( routing );
		}else{
			this.createUniquePort();
		}
	}
}
