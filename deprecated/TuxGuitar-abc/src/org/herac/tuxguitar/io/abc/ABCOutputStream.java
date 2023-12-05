package org.herac.tuxguitar.io.abc;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.abc.base.ABCChord;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;

public class ABCOutputStream {
	
	private static final String ABC_VERSION = "ABC standard 2.0 & ABCplus-1.1.0";
	private static final String[] ABC_KEY_SIGNATURES = new String[]{ "C","G","D","A","E","B","F#","C#","F","Bb","Eb","Ab", "Db", "Gb","Cb" };
	private TGSongManager manager;
	private TGChannel channelAux;
	private PrintWriter writer;
	private ABCSettings settings;
	private int barsperstaff;
	private int instrumentoffset;
	private int[] droneparm;
	private int from;
	private int to;
	private int measures;
	private int diagramt;
	private int chordt;
	private int baset;
	private String vId;
	private String wline;
	private String line;
	private int dronet;
	private String gchord;
	private boolean droneon;
	private String drum;
	private int enterstogo;
	private int enters;
	private int times;
	private String triol;
	
	public ABCOutputStream(OutputStream stream,ABCSettings settings){
		this.writer = new PrintWriter(stream);
		this.settings = settings;
		this.instrumentoffset=(settings==null) ? 1 : settings.isInstrumentsStartAt1() ? 1 : 0;
	}
	
	public void writeSong(TGSong song){
		this.manager = new TGSongManager();
		this.channelAux = null;
		
		this.addVersion();
		this.addPaper(song);
		this.addSongDefinitions(song);
		this.addSong(song);
		
		this.writer.flush();
		this.writer.close();
	}
	
	private void addVersion(){
		this.writer.println("% ABC version " + ABC_VERSION + " with ABCPlus");
	}
	
	private void addPaper(TGSong song){
		this.writer.println("%%scale 0.66");
	}
	
