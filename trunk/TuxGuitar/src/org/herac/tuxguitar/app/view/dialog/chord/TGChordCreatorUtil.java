/*

 * Created on 31-dic-2005

 * Redesigned on 05-March-2007

 * 

 */

package org.herac.tuxguitar.app.view.dialog.chord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.song.models.TGChord;

/**
 * 
 * Class that helps to create a chord from information put in ChordSelector
 * dialog.
 * 
 * Also contains ChordDatabase static field.
 * 
 * @author Nikola Kolarovic
 * 
 * @author julian
 * 
 */
public class TGChordCreatorUtil {
	
	/**
	 * Maximum number of strings variable - has twin in TrackPropertiesAction
	 * class
	 */
	public static final int MAX_STRINGS = 7;
	
	/** Maximum fret distance for a chord */
	
	public static final int MAX_FRET_SPAN = 5;
	
	/** mark for bass note type **/
	private final int BASS_INDEX = -1;
	
	/** mark for essential note in a chord - MUST be in */
	private final int ESSENTIAL_INDEX = -2;
	
	/** mark for essential note in a chord - PENALTY if not in */
	private final int NOT_ESSENTIAL_INDEX = -3;
	
	/** Keep the Thread control */
	private static long runningProcess;
	
	// ------ attributes ------
	
	//protected ChordInfo info;
	private long processId;
	
	private TGChordCreatorListener listener;
	
	/** the alteration List selectionIndex */
	private int alteration;
	
	private int chordIndex;
	
	/** essential notes for the chord (from ChordInfo) */
	private int[] requiredNotes;
	
	/** notes that expand the chord (add+-) */
	private int[] expandingNotes;
	
	/** is the fifth altered */
	private int add5 = 0;
	
	/** name of a chord */
	private String chordName = null;
	
	private int bassTonic; 
	
	private int chordTonic;
	
	/** current tunning */
	private int[] tuning;
	
	private TGChordCreatorUtil(long processId,TGChordCreatorListener listener){
		this.processId = processId;
		this.listener = listener;
	}
	
	public boolean isValidProcess(){
		return (this.processId == runningProcess);
	}
	
	public static long getNewProcess(){
		return (++ runningProcess);
	}
	
	public static void getChords(final TGChordCreatorListener listener,
	                             final int[] tuning,
	                             final int chordIndex,
	                             final int alteration,
	                             final int plusMinus,
	                             final boolean add,
	                             final int add5,
	                             final int add9,
	                             final int add11,
	                             final int bassTonic,
	                             final int chordTonic,
	                             final boolean sharp){
		
		final TGChordCreatorUtil chordCreator = new TGChordCreatorUtil(getNewProcess(), listener );
		new Thread(new Runnable() {
			public void run() {
				chordCreator.getChords( tuning, chordIndex, alteration, plusMinus, add, add5, add9, add11, bassTonic, chordTonic, sharp);
			}
		}).start();
	}
	
	protected void getChords(int[] tuning,
	                         int chordIndex,
	                         int alteration,
	                         int plusMinus,
	                         boolean add,
	                         int add5,
	                         int add9,
	                         int add11,
	                         int bassTonic,
	                         int chordTonic,
	                         boolean sharp) {
		
		if(!isValidProcess()){
			return;
		}
		
		this.add5 = add5;
		
		this.tuning = tuning;
		
		this.chordIndex = chordIndex;
		
		this.chordTonic = chordTonic;
		
		this.bassTonic = bassTonic;
		
		this.alteration = alteration;
		
		this.chordName = new TGChordNamingConvention().createChordName(this.chordTonic,
		                                                             this.chordIndex,
		                                                             this.alteration,
		                                                             plusMinus,
		                                                             add,
		                                                             add5,
		                                                             add9,
		                                                             add11,
		                                                             this.bassTonic,
		                                                             sharp);
		
		
		// find the notes that expand the chord
		if (this.alteration!=0) {
			if (add) {
				this.expandingNotes = new int[1];
				this.expandingNotes[0]= getAddNote(this.alteration-1,plusMinus);
			}
			else { // not just add...
				// 9+- = 7b !9(+-)    (index=1)
				// 11+- = 7b !11(+-) 9(+-)  (index=2)
				// 13+- = 7b !13(+-) 9(+-) 11(+-) (index=3)
				this.expandingNotes = new int[1+this.alteration];
				this.expandingNotes[0] = 11; //7b
				this.expandingNotes[1] = getAddNote(this.alteration-1,plusMinus); //this.alteration+-
				
				// rest
				for (int i=2; i<=this.alteration; i++)
					this.expandingNotes[i]=getAddNote(i-2, i==2 ? add9 : add11); // @2=add9+-, @3=add11+- tone
			}
			
		}
		else this.expandingNotes=new int[0];
		
		
		
		// Required notes
		//this.requiredNotes = ((ChordDatabase.ChordInfo)new ChordDatabase().getChords().get(chordIndex)).cloneRequireds();
		this.requiredNotes = TGChordDatabase.get(chordIndex).cloneRequireds();
		//IT DON'T BUILD UNDER JRE1.4
		//this.requiredNotes = ((ChordDatabase.ChordInfo) ChordCreatorUtil.getChordData().getChords().get(chordIndex)).getRequiredNotes().clone();
		
		
		// adjust the subdominant if needed
		if (add5!=0) {
			for (int i=0; i<this.requiredNotes.length; i++)
				if (this.requiredNotes[i]==8) // alternate the subdominant
					this.requiredNotes[i]+=(add5==1 ? 1 : -1);
		}
		
		// first count different from -1
		int count = 0;
		for (int i=0; i<this.requiredNotes.length; i++) {
			this.requiredNotes[i]=checkForOverlapping(this.requiredNotes[i]);
			if (this.requiredNotes[i]!=-1)
				count++;
		}
		// then fill in the new array
		int[] tempNotes = new int[count];
		count = 0;
		for (int i=0; i<this.requiredNotes.length; i++)
			if (this.requiredNotes[i]!=-1) {
				tempNotes[count]=this.requiredNotes[i];
				count++;
			}
		// and substitute them
		this.requiredNotes = tempNotes;
		
		//return getChords();
		if(isValidProcess()){
			List<TGChord> chords = getChords();
			if(chords != null && isValidProcess()){
				this.listener.notifyChords(this, chords);
			}
		}
	}
	
