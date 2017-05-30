package org.herac.tuxguitar.midi.synth.impl;

import java.io.File;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

import com.sun.media.sound.EmergencySoundbank;
import com.sun.media.sound.ModelPatch;

public class GervillSoundbankFactory {
	
	private static Soundbank defaultSoundbank;
	
	public void create(final TGContext context, final GervillProgram program, final GervillSoundbankCallback callback) {
		new Thread(new Runnable() {
			public void run() {
				try {
					createInstrument(context, program, callback);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void createInstrument(TGContext context, GervillProgram program, GervillSoundbankCallback callback) throws Exception {
		Instrument instrument = this.createInstrument(context, program);
		if( instrument != null ){
			callback.onCreate(instrument);
		}
	}
	
	private Instrument createInstrument(TGContext context, GervillProgram program) throws Exception {
		Soundbank soundbank = this.createSoundbank(context, program);
		
		boolean percussion = (program.getBank() == 128);
		Patch patch = new ModelPatch((percussion ? 0 : program.getBank()), program.getProgram(), percussion);
		return soundbank.getInstrument(patch);
	}
	
	private Soundbank createSoundbank(TGContext context, GervillProgram program) throws Exception {
		if( program.getSoundbankPath() != null && program.getSoundbankPath().length() > 0 ) {
			return this.createSoundbank(context, program.getSoundbankPath());
		}
		
		if( defaultSoundbank == null ) {
			String soundbankPath = new GervillSettings(context).getSoundbankPath();
			if( soundbankPath != null && soundbankPath.length() > 0 ) {
				defaultSoundbank = this.createSoundbank(context, soundbankPath);
			}
			if( defaultSoundbank == null ) {
				defaultSoundbank = EmergencySoundbank.createSoundbank();
			}
		}
		return defaultSoundbank;
	}
	
	private Soundbank createSoundbank(TGContext context, String soundbankPath) throws Exception {
		return MidiSystem.getSoundbank(new File(TGExpressionResolver.getInstance(context).resolve(soundbankPath)));
	}
}