	private void addSongDefinitions(TGSong song){
		String[] line={ "X:1" };
		if(song.getComments()!=null) line=song.getComments().split("\\n+");
		String s="X:1";
		for(int i=0;i<line.length;i++) {
			if(line[i].startsWith("X:")) {
				s=line[i];
				break;
			}
		}
		this.writer.println(s);
		this.writer.println("%%exprabove 0");
		this.writer.println("%%exprbelow 1");
		this.diagramt=this.settings.getDiagramTrack();
		this.chordt=this.settings.getChordTrack();
		this.baset=this.settings.getBaseTrack();
		this.dronet=this.settings.getDroneTrack();
		if(this.diagramt==ABCSettings.AUTO_TRACK 
		|| this.chordt==ABCSettings.AUTO_TRACK
		|| this.baset==ABCSettings.AUTO_TRACK
		|| this.dronet==ABCSettings.AUTO_TRACK) {
			for(int t=0;t<song.countTracks();t++) {
				TGTrack track = song.getTrack(t);
				int tnum=track.getNumber();
				if(this.settings.getTrack() == ABCSettings.ALL_TRACKS || this.settings.getTrack() == tnum) {
					String id = track.getName();
					
					if((tnum==this.diagramt+1 || id.indexOf('\t')<0) && !isPercussionTrack(song, track)) {
						for(int j=0;j<song.countTracks();j++) {
							TGTrack buddy=song.getTrack(j);
							if(this.chordt==j+1 || buddy.getName().matches(id+"\tchords")) {
								if(this.chordt==ABCSettings.AUTO_TRACK) this.chordt=j+1;
								if(this.diagramt==ABCSettings.AUTO_TRACK) this.diagramt=t+1;
							}
							if(this.baset==j+1 || buddy.getName().matches(id+"\tbase")) {
								if(this.baset==ABCSettings.AUTO_TRACK) this.baset=j+1;
								if(this.diagramt==ABCSettings.AUTO_TRACK) this.diagramt=t+1;
							}
							if(this.dronet==j+1 || buddy.getName().matches(id+"\tdrone")) {
								if(this.dronet==ABCSettings.AUTO_TRACK) this.dronet=j+1;
							}
						}
					}
				}
			}
		}
		if(this.chordt==ABCSettings.AUTO_TRACK) this.chordt=this.baset;
		if(this.baset==ABCSettings.AUTO_TRACK) this.baset=this.chordt;
		if(this.chordt==ABCSettings.NO_TRACK) this.baset=ABCSettings.NO_TRACK;
		if(this.baset==ABCSettings.NO_TRACK) this.chordt=ABCSettings.NO_TRACK;
		this.measures=song.countMeasureHeaders();
		this.from=ABCSettings.FIRST_MEASURE;
		this.to=ABCSettings.LAST_MEASURE;
		if(this.settings.getMeasureFrom() != ABCSettings.FIRST_MEASURE || this.settings.getMeasureTo() != ABCSettings.LAST_MEASURE) {
			this.from=this.settings.getMeasureFrom();
			to=this.settings.getMeasureTo();
		}
		if(this.from==ABCSettings.FIRST_MEASURE) this.from=0;
		if(this.to==ABCSettings.LAST_MEASURE) to=measures-1;
		if(this.from>measures-1) this.from=measures-1;
		if(this.to<this.from) this.to=this.from;
		this.measures=this.to-this.from+1;
		this.barsperstaff=this.settings.getMeasuresPerLine();
		if(this.barsperstaff==ABCSettings.AUTO_MEASURES) {
			this.barsperstaff=5;
			while((measures % this.barsperstaff) > 0) --this.barsperstaff;
			if(this.barsperstaff<2) {
				if((measures % 3)<2) this.barsperstaff=3;
				else this.barsperstaff=4;
			}
		}
		this.writer.println("%%%barsperstaff "+this.barsperstaff);
		if(song.getName()!=null && song.getName().trim().length()>0)               this.writer.println("T:"+song.getName());
		if(song.getAlbum()!=null && song.getAlbum().trim().length()>0)             this.writer.println("O:"+song.getAlbum());
		if(song.getAuthor()!=null && song.getAuthor().trim().length()>0)           this.writer.println("A:"+song.getAuthor());
		if(song.getTranscriber()!=null && song.getTranscriber().trim().length()>0) this.writer.println("Z:"+song.getTranscriber());
		TGTimeSignature ts = song.getMeasureHeader(0).getTimeSignature();
		int n=ts.getNumerator();
		int d=ts.getDenominator().getValue();
		if(n==4 && d==4) this.writer.println("M:C");
		else if(n==2 && d==2) this.writer.println("M:C|");
		else this.writer.println("M:"+n+"/"+d);
		this.writer.println("L:1/8");
		n=song.getMeasureHeader(0).getTempo().getValue();
		this.writer.println("Q:1/4="+n);
		int trackCount = 0;
		for(int i = 0; i < song.countTracks(); i ++) {
			TGTrack track = song.getTrack(i);
			String id = handleId(track.getName());
			if(id.indexOf('\t')<0 && !isPercussionTrack(song, track) && i!=this.chordt-1 && i!=this.baset-1 && i!=this.dronet-1)
				++trackCount;
		}
		if(trackCount > 1 && this.settings.getTrack() == ABCSettings.ALL_TRACKS) {
			for(int i = 0; i < song.countTracks(); i ++) {
				TGTrack track = song.getTrack(i);
				String id = handleId(track.getName());
				if(id.indexOf('\t')<0 && !isPercussionTrack(song, track) && i!=this.chordt-1 && i!=this.baset-1 && i!=this.dronet-1)
					this.writer.println("V:"+id.replaceAll("\\s+","_")+" clef="+clefname(track.getMeasure(0).getClef()));
			}
			if(this.settings.isTrackGroupEnabled()) {
				String staves="%%staves ";
				String stave2="";
				String stave3="";
				int v=0;
				for(int i = 0; i < song.countTracks(); i ++) {
					TGTrack track = song.getTrack(i);
					String id = handleId(track.getName());
					int clef=track.getMeasure(0).getClef();
					if(id.indexOf('\t')<0 && !isPercussionTrack(song, track) && i!=this.chordt-1 && i!=this.baset-1 && i!=this.dronet-1) {
						++v;
						if(v==1 && clef==TGMeasure.CLEF_TREBLE) staves+="1 ";
						else if(clef==TGMeasure.CLEF_TREBLE) stave2+=v+" ";
						else if(clef==TGMeasure.CLEF_BASS) stave3+=v+" ";
						else staves+=v+" ";
					}
				}
				if(stave2.trim().split(" ").length>1) stave2="("+stave2.trim()+")";
				if(stave3.trim().split(" ").length>1) stave3="("+stave3.trim()+")";
				if(stave2.length()>0 && stave3.length()>0) staves+="{"+stave2.trim()+" "+stave3.trim()+"}";
				else staves+=(stave2+stave3).trim();
				this.writer.println(staves);
			}
		}
		if(song.getMeasureHeader(0).getTripletFeel()!=TGMeasureHeader.TRIPLET_FEEL_NONE)
			this.writer.println("R:hornpipe");
		this.writer.println("K:"+ABC_KEY_SIGNATURES[song.getTrack(0).getMeasure(0).getKeySignature()]);
		int v=0;
		TGBeat[] gchordBeat=new TGBeat[2];
		for(int i = 0; i < song.countTracks(); i ++) {
			TGTrack track = song.getTrack(i);
			String id = handleId(track.getName());
			int tnum=track.getNumber();
			if(id.indexOf('\t')>0) {
				for(int j=0;j<song.countTracks();j++) {
					TGTrack buddy=song.getTrack(j);
					if(id.matches(buddy.getName()+"\t.*")) {
						tnum=buddy.getNumber();
						break;
					}
				}
			}
			if(this.settings.getTrack() == ABCSettings.ALL_TRACKS || this.settings.getTrack() == tnum){
				TGChannel channel = getChannel(song, track);
				int instrument=channel.getProgram()+this.instrumentoffset;
				String instrName=MidiInstrument.INSTRUMENT_LIST[instrument-this.instrumentoffset].getName();
				int pan=channel.getBalance();
				if(isPercussionTrack(song, track)) {
					TGBeat beat=getFirstBeat(track);
					if(beat==null) {
						this.writer.println("%%MIDI drumoff");
						this.drum="";
					}
					else {
						this.writer.println("%%MIDI drumon");
						this.drum=getABCDrum(track, beat.getMeasure().getNumber());
						this.writer.println("%%MIDI drum "+this.drum);
					}
				}
				else if(id.indexOf('\t')<0 && this.chordt!=i+1 && this.baset!=i+1 && this.dronet!=i+1) {
					++v;
					this.writer.println("%%MIDI voice "+id.replaceAll("\\s+","_")+" instrument="+instrument+" % "+instrName+" pan="+pan);
				}
				else if(this.dronet==i+1 || id.endsWith("\tdrone")) {
					TGBeat beat=getFirstBeat(track);
					if(beat!=null) {
						this.droneparm=getDroneParm(beat);
						this.droneparm[4]=instrument;
						String drone=instrument+"        "+droneparm[0]+"   "+droneparm[1]+"   "+droneparm[2]+"   "+droneparm[3];
						this.writer.println("%      drone <instrument> <pitch1> <pitch2> <vol1> <vol2>");
						this.writer.println("%%MIDI drone "+drone+" % "+instrName);
						this.droneon=beat.getMeasure().getNumber()<2;
						if(this.droneon)
							this.writer.println("%%MIDI droneon");
						else
							this.writer.println("%%MIDI droneoff");
					}
				}
				else if(this.chordt==i+1 || id.endsWith("\tchords")) {
					gchordBeat[0]=getFirstBeat(track);
					if(gchordBeat[0]!=null) {
						int[] p=getDroneParm(gchordBeat[0]);
						this.writer.println("%%MIDI chordprog "+instrument+" % "+instrName);
						this.writer.println("%%MIDI chordvol "+p[2]);
					}
				}
				else if(this.baset==i+1 || id.endsWith("\tbase")) {
					gchordBeat[1]=getFirstBeat(track);
					if(gchordBeat[1]!=null) {
						int[] p=getDroneParm(gchordBeat[1]);
						this.writer.println("%%MIDI bassprog "+instrument+" % "+instrName);
						this.writer.println("%%MIDI bassvol "+p[2]);
					}
				}
			}
		}
		s=null;
		v=-1;
		boolean gchords=false;
		for(int i=0;i<2;i++) {
			if(gchordBeat[i]!=null) {
				gchords=true;
				if(v<0) v=i;
				else if(gchordBeat[i].getMeasure().getNumber()<gchordBeat[v].getMeasure().getNumber()) v=i;
				if(gchordBeat[i].getMeasure().getNumber()<2)
					s="%%MIDI gchordon";
			}
		}
		if(s==null && gchords) s="%%MIDI gchordoff";
		this.gchord=null;
		if(v>=0) {
			this.gchord=getABCGchord(song, gchordBeat[v].getMeasure().getNumber());
		}
		if(this.gchord!=null) this.writer.println("%%MIDI gchord "+this.gchord);
		if(s!=null) this.writer.println(s);
	}
	