	/** We have to make sure that if required note is already inside
	 * expanding notes array so we don't put it twice...
	 */
	protected int checkForOverlapping(int checkIt) {
		for (int i=0; i<this.expandingNotes.length; i++)
			if (this.expandingNotes[i]==checkIt)
				return -1;
		return checkIt;
	}
	
	/**
	 * 
	 * Creates the chord combinations out of given data and then uses some kind
	 * of
	 * 
	 * heuristics to check the most suitable formations.
	 * 
	 * @return the list of TGChord structures that are most suitable
	 * 
	 */
	private List<TGChord> getChords() {
		if(!isValidProcess()){
			return null;
		}
		List<List<StringValue>> potentialNotes = makePotentialNotes();
		
		List<List<StringValue>> combinations = makeCombinations( potentialNotes);
		
		List<PriorityItem> priorities = determinePriority( combinations);
		
		List<List<StringValue>> theBestOnes = takeBest( priorities);
		
		return createChords( theBestOnes);
	}
	
	/**
	 * Creates the TGChord ArrayList out of StringValue's ArrayLists
	 * 
	 * @param Highest rated StringValues
	 * @return TGChord collection
	 */
	private List<TGChord> createChords(List<List<StringValue>> top) {
		if(!isValidProcess()){
			return null;
		}
		
		List<TGChord> chords = new ArrayList<TGChord>(top.size());
		
		Iterator<List<StringValue>> it = top.iterator();
		
		while (it.hasNext()) {
			TGChord chord = TuxGuitar.getInstance().getSongManager().getFactory()
					.newChord(this.tuning.length);
			Iterator<StringValue> it2 = it.next().iterator();
			
			while (it2.hasNext()) {
				StringValue stringValue = it2.next();
				int string = ((chord.getStrings().length - 1) - (stringValue.getString()));
				int fret = stringValue.getFret();
				chord.addFretValue(string, fret);
				chord.setName(this.chordName);
			}
			
			chords.add(chord);
		}
		return chords;
	}
	
	/**
	 * 
	 * If string/fret combination is needed for the chord formation, add it into
	 * List
	 * 
	 * @return true if the note is needed for chord formation
	 * 
	 */
	private void find(int stringTone, int stringIndex, int fret,List<StringValue> stringList){
		if(!isValidProcess()){
			return;
		}
		boolean bassAlreadyIn=false;
		// chord base notes
		for (int i = 0; i < this.requiredNotes.length; i++)
			if ((stringTone + fret) % 12 == (this.chordTonic+this.requiredNotes[i] - 1) % 12) {
				if (!bassAlreadyIn && (stringTone + fret) % 12 == this.bassTonic)
					bassAlreadyIn=true;
				stringList.add(new StringValue(stringIndex, fret, i));
				return;
			}
		
		// alterations
		if (this.expandingNotes.length!=0) {
			for (int i=0; i<this.expandingNotes.length; i++) {
				if ((stringTone+fret)%12==(this.chordTonic+this.expandingNotes[i]-1)%12) {
					if (!bassAlreadyIn && (stringTone + fret) % 12 == this.bassTonic)
						bassAlreadyIn=true;
					stringList.add(new StringValue(stringIndex,fret,(i<2 ? this.ESSENTIAL_INDEX : this.NOT_ESSENTIAL_INDEX)));
				}
			}
		}
		
		// bass tone
		if (!bassAlreadyIn)
			if ((stringTone + fret) % 12 == this.bassTonic) {
				stringList.add(new StringValue(stringIndex, fret, this.BASS_INDEX));
				return;
			}
		
	}
	
