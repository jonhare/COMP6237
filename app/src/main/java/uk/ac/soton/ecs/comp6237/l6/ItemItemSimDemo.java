package uk.ac.soton.ecs.comp6237.l6;

import java.io.IOException;

import javax.swing.JSplitPane;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.GroovyREPLConsoleSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "Comparing items to items")
public class ItemItemSimDemo extends GroovyREPLConsoleSlide {

	public ItemItemSimDemo() throws IOException {
		super(JSplitPane.VERTICAL_SPLIT, Lecture6.class.getResource("../ItemSim.groovy"));
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new ItemItemSimDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
