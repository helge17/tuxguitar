package org.herac.tuxguitar.io.ptb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.io.ptb.base.PTBar;
import org.herac.tuxguitar.io.ptb.base.PTComponent;
import org.herac.tuxguitar.io.ptb.base.PTDirection;
import org.herac.tuxguitar.io.ptb.base.PTPosition;
import org.herac.tuxguitar.io.ptb.base.PTSection;
import org.herac.tuxguitar.io.ptb.base.PTSong;
import org.herac.tuxguitar.io.ptb.base.PTSongInfo;
import org.herac.tuxguitar.io.ptb.base.PTSymbol;
import org.herac.tuxguitar.io.ptb.base.PTTrack;
import org.herac.tuxguitar.io.ptb.base.PTTrackInfo;

public class PTSongSynchronizerUtil {
	
	public static void synchronizeTracks(PTSong src, PTSong dst){
		applyInfo( src.getInfo() , dst.getInfo() );
		synchronizeTrack( src.getTrack1(), dst.getTrack1() );
		synchronizeTrack( src.getTrack2(), dst.getTrack2() );
	}
	
	private static void synchronizeTrack(PTTrack src, PTTrack dst){
		applyRepeats(src, dst);
		applyInfos(src, dst);
	}
	
	private static void applyInfo(PTSongInfo src, PTSongInfo dst){
		src.copy( dst );
	}
	
	private static void applyInfos(PTTrack src, PTTrack dst){
		Iterator<PTTrackInfo> it = src.getInfos().iterator();
		while( it.hasNext() ){
			PTTrackInfo srcInfo = (PTTrackInfo)it.next();
			dst.getInfos().add( srcInfo.getClone() );
		}
	}
	
	private static void applyRepeats(PTTrack src, PTTrack dst){
		applyRepeats(src, dst, new PTIndex(0,0,0), new PTSongSynchronizerData(), new ArrayList<PTDirection>() );
	}
	