	/**
	 * Returns the wanted note for ADD chord
	 * 
	 * @param type
	 *            0==add9; 1==add11; 2==add13; 
	 * @param selectionIndex
	 *            index of selected item in the List combo
	 * @return wanted note, or -1 if nothing was selected
	 * 
	 */
	private int getAddNote(int type, int selectionIndex) {
		
		int wantedNote = 0;
		
		switch (type) {
			case 0:
				wantedNote = 3; // add9
				break;
			case 1:
				wantedNote = 6; // add11
				break;
			case 2:
				wantedNote = 10; // add13
				break;
		}
		
		switch (selectionIndex) {
			case 1:
				wantedNote++;
				break;
			case 2:
				wantedNote--;
				break;
			default:
				break;
		}
		
		return wantedNote;
	}
	
	private List<List<StringValue>> makePotentialNotes(){
		if(!isValidProcess()){
			return null;
		}
		List<List<StringValue>> potentialNotes = new ArrayList<List<StringValue>>(this.tuning.length);
		
		for (int string = 0; string < this.tuning.length; string++) {
			
			List<StringValue> currentStringList = new ArrayList<StringValue>(10);
			
			// search all the frets
			
			if (TGChordSettings.instance().getFindChordsMin()>0 && TGChordSettings.instance().isEmptyStringChords())
				find(this.tuning[string], string, 0, currentStringList); // if it's open chord but wanted to search from different minimal fret
				
			
			for (int fret = TGChordSettings.instance().getFindChordsMin(); fret <= TGChordSettings.instance().getFindChordsMax(); fret++) {
				// put in all the needed notes
				find(this.tuning[string], string, fret, currentStringList);
			}
			
			potentialNotes.add(currentStringList);
			
		}
		return potentialNotes;
	}
	
	/**
	 * 
	 * Makes the all-possible combinations of found notes that can be reached by
	 * fingers
	 * 
	 * @param potentialNotes
	 *            list consisted of found notes for each string
	 * 
	 * @return list of list of StringValues, with tones that can form a chord
	 * 
	 */
	private List<List<StringValue>> makeCombinations(List<List<StringValue>> potentialNotes) {
		if(!isValidProcess()){
			return null;
		}
		
		// COMBINATIONS of strings used in a chord
		List<List<Integer>> stringCombination = new ArrayList<List<Integer>>(60);
		List<List<Integer>> lastLevelCombination = null;
		
		for (int i = 0; i < this.tuning.length - 1; i++)
		{
			lastLevelCombination = makeStringCombination(lastLevelCombination);
			
			// lastLevelCombination after 3rd round: [[0, 1, 2, 3], [0, 1, 2,
			// 4], [0, 1, 3, 4], [0, 2, 3, 4], [1, 2, 3, 4], [0, 1, 2, 5], [0,
			// 1, 3, 5], [0, 2, 3, 5], [1, 2, 3, 5], [0, 1, 4, 5], [0, 2, 4, 5],
			// [1, 2, 4, 5], [0, 3, 4, 5], [1, 3, 4, 5], [2, 3, 4, 5]]
			
			stringCombination.addAll(lastLevelCombination);
		}
		
		List<List<StringValue>> combinations = new ArrayList<List<StringValue>>(800);
		
		// --- combine the StringValues according to strings combination
		// ----------------------=======
		
		Iterator<List<Integer>> iterator = stringCombination.iterator();
		
		while (iterator.hasNext()) { // go through all string combinations list
			// take a string combinations
			List<Integer> currentStringCombination = iterator.next();
			List<List<StringValue>> lastStringValueCombination = null;
			
			// go through all the strings in one combination
			for (int level = 0; level < currentStringCombination.size(); level++) { 
				
				// take the string index
				int currentString = ((Integer) currentStringCombination.get(level)).intValue();
				
				// take all the potential notes from currentString and combine
				// them with potential notes from other strings
				
				lastStringValueCombination = makeStringValueCombination(lastStringValueCombination, potentialNotes.get(currentString));
				
				// the structure of combinations is AL { AL(StringValue,SV,SV),
				// AL(SV), AL(SV,SV),AL(SV,SV,SV,SV,SV,SV) }
				
			}
			
			if( lastStringValueCombination != null ){
				combinations.addAll(lastStringValueCombination);
			}
		}
		
		return combinations;
	}
	