	private String handleId(String name) {
		if(name.indexOf('\t')<0) return name;
		if(this.chordt==ABCSettings.NO_TRACK) name=name.replaceAll("\tchords", " chords");
		if(this.baset==ABCSettings.NO_TRACK) name=name.replaceAll("\tbase", " base");
		if(this.dronet==ABCSettings.NO_TRACK) name=name.replaceAll("\tdrone", " drone");
		return name;
	}

	private String getABCGchord(TGSong song, int m) {
		TGMeasure chordm=song.getTrack(chordt-1).getMeasure(m-1);
		TGMeasure basem=song.getTrack(baset-1).getMeasure(m-1);
		int clen=chordm.countBeats();
		int blen=basem.countBeats();
		char[] gc=new char[clen+blen];
		int[] gn=new int[clen+blen];
		int[] gd=new int[clen+blen];
		int cb=0;
		int bb=0;
		int kgv=1;
		int sx=0;
		while(cb<clen || bb<blen) {
			TGBeat beatc=null;
			TGBeat beatb=null;
			char b=0;
			char c=0;
			String db="";
			String dc="";
			int cticks=0;
			int bticks=0;
			if(cb<chordm.countBeats()) beatc=chordm.getBeat(cb);
			if(bb<basem.countBeats()) beatb=basem.getBeat(bb);
			if(beatb!=null && (beatc==null || beatb.getStart()<=beatc.getStart())) {
				int n=0;
				for(int v=0;v<beatb.countVoices();v++) n+=beatb.getVoice(v).countNotes();
				TGDuration duration = beatb.getVoice(0).getDuration();
				bticks=(int) duration.getTime();
				db=getABCDuration(duration);
				if(n==0) b='z';
				else {
					if(chordt==baset && n>3) b='f';
					else if(chordt!=baset) { 
						if(n==1) b=getABCGchordNote(song,beatb);
						else	 b='f';
						if(b=='G') b='f';
					}
				}
				++bb;
			}
			if(beatc!=null && (beatb==null || beatc.getStart()<=beatb.getStart())) {
				int n=0;
				for(int v=0;v<beatc.countVoices();v++) n+=beatc.getVoice(v).countNotes();
				TGDuration duration = beatc.getVoice(0).getDuration();
				cticks=(int) duration.getTime();
				dc=getABCDuration(duration);
				if(n==0) c='z';
				else {
					if(chordt==baset && n>3)  c='b';
					else if(chordt!=baset) {
						if(n==1) c=getABCGchordNote(song,beatc);
						else	 c='c';
					}
				}
				++cb;
			}
			if(b>0 && c>0) {
				if(b=='z') gc[sx]=c;
				else if(c=='z') gc[sx]=b;
				else if(cticks>bticks) gc[sx]=c;
				else if(bticks>cticks) gc[sx]=b;
				else gc[sx]='b';
			}
			else if(b>0) gc[sx]=b;
			else if(c>0) gc[sx]=c;
			else gc[sx]='z';
			String d="";
			if(b>0 && c>0) {
				if(cticks>bticks) d=dc;
				else d=db;
			}
			else if(b>0) d=db;
			else if(c>0) d=dc;
			if(d.indexOf('/')<0) {
				gd[sx]=1;
				if(d.length()==0) gn[sx]=1;
				else gn[sx]=Integer.parseInt(d, 10);
			}
			else {
				int i=d.indexOf('/');
				int q=2;
				int n=1;
				if(i>0) n=Integer.parseInt(d.substring(0,i), 10);
				if(i<d.length()-1) q=Integer.parseInt(d.substring(i+1), 10);
				kgv=getKgv(kgv,q);
				gn[sx]=n;
				gd[sx]=q;
			}
			++sx;
		}
		if(kgv>1) {
			while(kgv>8) {
				kgv/=2;
			}
			for(int i=0;i<sx;i++) {
				if(((gn[i]*kgv) % gd[i])==0) {
					gn[i]=(gn[i]*kgv)/gd[i];
					gd[i]=1;
				}
				else {
					gn[i]=gn[i]*kgv;
					while((gn[i]&1)==0 && (gd[i]&1)==0) {
						gn[i]/=2;
						gd[i]/=2;
					}
				}
			}
		}
		String s="";
		for(int i=0;i<sx;i++) {
			s+=gc[i];
			if(gn[i]>1) s+=gn[i];
			if(gd[i]>1) {
				s+='/';
				if(gd[i]>2) s+=gd[i];
			}
		}
		if(s.matches("(./)+")) s=s.replaceAll("/","");
		for(int i=1;i<10;i++)
			if(s.matches("(."+i+")+")) s=s.replaceAll(""+i,"");
		if(s.matches("z[z0-9]*")) return "";
		return s;
	}

