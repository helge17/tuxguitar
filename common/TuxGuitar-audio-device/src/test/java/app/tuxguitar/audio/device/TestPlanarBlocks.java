package app.tuxguitar.audio.device;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.FloatBuffer;

import org.junit.jupiter.api.Test;

class TestPlanarBlocks {

	@Test
	void eachChannelIsReadFromItsPlaceInTheBufferForTheFramesOfTheBlock() {
		TGPlanarBlocks blocks = new TGPlanarBlocks(2, 4);
		blocks.getBuffer().asFloatBuffer().put(new float[] {1, 2, 3, 4, 5, 6, 7, 8});
		blocks.read(3);
		assertArrayEquals(new float[][] {{1, 2, 3, 0}, {5, 6, 7, 0}}, blocks.getChannels());
	}

	@Test
	void eachChannelIsWrittenToItsPlaceInTheBufferForTheFramesOfTheBlock() {
		TGPlanarBlocks blocks = new TGPlanarBlocks(2, 4);
		blocks.getChannels()[0] = new float[] {1, 2, 3, 4};
		blocks.getChannels()[1] = new float[] {5, 6, 7, 8};
		blocks.write(3);
		float[] buffer = new float[8];
		FloatBuffer floats = blocks.getBuffer().asFloatBuffer();
		floats.get(buffer);
		assertArrayEquals(new float[] {1, 2, 3, 0, 5, 6, 7, 0}, buffer);
	}
}