	/**
	 * Makes a combination of string indices
	 * 
	 * @param lastLevelCombination
	 *            structure to be expanded by current level
	 * 
	 * @return structure of stringCombination is AL { AL(0), AL(0,1),
	 *         AL(0,2),AL(0,1,3,4),AL(0,1,2,3,4,5) }
	 */
	private List<List<Integer>> makeStringCombination(List<List<Integer>> lastLevelCombinationRef){
		if(!isValidProcess()){
			return null;
		}
		
		List<List<Integer>> lastLevelCombination = lastLevelCombinationRef;
		
		if (lastLevelCombination == null) {
			// first combination is AL { AL(0), AL(1), AL(2), AL(3), AL(4),
			// ...AL(tuning.length) }
			lastLevelCombination = new ArrayList<List<Integer>>();
			
			for (int i = 0; i < this.tuning.length; i++) {
				lastLevelCombination.add(new ArrayList<Integer>());
				lastLevelCombination.get(i).add(new Integer(i));
			}
		}
		
		ArrayList<List<Integer>> thisLevelCombination = new ArrayList<List<Integer>>();
		for (int current = 1; current < this.tuning.length; current++)
		{
			Iterator<List<Integer>> it = lastLevelCombination.iterator();
			
			while (it.hasNext()) {
				List<Integer> combination = it.next();
				Integer currentInteger = new Integer(current);
				if (((Integer) combination.get(combination.size() - 1))
						.intValue() < current
						&& !combination.contains(currentInteger)) {
					
					// check if the string is already in combination
					@SuppressWarnings("unchecked")
					List<Integer> newCombination = (List<Integer>) ((ArrayList<Integer>) combination).clone();
					newCombination.add(currentInteger);
					thisLevelCombination.add(newCombination);
				}
				
			}
			
		}
		
		return thisLevelCombination;
		
	}
	
	/**
	 * Makes a combination of notes by multiplying last combination and current
	 * note arrays
	 * 
	 * 
	 * 
	 * @param lastLevelCombination
	 *            structure to be expanded by current level
	 * 
	 * @param notes
	 *            notes that can be considered into making a chord
	 * 
	 * @return structure of StringValue combinations : AL {
	 *         AL(StringValue,SV,SV), AL(SV), AL(SV,SV),AL(SV,SV,SV,SV,SV,SV) }
	 * 
	 */
	private List<List<StringValue>> makeStringValueCombination(List<List<StringValue>> lastLevelCombination, List<StringValue> notes) {
		if(!isValidProcess()){
			return null;
		}
		List<List<StringValue>> thisLevelCombination = null;
		
		if (lastLevelCombination == null) { // initial combination
			
			thisLevelCombination = new ArrayList<List<StringValue>>(notes.size());
			
			for (int i = 0; i < notes.size(); i++) {
				
				thisLevelCombination.add(new ArrayList<StringValue>(6));
				
				thisLevelCombination.get(i).add(notes.get(i));
			}
			
			// first combination is AL { AL(firstOne), AL(anotherFret) }
			
		}
		
		else {
			
			thisLevelCombination = new ArrayList<List<StringValue>>();
			
			for (int i = 0; i < notes.size(); i++)
				for (int j = 0; j < lastLevelCombination.size(); j++) { // cartesian multiplication
					@SuppressWarnings("unchecked")
					List<StringValue> currentCombination = (List<StringValue>) ((ArrayList<StringValue>) lastLevelCombination.get(j)).clone();
					currentCombination.add(notes.get(i));
					
					// if the distance maximum between the existing frets
					// is less than wanted, add it into potential list
					
					if (checkCombination(currentCombination))
						thisLevelCombination.add(currentCombination);
					
				}
		}
		
		return thisLevelCombination;
	}
	
	/**
	 * Checks if the combination can be reached by fingers. It is reachable
	 * 
	 * if the distance between lowest and highest fret is less than
	 * 
	 * <i>ChordCreatorUtil.MAX_FRET_SPAN</i>.
	 * 
	 * Also note that this method eliminates or includes the chords with empty
	 * strings,
	 * 
	 * which is controlled with <i>boolean ChordCreatorUtil.EMPTY_STRING_CHORDS</i>
	 * 
	 * @param combination
	 *            current combination to be examined
	 * 
	 * @return true if it can be reached
	 * 
	 */
	private boolean checkCombination(List<StringValue> combination) {
		
		Iterator<StringValue> it = combination.iterator();
		int maxLeft, maxRight;
		
		maxLeft = maxRight = ((StringValue) combination.get(0)).getFret();
		
		while (it.hasNext()) {
			
			int fret = ((StringValue) it.next()).getFret();
			
			//chords with empty-string are welcome
			if (fret != 0 || !TGChordSettings.instance().isEmptyStringChords()) {
				
				if (fret < maxLeft)
					maxLeft = fret;
				
				if (fret > maxRight)
					maxRight = fret;
				
			}
			
		}
		
		if (Math.abs(maxLeft - maxRight) >= MAX_FRET_SPAN)
			
			return false;
		
		return true;
		
	}
	
