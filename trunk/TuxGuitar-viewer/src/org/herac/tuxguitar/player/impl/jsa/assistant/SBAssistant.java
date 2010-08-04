package org.herac.tuxguitar.player.impl.jsa.assistant;

import java.net.URL;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.swing.JOptionPane;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfig;

public class SBAssistant {
	
	private static SBAssistant instance;
	
	private Soundbank soundbank;
	
	private SBAssistant(){
		this.soundbank = null;
	}
	
	public static SBAssistant instance() {
		if (instance == null) {
			synchronized (SBAssistant.class) {
				instance = new SBAssistant();
			}
		}
		return instance;
	}

	public Soundbank getSoundbank(){
		if(this.soundbank == null){
			this.loadSoundbank();
		}
		return this.soundbank;
	}
	
	public void setSoundbank(Soundbank soundbank){
		this.soundbank = soundbank;
	}
	
	public void loadSoundbank(){
		try {
			if(TGConfig.SOUNDBANK_URL != null){
				URL url = new URL(TGConfig.SOUNDBANK_URL);
				if(isConfirmed()){
					System.out.println("Try to get soundbank from: " + TGConfig.SOUNDBANK_URL);
					this.setSoundbank( MidiSystem.getSoundbank( url.openStream() ) );
				}
			}
		}catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public boolean isConfirmed(){
		String title = "Soundbank Assistant";
		String message = "You don't seem to have any soundbank installed.\nDo you want to open one from internet?";
		int type = JOptionPane.YES_NO_OPTION;
		int result = JOptionPane.showConfirmDialog(TuxGuitar.instance().getShell(),message,title,type);
		return ( result == JOptionPane.YES_OPTION );
	}
}
