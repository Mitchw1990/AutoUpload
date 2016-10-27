import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * Created by Mitch on 8/21/2016.
 */
class ImageHelper {

    private static BufferedImage read(File originalImage) throws IOException {
        return ImageIO.read(originalImage);
    }

    private static BufferedImage resize(BufferedImage image) {
        return Scalr.resize(image,
                Scalr.Method.SPEED,
                Scalr.Mode.AUTOMATIC,
                640,
                480,
                Scalr.OP_ANTIALIAS);
    }

    public static void cleanUp(){
        try {
            Arrays.stream(new File("tmp" + File.separatorChar).listFiles()).forEach(File::delete);
        }catch (NullPointerException e){
            System.out.println("Error: tmp directory not found.");
        }
    }

    private static BufferedImage watermark(BufferedImage image, String date) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
        g2d.setComposite(alphaChannel);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(date, g2d);
        int centerX = (image.getWidth() - (int) rect.getWidth()) - 30;
        int centerY = image.getHeight() - 30;
        g2d.drawString(date, centerX, centerY);
        return image;
    }

    public static String resizeAndWatermark(File original, String stamp){
        BufferedImage photo;
        String pathStr = null;
        try {
            photo = read(original);
            BufferedImage resized = resize(photo);
            BufferedImage watermarked = watermark(resized, stamp);
            File file = new File("tmp/" + original.getName());
            pathStr = file.getAbsolutePath();
            ImageIO.write(watermarked, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathStr;
    }
}