	private int getKgv(int kgv, int q) {
		return kgv * q / getGgd(kgv,q);
	}

	private int getGgd(int p, int q) {
		int m=p;
		int n=q;
		if(m<n) {
			m=q;
			n=p;
		}
		int r=m%n;
		while(r>0) {
			m=n;
			n=r;
			r=m%n;
		}
		return n;
	}

	private char getABCGchordNote(TGSong song, TGBeat beat) {
		char c='f';
		TGChord chord=findChord(song,beat);
		if(chord!=null) {
			String name=chord.getName().replaceAll("[^ -~]", " ").trim();
			while(name.length()>0 && "CDEFGAB".indexOf(name.charAt(0))<0) name=name.substring(1);
			if(name.length()>0) {
				name=name.split("\\s+")[0];
				ABCChord abcChord=new ABCChord(name); 
				TGNote note=null;
				for(int v=0;v<beat.countVoices();v++) {
					for(int i=0;i<beat.getVoice(v).countNotes();i++)
						note=beat.getVoice(v).getNote(i);
				}
				if(note!=null) {
					int value=beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue();
					c=abcChord.getGchordChar(value+24);
				}
			}
		}
		return c;
	}

	private TGChord findChord(TGSong song, TGBeat beat) {
		TGChord chord=null;
		long start=beat.getStart();
		int m=beat.getMeasure().getNumber();
		for(int x=m;x>0;x--) {
			for(int t=0;t<song.countTracks();t++) {
				TGMeasure measure=song.getTrack(t).getMeasure(x-1);
				for(int b=0;b<measure.countBeats();b++) {
					TGBeat bx=measure.getBeat(b);
					if(x==m && bx.getStart()>start) break;
					if(bx.getChord()!=null) {
						chord=bx.getChord();
					}
				}
				if(chord!=null) return chord;
			}
		}
		return null;
	}

	private String getABCDrum(TGTrack drumTrack, int m) {
		String s="";
		String ins="";
		String vol="";
		TGMeasure drumm=drumTrack.getMeasure(m-1);
		int db=0;
		while(db<drumm.countBeats()) {
			TGBeat beatd=drumm.getBeat(db);
			int n=0;
			for(int v=0;v<beatd.countVoices();v++) n+=beatd.getVoice(v).countNotes();
			if(n==0) s+="z"+getABCDuration(beatd.getVoice(0).getDuration());
			else {
				s+="d"+getABCDuration(beatd.getVoice(0).getDuration());
				for(int v=0;v<beatd.countVoices();v++) {
					for(int i=0;i<beatd.getVoice(v).countNotes();i++) {
						TGNote note=beatd.getVoice(v).getNote(i);
						int val=note.getValue()+drumTrack.getString(note.getString()).getValue();
						ins+=" "+(val-12);
						vol+=" "+note.getVelocity();
					}
				}
			}
			++db;
		}
		if(s.matches("(./)+")) s=s.replaceAll("/","");
		for(int i=1;i<10;i++)
			if(s.matches("(."+i+")+")) s=s.replaceAll(""+i,"");
		if(s.matches("z[z0-9]*")) return "";
		return s+ins+vol;
	}

	private int[] getDroneParm(TGBeat beat) {
		int[] p=new int[5];
		int i=0;
		for(int v=0;v<beat.countVoices();v++) {
			for(int n=0;n<beat.getVoice(v).countNotes();n++) {
				TGNote note = beat.getVoice(v).getNote(n);
				p[i]=note.getValue()+beat.getMeasure().getTrack().getString(note.getString()).getValue();
				p[i+2]=note.getVelocity();
				i++;
				if(i>1) return p;
			}
		}
		return p;
	}

	private TGBeat getFirstBeat(TGTrack track) {
		for(int m=0;m<track.countMeasures();m++) {
			for(int b=0;b<track.getMeasure(m).countBeats();b++) {
				TGBeat beat=track.getMeasure(m).getBeat(b);
				if(!beat.isRestBeat()) {
					for(int v=0;v<beat.countVoices();v++) {
						if(beat.getVoice(v).countNotes()>0) return beat;
					}
				}
			}
		}
		return null;
	}

	private String clefname(int clef) {
		switch(clef) {
		case TGMeasure.CLEF_ALTO: return "alto";
		case TGMeasure.CLEF_BASS: return "bass";
		case TGMeasure.CLEF_TENOR: return "tenor";
		case TGMeasure.CLEF_TREBLE: return "treble";
		}
		return "treble";
	}

