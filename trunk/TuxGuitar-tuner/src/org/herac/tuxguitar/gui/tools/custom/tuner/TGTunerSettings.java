package org.herac.tuxguitar.gui.tools.custom.tuner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.herac.tuxguitar.gui.util.MessageDialog;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerSettings {
	
	protected int sampleSize;
	protected float sampleRate;
	protected String deviceName;
	protected int bufferSize;
	static final int DEFAULT_BUFFER_SIZE = (1 << TGTuner.LOG2_FFTSIZE); // was /4
	static final int CHANNELS_NUMBER = 1;
	
	
	
	/** default settings, if faild to load from properties */
	public static TGTunerSettings getDefaults() {
		TGTunerSettings retValue = new TGTunerSettings();
//		retValue.setDeviceName(null);
		retValue.setSampleRate(22050);
		retValue.setSampleSize(16);
		retValue.setBufferSize(DEFAULT_BUFFER_SIZE);
		return retValue;
	}

	
	
	/** gets dataline from format specification */
	protected static TargetDataLine getDataLine(TGTunerSettings settings) throws TGTuner.TGTunerException {
		TargetDataLine	targetDataLine = null;

		if (settings!=null) {
			// get info for initialization
			DataLine.Info info = settings.getDataLineInfo();
			
			try {
				
				targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
				
			} catch (Exception ex) {
				MessageDialog.errorMessage(ex);
			}
		}
		else 
			throw new TGTuner.TGTunerException("Could not retrieve data from the input. Check your system device settings.");
		
		return targetDataLine;
	}

	/** Creates DataLine.Info based on settings */
	protected DataLine.Info getDataLineInfo() {
				return new DataLine.Info(TargetDataLine.class,
													this.getAudioFormat(), this.getBufferSize());
				
	}

	/** creates AudioFormat based on settings */
	public AudioFormat getAudioFormat() {
		//return new AudioFormat(8000.0F,8,1,true,false);
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, // PCM encoding
				this.getSampleRate(), // sample rate
				this.getSampleSize(), // sample size (8-bit, 16-bit)
				1, // mono
				this.getFrameSize(), // 16-bit frame (was:4)
				this.getSampleRate(), // frame rate
				false // big-endian
				);
	}

	
	private int getFrameSize() {
		return (this.sampleSize / 8) * CHANNELS_NUMBER;
	}



	public int getBufferSize() {
		return this.bufferSize;
	}
	public void setBufferSize(int bufferSize) {
		// TODO: adjust size in TGTuner also then
		this.bufferSize = bufferSize;
	}
	public float getSampleRate() {
		return this.sampleRate;
	}
	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}
	public int getSampleSize() {
		return this.sampleSize;
	}
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}



	public static TGTunerSettings loadTuxGuitarSettings() throws TGTuner.TGTunerException {
		// TODO: load system properties and throw exception if failed to load
		return TGTunerSettings.getDefaults();
		//return null;
	}
	
	/* MAYBE USEFUL CODE
	 * 
	 * Port lineIn;
FloatControl volCtrl;
try {
  mixer = AudioSystem.getMixer(null);
  lineIn = (Port)mixer.getLine(Port.Info.LINE_IN);
  lineIn.open();
  volCtrl = (FloatControl) lineIn.getControl(
      FloatControl.Type.VOLUME);
  // Assuming getControl call succeeds, 
  // we now have our LINE_IN VOLUME control.
} catch (Exception e) {
  System.out.println("Failed trying to find LINE_IN"
    + " VOLUME control: exception = " + e);
}
float newValue = 2.0F;
if (volCtrl != null)
  // This changes the volume of the signal flowing though the line that "owns" the control.
  volCtrl.setValue(newValue); 
	 * 
	 */
	
}
