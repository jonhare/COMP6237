package uk.ac.soton.ecs.comp6237.utils;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * {@link PrintWriter} that flushes all output immediately. Useful for console
 * applications where the output is needed right away, and there are potentially
 * multiple threads writing to the underlying stream.
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 *
 */
public class ImmediateFlushingPrintWriter extends PrintWriter {
	public ImmediateFlushingPrintWriter(OutputStream out) {
		super(out);
	}

	@Override
	public void write(String s, int off, int len) {
		super.write(s, off, len);
		flush();
	}

	@Override
	public void write(int c) {
		super.write(c);
		flush();
	}

	@Override
	public void write(char[] buf) {
		super.write(buf);
		flush();
	}
}
