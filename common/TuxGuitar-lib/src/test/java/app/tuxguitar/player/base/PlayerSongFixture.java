package app.tuxguitar.player.base;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * The player of a new context whose song has 4 measures; beats, loops and positions are given by measure number.
 */
class PlayerSongFixture {

	final TGContext context = new TGContext();
	final MidiPlayer player = MidiPlayer.getInstance(context);
	final TGSongManager songManager = TGDocumentManager.getInstance(context).getSongManager();
	final TGSong song = TGDocumentManager.getInstance(context).getSong();

	PlayerSongFixture() {
		for (int i = 1; i < 4; i++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
	}

	// turns the loop mode on, from one header to another
	void loopMode(int from, int to) {
		player.getMode().setLoop(true);
		player.getMode().setLoopSHeader(from);
		player.getMode().setLoopEHeader(to);
	}

	// a new beat at the start of the measure
	TGBeat beat(int measureNumber) {
		TGMeasure measure = song.getTrack(0).getMeasure(measureNumber - 1);
		TGBeat beat = songManager.getFactory().newBeat();
		beat.setStart(measure.getStart());
		songManager.getMeasureManager().addBeat(measure, beat);
		return beat;
	}

	long start(int headerNumber) {
		return song.getMeasureHeader(headerNumber - 1).getStart();
	}
}