	/**
	 * orders the StringValue ArrayList by their priority, calculated here
	 * 
	 * for every single chord combination.<br>
	 * 
	 * Priority is higher if:<br>
	 *  - tone combination has all notes required for the chord basis<br>
	 *  - has good chord semantics uses many basic tones, and all necessary
	 * tones in their place<br>
	 *  - tone combination has all subsequent strings (no string skipping)<br>
	 *  - has a chord bass tone as lowest tone<br>
	 *  - uses more strings<br>
	 *  - uses good fingering positions<br>
	 * 
	 * @param allCombinations
	 *            all the StringValue combinations that make some sense
	 * 
	 * @return Treemap of the StringValue ArrayLists, in which the key is
	 * 
	 * <i>float priority</i>.
	 * 
	 */
	private List<PriorityItem> determinePriority(List<List<StringValue>> allCombinations) {
		if(!isValidProcess()){
			return null;
		}
		List<PriorityItem> ordered = new ArrayList<PriorityItem>();
		
		Iterator<List<StringValue>> it = allCombinations.iterator();
		
		while (it.hasNext() && isValidProcess()) {
			
			float priority = 0;
			
			List<StringValue> stringValueCombination = it.next();
			
			// tone combination has all notes required for the chord basis
			
			priority += combinationHasAllRequiredNotes(stringValueCombination);
			
			// uses good chord semantics
			
			priority += combinationChordSemantics(stringValueCombination);
			
			// tone combination has all subsequent strings (no string skipping)
			
			priority += combinationHasSubsequentStrings(stringValueCombination);
			
			// has a chord bass tone as lowest tone
			
			priority += combinationBassInBass(stringValueCombination);
			
			// uses many strings
			// 4 and less strings will be more praised in case of negative grade
			// 4 and more strings will be more praised in case of positive grade 
			priority += TGChordSettings.instance().getManyStringsGrade() / 3
				* (stringValueCombination.size()-this.tuning.length / 
						(TGChordSettings.instance().getManyStringsGrade()>0 ? 2 : 1.2) );
			
			// uses good fingering positions
			
			priority += combinationHasGoodFingering(stringValueCombination);
			
			// System.out.println("OVERALL:
			// "+priority+"----------------------------");
			
			PriorityItem item = new PriorityItem();
			
			item.priority = priority;
			
			item.stringValues = stringValueCombination;
			
			ordered.add(item);
			
		}
		
		return ordered;
		
	}
	
	/**
	 * 
	 * Takes the StringValue ArrayLists that has the best priority rating
	 * 
	 */
	private List<List<StringValue>> takeBest(List<PriorityItem> priorityItems) {
		if(!isValidProcess()){
			return null;
		}
		
		int maximum = TGChordSettings.instance().getChordsToDisplay();
		
		List<List<StringValue>> bestOnes = new ArrayList<List<StringValue>>(maximum);
		
		Collections.sort(priorityItems, new PriorityComparator());
		for(int i = 0; i < priorityItems.size() && isValidProcess(); i ++){
			PriorityItem item = (PriorityItem)priorityItems.get(i);
			if (!checkIfSubset(item.stringValues, bestOnes) ){
				bestOnes.add(item.stringValues);
				
				if( bestOnes.size() >= maximum ){
					break;
				}
			}
		}
		
		return bestOnes;
		
	}
	
	/** adds points if the combination has all the notes in the basis of chord */
	private float combinationHasAllRequiredNotes(List<StringValue> stringValueCombination) {
		if(!isValidProcess()){
			return 0;
		}
		Iterator<StringValue> it = stringValueCombination.iterator();
		int[] values = new int[this.requiredNotes.length];
		int currentIndex = 0;
		
		while (it.hasNext()) {
			StringValue sv = (StringValue) it.next();
			
			if (sv.getRequiredNoteIndex() >= 0) { // only basis tones
				boolean insert = true;
				
				for (int i = 0; i < currentIndex; i++)
					if (values[i] == sv.getRequiredNoteIndex() + 1)
						insert = false;
				
				// sv.requiredNoteIndex+1, because we have index 0 and we don't
				// want it inside
				
				if (insert) {
					values[currentIndex] = sv.getRequiredNoteIndex() + 1;
					currentIndex++;
				}
				
			}
		}
		
		if (currentIndex == this.requiredNotes.length) {
			return TGChordSettings.instance().getRequiredBasicsGrade();
		}
		
		if (currentIndex == this.requiredNotes.length - 1) {
			
			boolean existsSubdominant = false;
			
			Iterator<StringValue> it2 = stringValueCombination.iterator();
			while (it2.hasNext()) {
				StringValue current = (StringValue)it2.next();
				if ((this.tuning[current.getString()] + current.getFret()) % 12 == (this.chordTonic + 7) %12)
					existsSubdominant = true;
			}
			
			if (!existsSubdominant && currentIndex == this.requiredNotes.length-1) {
				// if not riff. "sus" chord, or chord with altered fifth allow chord without JUST subdominant (fifth) with small penalty 
				
				//if ( !((ChordInfo)new ChordDatabase().getChords().get(this.chordIndex)).getName().contains("sus") && this.requiredNotes.length!=2 && this.add5==0) {
				//String.contains(String) is not available at JRE1.4
				//Replaced by "String.indexOf(String) >= 0"
				if ( TGChordDatabase.get(this.chordIndex).getName().indexOf("sus") >= 0 && this.requiredNotes.length != 2 && this.add5 == 0) {
					return ( TGChordSettings.instance().getRequiredBasicsGrade() * 4 / 5 );
				}
			}
			
		}
		
		// required notes count should decrease the penalty
		int noteCount = (this.alteration == 0 ? 0 : 1+ this.alteration)+currentIndex+ (this.bassTonic == this.chordTonic ? 0 : 1);
		
		// sometimes, when noteCount is bigger then tunning length, this pennalty will become positive, which may help
		return -TGChordSettings.instance().getRequiredBasicsGrade()
				* (this.tuning.length - noteCount) / this.tuning.length * 2;
		
	}
	
