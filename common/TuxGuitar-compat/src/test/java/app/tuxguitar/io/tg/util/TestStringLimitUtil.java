package app.tuxguitar.io.tg.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;

/**
 * The legacy .tg formats store 4 to 7 strings. A track within that range is written as is;
 * any other track gets the default strings of the nearest allowed count, keeping its own
 * tuning only for the strings numbered 4 to 7.
 */
public class TestStringLimitUtil {

	private final TGFactory factory = new TGFactory();

	/** The first track of a new song, retuned to count strings numbered 1..count with values 101..(100 + count). */
	private TGTrack track(int count) {
		TGSong song = new TGSongManager(this.factory).newSong();
		TGTrack track = song.getTrack(0);
		List<TGString> strings = new ArrayList<TGString>();
		for (int number = 1; number <= count; number++) {
			strings.add(TGSongManager.newString(this.factory, number, 100 + number));
		}
		track.setStrings(strings);
		return track;
	}

	private static List<String> describe(List<TGString> strings) {
		List<String> lines = new ArrayList<String>();
		for (TGString string : strings) {
			lines.add(string.getNumber() + ":" + string.getValue());
		}
		return lines;
	}

	@Test
	public void tracksWithFourToSevenStringsKeepTheirOwnStrings() {
		for (int count = TGStringLimitUtil.MINIMUM; count <= TGStringLimitUtil.MAXIMUM; count++) {
			TGTrack track = this.track(count);
			assertSame(track.getStrings(), TGStringLimitUtil.createWritableStrings(track), count + " strings");
		}
	}

	@Test
	public void tooManyStringsBecomeSevenKeepingTheTuningOfStringsFourToSeven() {
		assertEquals(Arrays.asList("1:64", "2:59", "3:55", "4:104", "5:105", "6:106", "7:107"),
				describe(TGStringLimitUtil.createWritableStrings(this.track(9))));
	}

	@Test
	public void tooFewStringsBecomeTheDefaultFourStrings() {
		assertEquals(Arrays.asList("1:43", "2:38", "3:33", "4:28"),
				describe(TGStringLimitUtil.createWritableStrings(this.track(2))));
	}

	@Test
	public void percussionTracksGetUntunedDefaultStrings() {
		TGTrack track = this.track(8);
		TGChannel channel = track.getSong().getChannel(0);
		channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		track.setChannelId(channel.getChannelId());
		assertEquals(Arrays.asList("1:0", "2:0", "3:0", "4:104", "5:105", "6:106", "7:107"),
				describe(TGStringLimitUtil.createWritableStrings(track)));
	}
}
