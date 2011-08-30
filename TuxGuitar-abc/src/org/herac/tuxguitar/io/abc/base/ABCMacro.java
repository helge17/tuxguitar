/**
 * 
 */
package org.herac.tuxguitar.io.abc.base;

/**
 * @author peter
 * <p>
 * <b>Macros</b>
 * <p>
 *  This standard defines an optional system of macros which is principally used to define the way in which ornament symbols 
 *  such as the tilde ~ are played (although it could be used for many other purposes).
 *  <p> 
 *  Software implementing these macros, should first expand the macros defined in this section, 
 *  and only afterwards apply any relevant U: replacement (see section Redefinable symbols). 
 *  <p>
 *  When these macros are stored in an ABC Header file (see section Include field), they may form a powerful library. 
 *  <p>
 *  There are two kinds of macro, called Static and Transposing.
 *  <p> 
 * <b>Static macros</b>
 * <p>
 *  You define a static macro by writing into the tune header something like this:
 *  <p> 
 *  m: ~G3 = G{A}G{F}G
 *  <p>
 *  When you play the tune, the program searches the tune header for macro definitions, 
 *  then does a search and replace on its internal copy of the text before passing that 
 *  to the parser which plays the tune. Every occurence of ~G3 in the tune is replaced 
 *  by G{A}G{F}G, and that is what gets played. Only ~G3 notes are affected,
 *  ~G2, ~g3, ~F3 etc. are ignored. 
 *  <p>
 *  You can put in as many macros as you want, and indeed, if you only use static macros 
 *  you will need to write a separate macro for each combination of pitch and note-length. 
 *  Here is an example:
 *  <pre> 
 * X:50
 * T:Apples in Winter
 * S:Trad, arr. Paddy O'Brien
 * R:jig
 * E:9
 * m: ~g2 = {a}g{f}g
 * m: ~D2 = {E}D{C}D
 * M:6/8
 * K:D
 * G/2A/2|BEE dEE|BAG FGE|~D2D FDF|ABc ded|
 * BEE BAB|def ~g2 e|fdB AGF|GEE E2:|
 * d|efe edB|ege fdB|dec dAF|DFA def|
 * [1efe edB|def ~g2a|bgb afa|gee e2:|
 * [2edB def|gba ~g2e|fdB AGF|GEE E2||
 * </pre>
 * <p>
 *  Here I have put in two static macros, since there are two different notes in the tune marked with a tilde. 
 *  <p>
 *  A static macro definition consists of four parts:
 *  <ul><li> 
 *  the field identifier m:
 *  </li><li>
 *  the target string - e.g ~G3
 *  </li><li>
 *  the equals sign
 *  </li><li>
 *  the replacement string - e.g. G{A}G{F}G
 *  </li></ul>
 *  <p>
 *  The target string can consist of any string up to 31 characters in length, 
 *  except that it may not include the letter 'n', for reasons which will become obvious later. 
 *  You don't have to use the tilde, but of course if you don't use a legal combination of abc, 
 *  other programs will not be able to play your tune. 
 *  <p>
 *  The replacement string consists of any legal abc text up to 200 characters in length. 
 *  It's up to you to ensure that the target and replacement strings occupy the same time 
 *  interval (the program does not check this). Both the target and replacement strings may 
 *  have spaces embedded if necessary, but leading and trailing spaces are stripped off so 
 *  <p>
 * m:~g2={a}g{f}g
 * <p>
 *  is perfectly OK, although less readable. 
 * <p><b>Transposing macros</b>
 *  If your tune has ornaments on lots of different notes, and you want them to all play 
 *  with the same ornament pattern, you can use transposing macros to achieve this. 
 *  Transposing macros are written in exactly the same way as static macros, except that 
 *  the note symbol in the target string is represented by 'n' (meaning any note) and the 
 *  note symbols in the replacement string by other letters (h to z) which are interpreted 
 *  according to their position in the alphabet relative to n.
 *  <p> 
 *  So, for example I could re-write the static macro m: ~G3 = G{A}G{F}G as a transposing 
 *  macro m: ~n3 = n{o}n{m}n. When the transposing macro is expanded, any note of the form 
 *  ~n3 will be replaced by the appropriate pattern of notes. 
 *  Notes of the form ~n2 (or other lengths) will be ignored, so you will have to write 
 *  separate transposing macros for each note length. 
 *  <p>
 *  Here's an example:
 *  <pre> 
 * X:35
 * T:Down the Broom
 * S:Trad, arr. Paddy O'Brien
 * R:reel
 * M:C|
 * m: ~n2 = (3o/n/m/ n                % One macro does for all four rolls
 * K:ADor
 * EAAG~A2 Bd|eg~g2 egdc|BGGF GAGE|~D2B,D GABG|
 * EAAG ~A2 Bd|eg~g2 egdg|eg~g2 dgba|gedB BAA2:|
 * ~a2ea agea|agbg agef|~g2dg Bgdg|gfga gede|
 * ~a2 ea agea|agbg ageg|dg~g2 dgba|gedB BA A2:|
 * </pre>
 * <p>
 *  A transposing macro definition consists of four parts:
 *  <ul><li> 
 *  the field identifier m:
 *  </li><li>
 *  the target string - e.g ~n3
 *  </li><li>
 *  the equals sign
 *  </li><li>
 *  the replacement string - e.g. n{o}n{m}n
 *  </li></ul>
 *  <p>
 *  The target string can consist of any string up to 31 characters in length, 
 *  except that it must conclude with the letter 'n', followed by a number 
 *  which specifies the note length. 
 *  <p>
 *  The replacement string consists of any legal abc text up to 200 characters in length, 
 *  where note pitches are defined by the letters h - z, the pitches being interpreted 
 *  relative to that of the letter n. Once again you should ensure that the time intervals match. 
 *  You should not use accidentals in transposing macros (I can't for the life of me think of a way 
 *  to transpose ~=a3 or ~^G2 which will work correctly under all circumstances, so if you need to 
 *  do this you must use a static macro.) 
 * <p>
 * @see ABCSymbol
 */
