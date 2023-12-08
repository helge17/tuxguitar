package org.herac.tuxguitar.app.view.dialog.chord;
/* Created on 05-March-2007
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author Nikola Kolarovic
 * 
 */
public class TGChordRecognizer {
	
	// index for parameter array
	public static final int TONIC_INDEX = 0;
	public static final int CHORD_INDEX = 1;
	public static final int ALTERATION_INDEX = 2;
	public static final int PLUSMINUS_INDEX = 3;
	public static final int BASS_INDEX = 4;
	public static final int ADDCHK_INDEX = 5;
	public static final int I5_INDEX = 6;
	public static final int I9_INDEX = 7;
	public static final int I11_INDEX = 8;
	
	private TGChordDialog dialog;
	private UIPanel control;
	private UIListBoxSelect<Integer> proposalList;
	private List<int[]> proposalParameters;
	
	// this var keep a control to running threads.
	private long runningProcess;
	
	public TGChordRecognizer(TGChordDialog dialog, UIContainer parent) {
		this.runningProcess = 0;
		this.dialog = dialog;
		this.createControl(parent);
	}
	
	public void createControl(UIContainer parent) {
		final UIFactory uiFactory = this.dialog.getUIFactory();
		UITableLayout layout = new UITableLayout(0f);
		
		this.control = uiFactory.createPanel(parent, true);
		this.control.setLayout(layout);
		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(this.control, false);
		composite.setLayout(compositeLayout);
		layout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.proposalParameters = new ArrayList<int[]>();
		
		this.proposalList = uiFactory.createListBoxSelect(composite);
		this.proposalList.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( getDialog().getEditor() != null ){
					showChord();
				}
			}
		});
		compositeLayout.set(this.proposalList, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}
	
	/** sets the current chord to be selected proposal */
	protected void showChord() {
		UISelectItem<Integer> selectedItem = this.proposalList.getSelectedItem();
		if( selectedItem != null && selectedItem.getValue() < this.proposalParameters.size()) {
			int[] params = this.proposalParameters.get(selectedItem.getValue());
			this.dialog.getSelector().adjustWidgets(params[TONIC_INDEX],
			                                        params[CHORD_INDEX],
			                                        params[ALTERATION_INDEX],
			                                        params[BASS_INDEX],
			                                        params[PLUSMINUS_INDEX],
			                                        params[ADDCHK_INDEX],
			                                        params[I5_INDEX],
			                                        params[I9_INDEX],
			                                        params[I11_INDEX]);
			String chordName = selectedItem.getText();
			chordName = chordName.substring(0, chordName.indexOf('(')-1);
			this.dialog.getEditor().getChordName().setText(chordName);
			this.dialog.getEditor().redraw();
		}
	}
	
	/**
	 * - Recognizes the chord string
	 * - Fills the component's list with alternative names
	 * - Sets all the ChordSelector fields into recognized chord (tonic, bass, chord, alterations)
	 * - Makes the alternatives and puts them into ChordList
	 * - Writes the chord formula into appropriate label
	 *  @param chord chord structure (frets, strings)
	 *  @param redecorate is the Chord Editor in editing mode, or it is just changed by ChordSelector
	 */
	
	public void recognize(final TGChord chord,final boolean redecorate,final boolean setChordName) {
		
		final long processId = (++ this.runningProcess);
		final boolean sharp = this.dialog.getSelector().getSharpButton().isSelected();
		
		this.clearProposals();
		
		new Thread( new Runnable() {
			public void run() throws TGException {
				if(!getDialog().isDisposed() && isValidProcess(processId)){
					makeProposals(processId, chord, sharp, redecorate, setChordName);
				}
			}
		} ).start();
	}
	
	/** Fills the component's list with alternative names
	 * @param chord TGChord to be recognized
	 * @return parameters for adjustWidgets and getChordName methods 
	 */
	protected void makeProposals(long processId, TGChord chord, boolean sharp, boolean redecorate, boolean setChordName) {
		List<int[]> proposalParameters = new ArrayList<int[]>();
		List<String> proposalNames = new ArrayList<String>();		
		List<Integer> notesInside = new ArrayList<Integer>();
		int[] tuning = this.dialog.getSelector().getTuning();
		
		// find and put in all the distinct notes
		for (int i=0; i<tuning.length; i++) {
			int fret = chord.getStrings()[i];
			if (fret!=-1) {
				Integer note = new Integer((tuning[tuning.length-1-i] + fret) % 12);
				Iterator<Integer> it = notesInside.iterator();
				boolean found=false;
				while (it.hasNext())
					if (it.next().equals(note))
						found=true;
				if (!found)
					notesInside.add(note);
			}
		}
		
		// Now search:
		// go through all the possible tonics
		// it is required because tonic isn't mandatory in a chord
		List<Proposal> allProposals = new ArrayList<Proposal>(10);
		
		for (int tonic=0; tonic<12; tonic++) {
			
			Proposal currentProp = null;
			
			// first check for the basic chord tones
			for (int chordIndex = 0; chordIndex < TGChordDatabase.length(); chordIndex ++) {
				TGChordDatabase.ChordInfo info = TGChordDatabase.get(chordIndex);
				
				currentProp = new Proposal(notesInside);
				// it is more unusual the more we go down the index
				// except chords "5" and "m", they are quite usual :)
				currentProp.unusualGrade-=(chordIndex!=TGChordDatabase.length() && chordIndex!=4 ? 2*chordIndex : 0);
				
				//ChordDatabase.ChordInfo info = (ChordDatabase.ChordInfo)chordItr.next();
				boolean foundNote = false;
				for (int i=0; i<info.getRequiredNotes().length; i++) { // go through all the requred notes
					Iterator<Integer> nit = notesInside.iterator();
					while (nit.hasNext()) // go through all the needed notes
						if (((Integer)nit.next()).intValue() == (tonic+info.getRequiredNotes()[i]-1)%12) {
							foundNote=true;
							if (tonic+info.getRequiredNotes()[i]-1 == tonic)
								currentProp.dontHaveGrade+=15; // this means penalty for not having tonic is -65
							currentProp.foundNote(tonic+info.getRequiredNotes()[i]-1); // found a note in a chord
						}
					
				}
				// if something found, add it into a proposal if it's worth
				if (foundNote) {
					currentProp.params[TONIC_INDEX] = tonic;
					currentProp.params[CHORD_INDEX] = chordIndex;//possibleChords.indexOf(info);
					int foundNotesCount = currentProp.missingNotes.length-currentProp.missingCount;
					
					// it is worth if it is missing 1 essential note and/or fifth
					if (!info.getName().startsWith("dim") && !info.getName().startsWith("aug"))
						if (!currentProp.isFound(tonic+8-1)) {
							
							// hmmm. maybe it's altered fifth? Create a branch for it.
							if (currentProp.isNeeded(tonic+7-1) || currentProp.isNeeded(tonic+9-1)) {
								Proposal branchProp = (Proposal)currentProp.clone();
								if (branchProp.isNeeded(tonic+9-1)) {
									branchProp.params[I5_INDEX] = 1;
									branchProp.foundNote(tonic+8);
								}
								else {
									branchProp.params[I5_INDEX] = 2;
									branchProp.foundNote(tonic+6);
								}
								branchProp.unusualGrade-=35;
								if (foundNotesCount+1>=info.getRequiredNotes().length-1) {
									branchProp.dontHaveGrade-=(info.getRequiredNotes().length-(foundNotesCount+1))*50;
									allProposals.add(branchProp);
								}
								
							}
							else {
								currentProp.params[I5_INDEX] = 0;
								currentProp.dontHaveGrade+=30;
							}
						}
					
					currentProp.params[I5_INDEX] = 0;
					if (foundNotesCount>=info.getRequiredNotes().length-1 ) {
							currentProp.dontHaveGrade-=(info.getRequiredNotes().length-foundNotesCount)*50;
							allProposals.add(currentProp);
					}
				}
				currentProp=null;
			}
		}
		
		Iterator<Proposal> props = allProposals.iterator();
		List<Proposal> unsortedProposals = new ArrayList<Proposal>(5);
		while (props.hasNext()) {
			// place the still missing alterations notes accordingly... bass also
			///////////////////////////////////////////////////////////////
			
			final Proposal current = (Proposal)props.next();
			
			boolean bassIsOnlyInBass = true;
			// ---------------- bass tone ----------------
			for (int i=chord.getStrings().length-1; i>=0; i--) {
				if (chord.getStrings()[i]!=-1) {
					if (current.params[BASS_INDEX]==-1) {// if we still didn't determine bass
						current.params[BASS_INDEX] = (tuning[tuning.length-1-i] + chord.getStrings()[i]) % 12;
						if (current.params[BASS_INDEX]!=current.params[TONIC_INDEX])
							current.unusualGrade-=20;
					}
					if (current.params[BASS_INDEX]==(tuning[tuning.length-1-i] + chord.getStrings()[i]) % 12 )
						bassIsOnlyInBass=false; // if we stumbled upon bass tone again
				}
			}
			
			if (current.isNeeded(current.params[BASS_INDEX]) && bassIsOnlyInBass) {
				   // do not mark as FOUND if bass is somewhere other than in bass only
					current.foundNote(current.params[BASS_INDEX]);
					current.unusualGrade-=20;
			}
			// <=11 means "not DIM or AUG or 5"
			if (current.missingCount>0 && current.params[CHORD_INDEX]<=11) {
				// ---------------- alteration tones ----------------
				// determine seventh -->> 2 is HARDCODED!
				int seventh;
				if (current.params[CHORD_INDEX] == 2) seventh=current.params[TONIC_INDEX]+12-1; // plain 7
						else seventh=current.params[TONIC_INDEX]+11-1; // b7
				if (current.isExisting(seventh)) {
					if (!current.isFound(seventh)) {
						current.filled[3]=true;
						current.foundNote(seventh);
					}
				}
				for (int plusminus=0; plusminus<=2; plusminus++) {
					for (int i=2; i>=0; i--)  // 13, 11, 9
							if (current.isNeeded(current.params[TONIC_INDEX]+getAddNote(i, plusminus)) && !current.filled[i]) {
								current.filled[i]=true;
								current.plusminusValue[i]=plusminus;
								if (plusminus!=0) 
									current.unusualGrade-=15;
								current.foundNote(current.params[TONIC_INDEX]+getAddNote(i, plusminus));
							}
					
				}
			}
			
			// fill in the list
			///////////////////////////////////////////////////////////////
			if (!(current.filled[3] && !(current.filled[0] || current.filled[1] || current.filled[2])) &&  // if just found seventh, cancel it
					current.missingCount==0 && // we don't tollerate notes in chord that are not used in the ChordName
					current.dontHaveGrade>-51) {
						findChordLogic(current);
						unsortedProposals.add(current);
				}
			
		}
		// first, sort by DontHaveGrade
		shellsort(unsortedProposals,1);
		
		int cut=-1;
		int howManyIncomplete = TGChordSettings.instance().getIncompleteChords();
		
		for (int i=0; i<unsortedProposals.size() && cut==-1; i++) {
			int prior = ((Proposal)unsortedProposals.get(i)).dontHaveGrade;
			if (prior<0) 
				cut=i+howManyIncomplete;
		}
		// cut the search
		unsortedProposals=unsortedProposals.subList(0, (cut>0 && cut<unsortedProposals.size() ? cut : unsortedProposals.size()));
		// sort by unusualGrade
		shellsort(unsortedProposals,2);
		
		int firstNegative = 0;
		for (int i=0; i<unsortedProposals.size(); i++) {
			final Proposal current = (Proposal)unsortedProposals.get(i);
			if (firstNegative==0 && current.unusualGrade<0) 
				firstNegative=current.unusualGrade;
			
			if (current.unusualGrade > (firstNegative>=0 ? 0 : firstNegative)-60){
				proposalParameters.add(current.params);
				proposalNames.add((getChordName(current.params,sharp)+" ("+Math.round(100+current.dontHaveGrade*7/10)+"%)" ));
			}
		}
		
		notifyProposals(processId, sharp, redecorate, setChordName, proposalParameters, proposalNames);
	}
	
	public void notifyProposals(final long processId, final boolean sharp, final boolean redecorate, final boolean setChordName, final List<int[]> proposalParameters, final List<String> proposalNames) {
		final int params[] = (!proposalParameters.isEmpty() ? (int[])proposalParameters.get(0) : null);
		final String chordName = (params != null ? getChordName(params, sharp) : "");
		
		TGSynchronizer.getInstance(this.dialog.getContext().getContext()).executeLater(new Runnable() {
			public void run() {
				if(!getDialog().isDisposed() && isValidProcess(processId)){
					for(int[] currentProposalParameters : proposalParameters) {
						addProposalParameters(currentProposalParameters);
					}
					for(String currentProposalName : proposalNames) {
						addProposalName(currentProposalName);
					}
					if( redecorate && params != null ) {
						redecorate(params);
					}
					if( setChordName ) {
						getDialog().getEditor().setChordName( chordName != null ? chordName : ""  );
					}
				}
			}
		});
	}
	
	/** adjusts widgets on the Recognizer combo */
	public void redecorate(int params[]){
		this.dialog.getSelector().adjustWidgets(params[TONIC_INDEX],
		                                        params[CHORD_INDEX],
		                                        params[ALTERATION_INDEX],
		                                        params[BASS_INDEX],
		                                        params[PLUSMINUS_INDEX],
		                                        params[ADDCHK_INDEX],
		                                        params[I5_INDEX],
		                                        params[I9_INDEX],
		                                        params[I11_INDEX]);
	}
	
	/** Assembles chord name according to ChordNamingConvention */
	public String getChordName(int[] param, boolean sharp) {
		return new TGChordNamingConvention().createChordName(param[TONIC_INDEX],
		                                                   param[CHORD_INDEX],
		                                                   param[ALTERATION_INDEX],
		                                                   param[PLUSMINUS_INDEX],
		                                                   param[ADDCHK_INDEX] != 0,
		                                                   param[I5_INDEX],
		                                                   param[I9_INDEX],
		                                                   param[I11_INDEX],
		                                                   param[BASS_INDEX],
		                                                   sharp);
	}
	
	/** Return required interval in semitones for add type and +- modificator
	 * @param type 0=add9, 1=add11, 2=add13
	 * @param selectionIndex 0=usual, 1="+", 2="-"
	 */
	public int getAddNote(int type, int selectionIndex) {
		
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
		
		return --wantedNote;
		
	}
	
	public void findChordLogic(Proposal current) {
		boolean[] found = current.filled;
		int[] plusMinus = current.plusminusValue;
		/*if (!found[3])
			current.unusualGrade-=50;*/
		current.params[ALTERATION_INDEX]=0;
		current.params[I9_INDEX]=plusMinus[0];
		current.params[I11_INDEX]=plusMinus[1];
		current.params[ADDCHK_INDEX]=0;
		current.params[PLUSMINUS_INDEX]=0;
		
		if (found[2]) { // -------------- 13
			current.params[ALTERATION_INDEX]=3;
			current.params[PLUSMINUS_INDEX]=plusMinus[2];
			if (!found[1] || !found[0] || !found[3]) { // b7 or 9 or 11 not inside
				current.unusualGrade-=10;
				if (!found[1] && !found[0] && !found[3])
					current.params[ADDCHK_INDEX]=1;
				else { // just penalty if something's missing
					if (!found[3]) // don't-have penalty if seventh is missing
						current.dontHaveGrade-=25;
					if (!found[1]) { // if 9 or 11 is missing, it is more unusual
						current.unusualGrade-=30;
						current.dontHaveGrade-=10;
					}
					if (!found[0]) {
						current.unusualGrade-=30;
						current.dontHaveGrade-=10;
					}
				}
			}
		}
		else
			if (found[1]) { // -------------- 11
				current.params[ALTERATION_INDEX]=2;
				current.params[PLUSMINUS_INDEX]=plusMinus[1];
				current.params[I11_INDEX]=0;
				current.unusualGrade-=10;
				
				if (!found[0] || !found[3]) { // b7 or 9 not inside
					if (!found[0] && !found[3])
						current.params[ADDCHK_INDEX]=1;
					else{
						if (!found[3])
							current.dontHaveGrade-=25;
						if (!found[0]) {
							current.unusualGrade-=30;
							current.dontHaveGrade-=10;
						}
					}
				}
			}
			else 
				if (found[0]) { // 9
					current.params[ALTERATION_INDEX]=1;
					current.params[I9_INDEX]=0;
					current.params[I11_INDEX]=0;
					current.params[PLUSMINUS_INDEX]=plusMinus[0];
					current.unusualGrade-=10;
					if (!found[3])
						current.params[ADDCHK_INDEX]=1;
					
				}
	}
	
	/**
	 * Shellsort, using a sequence suggested by Gonnet.
	 * -- a little adopted
	 * @param a List of Proposals, unsorted
	 * @param sortIndex 1 to sort by don'tHaveGrade, 2 to sort by unusualGrade
	 * @return sorted list by selected criteria
	 */
	public void shellsort( List<Proposal> a, int sortIndex ){
		int length = a.size();
		for( int gap = length / 2; gap > 0;
					 gap = gap == 2 ? 1 : (int) ( gap / 2.2 ) )
			for( int i = gap; i < length; i++ ){
				Proposal tmp = (Proposal)a.get(i);
				int j = i;
				
				for( ; j >= gap && 
				(  sortIndex == 1 ?
				tmp.dontHaveGrade > ((Proposal)a.get(j - gap)).dontHaveGrade :
				tmp.unusualGrade > ((Proposal)a.get(j - gap)).unusualGrade  )
				; 
				j -= gap )
					a.set(j, a.get(j - gap));
				a.set( j , tmp);
			}
	}

	public void addProposalParameters(int[] params){
		this.proposalParameters.add(params);
	}
	
	public void addProposalName(String name) {
		this.proposalList.addItem(new UISelectItem<Integer>(name, this.proposalList.getItemCount()));
	}
	
	public void clearProposals(){
		this.proposalList.removeItems();
		this.proposalParameters.clear();
	}
	
	public TGChordDialog getDialog(){
		return this.dialog;
	}
	
