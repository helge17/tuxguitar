package org.herac.tuxguitar.midi.synth.impl;

import java.io.File;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

import media.sound.EmergencySoundbank;
import media.sound.ModelPatch;
import media.sound.SF2Instrument;

public class GervillSoundbankFactory {
	
	private static Instrument[][] defaultInstruments;
	
	private int locks;
	
	public boolean isBusy() {
		return (this.locks != 0);
	}
	
	public void create(final TGContext context, final GervillProgram program, final GervillSoundbankCallback callback) {
		this.locks ++;
		new Thread(new Runnable() {
			public void run() {
				try {
					createInstrument(context, program, callback);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					GervillSoundbankFactory.this.locks --;
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
		Instrument instrument = null;
		if( program.getSoundbankPath() != null && program.getSoundbankPath().length() > 0 ) {
			Soundbank soundbank = this.createSoundbank(context, program.getSoundbankPath());
			
			boolean percussion = (program.getBank() == 128);
			Patch patch = new ModelPatch((percussion ? 0 : program.getBank()), program.getProgram(), percussion);
			instrument = soundbank.getInstrument(patch);
		}
		if( instrument == null ) {
			instrument = getDefaultInstrument(context, program);
		}
		if( instrument != null ) {
			((SF2Instrument) instrument).setPatch(new Patch(0, 0));
		}
		return instrument;
	}
	
	private Instrument getDefaultInstrument(TGContext context, GervillProgram program) throws Exception {
		synchronized (GervillProcessorFactory.class) {
			if( defaultInstruments == null ) {
				defaultInstruments = new Instrument[129][128];
				
				Soundbank soundbank = null;
				String soundbankPath = new GervillSettings(context).getSoundbankPath();
				if( soundbankPath != null && soundbankPath.length() > 0 ) {
					soundbank = this.createSoundbank(context, soundbankPath);
				}
				if( soundbank == null ) {
					soundbank = EmergencySoundbank.createSoundbank();
				}
				if( soundbank != null ) {
					Instrument[] instruments = soundbank.getInstruments();
					for(Instrument instrument : instruments) {
						ModelPatch patch = (ModelPatch) instrument.getPatch();
						if( patch.getProgram() < 128 && patch.getBank() < 128 ) {
							defaultInstruments[patch.isPercussion() ? 128 : patch.getBank()][patch.getProgram()] = instrument;
						}
					}
				}
			}
		}
		return defaultInstruments[program.getBank()][program.getProgram()];
	}
	
	private Soundbank createSoundbank(TGContext context, String soundbankPath) {
		try {
			return MidiSystem.getSoundbank(new File(TGExpressionResolver.getInstance(context).resolve(soundbankPath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
