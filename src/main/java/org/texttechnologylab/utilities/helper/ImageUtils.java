package org.texttechnologylab.utilities.helper;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * Created by abrami on 19.12.16.
 */
public class ImageUtils {


    public static File stringToImage(String pString) throws IOException {

        // create a buffered image
        BufferedImage image = null;
        byte[] imageByte;

        Base64.Decoder decoder = Base64.getDecoder();
        imageByte = decoder.decode(pString);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

// write the image to a file
        File outputfile = TempFileHandler.getTempFile();
        ImageIO.write(image, "png", outputfile);

        return outputfile;

    }

    public static String ImageToString(BufferedImage bI) throws IOException {

        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(bI, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }

    }

}