//	public UIListBoxSelect<Integer> getProposalList(){
//		return this.proposalList;
//	}
	
	public boolean isValidProcess(long processId){
		return (this.runningProcess == processId);
	}
	
	public UIPanel getControl() {
		return control;
	}
	
	private class Proposal implements Cloneable{
		int[] params;
		
		/** grade for chord "unusualness" - Cm is less unusual than E7/9+/C */
		int unusualGrade = 0;
		/** penalty for notes that chord doesn't have */
		int dontHaveGrade = -15;
		
		/** counts the notes that are in chord but still not recognized */
		int missingCount;
		int[] missingNotes;
		
		boolean filled[]={false,false,false,false};
		int plusminusValue[]={0,0,0};
		
		private Proposal() {
			super();
			this.params = new int[9];
			for (int i=0; i<9; i++)
				this.params[i]=-1;
		}
		
		/** initialize with needed notes */
		public Proposal(List<Integer> notes) {
			this.params = new int[9];
			for (int i=0; i<9; i++)
				this.params[i]=-1;
			
			int length = notes.size();
			this.missingNotes = new int[length];
			for (int i = 0; i< length; i++){ // deep copy, because of clone() method
				this.missingNotes[i] = ((Integer)notes.get(i)).intValue();
			}
			this.missingCount = length;
		}
		
		/** if note is found, mark it as found in the Missing array*/
		void foundNote(int value) {
			int note = (value % 12);
			if (this.missingCount!=0)
				for (int i=0; i<this.missingCount; i++)
					if (this.missingNotes[i] == note) {
						// put the found one on the end, switch positions
						this.missingCount--;
						int temp = this.missingNotes[i]; 
						this.missingNotes[i]=this.missingNotes[this.missingCount];
						this.missingNotes[this.missingCount]=temp;
						return;
					}
		}
		
		/** is note already found? */
		boolean isFound(int value) {
			int note = (value % 12);
			for (int i=this.missingCount; i<this.missingNotes.length; i++)
				if (this.missingNotes[i] == note)
					return true;
			return false;
		}
		
		/** is note required to be found? */
		boolean isNeeded(int value) {
			int note = (value % 12);
			if (this.missingCount!=0)
				for (int i=0; i<this.missingCount; i++)
					if (this.missingNotes[i] == note)
						return true;
			return false;
		}
		
		/** does note exist in a chord? (found or not found) */
		boolean isExisting(int value) {
			int note = (value % 12);
			for (int i=0; i<this.missingNotes.length; i++)
				if (this.missingNotes[i] == note)
					return true;
			return false;
		}
		
		/** calls the Object.clone() method, since it is private (?!!??) */
		public Object clone() {
			Proposal proposal = new Proposal();
			for (int i=0; i<9; i++)
				proposal.params[i] = this.params[i];
			proposal.unusualGrade = this.unusualGrade;
			proposal.dontHaveGrade = this.dontHaveGrade;
			proposal.missingCount = this.missingCount;
			proposal.missingNotes = new int[this.missingNotes.length];
			for(int i = 0; i < proposal.missingNotes.length; i ++){
				proposal.missingNotes[i] = this.missingNotes[i];
			}
			proposal.filled = new boolean[this.filled.length];
			for (int i=0; i<proposal.filled.length; i++)
				proposal.filled[i] = this.filled[i];
			
			proposal.plusminusValue = new int[this.plusminusValue.length];
			for (int i=0; i<proposal.plusminusValue.length; i++)
				proposal.plusminusValue[i] = this.plusminusValue[i];
			
			return proposal;
		}
		
		public boolean equals(Object o) {
			Proposal another = (Proposal)o;
			for (int i=0; i<9; i++)
				if (this.params[i]!=another.params[i])
					return false;
			// not all attributes, but the rest is not needed YET!
			return true;
		}
	}
}