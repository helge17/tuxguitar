/**
 * 
 */
package org.herac.tuxguitar.io.abc.base;

/**
 * @author peter
 * <p>
 * <b>Redefinable symbols</b>
 * <p>
 * As a short cut to writing symbols which avoids the +symbol+ syntax (see decorations),
 * the letters H-W and h-w and the symbol ~ can be assigned with the U: field.<br>
 * For example, to assign the letter T to represent the trill, you can write: 
 * <pre>
 *     U: T = +trill+
 * </pre>
 * You can also use "^text" etc (see Annotations below) in definitions, e.g. 
 * <pre>
 *     U: X = ''^+''
 * </pre>
 * to print a plus sign over notes with X before them. 
 * <p>
 * Symbol definitions can be written in the file header, in which case they apply to all
 * the tunes in that file, or in a tune header, when they apply only to that tune, and 
 * override any previous definitions.
 * <p> 
 * Programs may also make use of a set of global default definitions, which apply 
 * everywhere unless overridden by local definitions. <br>
 * You can assign the same symbol to two or more letters e.g. 
 * <pre>
 *     U: T = +trill+
 *     U: U = +trill+
 * </pre>
 * in which case the same visible symbol will be produced by both letters 
 * (but they may be played differently), 
 * and you can de-assign a symbol by writing: 
 * <pre>
 *     U: T = +nil+
 * </pre>
 * or 
 * <pre>
 *     U: T = +none+
 * </pre>
 * The standard set of definitions (if you do not redefine them) is: 
 * <pre>
 *     U: ~ = +roll+
 *     U: T = +trill+
 *     U: H = +fermata+
 *     U: L = +emphasis+
 *     U: M = +lowermordent+
 *     U: P = +uppermordent+
 *     U: S = +segno+
 *     U: O = +coda+
 *     U: u = +upbow+
 *     U: v = +downbow+
 * </pre>
 * Please see ABCMacro for an advanced macro mechanism.
 * <p>
 * @see ABCMacro
 */
public class ABCSymbol {

	private char name;
	private String symbol;
	private boolean valid;
	

	public ABCSymbol(String string) {
		this.valid=false;
		int i=string.indexOf('=');
		if(i>0 && string.substring(0, i).trim().length()==1) {
			this.name=string.substring(0, i).trim().charAt(0);
			this.symbol=string.substring(i+1).trim();
			if("~HIJKLMNOPQRSTUVWhijklmnopqrstuvw".indexOf(this.name)>=0)
				if(this.symbol.length()>2 && this.symbol.charAt(0)==this.symbol.charAt(this.symbol.length()-1))
					if(this.symbol.charAt(0)=='+' || this.symbol.charAt(0)=='"')
						if(this.symbol.substring(1, this.symbol.length()-1).indexOf(this.symbol.charAt(0))<0)
							valid=true;
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
			case '!':
				if(q=='!') q=0;
				else if(q==0) q='!';
				break;
			case '|':
				if(q=='!') q=0;
				else if(q=='[') q=0;
				break;
			case '[':
				if(q==0) q='[';
				break;
			case ']':
				if(q=='!') q=0;
				else if(q=='[') q=0;
				break;
			default:
				if(q==0 && line.charAt(i)==name) {
					line=line.substring(0,i)+symbol+line.substring(i+1);
					i+=symbol.length()-1;
				}
			}
			++i;
		}
		return line;
	}

	public char getName() {
		return name;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isNil() {
		return symbol.equals("+nil+") || symbol.equals("+none+");
	}

}
