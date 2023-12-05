package org.herac.tuxguitar.app.view.dialog.chord;

/**
 * Creates a chord name String out of given parameters
 * @author Nikola Kolarovic
 */
public class TGChordNamingConvention {
	
	/** generates the chord name out of selected items */
	public String createChordName(int chordTonic,
	                              int chordIndex,
	                              int alteration,
	                              int plusMinus,
	                              boolean add,
	                              int add5,
	                              int add9,
	                              int add11,
	                              int bassTonic,
	                              boolean sharp) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(getTonic(chordTonic,sharp));
		
		//String chordName = ((ChordInfo)ChordCreatorUtil.getChordData().getChords().get(chordIndex)).getName();
		String chordName = TGChordDatabase.get(chordIndex).getName();
		if (!chordName.equals("M"))
			sb.append(chordName);
		
		if (add)
			sb.append("add");
		
		// TODO: ALTERATION
		if (alteration!=0) {
			char lastChar = sb.toString().charAt(sb.toString().length()-1);
			// if chord name ends with a number then add a backslash
			if (lastChar>='0' && lastChar <='9')
				sb.append("/");
			
			switch (alteration) {
				case 1 : sb.append( getAdd("9",plusMinus));
					break;
				case 2 : sb.append( getAdd("11",plusMinus));
					break;
				case 3 : sb.append( getAdd("13",plusMinus));
					break;
			}
			
		}
		
		if (add5!=0)
			sb.append("/").append(getAdd("5",add5));
		if (add9!=0)
			sb.append("/").append(getAdd("9",add9));
		if (add11!=0)
			sb.append("/").append(getAdd("11",add11));
		
		if (chordTonic!=bassTonic) {
			sb.append(" \\");
			sb.append(getTonic(bassTonic,sharp));
		}
		return sb.toString();
	}
	
	/** tonic marks */
	public String getTonic(int chordTonic, boolean sharp) {
		String retVal;
		switch(chordTonic) {
			case 0 : retVal="C"; break;
			case 1 : retVal= sharp ? "C#" : "Db"; break;
			case 2 : retVal="D"; break;
			case 3 : retVal= sharp ? "D#" : "Eb"; break;
			case 4 : retVal="E"; break;
			case 5 : retVal="F"; break;
			case 6 : retVal= sharp ? "F#" : "Gb"; break;
			case 7 : retVal="G"; break;
			case 8 : retVal= sharp ? "G#" : "Ab"; break;
			case 9 : retVal="A"; break;
			case 10 : retVal= sharp ? "A#" : "Bb"; break;
			default : retVal="B"; break;
		}
		return retVal;
	}
	
	/** adds + or - to an add chord String */
	private String getAdd(String number, int selectionIndex) {
		StringBuffer retVal=new StringBuffer(number);
		switch(selectionIndex) {
			case 1 : retVal.append("+"); break;
			case 2 : retVal.append("-"); break;
		}
		return retVal.toString();
	}
}
