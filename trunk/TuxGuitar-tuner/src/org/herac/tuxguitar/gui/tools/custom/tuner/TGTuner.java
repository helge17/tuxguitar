package org.herac.tuxguitar.gui.tools.custom.tuner;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import craigl.spectrumanalyzer.FFT;


/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 * 
 * 
 * Having great help analizyng project:
 * 
 * GuitarTuner developed by OpenStudio
 * www.openstudio.fr
 * Copyright (2005) Arnault Pachot.
 * Project URL: http://sourceforge.net/projects/guitartuner
 *
 */
public class TGTuner extends Thread {
	protected TGTunerListener mainWindow = null;
	protected boolean canceled;
	protected boolean paused;
	protected TGTunerSettings settings;
	protected TargetDataLine dataLine;
	protected TGTunerQueue queue;
	
	static final int LOG2_FFTSIZE = 14;
	protected int FFT_SIZE; 
	protected FFT fft;
	protected double[] ar;
	protected double[] ai;
	protected byte[] data;
	protected double rate;
	
	protected double maximumFrequency;
	protected double minimumFrequency;
	protected double wantedFrequency;
	
	TGTuner(TGTunerListener mainWindow) {
		this.mainWindow = mainWindow;
		this.canceled = false;
		this.paused = false;
		this.queue = new TGTunerQueue();
		try {
			this.settings = TGTunerSettings.loadTuxGuitarSettings();
			this.dataLine = TGTunerSettings.getDataLine(this.settings);
		} catch (Exception ex) {
			this.mainWindow.fireException(ex);
		}

	}


