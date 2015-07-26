package org.herac.tuxguitar.io.abc.base;

import org.herac.tuxguitar.app.view.dialog.chord.TGChordDatabase.ChordInfo;

public class ABCChord {

	private static final String GCHORDNOTESET = "fghijklGHIJ";

	private static final ChordInfo[] data = new ChordInfo[]{
		

		new ChordInfo("",      new int[]{ 1, 5, 8 }),		// Major
		new ChordInfo("7",     new int[]{ 1, 5, 8, 11 }),
		new ChordInfo("maj7",  new int[]{ 1, 5, 8, 12 }),
		new ChordInfo("6",     new int[]{ 1, 5, 8, 10 }),
		new ChordInfo("m",     new int[]{ 1, 4, 8 }),
		new ChordInfo("m7",    new int[]{ 1, 4, 8, 11 }),
		new ChordInfo("m/maj7",new int[]{ 1, 4, 8, 12 }),
		new ChordInfo("m6",    new int[]{ 1, 4, 8, 10 }),
		new ChordInfo("sus2",  new int[]{ 1, 3, 8 }),
		new ChordInfo("sus4",  new int[]{ 1, 6, 8 }),
		new ChordInfo("7sus2", new int[]{ 1, 3, 8, 11 }),
		new ChordInfo("7sus4", new int[]{ 1, 6, 8, 11 }),
		new ChordInfo("dim",   new int[]{ 1, 4, 7 }),
		new ChordInfo("dim7",  new int[]{ 1, 4, 7, 10 }),
		new ChordInfo("aug",   new int[]{ 1, 5, 9 }),
		new ChordInfo("5",     new int[]{ 1, 8 }),
//		from here extending ChordDataBase 
		new ChordInfo("M7",    new int[]{ 1, 5, 8, 12 }),
		new ChordInfo("+",     new int[]{ 1, 5, 9 }),
		new ChordInfo("aug7",  new int[]{ 1, 5, 9, 11 }),
		new ChordInfo("7+",    new int[]{ 1, 5, 9, 11 }),
		new ChordInfo("9",     new int[]{ 1, 5, 8, 11, 3 }),
		new ChordInfo("m9",    new int[]{ 1, 4, 8, 11, 3 }),
		new ChordInfo("maj9",  new int[]{ 1, 5, 8, 12, 3 }),
		new ChordInfo("M9",    new int[]{ 1, 5, 8, 12, 3 }),
		new ChordInfo("11",    new int[]{ 1, 5, 8, 11, 3, 6 }),
		new ChordInfo("dim9",  new int[]{ 1, 5, 8, 11, 14 }),
		new ChordInfo("sus",   new int[]{ 1, 6, 8 }),
		new ChordInfo("sus9",  new int[]{ 1, 3, 8 }),
		new ChordInfo("7sus",  new int[]{ 1, 6, 8, 11 }),
		new ChordInfo("7sus9", new int[]{ 1, 3, 8, 11 }),
		new ChordInfo("9sus",  new int[]{ 1, 6, 11, 15, 20 }),
		new ChordInfo("9sus4", new int[]{ 1, 6, 11, 15, 20 }),
		new ChordInfo("5",     new int[]{ 1, 8 }),
		new ChordInfo("13",    new int[]{ 1, 5, 8, 11, 17, 21 }),
		
	};
	
	private byte[] strings;
	private String name;
	private String[] chordnote;
	private int size;

	private int[] gchordnote;
	
	public ABCChord(String name){
		this.name = name.substring(0, 1).toUpperCase()+name.substring(1);
		plotChord(this.name);
	}
	
	private void plotChord(String name) {
		String base,variant,root;
		if(name.length()>1 && (name.charAt(1)=='#' || name.charAt(1)=='b')) {
			base=name.substring(0,2);
			variant=name.substring(2);
		}
		else {
			base=name.substring(0, 1);
			variant=name.substring(1);
		}
		if(variant.length()==0) variant="M";
		if(variant.lastIndexOf('/')<0 || variant.equals("m/maj7")) root=base;
		else {
			int i=variant.indexOf('/');
			root=variant.substring(i+1);
			variant=variant.substring(0, i);
		}
		if(variant.indexOf('(')>0) {
			int i=variant.indexOf('(');
			variant=variant.substring(0, i);
		}
		if(root.length()>1 && root.charAt(1)=='#') root="^"+root.substring(0,1);
		else if(root.length()>1 && root.charAt(1)=='b') root="_"+root.substring(0,1);
		else root="="+root.substring(0,1);
		// chord builder
		strings=new byte[6];
		for(int x=0;x<strings.length;x++) strings[x]=-1;
		for(int i=0;i<data.length;i++) {
			ChordInfo info=data[i];
			if(variant.equals(info.getName())) {
				initChord(info,base,root);
				break;
			}
		}
		if(chordnote==null) {
			for(int i=0;i<data.length;i++) {
				ChordInfo info=data[i];
				if(variant.startsWith(info.getName())) {
					initChord(info,base,root);
					break;
				}
			}
		}
	}