	private void addSong(TGSong song) {
		for(int m=this.from-1;m<this.to;m+=this.barsperstaff) {
			this.writer.println("% "+(m+1));
			boolean first=true;
			for(int t=0;t<song.countTracks();t++) {
				if(t==this.baset-1 || t==this.chordt-1 || t==this.dronet-1) continue;
				TGTrack track = song.getTrack(t);
				int tnum=track.getNumber();
				if(this.settings.getTrack() == ABCSettings.ALL_TRACKS || this.settings.getTrack() == tnum) {
					this.vId = track.getName().replaceAll("\\s+", "_");
					if(!isPercussionTrack(song, track)) {
						this.line="[V:"+this.vId+"] ";
						this.wline="w:";
						for(int b=0;b<this.barsperstaff && m+b<this.to; b++) {
							TGMeasure measure = track.getMeasure(m+b);
							TGMeasure prevMeasure = m+b==0? measure: track.getMeasure(m+b-1);
							this.handleBeginMeasure(measure, prevMeasure);
							if(first && this.chordt > 0 && this.baset > 0) handleGChord(song,m+b+1);
							if(first && this.dronet > 0) handleDrone(song,m+b+1);
							if(t==this.diagramt-1 && this.chordt > 0) {
								TGTrack chordTrack=song.getTrack(this.chordt-1);
								TGMeasure chordsMeasure = chordTrack.getMeasure(m+b);
								measure=mergeChords(measure,chordsMeasure,song.getMeasureHeader(m+b));
								measure.setTrack(track); // prevent nullpointer in addBeat later on...
							}
							this.addMeasureComponents(measure,0);
							this.handleEndMeasure(measure);
						}
						this.writer.println(this.line.replaceAll(":\\]\\s*\\|", ":|").replaceAll("\\|\\s*\\[:", "|:"));
						if(this.wline.length()>2) this.writer.println(this.wline);
						first=false;
					}
				}
			}
			this.writer.println("%");
		}
	}
	
