package uk.ac.soton.ecs.comp6237;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;

@uk.ac.soton.ecs.comp6237.utils.annotations.Lecture(title = "L1: Big Data Processing",
		handoutsURL = "https://github.com/jonhare/COMP6237/raw/master/handouts/pdf/...",
		slidesURL = "https://github.com/jonhare/COMP6237/raw/master/lectures/pdf/...")
@uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs(vmArguments = "-Xmx1G")
public class TestSlide implements Slide {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		slides.add(new TestSlide());

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
		final RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		textArea.setCodeFoldingEnabled(true);
		final RTextScrollPane sp = new RTextScrollPane(textArea);
		base.add(sp);

		return base;
	}

	@Override
	public void close() {

	}
}
