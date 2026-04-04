package app.tuxguitar.app.system.icons;

import java.io.*;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class TGSvgToPngConverter {
    public static InputStream convertSvgToPng(InputStream svgStream, float width, float height) throws Exception {
        TranscoderInput input = new TranscoderInput(svgStream);
        ByteArrayOutputStream oss = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(oss);
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
        transcoder.transcode(input, output);
        InputStream pipeStream = new ByteArrayInputStream(oss.toByteArray());
        oss.flush();
        oss.close();
        return pipeStream;
    }
}