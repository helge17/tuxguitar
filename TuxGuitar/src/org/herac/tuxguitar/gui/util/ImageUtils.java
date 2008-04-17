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
	 * Crea un java.awt.image.BufferedImage a partir de un ImageData
	 * @param data
	 * @return
	 *//*
	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask,palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel,colorModel.createCompatibleWritableRaster(data.width,data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		} 
		RGB[] rgbs = palette.getRGBs();
		byte[] red = new byte[rgbs.length];
		byte[] green = new byte[rgbs.length];
		byte[] blue = new byte[rgbs.length];
		for (int i = 0; i < rgbs.length; i++) {
			RGB rgb = rgbs[i];
			red[i] = (byte) rgb.red;
			green[i] = (byte) rgb.green;
			blue[i] = (byte) rgb.blue;
		}
		if (data.transparentPixel != -1) {
			colorModel = new IndexColorModel(data.depth, rgbs.length, red,green, blue, data.transparentPixel);
		} else {
			colorModel = new IndexColorModel(data.depth, rgbs.length, red,green, blue);
		}
		BufferedImage bufferedImage = new BufferedImage(colorModel,colorModel.createCompatibleWritableRaster(data.width,data.height), false, null);
		WritableRaster raster = bufferedImage.getRaster();
		int[] pixelArray = new int[1];
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				int pixel = data.getPixel(x, y);
				pixelArray[0] = pixel;
				raster.setPixel(x, y, pixelArray);
			}
		}
		return bufferedImage;		
	}	
*/
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
