package uk.ac.soton.ecs.comp6237.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.io.FileUtils;

public class JythonREPLConsoleSlide implements Slide {
	private RSyntaxTextArea textArea;
	private JSplitPane splitPane;
	private int orientation;
	private String initialScript;
	private transient boolean codeChanged;
	private String[] initialCommands;
	private JConsole console;

	public JythonREPLConsoleSlide(int orientation) {
		this(orientation, "");
	}

	public JythonREPLConsoleSlide(int orientation, String initialScript, String... initialCommands) {
		this.orientation = orientation;
		this.initialScript = initialScript;
		this.initialCommands = initialCommands;
	}

	public JythonREPLConsoleSlide(int orientation, URL initialScript, String... initialCommands) throws IOException {
		this(orientation, initialScript.openStream(), initialCommands);
	}

	public JythonREPLConsoleSlide(int orientation, InputStream initialScript, String... initialCommands)
			throws IOException
	{
		this(orientation, FileUtils.readall(initialScript), initialCommands);
		initialScript.close();
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BorderLayout());

		// border all the way around to make it possible to get to the
		// next/previous slide
		base.add(new JPanel(), BorderLayout.NORTH);
		base.add(new JPanel(), BorderLayout.EAST);
		base.add(new JPanel(), BorderLayout.WEST);
		base.add(new JPanel(), BorderLayout.SOUTH);

		createEditorAndConsole(orientation == JSplitPane.HORIZONTAL_SPLIT ? width / 2 : height / 2, base);

		return base;
	}

	protected void createEditorAndConsole(int dividerLocation, final JPanel base) throws IOException {
		textArea = new RSyntaxTextArea(20, 60);
		Font font = textArea.getFont();
		font = font.deriveFont(font.getStyle(), 18);
		textArea.setFont(font);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		textArea.setCodeFoldingEnabled(true);

		textArea.setText(initialScript);

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				codeChanged = true;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				codeChanged = true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				codeChanged = true;
			}
		});

		final RTextScrollPane inputScrollPane = new RTextScrollPane(textArea);

		final JythonInterpreter interpreter = new JythonInterpreter();

		console = new JConsole(interpreter.getInputStream(), interpreter.getOutputStream());
		console.setPythonMode();

		splitPane = new JSplitPane(orientation, inputScrollPane, console);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(dividerLocation);

		final Dimension minimumSize = new Dimension(100, 50);
		inputScrollPane.setMinimumSize(minimumSize);
		console.setMinimumSize(minimumSize);

		final JPanel body = new JPanel();
		body.setBackground(Color.RED);
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.add(splitPane);
		base.add(body, BorderLayout.CENTER);

		new Thread(interpreter).start();

		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (codeChanged) {
					interpreter.execute(textArea.getText());
				}
			}
		});

		// run any initial content
		interpreter.execute(textArea.getText());

		// bit risky - rather dependent on timing!

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// bootstrap history
				for (int i = 0; i < initialCommands.length; i++)
					console.history.add(initialCommands[i]);
				if (initialCommands.length > 0)
					console.historyUp();
			}
		});
	}

	@Override
	public void close() {
	}

	void display(Object obj) {
		if (obj instanceof Icon)
			console.println((Icon) obj);
		if (obj instanceof BufferedImage)
			console.println(new ImageIcon((BufferedImage) obj));
		else
			console.println(obj.toString());
	}

	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		slides.add(new JythonREPLConsoleSlide(JSplitPane.HORIZONTAL_SPLIT));

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
