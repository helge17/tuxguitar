package org.herac.tuxguitar.gui.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class ImageUtils {
	/**
	 * Escribe el ImageData en el outputStream
	 * @param data
	 * @param outputStream
	 * @param format
	 */
	public static void writeImage(ImageData data,OutputStream outputStream,int format){
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[]{data};
		loader.save(outputStream,format);
	}
	
	/**
	 * Convierte un ImageData en un array de bytes
	 * @param data
	 * @param format
	 * @return
	 */
	public static byte[] imageToByteArray(ImageData data,int format){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeImage(data,out,format);
		return out.toByteArray();
	}
	
	/**
	 * Crea una mascara a partir de src.
	 * 
	 * @param src
	 * @param alpha
	 * @param none
	 * @return ImageData
	 */
	public static ImageData applyMask(ImageData src,RGB alpha,RGB none){
		ImageData maskData = new ImageData(src.width,src.height,1,new PaletteData(new RGB[]{ none,alpha }  ));
		for(int x = 0; x< maskData.width; x++) {
			for(int y = 0; y < maskData.height; y++) {
				RGB rgb = src.palette.getRGB(src.getPixel(x, y));
				if(rgb.red == alpha.red && rgb.green == alpha.green && rgb.blue == alpha.blue){
					maskData.setPixel(x, y, maskData.palette.getPixel(none));
				}else{
					maskData.setPixel(x, y, maskData.palette.getPixel(alpha));
				}
			}
		}
		return maskData;
	}
}
