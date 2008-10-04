package org.herac.tuxguitar.io.ascii;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;

public class ASCIITabOutputStream {
	
	private static final String[] TONIC_NAMES = new String[]{"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
	
	private static final int MAX_LINE_LENGTH = 80;
	
	private TGSongManager manager;
	private PrintStream stream;
	private ASCIIOutputStream out;
	
	public ASCIITabOutputStream(PrintStream stream){
		this.stream = stream;
	}
	
	public ASCIITabOutputStream(OutputStream stream){
		this(new PrintStream(stream));
	}
	
	public ASCIITabOutputStream(String fileName) throws FileNotFoundException{
		this(new FileOutputStream(fileName));
	}
	
	public void writeSong(TGSong song){
		this.manager = new TGSongManager();
		this.manager.setSong(song);
		this.out = new ASCIIOutputStream(this.stream);
		this.drawSong();
		this.out.flush();
		this.out.close();
	}
	
	private void drawSong(){
		TGSong song = this.manager.getSong();
		
		//Propiedades de cancion
		this.out.drawStringLine("Title: " + song.getName());
		this.out.drawStringLine("Artist: " + song.getArtist());
		this.out.drawStringLine("Album: " + song.getAlbum());
		this.out.drawStringLine("Author: " + song.getAuthor());
		
		Iterator it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			this.out.nextLine();
			drawTrack(track);
			this.out.nextLine();
		}
	}
	
	private void drawTrack(TGTrack track){
		//Propiedades de pista
		this.out.nextLine();
		this.out.drawStringLine("Track " + track.getNumber() + ": " + track.getName());
		
		//Obtengo los nombres de la afinacion, y el ancho maximo que ocupa
		String[] tuning = new String[track.getStrings().size()];
		int maxTuningLength = 1;
		for(int i = 0; i < track.getStrings().size();i++){
			TGString string = (TGString)track.getStrings().get(i);
			tuning[i] = TONIC_NAMES[(string.getValue() % TONIC_NAMES.length)];
			maxTuningLength = Math.max(maxTuningLength,tuning[i].length());
		}
		
		int nextMeasure = 0;
		boolean eof = false;
		while(!eof){
			this.out.nextLine();
			int index = nextMeasure;
			for(int i = 0; i < track.getStrings().size();i++){
				TGString string = (TGString)track.getStrings().get(i);
				
				//Dibujo la afinacion de la cuerda
				this.out.drawTuneSegment(tuning[i],maxTuningLength);
				int measureCount = track.countMeasures();
				for(int j = index; j < measureCount; j++){
					TGMeasure measure = track.getMeasure(j);
					drawMeasure(measure,string);
					nextMeasure = (j + 1);
					
					//Calculo si era el ultimo compas
					eof = (this.manager.getTrackManager().isLastMeasure(measure));
					
					//Si se supero el ancho maximo, bajo de linea
					if(this.out.getPosX() > MAX_LINE_LENGTH){
						break;
					}
				}
				//Cierro los compases
				this.out.drawBarSegment();
				this.out.nextLine();
			}
			this.out.nextLine();
		}
		this.out.nextLine();
	}
	
	private void drawMeasure(TGMeasure measure,TGString string){
		//Abro el compas
		this.out.drawBarSegment();
		this.out.drawStringSegments(1);
		TGBeat beat = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
		while(beat != null){
			int outLength = 0;
			
			//Si hay una nota en la misma cuerda, la dibujo
			TGNote note = this.manager.getMeasureManager().getNote(beat, string.getNumber());
			if(note != null){
				outLength = (Integer.toString(note.getValue()).length() - 1);
				this.out.drawNote(note.getValue());
			}
			//dejo el espacio
			else{
				this.out.drawStringSegments(1);
			}
			
			TGBeat nextBeat = this.manager.getMeasureManager().getNextBeat( measure.getBeats() , beat);
			
			long length = (nextBeat != null ? nextBeat.getStart() - beat.getStart() : (measure.getStart() + measure.getLength()) - beat.getStart());
			//Agrego espacios correspondientes hasta el proximo pulso.
			this.out.drawStringSegments(getDurationScaping(length) - outLength);
			
			beat = nextBeat;
			
			//Agrego espacios correspondientes hasta el proximo pulso.
			//this.out.drawStringSegments(getDurationScaping(beat.getDuration()) - outLength);
			
			//beat = this.manager.getMeasureManager().getNextBeat( measure.getBeats() , beat);
		}
		
	}
	/*
	private int getDurationScaping(TGDuration duration){
		int spacing = 1;
		
		if(duration.getValue() >= TGDuration.SIXTEENTH){
			spacing = 2;
		}
		else if(duration.getValue() >= TGDuration.EIGHTH){
			spacing = 3;
		}
		else if(duration.getValue() >= TGDuration.QUARTER){
			spacing = 4;
		}
		else if(duration.getValue() >= TGDuration.HALF){
			spacing = 5;
		}
		else if(duration.getValue() >= TGDuration.WHOLE){
			spacing = 6;
		}
		return spacing;
	}
	*/
	private int getDurationScaping(long length){
		int spacing = 6;
		if(length <= (TGDuration.QUARTER_TIME / 8)){
			spacing = 1;
		}
		else if(length <= (TGDuration.QUARTER_TIME / 4)){
			spacing = 2;
		}
		else if(length <= (TGDuration.QUARTER_TIME / 2)){
			spacing = 3;
		}
		else if(length <= TGDuration.QUARTER_TIME){
			spacing = 4;
		}
		else if(length <= (TGDuration.QUARTER_TIME * 2)){
			spacing = 5;
		}
		return spacing;
	}
}
