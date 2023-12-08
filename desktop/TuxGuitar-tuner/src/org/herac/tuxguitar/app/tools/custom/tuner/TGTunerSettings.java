package org.herac.tuxguitar.app.tools.custom.tuner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.util.error.TGErrorManager;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerSettings {
	
	protected int sampleSize;
	protected float sampleRate;
	protected String deviceName;
	protected double threshold;
	protected int bufferSize;
	protected int fftSize;
	protected int waitPeriod;
	static final int DEFAULT_BUFFER_SIZE = 4096; // 2^12
	static final int DEFAULT_FFT_SIZE = 16384; // 2^14
	static final int CHANNELS_NUMBER = 1;
	
	
	
	/** default settings, if faild to load from properties */
	public static TGTunerSettings getDefaults() {
		TGTunerSettings retValue = new TGTunerSettings();
		retValue.setSampleRate(11025);
		retValue.setSampleSize(8);
		retValue.setBufferSize(DEFAULT_BUFFER_SIZE);
		retValue.setFFTSize(DEFAULT_FFT_SIZE);
		retValue.setThreshold(0.03);
		retValue.setWaitPeriod(100);
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
				TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(ex);
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
		return new AudioFormat(this.getSampleRate(),this.getSampleSize(),1,true,false);
		// TODO: this is changed!!!!
		/* return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, // PCM encoding
				this.getSampleRate(), // sample rate
				this.getSampleSize(), // sample size (8-bit, 16-bit)
				1, // mono
				this.getFrameSize(), // 16-bit frame (was:4)
				this.getSampleRate(), // frame rate
				false // big-endian
				); */
	}

	/*
	private int getFrameSize() {
		return (this.sampleSize / 8) * CHANNELS_NUMBER;
	}
	*/


	public int getBufferSize() {
		return this.bufferSize;
	}
	public void setBufferSize(int bufferSize) {
		// TODO: adjust size in TGTuner also then
		this.bufferSize = bufferSize;
	}
	
	public int getFFTSize() {
		return this.fftSize;
	}
	public void setFFTSize(int size) {
		this.fftSize = size;
	}
	
	
	
	public float getSampleRate() {
		return this.sampleRate;
	}
	
	public double getThreshold() {
		return this.threshold;
	}
	
	public void setThreshold(double nt) {
		this.threshold = nt;
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

	public int getWaitPeriod() {
		return this.waitPeriod;
	}
	public void setWaitPeriod(int time) {
		this.waitPeriod = time;
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
