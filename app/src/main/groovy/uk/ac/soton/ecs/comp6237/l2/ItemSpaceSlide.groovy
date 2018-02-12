package uk.ac.soton.ecs.comp6237.l2;

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.labels.ItemLabelAnchor
import org.jfree.chart.labels.ItemLabelPosition
import org.jfree.chart.labels.StandardXYItemLabelGenerator
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYItemRenderer
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.AbstractXYDataset
import org.jfree.data.xy.XYDataset
import org.jfree.ui.TextAnchor
import org.openimaj.content.slideshow.Slide
import org.openimaj.content.slideshow.SlideshowApplication

import uk.ac.soton.ecs.comp6237.utils.Utils
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration

@Demonstration(title='Visualising users in item space')
public class ItemSpaceSlide implements Slide {
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
		movies = movies as List

		def dataset = new AbstractXYDataset() {
					def _x
					def _y
					def _data = []
					def _labels = []
					def _collision = []

					void setAxes(String x, String y) {
						_x = x
						_y = y
						_data.clear()
						_labels.clear()
						_collision.clear()

						MovieData.SMALLDATA.each{ k,v ->
							if (v[x] && v[y]) {
								def coll = false
								_data.each{
									if (it[0] == v[x] && it[1] == v[y])
										coll = true
								}

								_data << [v[x], v[y]]
								_labels << k
								_collision << coll
							}
						}
					}

					void setXAxis(String x) {
						setAxes(x, _y)
					}

					void setYAxis(String y) {
						setAxes(_x, y)
					}

					Number getY(int series, int item) {
						return _data[item][1]
					}

					Number getX(int series, int item) {
						return _data[item][0]
					}

					String getLabel(int series, int item) {
						return _labels[item]
					}

					int getItemCount(int arg0) {
						return _data.size()
					}

					int getSeriesCount() {
						return 1
					}

					Comparable getSeriesKey(int arg0) {
						return "DATA"
					}
				}

		dataset.setAxes(movies[4], movies[1])

		def chart = ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, false, false, false)

		XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true) {
					public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
						if (dataset._collision[column])
							return new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_LEFT)
						return new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_LEFT)
					}
				}
		def font = Font.decode("Helvetica Neue-22")
		renderer.setBaseItemLabelFont(font)
		chart.getXYPlot().setRenderer(renderer)
		chart.getXYPlot().getDomainAxis().setRange(-0.5, 5.5)
		chart.getXYPlot().getDomainAxis().setTickLabelFont(font)
		chart.getXYPlot().getDomainAxis().setTickUnit(new NumberTickUnit(1))
		chart.getXYPlot().getRangeAxis().setRange(-0.5, 5.5)
		chart.getXYPlot().getRangeAxis().setTickLabelFont(font)
		chart.getXYPlot().getRangeAxis().setTickUnit(new NumberTickUnit(1))

		chart.getXYPlot().getRenderer().setBaseItemLabelGenerator(new StandardXYItemLabelGenerator() {
					String generateLabel(XYDataset ds, int series, int item) {
						return ds.getLabel(series, item);
					};
				});
		chart.getXYPlot().getRenderer().setBaseItemLabelsVisible(true);

		def chartPanel = new ChartPanel(chart);
		chart.setBackgroundPaint(new java.awt.Color(255, 255, 255, 255));
		chart.getXYPlot().setBackgroundPaint(java.awt.Color.WHITE)
		chart.getXYPlot().setRangeGridlinePaint(java.awt.Color.GRAY)
		chart.getXYPlot().setDomainGridlinePaint(java.awt.Color.GRAY)

		def table = swing.panel(layout: new BorderLayout(), opaque:false) {
			widget(constraints:BorderLayout.CENTER, chartPanel)
			panel(constraints:BorderLayout.WEST, opaque: false) {
				comboBox(id: 'cy', items: movies, font:font, actionPerformed: {
					dataset.setYAxis(cy.selectedItem)
					chart.getXYPlot().setDataset(chart.getXYPlot().getDataset())
				}, selectedIndex: 1)
			}
			panel(constraints:BorderLayout.SOUTH, layout: new BorderLayout(), opaque: false) {
				comboBox(id: 'cx', constraints: BorderLayout.EAST, font:font, items: movies, actionPerformed: {
					dataset.setXAxis(cx.selectedItem)
					chart.getXYPlot().setDataset(chart.getXYPlot().getDataset())
				}, selectedIndex: 4)
			}
		}

		table.setOpaque(false);

		return table;
	}

	@Override
	public void close() {
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new ItemSpaceSlide(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
