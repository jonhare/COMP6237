package uk.ac.soton.ecs.comp6237.l2;

import java.io.IOException;

import javax.swing.JSplitPane;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.GroovyREPLConsoleSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "User-based recommendation")
public class UserBasedRecDemo extends GroovyREPLConsoleSlide {

	public UserBasedRecDemo() throws IOException {
		super(JSplitPane.VERTICAL_SPLIT, Lecture2.class.getResource("UserRec.groovy"), "getRecommendations(data, 'Toby')");
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new UserBasedRecDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
