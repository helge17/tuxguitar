package org.herac.tuxguitar.io.gervill;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class MidiToAudioSynth {
	
	private static final AudioFormat SRC_FORMAT = MidiToAudioSettings.DEFAULT_FORMAT;
	
	private static final String MODEL_PATCH_CLASSNAME = "com.sun.media.sound.ModelPatch";
	private static final String SYNTHESIZER_CLASSNAME = "com.sun.media.sound.SoftSynthesizer";
	private static final String SYNTHESIZER_OPEN_STREAM_METHOD = "openStream";
	private static final String SYNTHESIZER_LOAD_DEFAULT_SOUNDBANK_PARAM = "load default soundbank";
	
	private static MidiToAudioSynth instance;
	
	private Synthesizer synthesizer;
	private AudioInputStream stream;
	private Receiver receiver;
	
	private MidiToAudioSynth(){
		this.stream = null;
		this.receiver = null;
		this.synthesizer = null;
	}
	
	public static MidiToAudioSynth instance(){
		if( instance == null ){
			instance = new MidiToAudioSynth();
		}
		return instance;
	}
	
	public Receiver getReceiver(){
		return this.receiver;
	}
	
	public AudioInputStream getStream(){
		return this.stream;
	}
	
	public void openSynth() throws Throwable {
		if( this.synthesizer == null || !this.synthesizer.isOpen() ){
			this.synthesizer = createSynthesizer();
			this.receiver = this.synthesizer.getReceiver();
			this.stream = invokeOpenStream(this.synthesizer, SRC_FORMAT, getDefaultInfo());
		}
	}
	
	public void closeSynth() throws Throwable {
		if( this.receiver != null ){
			this.receiver.close();
		}
		if( this.synthesizer != null && this.synthesizer.isOpen() ){
			this.synthesizer.close();
		}
		this.stream = null;
		this.receiver = null;
		this.synthesizer = null;
	}
	
	private Map<String, Object> getDefaultInfo(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SYNTHESIZER_LOAD_DEFAULT_SOUNDBANK_PARAM, new Boolean(false));
		return map;
	}
	
	public void loadSoundbank(List<Patch> patchList, String soundbankPath) throws Throwable {
		Soundbank soundbank = null;
		if( soundbankPath == null || soundbankPath.length() == 0 ){
			soundbank = this.synthesizer.getDefaultSoundbank();
		} else{
			soundbank = MidiSystem.getSoundbank(new File(soundbankPath));
		}
		
		Iterator<Patch> it = patchList.iterator();
		while( it.hasNext() ){
			Patch patch = (Patch)it.next();
			
			boolean percussion = (patch.getBank() == 128);
			int bank = (percussion ? 0 : patch.getBank());
			int program = patch.getProgram();
			
			Instrument instrument = soundbank.getInstrument(createModelPatch(bank, program, percussion));
			if( instrument != null ){
				this.synthesizer.loadInstrument(instrument);
			}
		}
	}
	
	public boolean isAvailable(){
		try {
			Class.forName(SYNTHESIZER_CLASSNAME, false, getClass().getClassLoader() );
			return true;
		} catch (Throwable throwable) {
			return false;
		}
	}
	
	private AudioInputStream invokeOpenStream(Synthesizer synthesizer, AudioFormat audioFormat, Map<String, Object> map) throws Throwable {
		Class<?>[] methodSignature = new Class[]{AudioFormat.class,Map.class};
		Object[] methodArguments = new Object[]{audioFormat, map};
		
		Class<?> classInstance = synthesizer.getClass();
		Method method = classInstance.getMethod(SYNTHESIZER_OPEN_STREAM_METHOD, methodSignature);
		Object returnValue = method.invoke(synthesizer, methodArguments);
		
		return (AudioInputStream)returnValue;
	}
	
	private Synthesizer createSynthesizer() throws Throwable {
		ClassLoader classLoader = getClass().getClassLoader();
		Class<?> classInstance = classLoader.loadClass(SYNTHESIZER_CLASSNAME);
		Object objectInstance = classInstance.getConstructor(new Class[0]).newInstance(new Object[0]);
		
		return (Synthesizer) objectInstance;
	}
	
	private Patch createModelPatch(int bank, int program, boolean percussion) throws Throwable {
		Class<?>[] constructorSignature = new Class[]{int.class, int.class, boolean.class};
		Object[] constructorArguments = new Object[]{new Integer(bank), new Integer(program), new Boolean(percussion)};
		
		ClassLoader classLoader = getClass().getClassLoader();
		Class<?> classInstance = classLoader.loadClass(MODEL_PATCH_CLASSNAME);
		Object objectInstance = classInstance.getConstructor(constructorSignature).newInstance(constructorArguments);
		
		return (Patch) objectInstance;
	}
}
