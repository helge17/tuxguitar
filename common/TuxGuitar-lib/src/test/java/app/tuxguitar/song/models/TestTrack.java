package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.tg.TestFileFormat20;

public class TestTrack {
	@Test
	public void testMinMaxPitch() throws IOException {
		TGSong song = new TestFileFormat20().readSong("track_pitch_20.tg",true).getSong();
		TGTrack track = song.getTrack(0);
		assertEquals(40, track.getMinPlayablePitch());
		assertEquals(29+64, track.getMaxPlayablePitch());
		track = song.getTrack(1);
		assertEquals(28, track.getMinPlayablePitch());
		assertEquals(25+43, track.getMaxPlayablePitch());
		track = song.getTrack(2);
		assertEquals(1, track.getMinPlayablePitch());
		assertEquals(100, track.getMaxPlayablePitch());
		
	}
}
