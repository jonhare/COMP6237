package uk.ac.soton.ecs.comp6237.testing;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import uk.ac.soton.ecs.comp6237.utils.Utils;

public class GroovySlide implements Slide {

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		final Binding sharedData = new Binding();
		final GroovyShell shell = new GroovyShell(sharedData);
		sharedData.setProperty("slidePanel", base);
		final Script script = shell.parse(new InputStreamReader(GroovySlide.class.getResourceAsStream("../test.groovy")));
		script.run();

		// final RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
		// textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		// textArea.setCodeFoldingEnabled(true);
		// final RTextScrollPane sp = new RTextScrollPane(textArea);
		// base.add(sp);

		return base;
	}

	@Override
	public void close() {

	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new GroovySlide(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
