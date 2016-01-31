package uk.ac.soton.ecs.comp6237.utils;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.ui.SystemOutputInterceptor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.codehaus.groovy.runtime.MethodClosure;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.io.FileUtils;

public class GroovyConsoleSlide implements Slide, ActionListener {
	private static final MutableAttributeSet ERROR_STYLE = new SimpleAttributeSet();
	private static final MutableAttributeSet OUTPUT_STYLE = new SimpleAttributeSet();
	static {
		StyleConstants.setForeground(ERROR_STYLE, Color.RED);
		StyleConstants.setForeground(OUTPUT_STYLE, Color.BLACK);
	}

	private RSyntaxTextArea textArea;
	private JTextPane outputPane;
	private JSplitPane splitPane;
	private SystemOutputInterceptor systemOutInterceptor;
	private SystemOutputInterceptor systemErrorInterceptor;
	private int orientation;
	private String initialScript;

	public GroovyConsoleSlide(int orientation) {
		this(orientation, "");
	}

	public GroovyConsoleSlide(int orientation, String initialScript) {
		this.orientation = orientation;
		this.initialScript = initialScript;
	}

	public GroovyConsoleSlide(int orientation, URL initialScript) throws IOException {
		this(orientation, initialScript.openStream());
	}

	public GroovyConsoleSlide(int orientation, InputStream initialScript) throws IOException {
		this(orientation, FileUtils.readall(initialScript));
		initialScript.close();
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BorderLayout());

		final JPanel controls = new JPanel();
		final JButton runBtn = new JButton("Run");
		runBtn.setActionCommand("run");
		runBtn.addActionListener(this);
		controls.add(runBtn);

		base.add(controls, BorderLayout.NORTH);

		textArea = new RSyntaxTextArea(20, 60);
		Font font = textArea.getFont();
		font = font.deriveFont(font.getStyle(), 18);
		textArea.setFont(font);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		textArea.setCodeFoldingEnabled(true);

		textArea.setText(initialScript);

		final RTextScrollPane inputScrollPane = new RTextScrollPane(textArea);

		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setFont(new Font("Monospaced", Font.PLAIN, 18));
		outputPane.setBorder(new EmptyBorder(4, 4, 4, 4));
		final JScrollPane outputScrollPane = new JScrollPane(outputPane);

		splitPane = new JSplitPane(orientation, inputScrollPane, outputScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(width / 2);

		final Dimension minimumSize = new Dimension(100, 50);
		inputScrollPane.setMinimumSize(minimumSize);
		outputScrollPane.setMinimumSize(minimumSize);

		final JPanel body = new JPanel();
		body.setBackground(Color.RED);
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.add(splitPane);
		base.add(body, BorderLayout.CENTER);

		installInterceptors();

		return base;
	}

	public void installInterceptors() {
		systemOutInterceptor = new SystemOutputInterceptor(new MethodClosure(this, "notifySystemOut"), true);
		systemOutInterceptor.start();
		systemErrorInterceptor = new SystemOutputInterceptor(new MethodClosure(this, "notifySystemErr"), false);
		systemErrorInterceptor.start();
	}

	public Boolean notifySystemErr(final String str) {
		notifySystem(str, ERROR_STYLE);
		return false;
	}

	private void notifySystem(final String str, final AttributeSet style) {
		if (EventQueue.isDispatchThread()) {
			appendText(str, style);
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					appendText(str, style);
				}
			});
		}
	}

	private void appendText(String str, AttributeSet style) {
		final Document doc = outputPane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, style);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	public Boolean notifySystemOut(String str) {
		notifySystem(str, OUTPUT_STYLE);
		return false;
	}

	@Override
	public void close() {
		systemErrorInterceptor.stop();
		systemOutInterceptor.stop();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals("run")) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					final JButton btn = (JButton) e.getSource();
					btn.setText("Running");
					btn.setEnabled(false);
					try {
						outputPane.setText("");
						final GroovyShell shell = new GroovyShell();
						final Script script = shell.parse(textArea.getText());
						script.run();
					} finally {
						btn.setText("Run");
						btn.setEnabled(true);
					}
				}
			}).start();
			;
		}
	}

	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		slides.add(new GroovyConsoleSlide(JSplitPane.HORIZONTAL_SPLIT));

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