	private static void applyRepeats(PTTrack src, PTTrack dst, PTIndex index, PTSongSynchronizerData rd, List<PTDirection> useds){
		
		for( int s = index.s; s < src.getSections().size(); s ++){
			PTSection srcSection = (PTSection) src.getSections().get(s);
			srcSection.sort();
			
			PTSection dstSection = new PTSection( srcSection.getNumber() );
			dstSection.setStaffs( srcSection.getStaffs() );
			dst.getSections().add( dstSection );
			
			for( int p = (s == index.s ? index.p : 0); p < srcSection.getPositions().size(); p ++){
				PTPosition srcPosition = (PTPosition)srcSection.getPositions().get(p);
				srcPosition.sort();
				
				PTPosition dstPosition = new PTPosition(srcPosition.getPosition() );
				dstSection.getPositions().add( dstPosition );
				
				for(int c = (s == index.s && p == index.p ? index.c : 0); c < srcPosition.getComponents().size(); c ++){
					PTComponent component = (PTComponent)srcPosition.getComponents().get(c);
					
					if(!rd.skip){
						dstPosition.addComponent( component.getClone() );
					}
					
					// ------------------------------ PTBar ------------------------------//
					if(component instanceof PTBar){
						PTBar bar = (PTBar) component;
						if(bar.getRepeatClose() > 0 && rd.repeatStart != null ){
							rd.repeatNumber ++;
							rd.repeatInProgress = true;
							rd.repeatAlternative = false;
							rd.skip = false;
							
							if(rd.repeatNumber < bar.getRepeatClose()){
								applyRepeats(src, dst, rd.repeatStart , rd, useds);
								return;
							}
							
							rd.repeatStart = null;
							rd.repeatNumber = 0;
						}
						if( bar.isRepeatStart() ){
							rd.repeatStart = new PTIndex(s, p, c );
							if(! rd.repeatInProgress ){
								rd.repeatNumber = 0;
							}
							rd.repeatInProgress = false;
						}
					}
					
					// ------------------------------ PTSymbol ------------------------------//
					else if(component instanceof PTSymbol){
						PTSymbol symbol = (PTSymbol)component;
						
						rd.skip = false;
						
						if( !rd.repeatAlternative && ((symbol.getEndNumber() & 1 ) != 0 ) ){
							boolean validEnding = ((symbol.getEndNumber() & (1 << (rd.repeatNumber))) != 0 );
							if(rd.repeatNumber > 0 && !validEnding ){
								rd.skip = true;
							}
							rd.repeatAlternative =  true;
						}
					}
					
					// ------------------------------ PTDirection ------------------------------//
					else if(component instanceof PTDirection){
						
						PTDirection direction = (PTDirection)component;
						
						boolean validRepeat = (direction.getRepeat() == 0 || (rd.repeatStart != null && (rd.repeatNumber + 1) == direction.getRepeat()));
						boolean validDirection = ( direction.getActiveSymbol() == rd.findActiveSymbol );
						
						if ( validDirection && validRepeat ){
							rd.findActiveSymbol = 0;
							
							if( direction.getDirection() == PTDirection.DIRECTION_FINE ){
								// Used to mark when to stop playing (usually the end of the score)
								if ( canUseDirection(direction, useds) ){
									return;
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DA_CAPO ){
								// Go back to the beginning of the score and play from there
								if ( canUseDirection(direction, useds) ){
									applyRepeats(src, dst, new PTIndex( 0, 0, 0 ) , rd , useds );
									return;
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO ){
								// Go back to the Segno and play from there
								if ( canUseDirection(direction, useds) ){
									PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO, s, p );
									if(segno != null ){
										applyRepeats(src, dst, segno ,rd, useds);
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_SEGNO ){
								// Go back to the Segno Segno and play from there
								if ( canUseDirection(direction, useds) ){
									PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO_SEGNO, s, p );
									if(segno != null ){
										applyRepeats(src, dst, segno , rd, useds );
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_TO_CODA ){
								// Go to the Coda sign and play from there. Used in conjunction with D.C./D.S. al Coda signs.
								if ( canUseDirection(direction, useds) ){
									PTIndex coda = findUnusedDirection(src, useds, PTDirection.DIRECTION_CODA, -1, -1 );
									if( coda != null ){
										applyRepeats(src, dst, coda , rd , useds );
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_TO_DOUBLE_CODA ){
								// Go to the Double Coda and play from there. Used in conjunction with D.C./D.S. al Double Coda signs.
								if ( canUseDirection(direction, useds) ){
									PTIndex coda = findUnusedDirection(src, useds, PTDirection.DIRECTION_DOUBLE_CODA, -1, -1 );
									if( coda != null ){
										applyRepeats(src, dst, coda , rd , useds );
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DA_CAPO_AL_CODA ){
								// Go back to the beginning of the score and play from there until the To Coda sign is reached,
								// then jump to the Coda sign.
								if( canUseDirection(direction, useds)  ){
									rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DC;
									applyRepeats(src, dst, new PTIndex(0, 0, 0) , rd ,useds);
									return;
								}
							
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DA_CAPO_AL_DOUBLE_CODA ){
								// Go back to the beginning of the score and play from there until the To Double Coda sign is reached,
								// then jump to the Double Coda sign.
								if( canUseDirection(direction, useds)  ){
									rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DC;
									applyRepeats(src, dst, new PTIndex(0, 0, 0) , rd , useds);
									return;
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_AL_CODA ){
								// Go back to the Segno sign and play from there until the To Coda sign is reached,
								// then jump to the Coda sign.
								if( canUseDirection(direction, useds)  ){
									PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO, s, p );
									if( segno != null ){
										rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DS;
										applyRepeats(src, dst, segno, rd , useds);
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_AL_DOUBLE_CODA ){
								// Go back to the Segno sign and play from there until the To Double Coda sign is reached,
								// then jump to the Double Coda sign.
								if( canUseDirection(direction, useds)  ){
									PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO, s, p );
									if( segno != null ){
										rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DS;
										applyRepeats(src, dst, segno, rd , useds);
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_SEGNO_AL_CODA ){
								// Go back to the Segno Segno sign and play from there until the To Coda sign is reached,
								// then jump to the Coda sign.
								if( canUseDirection(direction, useds)  ){
									PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO_SEGNO, s, p );
									if( segno != null ){
										rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DSS;
										applyRepeats(src, dst, segno, rd , useds);
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_SEGNO_AL_DOUBLE_CODA ){
								// Go back to the Segno Segno sign and play from there until the To Double Coda sign is reached,
								// then jump to the Double Coda sign.
								if( canUseDirection(direction, useds)  ){
									PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO_SEGNO, s, p );
									if( segno != null ){
										rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DSS;
										applyRepeats(src, dst, segno, rd , useds);
										return;
									}
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DA_CAPO_AL_FINE ){
								// Go back to the beginning and play until the Fine sign is reached.
								if( canUseDirection(direction, useds)  ){
									rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DC;
									applyRepeats(src, dst, new PTIndex(0, 0, 0) , rd , useds);
									return;
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_AL_FINE ){
								// Go back to the Segno sign and play until the Fine sign is reached.
								PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO, s, p );
								if( segno != null ){
									rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DS;
									applyRepeats(src, dst, segno, rd , useds);
									return;
								}
							}
							
							else if( direction.getDirection() == PTDirection.DIRECTION_DAL_SEGNO_SEGNO_AL_FINE ){
								// Go back to the Segno Segno sign and play until the Fine sign is reached.
								PTIndex segno = findUnusedDirection(src, useds, PTDirection.DIRECTION_SEGNO_SEGNO, s, p );
								if( segno != null ){
									rd.findActiveSymbol = PTDirection.ACTIVE_SYMBOL_DSS;
									applyRepeats(src, dst, segno, rd , useds);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean canUseDirection(PTDirection direction, List<PTDirection> useds){
		boolean inUse = false;
		for( int i = 0 ; i < useds.size() && !inUse; i ++ ){
			PTDirection used = (PTDirection)useds.get( i );
			if( used.equals( direction )){
				return false;
			}
		}
		useds.add( direction );
		return true;
	}
	
	private static PTIndex findUnusedDirection(PTTrack src, List<PTDirection> useds, int value, int sEndIndex, int pEndIndex){
		return findUnusedDirection(src, useds, value, sEndIndex, pEndIndex, 0 );
	}
	
	private static PTIndex findUnusedDirection(PTTrack src, List<PTDirection> useds, int value, int sEndIndex, int pEndIndex, int activeSymbol){
		for( int s = 0; s < ( sEndIndex >= 0 ? sEndIndex+1 : src.getSections().size() ); s ++){
			PTSection section = (PTSection) src.getSections().get( s );
			
			for( int p = 0; p < (s == sEndIndex ? pEndIndex+1 : section.getPositions().size() ); p ++){
				PTPosition position = (PTPosition)section.getPositions().get(p);
				
				for( int c = 0; c < position.getComponents().size() ; c ++){
					PTComponent component = (PTComponent)position.getComponents().get( c );
					if(component instanceof PTDirection){
						PTDirection direction = (PTDirection)component;
						if( direction.getDirection() == value && ( activeSymbol == 0 || direction.getActiveSymbol() == activeSymbol)){
							if( canUseDirection(direction, useds)){
								return new PTIndex(s, p , 0);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private static class PTIndex{
		/** Index Of Section **/
		protected int s;
		/** Index Of Position **/
		protected int p;
		/** Index Of Component **/
		protected int c;
		
		protected PTIndex(int s, int p,int c) {
			this.s = s;
			this.p = p;
			this.c = c;
		}
	}
	
	private static class PTSongSynchronizerData{
		/** Current Repeat Start Index **/
		protected PTIndex repeatStart;
		/** Define If Repeat Is In Progress  **/
		protected boolean repeatInProgress;
		/** Current Repetition Number **/
		protected int repeatNumber;
		/** Define if there is an Alternative Ending Running **/
		protected boolean repeatAlternative;
		/** Define If Next Components Should Be Skipped  **/
		protected boolean skip;
		/** Active Symbol To Search Directions ( D.C, D.S, D.SS ) **/
		protected int findActiveSymbol;
		
		protected PTSongSynchronizerData(){
			this.repeatStart = null;
			this.repeatNumber = 0;
			this.repeatAlternative = false;
			this.skip = false;
		}
	}
}