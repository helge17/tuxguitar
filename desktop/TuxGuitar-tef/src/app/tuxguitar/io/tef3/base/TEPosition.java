package org.herac.tuxguitar.io.tef3.base;

public class TEPosition {
    private int measure;

    private int positionInMeasure;

    private int string;

    private int trackIndex;

    public TEPosition(int measure, int positionInMeasure, int string, int trackIndex) {
        this.measure = measure;
        this.positionInMeasure = positionInMeasure;
        this.string = string;
        this.trackIndex = trackIndex;
    }

    public static TEPosition createPositionFromLocation(TESong song, int location) {
        // Constants
		int maxPositionsInFourFour = 16;
		int valuePerString = 8;
		int valuePerPositionPerString = 32;
		int valuePerPosition = valuePerPositionPerString * song.getTotalStringCount();
		int totalMeasures = song.getMeasures().size();

		// Runtime variables
		int measureOfComponent = 0;
		int positionOfComponentInMeasure = 0;
		int stringOfComponentInMeasure = 0;

		for (int i = 0; i < totalMeasures; i++) {
			TEMeasure currentMeasure = song.getMeasures().get(i);
			TETimeSignature measureTimeSignature = currentMeasure.getTimeSignature();
			float timeSignatureRatio = (float)measureTimeSignature.getNumerator() / measureTimeSignature.getDenominator();
			int maxPositionsInMeasure = (int)(maxPositionsInFourFour * timeSignatureRatio);
			int valueForWholeMeasure = valuePerPosition * maxPositionsInMeasure;

			if ((location - valueForWholeMeasure) <= 0)
			{
				positionOfComponentInMeasure = location / valuePerPosition;
				location %= valuePerPosition;

				stringOfComponentInMeasure = location / valuePerString;

				break;
			}
			else
			{
				location -= valueForWholeMeasure;
				measureOfComponent += 1;
			}
		}

        int trackIndex = 0;

        for (int i = 0; i < song.getTracks().size(); i++) {
            TETrack track = song.getTracks().get(i);

            if ((stringOfComponentInMeasure - track.getStringCount()) < 0)
            {
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
