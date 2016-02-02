package uk.ac.soton.ecs.comp6237.l6;

import java.io.IOException;

import javax.swing.JSplitPane;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.GroovyREPLConsoleSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "Precomputing item to item similarity")
public class ItemItemPrecompDemo extends GroovyREPLConsoleSlide {

	public ItemItemPrecompDemo() throws IOException {
		super(JSplitPane.VERTICAL_SPLIT, Lecture6.class.getResource("ItemPrecomp.groovy"),
				"calculateSimilarItems(data, 2)");
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new ItemItemPrecompDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
