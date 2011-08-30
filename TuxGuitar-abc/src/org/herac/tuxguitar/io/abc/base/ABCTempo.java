package org.herac.tuxguitar.io.abc.base;

public class ABCTempo {
	
	private int value;
	
	public ABCTempo(int value){
		this.value = value;
	}
	
	/**
	 * @param string
	 * <p>
	 * "the actual number of beats per minute in a piece marked allergro (for example),
	 *  will depend on the music itself.
	 *  A piece consisting mainly of 'half notes' can be played very much quicker in
	 *  terms of bpm than a piece consisting mainly of 'sixteenth notes', 
	 *  but still be described with the same word."
	 *  <p>
	 * Here are the best descriptions for the following tempo markings:<br>
	 * <table border="2">
	 * <tr><th>Beats/minute</th><th>marking</th><th>description</th></tr>
	 * <tr><td>20</td><td>Larghissimo/td><td>very, very slow (20 bpm and below)</td></tr>
	 * <tr><td>40</td><td>Grave/td><td>slow and solemn (20-40 bpm)</td></tr>
	 * <tr><td>50</td><td>Lento/td><td>very slow (40-60 bpm)</td></tr>
	 * <tr><td>60</td><td>Largo/td><td>very slow (40-60 bpm), like lento</td></tr>
	 * <tr><td>65</td><td>Larghetto</td><td>rather broadly (60-66 bpm)</td></tr> 
	 * <tr><td>76</td><td>Adagio</td><td>slowly </td></tr>
	 * <tr><td>80</td><td>Andante</td><td>at a walking pace</td></tr> 
	 * <tr><td>90</td><td>Andante Moderato</td><td>a bit faster than andante</td></tr>
	 * <tr><td>100</td><td>Andantino</td><td>slightly faster than andante</td></tr>
	 * <tr><td>105</td><td>Moderato</td><td>moderately (101-110 bpm)</td></tr>
	 * <tr><td>110</td><td>Allegretto</td><td>moderately fast (but less so than allegro)</td></tr>
	 * <tr><td>120</td><td>Allegro moderato</td><td>moderately quick (112-124 bpm)</td></tr>
	 * <tr><td>130</td><td>Allegro</td><td>fast, quickly and bright (120-139 bpm)</td></tr>
	 * <tr><td>140</td><td>Vivace</td><td>lively and fast (~140 bpm) (quicker than allegro)</td></tr>
	 * <tr><td>150</td><td>Vivacissimo</td><td>very fast and lively</td></tr>
	 * <tr><td>160</td><td>Allegrissimo</td><td>very fast</td></tr>
	 * <tr><td>180</td><td>Presto</td><td>very fast (168-200 bpm)</td></tr>
	 * <tr><td>220</td><td>Prestissimo</td><td>extremely fast (more than 200bpm)</td></tr>
	 * </table>
	 * 
	 */
	public ABCTempo(String string) {
		this.value=120;
		if(string.indexOf("larghissimo")>=0) this.value=20;
		else if(string.indexOf("grave")>=0) this.value=40;
		else if(string.indexOf("lento")>=0) this.value=50;
		else if(string.indexOf("largo")>=0) this.value=60;
		else if(string.indexOf("larghetto")>=0) this.value=65;
		else if(string.indexOf("adagio")>=0) this.value=76;
		else if(string.indexOf("andante moderato")>=0) this.value=90;
		else if(string.indexOf("andante")>=0) this.value=80;
		else if(string.indexOf("andantino")>=0) this.value=100;
		else if(string.indexOf("allegro moderato")>=0) this.value=120;
		else if(string.indexOf("moderato")>=0) this.value=105;
		else if(string.indexOf("allegretto")>=0) this.value=110;
		else if(string.indexOf("allegro")>=0) this.value=130;
		else if(string.indexOf("vivace")>=0) this.value=140;
		else if(string.indexOf("vivacissimo")>=0) this.value=150;
		else if(string.indexOf("allegrissimo")>=0) this.value=160;
		else if(string.indexOf("presto")>=0) this.value=180;
		else if(string.indexOf("prestissimo")>=0) this.value=220;
		else if(string.indexOf("slow")>=0) this.value=80;
		else if(string.indexOf("fast")>=0) this.value=130;
		else if(string.indexOf("lively")>=0) this.value=140;
	}

	public int getValue() {
		return this.value;
	}
	
	public String toString(){
		String string = new String("[TEMPO]");
		string += "\n     Value:       " + getValue();
		return string;
	}

	public ABCTempo copy() {
		return new ABCTempo(value);
	}
}