	public void run() {
		
		// TODO: remove this benchmark
		int cycles=0;
		long timePassed=0;
		
		
		if (this.dataLine!=null) {
			this.openDataLine();
		}

		while (!this.canceled) {
		  if (this.dataLine!=null)
			if (!this.paused && this.dataLine.isOpen()) {
				 synchronized(this) {
				
				
					// read from the input
					this.dataLine.read(this.data,0,this.settings.getBufferSize());
					
					// preapare the arrays for FFT
					for (int i=0; i<this.settings.getBufferSize(); i++) {
						this.ar[i] = this.data[i]; // implicit cast to double
						this.ai[i] = 0.0f;
					}
					
					// when buffer size is smaller than FFT_SIZE 
					for (int i=this.settings.getBufferSize(); i<this.FFT_SIZE; i++) {
						this.ar[i] = 0.0f;
						this.ai[i] = 0.0f;
					}
					
					/////////////////////////////////////////////////////////////
					// TODO: look at the benchmark, uncoment the other one and comment the first one
					// TODO: The second one is a lot faster here (3 times), maybe because it is in the local class?
					cycles++;
					long startTime = System.currentTimeMillis();
					
					//this.fft.doFFT(this.ar, this.ai, false);
					TGTuner.computeFFT(1, this.FFT_SIZE, this.ar, this.ai);
					
					timePassed+=System.currentTimeMillis()-startTime;
					/////////////////////////////////////////////////////////////
					
					
					// ------ determine the dominant frequency -------
					double frequency = -1;
					double maxAmplitude = this.settings.getTreshold(); // frequency noise gate can be defined here 0.01 is OK
					
					
					// TODO: maybe to analyze only the data around the area of interest
					// TODO: and put a "freqyency noise gate"
					// start has to be at least 1, because we want to skip the DC component!
					for (int i=(int)Math.round(this.minimumFrequency / this.rate); i < Math.round(this.maximumFrequency / this.rate); i++) {
						double curFreq = i * this.rate;
						// z = x*i + y*j
						// power = |z| = sqrt(x^2 + y^2)
						double power = Math.sqrt(Math.pow(this.ar[i], 2) + Math.pow(this.ai[i], 2));
						
						if (power > maxAmplitude) {
							maxAmplitude = power;
							frequency = curFreq;
						}
						
					}
					// TODO: I often get some noise (75Hz on my guitar) for dominant amplitude when
					// TODO: not playing. The solution is to put bigger noise gate (user can test and modify?!)
					// TODO: or to put short range of frequencies tested.
					//System.out.println("Max Amplitude: "+maxAmplitude);
					
					//** buffer the frequency
					this.queue.add(frequency);
					
					// fire the frequency event on GUI
					this.mainWindow.fireFrequency(this.queue.getFreqApproximation());
					
					// flush the dataline, so the new data is received
					this.dataLine.flush();
			
				 }  // end of synchronized block
			}
		  
		    // TODO: Julian, what did you mean by this?
			/*
			synchronized(this.sequencer) {
				while( this.sequencer.process() ){
					this.sequencer.wait( TIMER_DELAY );
				}
			}
			*/
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		
		this.closeDataLine();
		System.out.println(" Average FFT time: "+(double)timePassed/(double)cycles);
		
	}
	
	
	protected void computeFFTParams() {
		if (this.settings!=null) {
				this.FFT_SIZE = ( 1 << TGTuner.LOG2_FFTSIZE ); // 2^(LOG2_FFTSIZE)
				this.fft = new FFT(TGTuner.LOG2_FFTSIZE); // instantiate proper FFT alrgorithm
				this.data = new byte[this.settings.getBufferSize()]; // data buffer
				this.ar = new double[this.FFT_SIZE]; // create real array for FFT
				this.ai = new double[this.FFT_SIZE]; // create imaginary array for FFT
				this.rate = ((double) this.settings.sampleRate) / this.FFT_SIZE;
				int[] tuning = this.mainWindow.getTuning();
				this.maximumFrequency = getNoteFrequency(tuning[0]+3); // 3 frets higher that the thiniest string
				this.minimumFrequency = getNoteFrequency(tuning[tuning.length-1]-3); // 3 frets lower that the thickest string
				
				
				System.out.println(" Minimal freq diff = "+this.settings.getMinimalFrequencyDiff());
				System.out.println(" Time to fill the buffer = "+ this.settings.getTimeToFillBuffer());
		}
	}

	public void setCanceled(boolean canceled) {
			this.canceled = canceled;
	}

	public void pause() {
			this.paused = true;
		
	}

	public void resumeFromPause() {
			this.openDataLine();
			this.paused = false;
			this.queue.clear();
	}


	public void setSettings(TGTunerSettings settings) {
		this.closeDataLine();
		synchronized(this) {
			this.settings = settings;
			try {
				this.dataLine = TGTunerSettings.getDataLine(settings);
			} catch (Exception ex) {
				this.mainWindow.fireException(ex);
			}
		}

	}

	public TGTunerSettings getSettings() {
			return this.settings;
	}
	
	public void openDataLine() {
     if (this.dataLine!=null) {
   	  synchronized(this) {
			this.computeFFTParams();
			try {
				this.dataLine.open();
				this.dataLine.start();
				//this.dataLine.open(this.settings.getAudioFormat(), this.settings.getBufferSize());
			} catch (LineUnavailableException e) {
				System.out.println("------- TGTuner: openDataLine() exception -------");
				e.printStackTrace();
			}
		}
	  }
	}
	
	public void closeDataLine() {
	  synchronized(this) {
		if (this.dataLine!=null) {
				this.dataLine.stop();
				this.dataLine.close();
			}
	  }
	}
	
	public double getNoteFrequency(int midiNote) {
		// A = 110Hz = MidiNote(45)
		return 110 * Math.pow(2.0, (float)((midiNote-45))/12.0 );
	}
	
	
	 public static void computeFFT(int sign, int n, double[] ar, double[] ai)
	   {
	      double scale = 2.0 / (double)n;
	      int i, j;
	      for(i = j = 0; i < n; ++i)
	      {
	         if (j >= i)
	         {
	            double tempr = ar[j] * scale;
	            double tempi = ai[j] * scale;
	            ar[j] = ar[i] * scale;
	            ai[j] = ai[i] * scale;
	            ar[i] = tempr;
	            ai[i] = tempi;
	         }
	         int m = n/2;
	         while ((m >= 1) && (j >= m))
	         {
	            j -= m;
	            m /= 2;
	         }
	         j += m;
	      }
	      int mmax, istep;
	      for(mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax)
	      {
	         double delta = sign * Math.PI / (double)mmax;
	         for(int m = 0; m < mmax; ++m)
	         {
	            double w = m * delta;
	            double wr = Math.cos(w);
	            double wi = Math.sin(w);
	            for(i = m; i < n; i += istep)
	            {
	               j = i + mmax;
	               double tr = wr * ar[j] - wi * ai[j];
	               double ti = wr * ai[j] + wi * ar[j];
	               ar[j] = ar[i] - tr;
	               ai[j] = ai[i] - ti;
	               ar[i] += tr;
	               ai[i] += ti;
	            }
	         }
	         mmax = istep;
	      }
	   }
	

	 
	 
	 
	 static class TGTunerException extends Exception {

		private static final long serialVersionUID = -1860514279319403845L;

		public TGTunerException(String string) {
			super(string);
		}
		 
	 }





	public double getWantedFrequency() {
		return this.wantedFrequency;
	}


	public void setWantedFrequency(double wantedFrequency) {
		synchronized(this) {
			this.wantedFrequency = wantedFrequency;
		}
	}
	
}
