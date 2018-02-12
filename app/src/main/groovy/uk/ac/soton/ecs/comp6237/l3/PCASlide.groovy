package uk.ac.soton.ecs.comp6237.l3;

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
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
import org.openimaj.math.matrix.algorithm.pca.ThinSvdPrincipalComponentAnalysis

import uk.ac.soton.ecs.comp6237.utils.Utils
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration

@Demonstration(title='Visualising in 2D using PCA')
public class PCASlide implements Slide {
	@Override
	public Component getComponent(int width, int height) throws IOException {
		def swing = new SwingBuilder()

		def data = new ItemTermData("moduledata.txt")
		double[][] ddata = org.openimaj.util.array.ArrayUtils.convertToDouble(data.counts)
		def pca = new ThinSvdPrincipalComponentAnalysis(2);
		pca.learnBasis(ddata)

		def dataset = new AbstractXYDataset() {
					def _data = []
					def _labels = []

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

		data.itemNames.eachWithIndex {it, idx ->
			double[] res = pca.project(ddata[idx])
			dataset._data << res
			dataset._labels << it
		}

		def chart = ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, false, false, false)

		XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true) {
					public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
						return new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_LEFT)
					}
				}
		def font = Font.decode("Helvetica Neue-22")
		renderer.setBaseItemLabelFont(font)
		chart.getXYPlot().setRenderer(renderer)
		//chart.getXYPlot().getDomainAxis().setRange(-0.5, 5.5)
		chart.getXYPlot().getDomainAxis().setTickLabelFont(font)
		//chart.getXYPlot().getDomainAxis().setTickUnit(new NumberTickUnit(1))
		//chart.getXYPlot().getRangeAxis().setRange(-0.5, 5.5)
		chart.getXYPlot().getRangeAxis().setTickLabelFont(font)
		//chart.getXYPlot().getRangeAxis().setTickUnit(new NumberTickUnit(1))

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

		def panel = swing.panel(layout: new BorderLayout(), opaque:false) {
			widget(constraints:BorderLayout.CENTER, chartPanel)
		}

		panel.setOpaque(false);

		return panel;
	}

	@Override
	public void close() {
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new PCASlide(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
