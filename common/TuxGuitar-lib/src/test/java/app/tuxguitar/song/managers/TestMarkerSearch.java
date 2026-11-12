package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGSong;

/**
 * Marker navigation, listing and removal: a song of six measures, marked at the given measure numbers.
 */
public class TestMarkerSearch {

	private final TGSongManager manager = new TGSongManager();

	private TGSong songMarkedAt(int... measures) {
		TGSong song = manager.newSong();
		while (song.countMeasureHeaders() < 6) {
			manager.addNewMeasureBeforeEnd(song);
		}
		for (int measure : measures) {
			manager.updateMarker(song, measure, "marker " + measure, manager.getFactory().newColor());
		}
		return song;
	}

	// the measure number of a found marker, or null when none was found
	private static Integer measureOf(TGMarker marker) {
		return (marker != null ? marker.getMeasure() : null);
	}

	@Test
	public void firstMarkerIsTheLowestMarkedMeasure() {
		assertEquals(Integer.valueOf(2), measureOf(manager.getFirstMarker(songMarkedAt(4, 2, 5))));
		assertNull(manager.getFirstMarker(songMarkedAt()));
	}

	@Test
	public void lastMarkerIsTheHighestMarkedMeasure() {
		assertEquals(Integer.valueOf(5), measureOf(manager.getLastMarker(songMarkedAt(2, 5, 4))));
		assertNull(manager.getLastMarker(songMarkedAt()));
	}

	@Test
	public void previousMarkerIsTheNearestMarkedMeasureBeforeTheMeasure() {
		TGSong song = songMarkedAt(2, 4, 5);
		assertEquals(Integer.valueOf(4), measureOf(manager.getPreviousMarker(song, 5)));
		assertEquals(Integer.valueOf(2), measureOf(manager.getPreviousMarker(song, 4)));
		assertNull(manager.getPreviousMarker(song, 2));
		assertNull(manager.getPreviousMarker(songMarkedAt(), 6));
	}

	// the measure numbers of the markers listed
	private String measuresOf(TGSong song) {
		StringBuilder measures = new StringBuilder();
		for (TGMarker marker : manager.getMarkers(song)) {
			measures.append(measures.length() == 0 ? "" : " ").append(marker.getMeasure());
		}
		return measures.toString();
	}

	@Test
	public void markersAreListedInMeasureOrderAndFoundByTheirMeasure() {
		TGSong song = songMarkedAt(5, 2, 4);
		assertEquals("2 4 5", measuresOf(song));
		assertEquals(Integer.valueOf(4), measureOf(manager.getMarker(song, 4)));
		assertNull(manager.getMarker(song, 3));
		assertNull(manager.getMarker(song, 7));
	}

	@Test
	public void removingAMarkerLeavesTheOthers() {
		TGSong song = songMarkedAt(2, 4, 5);
		manager.removeMarker(song, manager.getMarker(song, 4));
		manager.removeMarker(song, (TGMarker) null);
		manager.removeMarker(song, 3);
		manager.removeMarker(song, 7);
		assertEquals("2 5", measuresOf(song));

		manager.removeMarker(song, 5);
		assertEquals("2", measuresOf(song));
	}

	@Test
	public void nextMarkerIsTheNearestMarkedMeasureAfterTheMeasure() {
		TGSong song = songMarkedAt(2, 4, 5);
		assertEquals(Integer.valueOf(4), measureOf(manager.getNextMarker(song, 2)));
		assertEquals(Integer.valueOf(4), measureOf(manager.getNextMarker(song, 3)));
		assertNull(manager.getNextMarker(song, 5));
		assertNull(manager.getNextMarker(songMarkedAt(), 1));
	}
}
