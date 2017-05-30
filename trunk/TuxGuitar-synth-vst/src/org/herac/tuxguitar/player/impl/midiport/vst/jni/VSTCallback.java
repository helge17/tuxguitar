package org.herac.tuxguitar.player.impl.midiport.vst.jni;

import java.util.HashMap;
import java.util.Map;

public class VSTCallback {
	
	private static final int audioMasterAutomate = 0;
	private static final int audioMasterVersion = 1;
	private static final int audioMasterWantMidi = 6;
	private static final int audioMasterGetTime = 7;
	private static final int audioMasterGetCurrentProcessLevel = 23;
	
	private static final Map<Long, VSTEffect> audioEffects = new HashMap<Long, VSTEffect>();
	
	public static void addEffect( VSTEffect effect ){
		Long key = new Long( effect.getInstance() );
		if( !audioEffects.containsKey(key) ){
			audioEffects.put( key , effect );
		}
	}
	
	public static void removeEffect( VSTEffect effect ){
		Long key = new Long( effect.getInstance() );
		if( audioEffects.containsKey(key) ){
			audioEffects.remove( key );
		}
	}
	
	public static VSTEffect getEffect( long instance ){
		Long key = new Long( instance );
		if( audioEffects.containsKey(key) ){
			return (VSTEffect)audioEffects.get( key );
		}
		return null;
	}
	
	public static long invoke( long instance, long opcode, long index, long value, long ptr, float opt){
		System.out.println("VSTCallback => : " + opcode);
		if( opcode == audioMasterVersion ){
			return 2400;
		}
		else if( opcode == audioMasterAutomate ){
			return 1;
		}
		else if( opcode == audioMasterWantMidi ){
			return 0;
		}
		else if( opcode == audioMasterGetTime ){
			return 0;
		}
		else if( opcode == audioMasterGetCurrentProcessLevel ){
			return 0;
		}
		
		System.out.println("Unknown");
		
		return 0;
	}
}
