package app.tuxguitar.io.tef3.base;

public class TEPosition {
	private int measure;

	private int positionInMeasure;

	private int string;

	private int trackIndex;

	public TEPosition(int measure, int positionInMeasure, int string, int trackIndex) {
		this.measure = measure;
		this.positionInMeasure = positionInMeasure; // resolution = 1 whole / 256
		this.string = string;
		this.trackIndex = trackIndex;
	}

	public static TEPosition createPositionFromLocation(TESong song, int location) {
		// Constants
		int valuePerString = 8;
		int totalMeasures = song.getMeasures().size();

		// Runtime variables
		int measureOfComponent = 0;
		int positionOfComponentInMeasure = 0;
		int stringOfComponentInMeasure = 0;

		for (int i = 0; i < totalMeasures; i++) {
			TEMeasure currentMeasure = song.getMeasures().get(i);
			TETimeSignature measureTimeSignature = currentMeasure.getTimeSignature();
			int locationOfMeasureEnd = 64 * song.getTotalStringCount() * valuePerString
					* measureTimeSignature.getNumerator() / measureTimeSignature.getDenominator();

			// A component exactly at the measure boundary belongs to the
			// following measure at position zero, not to the previous measure
			// at its (out-of-range) end position.
			if (location < locationOfMeasureEnd) {
				positionOfComponentInMeasure = location / song.getTotalStringCount() / valuePerString;
				stringOfComponentInMeasure = (location / valuePerString) % song.getTotalStringCount();
				break;
			}
			location -= locationOfMeasureEnd;
			measureOfComponent += 1;
		}

		int trackIndex = 0;

		for (int i = 0; i < song.getTracks().size(); i++) {
			TETrack track = song.getTracks().get(i);

			if ((stringOfComponentInMeasure - track.getStringCount()) < 0) {
				trackIndex = i;
				break;
			}

			stringOfComponentInMeasure -= track.getStringCount();
		}

		return new TEPosition(measureOfComponent, positionOfComponentInMeasure, stringOfComponentInMeasure, trackIndex);
	}

	public int getMeasure() {
		return this.measure;
	}

	public int getPositionInMeasure() {
		return this.positionInMeasure;
	}

	public int getString() {
		return this.string;
	}

	public int getTrackIndex() {
		return this.trackIndex;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		String measureStr = "Measure: " + Integer.toString(this.measure) + "\n";
		String positionStr = "Pos in Measure: " + Integer.toString(this.positionInMeasure) + "\n";
		String stringStr = "String: " + Integer.toString(this.string) + "\n";
		String trackStr = "Track: " + Integer.toString(this.trackIndex) + "\n";

		stringBuilder.append(measureStr);
		stringBuilder.append(positionStr);
		stringBuilder.append(stringStr);
		stringBuilder.append(trackStr);

		return stringBuilder.toString();
	}
}