public class ABCMacro {
	
	private static final String N_SCALE = 
		"D, E, F, G, A, B, C  D  E  F  G  A  B  c  d  e  f  g  a  b  c' d' e' f' g' a' b' c''d''e''f''g''";
	//   h  i  j  k  l  m  n                                      n  o  p  q  r  s  t  u  v  w  x  y  z  
	private String name;
	private String symbol;
	private boolean valid;
	private String len;
	private char n;
	private int targetlen;
	private String nmatch;

	public ABCMacro(String string) {
		this.valid=false;
		int i=string.indexOf('=');
		if(i>0 && string.substring(0, i).trim().length()<32) {
			this.name=string.substring(0, i).trim();
			this.symbol=string.substring(i+1).trim();
			this.targetlen=this.name.length();
			if(this.name.matches(".*[A-Ga-gn][1-9][0-9]*")) {
				this.valid=true;
				this.len="";
				while(this.name.matches(".*[0-9]+")) {
					this.len  = this.name.substring(this.name.length()-1)+this.len;
					this.name = this.name.substring(0, this.name.length()-1);
				}
				this.n = this.name.charAt(this.name.length()-1);
				this.name=this.name.substring(0, this.name.length()-1);
				if(this.n=='n')
					this.nmatch="[A-Ga-g]"+len+".*";
				else
					this.nmatch=String.valueOf(this.n);
			}
		}
		
	}

	public String execute(String line) {
		int i=0;
		char q=0;
		while(i<line.length()) {
			switch(line.charAt(i)) {
			case '"': 
				if(q=='"') q=0;
				else if(q==0) q='"';
				break;
			case '+':
				if(q=='+') q=0;
				else if(q==0) q='+';
				break;
			default:
				if(q==0 
				&& line.substring(i).length()>=targetlen 
				&& line.substring(i).startsWith(name)
				&& line.substring(i+name.length()).matches(nmatch)) {
					line=line.substring(0,i)+invoke(line.charAt(i+name.length()))+line.substring(i+targetlen);
					i+=symbol.length()-1;
				}
				break;
			}
			++i;
		}
		return line;
	}

	private String invoke(char note) {
		if(this.n!='n') return this.symbol;
		String s="";
		for(int i=0;i<symbol.length();i++) {
			s+=replacement(symbol.charAt(i),note);
		}
		return s;
	}

	private String replacement(char c, char note) {
		int i="hijklmnopqrstuvwxyz".indexOf(c);
		if(i<0) return String.valueOf(c);
		i=N_SCALE.indexOf(note+"  ")+3*(c-'n');
		return N_SCALE.substring(i, i+3).trim();
	}

	public String getName() {
		return name;
	}

	public boolean isValid() {
		return valid;
	}

}
