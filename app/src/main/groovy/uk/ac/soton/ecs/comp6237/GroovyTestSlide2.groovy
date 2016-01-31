

package uk.ac.soton.ecs.comp6237;

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Font

import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

import org.openimaj.content.slideshow.Slide
import org.openimaj.content.slideshow.SlideshowApplication

import uk.ac.soton.ecs.comp6237.utils.Utils

public class GroovyTestSlide2 implements Slide {
	@Override
	public Component getComponent(int width, int height) throws IOException {
		def swing = new SwingBuilder()

		def users = []
		def movies = [] as Set
		MovieData.SMALLDATA.each{ k,v ->
			users << [name: k]
			v.each{ k2,v2 ->
				movies << k2
			}
		}

		Font font = Font.decode("Helvetica Neue-28")

		def cellRenderer = new DefaultTableCellRenderer() {
					Component getTableCellRendererComponent(JTable arg0,  arg1, boolean arg2, boolean arg3, int row, int col) {
						def c = super.getTableCellRendererComponent(arg0,  arg1, arg2, arg3, row, col)
						c.setFont(font);
						c.setVerticalAlignment(JLabel.TOP)
						c.setHorizontalAlignment(JLabel.CENTER)
						c.setForeground(new Color(0x444444))
						if (row == -1) {
							c.setBackground(new Color(0x274B57));
							c.setForeground(Color.WHITE)
						} else if (col == 0) c.setBackground(new Color(0xE2E4E4));
						else c.setBackground(Color.WHITE);
						return c;
					};

					void setText(String arg0) {
						super.setText("<html><div style='text-align: center;\'>" + arg0 + "</div></html>")
					};
				}

		def table = swing.panel(layout: new BorderLayout()) {
			def tab = table(constraints:BorderLayout.CENTER) {
				tableModel(list: users) {
					propertyColumn(header:'', propertyName: 'name', editable: false, cellRenderer: cellRenderer, headerRenderer: cellRenderer)

					movies.each { movie ->
						closureColumn(header:movie, read: { row ->
							return MovieData.SMALLDATA[row.name][movie]
						}, cellRenderer: cellRenderer, headerRenderer: cellRenderer)
					}
				}
			}

			for (int i = 0; i < tab.getRowCount(); i++)
				tab.setRowHeight(i, (int) tab.getCellRenderer(i, 0)
						.getTableCellRendererComponent(tab, tab.getValueAt(i, 0), false, false, i, 0)
						.getPreferredSize().height);

			widget(constraints:BorderLayout.NORTH, tab.tableHeader)

			def sz = tab.tableHeader.getPreferredSize()
			sz.height *= 3
			tab.tableHeader.setPreferredSize(sz)

			tab.setGridColor(new Color(0xB7BABA))
			tab.setShowVerticalLines(false)
		}

		return table;
	}

	@Override
	public void close() {
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new GroovyTestSlide2(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