	/** adds points if the combination has strings in a row */
	private float combinationHasSubsequentStrings(List<StringValue> stringValueCombination) {
		if(!isValidProcess()){
			return 0;
		}
		boolean stumbled = false, noMore = false, penalty = false;
		
		for (int i = 0; i < this.tuning.length; i++) {
			boolean found = false;
			Iterator<StringValue> it = stringValueCombination.iterator();
			while (it.hasNext())
				if (((StringValue) it.next()).getString() == i)
					found = true;
			if (found) {
				if (!stumbled)
					stumbled = true;
				if (noMore)
					penalty = true;
				if (penalty) // penalty for skipped strings
					return -TGChordSettings.instance().getSubsequentGrade();
			}
			else
			if (stumbled)
				noMore = true;
		}
		
		if (penalty)
			return 0.0f;
		
		return TGChordSettings.instance().getSubsequentGrade();
	}
	
	/** checks if the bass tone is the lowest tone in chord */
	private float combinationBassInBass(List<StringValue> stringValueCombination) {
		if(!isValidProcess()){
			return 0;
		}
		for (int i = 0; i < this.tuning.length; i++) {
			
			Iterator<StringValue> it = stringValueCombination.iterator();
			
			while (it.hasNext()) {
				StringValue sv = (StringValue) it.next();
				
				if (sv.getString() == i) { // stumbled upon lowest tone
					if ( (this.tuning[sv.getString()]+sv.getFret()) % 12 == this.bassTonic  )
					  return TGChordSettings.instance().getBassGrade();
					// else
					return -TGChordSettings.instance().getBassGrade();
				}
			}
			
		}
		
		return 0;
	}
	
	/**
	 * grades the fingering in a chord.
	 * 
	 * fingering is good if:<br>
	 *  - uses as little as possible fret positions<br>
	 *  - uses less than 3 fret positions<br>
	 *  - distributes good among fingers<br>
	 *  - can be placed capo <br>
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private float combinationHasGoodFingering(List<StringValue> stringValueCombination) {
		if(!isValidProcess()){
			return 0;
		}
		// init: copy into simple array
		float finalGrade = 0;
		int[] positions = new int[this.tuning.length];
		for (int i = 0; i < this.tuning.length; i++)
			positions[i] = -1;
		{
			Iterator<StringValue> it = stringValueCombination.iterator();
			
			while (it.hasNext()) {
				StringValue sv = (StringValue) it.next();
				positions[sv.getString()] = sv.getFret();
			}
		}
		// algorithm
		
		// distance between fingers
		int min = TGChordSettings.instance().getFindChordsMax()+2, max = 0, maxCount=0;
		boolean openChord = false, zeroString = false;
		
		for (int i = 0; i < this.tuning.length; i++) {
			
			openChord|= TGChordSettings.instance().isEmptyStringChords() && positions[i] == 0;
			zeroString |= positions[i]==0;
			
			if (positions[i] < min && positions[i] != 0 && positions[i]!=-1)
				min = positions[i];
			
			if (positions[i] > max) {
				max = positions[i];
				maxCount=1;
			}
			else 
				if (positions[i]==max) 
					maxCount++;
			
		}
		
		// finger as capo
		
		int count = 0;
		
		for (int i = 0; i < this.tuning.length; i++)
			if (positions[i] == min)
				count++;
		
		if (!openChord) { 
			if (zeroString)
				finalGrade += TGChordSettings.instance().getFingeringGrade()/8;
			else
				if (count >= 2)
					finalGrade += TGChordSettings.instance().getFingeringGrade()/8;
		}
		else
			if (openChord)
				finalGrade += TGChordSettings.instance().getFingeringGrade()/8;
		
		// position distance: 1-2 nice 3 good 4 bad 5 disaster
		float distanceGrade;
		
		switch(Math.abs(max-min)) {
			case 0 : distanceGrade=TGChordSettings.instance().getFingeringGrade()/5;
					break;
			case 1 : distanceGrade=TGChordSettings.instance().getFingeringGrade()/(5+maxCount);
					break;
			case 2 : distanceGrade=TGChordSettings.instance().getFingeringGrade()/(6+maxCount);
					 if (min<5) distanceGrade*=0.9;
					break;
			case 3 : distanceGrade=-TGChordSettings.instance().getFingeringGrade()/10*maxCount;
					// I emphasize the penalty if big difference is on some 
					// lower frets (it is greater distance then)
					if (min<5) distanceGrade*=1.3;
					break;
			case 4 : distanceGrade=-TGChordSettings.instance().getFingeringGrade()/4*maxCount;
					if (min<=5) distanceGrade*=1.8;
					break;
			default : distanceGrade=-TGChordSettings.instance().getFingeringGrade()*maxCount;
					break;
		}
		finalGrade += distanceGrade;
		
		// ============== finger position abstraction ==================
		// TODO: what to do with e.g. chord -35556 (C7)
		// ... it can be held with capo on 5th fret, but very hard :)
		// ... This is the same as with "capo after", I didn't consider that (e.g. chord -35555)
		List[] fingers = {new ArrayList<Integer>(2),new ArrayList<Integer>(2),new ArrayList<Integer>(2),new ArrayList<Integer>(2)};
		
		// TODO: still no thumb, sorry :)
		
		// STRUCTURE: ArrayList consists of Integers - first is fret
		//                                           - others are strings
/*		
		for (int i=0; i<this.tuning.length; i++)
			System.out.print(" "+positions[i]);
		System.out.println("");
*/
		
