package org.herac.tuxguitar.io.gpx.v7;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class GPXFileSystem {
	
	public static final String RESOURCE_SCORE = "Content/score.gpif";
	public static final String RESOURCE_VERSION = "VERSION";
	
	public static final String[] SUPPORTED_VERSIONS = {"7.0"};
	
	private byte[] fsBuffer;

	public GPXFileSystem() {
		super();
	}

	public void load(InputStream in) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int read = 0;
		while ((read = in.read()) != -1) {
			out.write(read);
		}
		out.close();
		out.flush();

		this.fsBuffer = out.toByteArray();
	}

	public InputStream getFileContentsAsStream(String resource) throws Throwable {
		byte[] resourceBytes = null;

		ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(new ByteArrayInputStream(this.fsBuffer));
		ArchiveEntry zipEntry = null;
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			if (zipEntry.getName().equals(resource)) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				byte buffer[] = new byte[2048];
				int read = 0;
				while ((read = zipInputStream.read(buffer)) > 0) {
					out.write(buffer, 0, read);
				}

				resourceBytes = out.toByteArray();

				out.close();
			}
		}
		zipInputStream.close();

		return (resourceBytes != null ? new ByteArrayInputStream(resourceBytes) : null);
	}
	
	public boolean isSupportedVersion() throws Throwable {
		InputStream stream = this.getFileContentsAsStream(RESOURCE_VERSION);
		if( stream != null ) {
			byte[] bytes = new byte[3];			
			stream.read(bytes);
			String version = new String(bytes);
			if( version != null ) {
				for(String supportedVersion : SUPPORTED_VERSIONS) {
					if( supportedVersion.equals(version)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
