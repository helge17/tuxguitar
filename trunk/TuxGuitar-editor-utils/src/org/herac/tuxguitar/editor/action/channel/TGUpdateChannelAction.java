package org.herac.tuxguitar.editor.action.channel;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateChannelAction extends TGActionBase{
	
	public static final String NAME = "action.channel.update";
	
	public static final String ATTRIBUTE_BANK = "bank";
	public static final String ATTRIBUTE_PROGRAM = "program";
	public static final String ATTRIBUTE_VOLUME = "volume";
	public static final String ATTRIBUTE_BALANCE = "balance";
	public static final String ATTRIBUTE_CHORUS = "chorus";
	public static final String ATTRIBUTE_REVERB = "reverb";
	public static final String ATTRIBUTE_PHASER = "phaser";
	public static final String ATTRIBUTE_TREMOLO = "tremolo";
	public static final String ATTRIBUTE_NAME = "name";
	
	public TGUpdateChannelAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGChannel channel = ((TGChannel) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL));
		short bank = ((Short) findAttribute(context, ATTRIBUTE_BANK, Short.valueOf(channel.getBank()))).shortValue();
		short program = ((Short) findAttribute(context, ATTRIBUTE_PROGRAM, Short.valueOf(channel.getProgram()))).shortValue();
		short volume = ((Short) findAttribute(context, ATTRIBUTE_VOLUME, Short.valueOf(channel.getVolume()))).shortValue();
		short balance = ((Short) findAttribute(context, ATTRIBUTE_BALANCE, Short.valueOf(channel.getBalance()))).shortValue();
		short chorus = ((Short) findAttribute(context, ATTRIBUTE_CHORUS, Short.valueOf(channel.getChorus()))).shortValue();
		short reverb = ((Short) findAttribute(context, ATTRIBUTE_REVERB, Short.valueOf(channel.getReverb()))).shortValue();
		short phaser = ((Short) findAttribute(context, ATTRIBUTE_PHASER, Short.valueOf(channel.getPhaser()))).shortValue();
		short tremolo = ((Short) findAttribute(context, ATTRIBUTE_TREMOLO, Short.valueOf(channel.getTremolo()))).shortValue();
		String name = ((String) findAttribute(context, ATTRIBUTE_NAME, channel.getName()));
		
		getSongManager(context).updateChannel(song, channel.getChannelId(), bank, program, volume, balance, chorus, reverb, phaser, tremolo, name);
	}
	
	private Object findAttribute(TGActionContext context, String attribute, Object defaultValue) {
		Object value = context.getAttribute(attribute);
		if( value == null ) {
			value = defaultValue;
		}
		return value;
	}
}
