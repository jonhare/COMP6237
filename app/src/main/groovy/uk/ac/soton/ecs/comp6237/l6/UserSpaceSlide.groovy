package uk.ac.soton.ecs.comp6237.l6;

import static java.lang.Math.sqrt
import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font

import org.apache.commons.math3.stat.regression.SimpleRegression
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.annotations.XYLineAnnotation
import org.jfree.chart.axis.NumberTickUnit
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

import uk.ac.soton.ecs.comp6237.MovieData
import uk.ac.soton.ecs.comp6237.utils.Utils

public class UserSpaceSlide implements Slide {
	def transpose(prefs) {
		def newprefs = [:]
		prefs.each { user, movieRatings ->
			movieRatings.each { movie, rating ->
				if (!newprefs[movie]) newprefs[movie] = [:]
				newprefs[movie][user] = rating
			}
		}
		return newprefs
	}

	//Compute a Pearson Correlation similarity
	def simPearson(prefs, key1, key2) {
		if (!prefs[key1] || !prefs[key2]) return 0
		//find items in common
		def si = prefs[key1].keySet().intersect(prefs[key2].keySet())

		def n = si.size()
		if (n == 0) return 0

		def sum1 = si.sum{ prefs[key1][it] }
		def sum2 = si.sum{ prefs[key2][it] }

		def sumSq1 = si.sum{ prefs[key1][it]**2 }
		def sumSq2 = si.sum{ prefs[key2][it]**2 }

		def sumProd = si.sum{ prefs[key1][it]*prefs[key2][it] }

		def num = sumProd - (sum1*sum2/n)
		def den = sqrt((sumSq1 - sum1*sum1/n)*(sumSq2 - sum2*sum2/n))

		if (den == 0) return 0

		return num/den
	}

	def ols(dataset) {
		SimpleRegression r = new SimpleRegression()
		for (int i=0; i<dataset.getItemCount(0); i++) {
			r.addData(dataset.getX(0, i), dataset.getY(0, i))
		}
		return [r.intercept, r.slope]
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		def swing = new SwingBuilder()

		def users = []
		def movies = [] as Set
		transpose(MovieData.SMALLDATA).each{ k,v ->
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

						transpose(MovieData.SMALLDATA).each{ k,v ->
							if (v[x] && v[y]) {
								def coll = false
								_data.each{
									if (it[0] == v[x] && it[1] == v[y])
										coll = true
								}

								_data << [v[x], v[y]]
								_labels << MovieData.SMALLDATA_SHORTNAMES[k]
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
							return new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER)
						return new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER)
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

		XYLineAnnotation bestFit = new XYLineAnnotation(-1,0,6,0)
		chart.getXYPlot().addAnnotation(bestFit)

		def table = swing.panel(layout: new BorderLayout(), opaque:false) {
			widget(constraints:BorderLayout.CENTER, chartPanel)
			panel(constraints:BorderLayout.WEST, opaque: false) {
				comboBox(id: 'cy', font:font, items: movies, actionPerformed: {
					dataset.setYAxis(cy.selectedItem)
					def r = ols(dataset)
					bestFit.y1 = r[0] - 1*r[1]
					bestFit.y2 = r[0] + 6*r[1]
					chart.getXYPlot().setDataset(chart.getXYPlot().getDataset())
					if (swing.hasVariable("pearson"))
						swing."pearson".text = simPearson(MovieData.SMALLDATA, swing."cx".selectedItem, swing."cy".selectedItem).round(2)
				}, selectedIndex: 1)
			}
			panel(constraints:BorderLayout.SOUTH, layout: new BorderLayout(), opaque: false) {
				panel(constraints: BorderLayout.WEST, opaque:false) {
					label('Pearson Correlation: ', font:font)
					label(id: 'pearson', font:font)
				}
				comboBox(id: 'cx', font:font, constraints: BorderLayout.EAST, items: movies, actionPerformed: {
					dataset.setXAxis(cx.selectedItem)
					def r = ols(dataset)
					bestFit.y1 = r[0] - 1*r[1]
					bestFit.y2 = r[0] + 6*r[1]
					chart.getXYPlot().setDataset(chart.getXYPlot().getDataset())
					swing."pearson".text = simPearson(MovieData.SMALLDATA, swing."cx".selectedItem, swing."cy".selectedItem).round(2)
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
		new SlideshowApplication(new UserSpaceSlide(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
