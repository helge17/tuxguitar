package app.tuxguitar.io.base;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class TestFileFormatUtils {

	private byte[] createData(int length) {
		byte[] data = new byte[length];
		for(int i = 0; i < length; i++) {
			data[i] = (byte) (i * 31);
		}
		return data;
	}

	@Test
	public void testGetBytesEmptyStream() throws Throwable {
		assertEquals(0, TGFileFormatUtils.getBytes(new ByteArrayInputStream(new byte[0])).length);
	}

	@Test
	public void testGetBytesSmallStream() throws Throwable {
		byte[] data = createData(100);
		assertArrayEquals(data, TGFileFormatUtils.getBytes(new ByteArrayInputStream(data)));
	}

	@Test
	public void testGetBytesStreamLargerThanBuffer() throws Throwable {
		byte[] data = createData(100000);
		assertArrayEquals(data, TGFileFormatUtils.getBytes(new ByteArrayInputStream(data)));
	}

	@Test
	public void testGetBytesStreamWithPartialReads() throws Throwable {
		byte[] data = createData(20000);
		InputStream stream = new ByteArrayInputStream(data) {
			@Override
			public synchronized int read(byte[] b, int off, int len) {
				return super.read(b, off, Math.min(len, 7));
			}
		};
		assertArrayEquals(data, TGFileFormatUtils.getBytes(stream));
	}

	@Test
	public void testGetInputStream() throws Throwable {
		byte[] data = createData(50000);
		InputStream stream = TGFileFormatUtils.getInputStream(new ByteArrayInputStream(data));
		assertArrayEquals(data, TGFileFormatUtils.getBytes(stream));
	}
}
