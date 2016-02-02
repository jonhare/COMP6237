package uk.ac.soton.ecs.comp6237.l6;

import java.io.IOException;

import javax.swing.JSplitPane;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.GroovyREPLConsoleSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "Item-based recommendation")
public class ItemBasedRecDemo extends GroovyREPLConsoleSlide {

	public ItemBasedRecDemo() throws IOException {
		super(JSplitPane.VERTICAL_SPLIT, Lecture6.class.getResource("../ItemRec.groovy"));
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new ItemBasedRecDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