		// if chord is open, then we can have capo only in strings before open string
		int lastZeroIndex = 0;
		
		if (zeroString)
			for (int i=0; i<positions.length; i++)
				if (positions[i]==0) lastZeroIndex=i;
		
		// open or not not open chord,
		// index finger is always on lowest fret possible
		fingers[0].add(new Integer(min));
		
		for (int i=lastZeroIndex; i<positions.length; i++)
				if (positions[i]==min) {
					fingers[0].add(new Integer(i));
					positions[i]=-1;
				}
		
		// other fingers
		// if not zero-fret, occupy fingers respectivly
		int finger=1;
		for (int i=0; i<positions.length; i++) {
			if (positions[i]!=0 && positions[i]!=-1) {
				if (finger<4) {
					fingers[finger].add(new Integer(positions[i]));
					fingers[finger].add(new Integer(i));
					positions[i]=-1;
				}
				finger++;
			}
		}
		
/*		System.out.println("Positions:");
		for (int i=0; i<4; i++) {
			if (fingers[i].size()>1)
				System.out.print("G"+(i+1)+"R"+((Integer)fingers[i].get(0)).intValue()+"S"+((Integer)fingers[i].get(1)).intValue()+" ");
		}
*/		
		
		if (finger>4) 
			finalGrade-=TGChordSettings.instance().getFingeringGrade();
		 else
			finalGrade+=TGChordSettings.instance().getFingeringGrade()*0.1*(15-2*finger);
		