	private void initChord(ChordInfo info, String base, String root) {
		int[] n = info.cloneRequireds();
		size=n.length;
		gchordnote=new int[GCHORDNOTESET.length()];
		chordnote=new String[7];
		chordnote[0]=root.toUpperCase();
		int note=" C D EF G A B ".indexOf(base.charAt(0));
		if(base.endsWith("#")) note++;
		else if(base.endsWith("b")) note--;
		note+=70; // YES not 71 for B3 because the requireds are base 1
		gchordnote[0]=note+1;
		for(int x=0;x<n.length;x++) {
			n[x]+=note;
			gchordnote[x+1]=n[x];
			if(n[x]<96)
				chordnote[x+1]="=C^C=D^D=E=F^F=G^G=A^A=B=c^c=d^d=e=f^f=g^g=a^a=b".substring((n[x]-72)*2,(n[x]-72)*2+2);
			else
				chordnote[x+1]="=c'^c'=d'^d'=e'=f'^f'=g'^g'=a'^a'=b'".substring((n[x]-96)*3,(n[x]-96)*3+3);
		}
		for(int x=n.length;x<chordnote.length-1;x++) {
			note=n[x-n.length]+12;
			gchordnote[x+1]=note;
			if(note<96)
				chordnote[x+1]="=C^C=D^D=E=F^F=G^G=A^A=B=c^c=d^d=e=f^f=g^g=a^a=b".substring((note-72)*2,(note-72)*2+2)+"'";
			else
				chordnote[x+1]="=c'^c'=d'^d'=e'=f'^f'=g'^g'=a'^a'=b'".substring((note-96)*3,(note-96)*3+3);
		}
		for(int x=chordnote.length-1;x<gchordnote.length-1;x++)
			gchordnote[x+1]=gchordnote[x+2-chordnote.length]-12;
		int[] snare={64, 69, 74, 79, 83, 88};
		for(int y=0;y<strings.length;y++) {
			int x=0;
			while(x<n.length && snare[y]>n[x]-24) ++x;
			if(x<n.length) {
				strings[strings.length-y-1]=(byte) (n[x]-24-snare[y]);
				continue;
			}
			x=0;
			while(x<n.length && snare[y]>n[x]-12) ++x;
			if(x<n.length) {
				strings[strings.length-y-1]=(byte) (n[x]-12-snare[y]);
				continue;
			}
			x=0;
			while(x<n.length && snare[y]>n[x]) ++x;
			if(x<n.length) {
				strings[strings.length-y-1]=(byte) (n[x]-snare[y]);
				continue;
			}
			x=0;
			while(x<n.length && snare[y]>n[x]+12) ++x;
			if(x<n.length) {
				strings[strings.length-y-1]=(byte) (n[x]+12-snare[y]);
				continue;
			}
		}
	}

	public String getName() {
		return this.name;
	}
	
	public byte[] getStrings() {
		return this.strings;
	}
	
	public int size() {
		return this.size;
	}
	
	public String toString(){
		String string = new String("[CHORD]");
		string += "\n     Name:       " + getName();
		for(int i = 0; i < this.strings.length; i ++){
			if(this.strings[i] != -1){
				string += "\n     String " + (i + 1) + ":    " + this.strings[i];
			}
		}
		return string;
	}

	public String getNote(char c) {
		switch(c) {
		case 'f': return chordnote[0];
		case 'g': return chordnote[1];
		case 'h': return chordnote[2];
		case 'i': return chordnote[3];
		case 'j': return chordnote[4];
		case 'k': return chordnote[5];
		case 'l': return chordnote[6];
		case 'G': return chordnote[1]+",";
		case 'H': return chordnote[2]+",";
		case 'I': return chordnote[3]+",";
		case 'J': return chordnote[4]+",";
		}
		return "z";
	}

	public char getGchordChar(int value) {
		while(value>gchordnote[GCHORDNOTESET.indexOf('l')]) value-=12;
		while(value<gchordnote[GCHORDNOTESET.indexOf('f')] && value<gchordnote[GCHORDNOTESET.indexOf('G')]) value+=12;
		for(int i=0;i<gchordnote.length;i++) {
			if(gchordnote[i]==value) return GCHORDNOTESET.charAt(i);
		}
		return 'f';
	}

}
