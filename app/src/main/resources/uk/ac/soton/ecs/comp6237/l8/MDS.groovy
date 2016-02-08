import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import org.openimaj.OpenIMAJ

image = ImageIO.read(OpenIMAJ.getLogoAsStream());

display(image)
