package org.herac.tuxguitar.app.tools.custom.tuner;

/**
 * Class that represents a frequency buffer that eliminates large frequency fluctuations.
 * 
 * It is implemented as a queue list, where you put each frequency, and based on QUEUE_SIZE
 * last frequencies it determines what current dominant frequency should be.
 * 
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTunerQueue {
	
	/** size of the queue */
	int QUEUE_SIZE = 5;
	
	/** pointer to index of the newest element */
	protected int head = 0;
	
	/** the frequency queue itself */
	protected double[] queue;
	
	private double[] similars;
	
	
	// TODO: tweak the tollerances
	private final double upperTolerance = 1.05; // +5%
	private final double lowerTolerance = 0.95; // -5%
	
	
	public TGTunerQueue() {
		this.queue = new double[this.QUEUE_SIZE];
		this.similars = new double[this.QUEUE_SIZE];
		this.clear();
	}

	/** add new frequency to a queue */
	public void add(double newValue) {
		this.head = (++this.head) % this.QUEUE_SIZE;
		this.queue[this.head] = newValue;
	}
	
	/** 
	 * 
	 * @return approximated frequency 
	 */
	public double getFreqApproximation() {
		
		for (int i=0; i<this.QUEUE_SIZE; i++)
			this.similars[i]=-2; // -2, because it will always find 1 exact frequency when i==j
		
		for (int i=0; i<this.QUEUE_SIZE; i++) {
			if (this.queue[i]!=-1)
				for (int j=0; j<this.QUEUE_SIZE; j++) {
					// exact frequency with tollerance
					if (this.queue[i] > (this.queue[j]*this.lowerTolerance) && 
						this.queue[i] < (this.queue[j]*this.upperTolerance) )
							this.similars[i]=this.similars[i]+2;
					
					// half frequency with tollerance
					if (this.queue[i]/2 > (this.queue[j]*this.lowerTolerance) &&
						this.queue[i]/2 < (this.queue[j]*this.upperTolerance) )
							this.similars[i]++;
					
					// double frequency with tollerance
					if (this.queue[i]*2 > (this.queue[j]*this.lowerTolerance) &&
						this.queue[i]*2 < (this.queue[j]*this.upperTolerance) )
							this.similars[i]++;
				}
		}
		
		
		// find one with max similars
		int maxIndex = 0;
		for (int i=1; i<this.QUEUE_SIZE; i++)
			if (this.similars[i]>=this.similars[maxIndex])
				maxIndex=i;
		
		
		
/*		
		int test=(this.head+1)%this.QUEUE_SIZE;
		System.out.print(Math.floor(queue[head])+ " ");
		while (test!=this.head) {
			System.out.print(Math.floor(queue[test])+ " ");
			test = (++test)%this.QUEUE_SIZE;
		
		}
		System.out.println(" =  "+queue[maxIndex]%110+" **");
*/
		
		return this.queue[maxIndex];
		//return this.queue[head];
		
	}
	
	
	/** clears the queue data */
	public void clear() {
		for (int i=0; i<this.QUEUE_SIZE; i++)
			this.queue[i]=-1;
		this.head = 0;
	}
	
}
