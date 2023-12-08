package org.herac.tuxguitar.io.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;

public class ImageWriter {
	
	public static void write(ImageFormat format, String path, List<UIImage> pages) throws TGFileFormatException {
		try {
			for(int i = 0; i < pages.size() ; i ++ ) {
				OutputStream stream = new FileOutputStream(new File(path + File.separator + "page-" + i + format.getExtension() ));
				
				Image image = ((SWTImage) pages.get(i)).getHandle();
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.data = new ImageData[] { image.getImageData() };
				imageLoader.save(stream, format.getFormat() );
				
				stream.flush();
				stream.close();
			}
		} catch (Throwable throwable) {
			throw new TGFileFormatException("Could not write song!.",throwable);
		}
	}
}