		// TODO: maybe to put each finger's distance from the minimum
		return finalGrade;
		
	}
	
	/**
	 * grades the chord semantics, based on theory.
	 * 
	 * Tone semantics is good if:<br>
	 *  - there appear tones from chord basis or bass tone<br>
	 *  - there appear alteration tones on their specific places<br><br>
	 *
	 * Algorithm:<br>
	 *  - search for chord tonic. If some note is found before (and it's not bass) do penalty<br>
	 *  - make penalty if the bass tone is not in bass<br>
	 *  - check if all the expanding notes are here. If some are not, do penalty<br>
	 *  - if expanding note isn't higher than tonic octave, then priority should be less<br>
	 *  - If there are not some with NON_ESSENTIAL_INDEX are not here, penalty should be less<br>
	 */
	private float combinationChordSemantics(List<StringValue> stringValueCombination) {
		if(!isValidProcess()){
			return 0;
		}
		float finalGrade = 0;
		
		int foundTonic = -1;
		
		int[] foundExpanding = new int[this.expandingNotes.length];
		int stringDepth=0;
		
		for (int string = 0; string < this.tuning.length; string++) {
			// we have to go string-by-string because of the octave
			Iterator<StringValue> it = stringValueCombination.iterator();
			StringValue current = null;
			boolean found=false;
			
			while (it.hasNext() && !found) {
				StringValue sv = (StringValue) it.next();
				if (sv.getString() == string &&!found && sv.getFret()!=-1) { // stumbled upon next string
					current = sv;
					found=true;
					stringDepth++;
				}
			}
			
			// grade algorithms----
			if (current != null) {
				// search for tonic
				if (foundTonic==-1 && current.getRequiredNoteIndex()==0)
					foundTonic=this.tuning[current.getString()]+current.getFret();
				
				// specific bass not in bass?
				if (stringDepth>1) {
					if (current.getRequiredNoteIndex()==this.BASS_INDEX)  
						finalGrade -= TGChordSettings.instance().getGoodChordSemanticsGrade();
					
					if (current.getRequiredNoteIndex()<0) { // expanding tones
						// expanding tone found before the tonic
						if (foundTonic==-1)
							finalGrade -= TGChordSettings.instance().getGoodChordSemanticsGrade()/2;
						else {
							// if expanding note isn't higher than tonic's octave
							if (foundTonic+11 > this.tuning[current.getString()]+current.getFret())
								finalGrade -= TGChordSettings.instance().getGoodChordSemanticsGrade()/3;
						}
						
						// search for distinct expanding notes
						for (int i=0; i<this.expandingNotes.length; i++)
							if ((this.tuning[string]+current.getFret())%12==(this.chordTonic+this.expandingNotes[i]-1)%12)
								if (foundExpanding[i]==0)
									foundExpanding[i]=current.getRequiredNoteIndex();
						
					}
				}
			}
		}
		
		// penalties for not founding an expanding note
		if (this.alteration!=0) {
			int essentials=0, nonEssentials=0;
			for (int i=0; i<foundExpanding.length; i++) {
				if (foundExpanding[i]==this.ESSENTIAL_INDEX)
					essentials++;
				else
					if (foundExpanding[i]!=0)
						nonEssentials++;
			}
			
			if (essentials+nonEssentials==this.expandingNotes.length)
				finalGrade+=TGChordSettings.instance().getGoodChordSemanticsGrade();
			else {
				if (essentials==2) // if all essentials are there, it's good enough
					finalGrade+=TGChordSettings.instance().getGoodChordSemanticsGrade()/2;
				
				// but if some are missing, that's BAD:
				else {
					finalGrade+= (essentials+nonEssentials-this.expandingNotes.length)*TGChordSettings.instance().getGoodChordSemanticsGrade();
					// half of the penalty for non-essential notes
					finalGrade+= nonEssentials*TGChordSettings.instance().getGoodChordSemanticsGrade()/2;
				}
			}
		}
		
		return finalGrade;
		
	}
	
	/**
	 *  If current StringValue is a subset or superset of already better ranked
	 *  chords, it shouldn't be put inside, because it is duplicate.
	 *  @param stringValues current StringValue to be examined
	 *  @param betterOnes ArrayList of already stored StringList chords
	 *  @return true if it is duplicate, false if it is unique
	 */
	private boolean checkIfSubset(List<StringValue> stringValues, List<List<StringValue>> betterOnes) {
		if(!isValidProcess()){
			return false;
		}
		Iterator<List<StringValue>> it = betterOnes.iterator();
		while (it.hasNext()) {
			List<StringValue> currentStringValue = it.next();
			boolean foundDifferentFret = false;
			// repeat until gone through all strings, or found something different
			for (int i=0; i<currentStringValue.size(); i++) {
				int currentString = ((TGChordCreatorUtil.StringValue)currentStringValue.get(i)).getString() ;
				// search for the same string - if not found do nothing
				for (int j=0; j<stringValues.size(); j++)
				if ( ((TGChordCreatorUtil.StringValue)stringValues.get(j)).getString() == currentString) {
					// if the frets on the same string differ, then chords are not subset/superset of each other
					if (((TGChordCreatorUtil.StringValue)stringValues.get(j)).getFret() != ((TGChordCreatorUtil.StringValue)currentStringValue.get(i)).getFret())
						foundDifferentFret=true;
				}
				
			}
			if (!foundDifferentFret)// nothing is different
				return true;
		}
		
		return false;
	}
	
	/**
	 * contains information about the note: string, fret and tone function in a
	 * chord
	 * 
	 * @author julian
	 * 
	 */
	
	private class StringValue {
		
		protected int string;
		protected int fret;
		protected int requiredNoteIndex;
		
		public StringValue(int string, int fret, int requiredNoteIndex) {
			this.string = string;
			this.fret = fret;
			this.requiredNoteIndex = requiredNoteIndex;
			
		}
		
		public int getString() {
			return this.string;
		}
		
		public int getFret() {
			return this.fret;
		}
		
		public int getRequiredNoteIndex() {
			return this.requiredNoteIndex;
		}
	}
	
	/** used just to sort StringValue ArrayLists by priorities */
	protected class PriorityItem {
		
		List<StringValue> stringValues;
		float priority;
		
	}
	
	/** used to sort the array */
	protected class PriorityComparator implements Comparator<PriorityItem> {
		
		public int compare(PriorityItem o1, PriorityItem o2) {
			return Math.round(o2.priority - o1.priority);
		}
		
	}
}