	private void handleBeginMeasure(TGMeasure measure, TGMeasure previous) {
		// Open repeat
		if(measure.isRepeatOpen()){
			this.addRepeatOpen(measure.getHeader());
		}
		else if(measure.getHeader().getRepeatAlternative() > 0){
			// Open a repeat alternative only if this measure isn't who opened the repeat.
			this.addRepeatAlternativeOpen(measure.getHeader().getRepeatAlternative());
		}
		if(previous == null || measure.getTempo().getValue() != previous.getTempo().getValue()) {
			this.addTempo(measure.getTempo());
		}
		
		if(previous == null || measure.getClef() != previous.getClef()){
			this.addClef(measure.getClef());
		}
		if(previous == null || measure.getKeySignature() != previous.getKeySignature()){
			this.addKeySignature(measure.getKeySignature());
		}
		
		if(previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature())){
			this.addTimeSignature(measure.getTimeSignature());
		}
		
	}

	private void handleEndMeasure(TGMeasure measure) {
		handleTrioles();
		// Close repeat
		int count=measure.getRepeatClose();
		if(count > 0){
			this.line += " "+(count>1? String.valueOf(count): "") + ":";
		}
		if(!this.line.endsWith(":")) this.line+=" ";
		this.line += "|";
	}

	private void handleDrone(TGSong song, int m) {
		TGTrack droneTrack = song.getTrack(this.dronet-1);
		TGMeasure droneMeasure=droneTrack.getMeasure(m);
		if(droneMeasure.countBeats()>0) {
			int[] dp=getDroneParm(droneMeasure.getBeat(0));
			for(int i=0;i<4;i++) {
				if(dp[i]!=this.droneparm[i]) {
					this.line += " [I:MIDI=drone "+this.droneparm[4]+" "+dp[0]+" "+dp[1]+" "+dp[2]+" "+dp[3]+"]";
					break;
				}
			}
			for(int i=0;i<4;i++) this.droneparm[i]=dp[i];
			if(!this.droneon) {
				this.line += " [I:MIDI=droneon]";
				this.droneon=true;
			}
		}
		else {
			if(this.droneon) {
				this.line += " [I:MIDI=droneoff]";
				this.droneon=false;
			}
		}
	}

	private void handleGChord(TGSong song, int m) {
		String gc=getABCGchord(song, m);
		if(this.gchord==null || !this.gchord.equals(gc)) {
			if(gc.length()==0) {
				if(this.gchord != null) {
					this.line += "[I:MIDI=gchordoff]";
					this.gchord=null;
				}
			}
			else {
				this.line+=" [I:MIDI=gchord "+gc+"]";
				if(this.gchord==null)
					this.line+=" [I:MIDI=gchordon]";
				this.gchord=gc;
			}
		}
	}

	private TGMeasure mergeChords(TGMeasure measure, TGMeasure chordsMeasure, TGMeasureHeader header) {
		TGFactory factory = TuxGuitar.getInstance().getSongManager().getFactory();
		TGMeasure m=measure.clone(factory, header);
		for(int i=0;i<chordsMeasure.countBeats();i++) {
			TGBeat cb=chordsMeasure.getBeat(i).clone(factory);
			if(cb.isChordBeat()) {
				boolean hit=false;
				int x=-1;
				TGBeat prevmb=null;
				for(int j=0;j<m.countBeats();j++) {
					TGBeat mb = m.getBeat(j);
					if(mb.getStart()==cb.getStart()) {
						mb.setChord(cb.getChord());
						if(cb.getText()!=null && mb.getText()==null)
							mb.setText(cb.getText());
						hit=true;
						break;
					}
					else if(mb.getStart()>cb.getStart()) break;
					x=j;
					prevmb=mb;
				}
				if(!hit) {
					cb.removeText();
					for(int v=0;v<cb.countVoices();v++) {
						TGVoice voice = cb.getVoice(v);
						while(voice.countNotes()>0)
							voice.removeNote(voice.getNote(0));
					}
					if(prevmb!=null && prevmb.getStart()+prevmb.getVoice(0).getDuration().getTime()>cb.getStart()) {
						long time=cb.getStart()-prevmb.getStart();
						TGDuration duration1=TGDuration.fromTime(factory, time);
						TGDuration duration2=TGDuration.fromTime(factory, prevmb.getVoice(0).getDuration().getTime()-time);
						for(int v=0;v<cb.countVoices();v++) {
							TGVoice voice = cb.getVoice(v);
							voice.setDuration(duration2);
							if(v<prevmb.countVoices()) {
								TGVoice prevvoice = prevmb.getVoice(v);
								prevvoice.setDuration(duration1);
								for(int n=0;n<prevvoice.countNotes();n++) {
									TGNote note=prevvoice.getNote(n).clone(factory);
									note.setTiedNote(true);
									voice.addNote(note);
								}
							}
						}
					}
					m.addBeat(cb);
					m.moveBeat(x+1, cb);
				}
			}
		}
		return m;
	}

	private void addRepeatOpen(TGMeasureHeader measure){
		if(!this.line.endsWith("|")) this.line += "[";
		this.line += ": ";
	}
	
	private void addRepeatAlternativeOpen(int alternatives) {
		int bit=1;
		String comma="[";
		if(this.line.endsWith("|")) comma="";
		for(int i=0;i<8;i++) {
			if((alternatives&bit)==bit) {
				this.line += comma+(i+1);
				comma=",";
			}
			bit<<=1;
		}
	}
	
	private void addTempo(TGTempo tempo) {
		this.line += "[Q:1/4=" + tempo.getValue()+"]";
	}
	
	private void addTimeSignature(TGTimeSignature ts) {
		this.line += "[M:" + ts.getNumerator() + "/" + ts.getDenominator().getValue()+"]";
	}
	
	private void addKeySignature(int keySignature) {
		if(keySignature >= 0 && keySignature < ABC_KEY_SIGNATURES.length){
			this.line += "[K:" + ABC_KEY_SIGNATURES[keySignature] + "]";
		}
	}
	
	private void addClef(int clef){
		String clefName = "";
		if(clef == TGMeasure.CLEF_TREBLE){
			clefName = "treble";
		}
		else if(clef == TGMeasure.CLEF_BASS){
			clefName = "bass";
		}
		else if(clef == TGMeasure.CLEF_ALTO){
			clefName = "alto";
		}
		else if(clef == TGMeasure.CLEF_TENOR){
			clefName = "tenor";
		}
		if(clefName!=""){
			this.line += "[V:"+vId+" clef="+clefName+"]";
		}
	}
	
	private void addMeasureComponents(TGMeasure measure,int voice){
		this.line += " ";
		this.addComponents(measure,voice);
	}
	
	private void addComponents(TGMeasure measure,int vIndex){
		int key = measure.getKeySignature();
		TGBeat previous = null;
		
		for(int i = 0 ; i < measure.countBeats() ; i ++){
			TGBeat beat = measure.getBeat( i );
			TGVoice voice = beat.getVoice( vIndex );
			if( !voice.isEmpty() ){
				this.addBeat(key, beat, voice);
				previous = beat;
			}
		}
		// It Means that all voice beats are empty 
		if( previous == null ){
			int ticks=(int) (measure.getTimeSignature().getDenominator().getTime() * measure.getTimeSignature().getNumerator());
			int eight=(int) (TGDuration.QUARTER_TIME/2);
			this.line += "x"+String.valueOf(ticks/eight);
		}
		
	}
	
	private void addBeat(int key,TGBeat beat, TGVoice voice){		
		// Add Chord, if was not previously added in another voice
		if( beat.isChordBeat() && voice.getIndex()==0) {
			String name=beat.getChord().getName().replaceAll("[^ -~]", " ").trim();
			while(name.length()>0 && "CDEFGAB".indexOf(name.charAt(0))<0) name=name.substring(1);
			if(name.length()>0) {
				name=name.split("\\s+")[0];
				this.line += "\""+name+"\"";
			}
		}
		
		if(voice.isRestVoice()){
			boolean skip = false;
			for( int v = 0 ; v < beat.countVoices() ; v ++ ){
				if( !skip && v != voice.getIndex() ){
					TGVoice current = beat.getVoice( v );
					if(!current.isEmpty() && current.getDuration().isEqual( voice.getDuration() )){
						skip = (!current.isRestVoice() || current.getIndex() < voice.getIndex());
					}
				}
			}
			this.addTrioles(voice.getDuration());
			this.line +=  ( skip ? "x" : "z" ) ;
			this.addDuration( voice.getDuration() );
		}
		else{
			this.addEffectsBeforeBeat(voice);
			
			
			int size = voice.countNotes();
			this.addTrioles(voice.getDuration());
			this.addEffectsOnBeat( voice );
			this.addEffectsOnDuration( voice );
			if(size > 1) this.line += "[";
			String tie="";
			for(int i = 0 ; i < size ; i ++){
				TGNote note = voice.getNote(i);
				
				this.addEffectsBeforeNote(note);
				
				this.addKey(key, (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue()) );
				if(this.isAnyTiedTo(note)){
					tie = "-";
				}
				
				this.addEffectsOnNote(note.getEffect());
				
			}
			
			if(size > 1) this.line += "]";
			
			this.addDuration( voice.getDuration() );
			this.line += tie;
		}
		
		// Add Text, if was not previously added in another voice
		boolean skip = false;
		for( int v = 0 ; v < voice.getIndex() ; v ++ ){
			skip = (skip || !beat.getVoice( v ).isEmpty() );
		}
		if( !skip ) {
			if( beat.isTextBeat() ){
				this.wline+=" " + beat.getText().getValue();
			}

			// Check if it's a lyric beat to skip
			if( beat.getMeasure().getTrack().getLyrics().getFrom() > beat.getMeasure().getNumber()){
				this.wline+=" *";
			}
		}
		
		this.line += " ";
	}
	
	private void addTrioles(TGDuration duration) {
		TGDivisionType dt=duration.getDivision();
		int times=dt.getTimes();
		int enters=dt.getEnters();
		if(enters==times) {
			handleTrioles();
		}
		else {
			if(this.enterstogo==0) {
				this.enterstogo=enters;
				this.triol = "("+enters+":"+times+":"+enters; 
				this.line += this.triol+" ";
			}
			this.enters=enters;
			this.times=times;
			--this.enterstogo;
			if(this.enterstogo==0) {
				int p=this.line.lastIndexOf(this.triol);
				if(this.times==2)
					this.line=this.line.substring(0, p)+"("+this.enters+" "+this.line.substring(p+this.triol.length());
				this.enters=1;
				this.times=1;
			}
		}
	}

	private void handleTrioles() {
		if(this.enters == this.times) return;
		int p=this.line.lastIndexOf(this.triol);
		if(p>=0) {
			if(this.enterstogo>0) {
				this.line=this.line.substring(0, p)+
				"("+this.enters+":"+this.times+":"+(this.enters-this.enterstogo)+
				this.line.substring(p+this.triol.length());
			}
			else {
				if(this.times==2)
					this.line=this.line.substring(0, p)+"("+this.enters+" "+this.line.substring(p+this.triol.length());
			}
		}
		this.enterstogo=0;
		this.enters=1;
		this.times=1;
	}

	private void addKey(int keySignature,int value){
		String pitch=getABCKey(keySignature, value) ;
		int i=this.line.lastIndexOf('|')+1;
		this.line += scanMeasure(this.line.substring(i),pitch);
	}
	
	private String scanMeasure(String m, String pitch) {
		char c=pitch.charAt(0);
		switch(c) {
		case '=':
		case '_':
		case '^':
			c=pitch.charAt(1);
		}
		if(m.lastIndexOf(c)<0) return pitch;
		String sharp="^"+c;
		String flat="_"+c;
		String normal="="+c;
		int isharp=m.lastIndexOf(sharp);
		int iflat=m.lastIndexOf(flat);
		int inormal=m.lastIndexOf(normal);
		if(inormal<0 && isharp<0 && iflat<0) return pitch;
		switch(pitch.charAt(0)) {
		case '=':
			if(inormal>isharp && inormal>iflat) pitch=pitch.substring(1);
			break;
		case '^':
			if(isharp>inormal && isharp>iflat) pitch=pitch.substring(1);
			break;
		case '_':
			if(iflat>inormal && iflat>isharp) pitch=pitch.substring(1);
			break;
		default:
			if(inormal<iflat || inormal<isharp) pitch="="+pitch;
			break;
		}
		return pitch;
	}

	private void addDuration(TGDuration duration){
		this.line += getABCDuration(duration);
	}
	
	private void addEffectsBeforeNote(TGNote note){
		TGNoteEffect effect = note.getEffect();
		if( effect.isDeadNote() ){
			this.line += "\"<x\"";
		}
		if( effect.isPalmMute() ){
			this.line += "\"_palmMute\"";
		}
		if( effect.isGhostNote() ){
			this.line += "\"<(\"";
			this.line += "\">)\"";
		}
		if( effect.isBend() ){
			this.line += "+upbow+";
		}
	}
	
	private void addEffectsOnNote(TGNoteEffect effect){
		if( effect.isHarmonic() ){
			this.line += "\"_harmonic\"";
		}
	}
	
	private void addEffectsOnDuration(TGVoice voice){
		int tremoloPicking = -1;
		for( int i = 0 ; i < voice.countNotes() ; i ++ ){
			TGNote note = voice.getNote(i);
			if( tremoloPicking == -1 && note.getEffect().isTremoloPicking() ){
				tremoloPicking = note.getEffect().getTremoloPicking().getDuration().getValue();
			}
		}
		if( tremoloPicking != -1 ){
			this.line += "+trill+";
		}
	}
	
	private void addEffectsOnBeat(TGVoice voice){
		boolean trill = false;
		boolean vibrato = false;
		boolean staccato = false;
		boolean accentuatedNote = false;
		boolean heavyAccentuatedNote = false;
		boolean arpeggio = ( voice.getBeat().getStroke().getDirection() != TGStroke.STROKE_NONE );
		for( int i = 0 ; i < voice.countNotes() ; i ++ ){
			TGNoteEffect effect = voice.getNote(i).getEffect();
			
			trill = (trill || effect.isTrill() );
			vibrato = (vibrato || effect.isVibrato() );
			staccato = (staccato || effect.isStaccato() );
			accentuatedNote = (accentuatedNote || effect.isAccentuatedNote() );
			heavyAccentuatedNote = (heavyAccentuatedNote || effect.isHeavyAccentuatedNote() );
		}
		if( trill ){
			this.line += "+trill+";
		}
		if( vibrato ){
			this.line += "+prall+";
		}
		if( staccato ){
			this.line += ".";
		}
		if( accentuatedNote ){
			this.line += "+>+";
		}
		if( heavyAccentuatedNote ){
			this.line += "^^";
		}
		if( arpeggio ){
			this.line += "+arpeggio+";
		}
	}
	
	private void addEffectsBeforeBeat(TGVoice voice){
		List<TGNote> graceNotes = new ArrayList<TGNote>();
		for( int i = 0 ; i < voice.countNotes() ; i ++ ){
			TGNote note = voice.getNote(i);
			if( note.getEffect().isGrace() ){
				graceNotes.add( note );
			}
		}
		if( !graceNotes.isEmpty() ){
			this.line += "{";
			
			int duration = 0;
			for( int i = 0 ; i < graceNotes.size() ; i ++ ){
				TGNote note = (TGNote)graceNotes.get( i );
				TGMeasure measure = voice.getBeat().getMeasure();
				TGString string = measure.getTrack().getString(note.getString());
				TGEffectGrace grace = note.getEffect().getGrace();
				
				if( duration < TGDuration.SIXTY_FOURTH && grace.getDuration() == 1 ){
					duration = TGDuration.SIXTY_FOURTH;
				}else if( duration < TGDuration.THIRTY_SECOND && grace.getDuration() == 2 ){
					duration = TGDuration.THIRTY_SECOND;
				}else if( duration < TGDuration.SIXTEENTH && grace.getDuration() == 3 ){
					duration = TGDuration.SIXTEENTH;
				}
				this.addKey(measure.getKeySignature(), (string.getValue() + grace.getFret()) );
			}
			this.line += "}";
		}
	}
	
	private boolean isAnyTiedTo(TGNote note){
		TGMeasure measure = note.getVoice().getBeat().getMeasure();
		TGBeat beat = this.manager.getMeasureManager().getNextBeat( measure.getBeats(), note.getVoice().getBeat());
		while( measure != null){
			while( beat != null ){
				TGVoice voice = beat.getVoice(0);
				
				// If is a rest beat, all voice sounds must be stopped.
				if(voice.isRestVoice()){
					return false;
				}
				// Check if is there any note at same string.
				Iterator<TGNote> it = voice.getNotes().iterator();
				while( it.hasNext() ){
					TGNote current = (TGNote) it.next();
					if(current.getString() == note.getString()){
						return current.isTiedNote();
					}
				}
				beat = this.manager.getMeasureManager().getNextBeat( measure.getBeats(), beat);
			}
			measure = this.manager.getTrackManager().getNextMeasure(measure);
			if( measure != null ){
				beat = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
			}
		}
		return false;
	}
	
	private String getABCKey(int keySignature , int value){
		final String[][] ABC_SCALE = {
				{ "B", "c", "=d", "d", "=e", "e", "f", "=g", "g", "=a", "a", "=b" } ,	// 7 sharps
				{ "=c", "c", "=d", "d", "=e", "e", "f", "=g", "g", "=a", "a", "b" } ,	// 6 sharps
				{ "=c", "c", "=d", "d", "e", "=f", "f", "=g", "g", "=a", "a", "b" } ,	// 5 sharps
				{ "=c", "c", "=d", "d", "e", "=f", "f", "=g", "g", "a", "^a", "b" } ,	// 4 sharps
				{ "=c", "c", "d", "^d", "e", "=f", "f", "=g", "g", "a", "^a", "b" } ,	// 3 sharps
				{ "=c", "c", "d", "^d", "e", "=f", "f", "g", "^g", "a", "^a", "b" } ,	// 2 sharps
				{ "c", "^c", "d", "^d", "e", "=f", "f", "g", "^g", "a", "^a", "b" } ,	// 1 sharp
				{ "c", "^c", "d", "^d", "e", "f", "^f", "g", "^g", "a", "^a", "b" } ,	// 0 sharps
				{ "c", "_d", "d", "_e", "e", "f", "_g", "g", "_a", "a", "b", "=b" } ,	// 1 flat
				{ "c", "_d", "d", "e", "=e", "f", "_g", "g", "_a", "a", "b", "=b" } ,	// 2 flats
				{ "c", "_d", "d", "e", "=e", "f", "_g", "g", "a", "=a", "b", "=b" } ,	// 3 flats
				{ "c", "d", "=d", "e", "=e", "f", "_g", "g", "a", "=a", "b", "=b" } ,	// 4 flats
				{ "c", "d", "=d", "e", "=e", "f", "g", "=g", "a", "=a", "b", "=b" } ,	// 5 flats
				{ "=c", "d", "=d", "e", "=e", "f", "g", "=g", "a", "=a", "b", "c" } ,	// 6 flats
				{ "=c", "d", "=d", "e", "f", "=f", "g", "=g", "a", "=a", "b", "c" } ,	// 7 flats
		};
		int octave=value / 12;
		int note=value % 12;
		String[] ABC_NOTES = ABC_SCALE[keySignature>7?keySignature:7-keySignature]; // see TGMeasureimpl.java
		String key = ABC_NOTES[ note ];
		if(keySignature==7 && note==0 && octave==5) key="B";
		else if(keySignature>12 && note==11 && octave==4) key="c";
		else {
			if(octave < 5) key=key.toUpperCase();
			else key=key.toLowerCase();
		}
		for(int i = 5; i < octave; i ++){
			key += "'";
		}
		for(int i = octave; i < 4; i ++){
			key += ",";
		}
		return key;
	}
	
	private String getABCDuration(TGDuration value) {
		int ticks=getTime(value);
		int eight=(int) (TGDuration.QUARTER_TIME/2);
		int d=1;
		while(((ticks * d) % eight)>0) d = d * 2;
		String duration = Integer.toString((ticks*d)/eight);
		if(duration.equals("1")) duration="";
		if(d==1) return duration;
		if(d==2) return duration+"/";
		duration+="/"+Integer.toString(d);
		return duration;
	}
	
	private int getTime(TGDuration value) {
		long time = (long)( TGDuration.QUARTER_TIME * ( 4.0f / value.getValue()) ) ;
		if(value.isDotted()){
			time += time / 2;
		}else if(value.isDoubleDotted()){
			time += ((time / 4) * 3);
		}
		return (int)time;
	}
	
	protected TGChannel getChannel(TGSong song, TGTrack track ){
		TGChannel tgChannel = this.manager.getChannel(song, track.getChannelId() );
		if( tgChannel != null ){
			return tgChannel;
		}
		if( this.channelAux == null ){
			this.channelAux = this.manager.createChannel();
		}
		return this.channelAux;
	}
	
	private boolean isPercussionTrack(TGSong song, TGTrack track){
		return this.manager.isPercussionChannel(song, track.getChannelId());
	
	}
}
