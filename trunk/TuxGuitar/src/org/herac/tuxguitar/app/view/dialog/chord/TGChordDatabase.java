package org.herac.tuxguitar.app.view.dialog.chord;

/**
 * Stores the information about the chord name, structure and
 * alteration abilities into a list
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class TGChordDatabase {
	
	/**
	 * fills all the necessary data into chords List consisted out of ChordInfo-s
	 * 
	 * If you want to change it, please contact me on
	 * nikola.kolarovic@gmail.com
	 */
	private static final ChordInfo[] data = new ChordInfo[]{
		
		// Major--------------------------
		new ChordInfo("M",new int[]{ 1, 5, 8 }),
		
		// 7--------------------------
		new ChordInfo("7",new int[]{ 1, 5, 8, 11 }),
		
		// 7M--------------------------
		// hard-coded index used in ChordRecognizer, below comment "determine seventh", line 315 now
		new ChordInfo("maj7",new int[]{ 1, 5, 8, 12 }),
		
		// 6--------------------------
		new ChordInfo("6",new int[]{ 1, 5, 8, 10 }),
		
		// m--------------------------
		// index 4 hard-coded in ChordRecognizer line 220, so it is not so unusual
		new ChordInfo("m",new int[]{ 1, 4, 8 }),
		
		// m7--------------------------
		new ChordInfo("m7",new int[]{ 1, 4, 8, 11 }),
		
		// m7M--------------------------
		new ChordInfo("m/maj7",new int[]{ 1, 4, 8, 12 }),
		
		// m6--------------------------
		new ChordInfo("m6",new int[]{ 1, 4, 8, 10 }),
		
		// sus2--------------------------
		new ChordInfo("sus2",new int[]{ 1, 3, 8 }),
		
		// sus4--------------------------
		new ChordInfo("sus4",new int[]{ 1, 6, 8 }),
		
		// 7sus2--------------------------
		new ChordInfo("7sus2",new int[]{ 1, 3, 8, 11 }),
		
		// 7sus4--------------------------
		new ChordInfo("7sus4",new int[]{ 1, 6, 8, 11 }),
		
		// below indexes are hard-coded in ChordRecognizer line 311 now
		
		// dim--------------------------
		new ChordInfo("dim",new int[]{ 1, 4, 7 }),
		
		// dim7--------------------------
		new ChordInfo("dim7",new int[]{ 1, 4, 7, 10 }),
		
		// aug--------------------------
		new ChordInfo("aug",new int[]{ 1, 5, 9 }),
		
		// 5--------------------------
		// index <last> hard-coded in ChordRecognizer line 220, so it is not so unusual
		new ChordInfo("5",new int[]{ 1, 8 }),
		
	};
	
	public static int length(){
		return data.length;
	}
	
	public static ChordInfo get(int index){
		return data[index];
	}
	
	/** chord data structure, contains all info for chord formation **/
	public static class ChordInfo {
		
		private String name;
		private int[] requiredNotes;
		
		public ChordInfo(String name,int[] requiredNotes){
			this.name = name;
			this.requiredNotes = requiredNotes;
		}
		
		public String getName() {
			return this.name;
		}
		
		public int[] getRequiredNotes() {
			return this.requiredNotes;
		}
		
		public int[] cloneRequireds() {
			int[] requiredNotes = new int[this.requiredNotes.length];
			for(int i = 0; i < requiredNotes.length; i ++){
				requiredNotes[i] = this.requiredNotes[i];
			}
			return requiredNotes;
		}
	}
}