import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.chart.plot.PlotOrientation as Orientation
import groovy.swing.SwingBuilder
import javax.swing.WindowConstants as WC
import java.awt.BorderLayout

def dataset = new DefaultCategoryDataset()
dataset.with{
    addValue 150, "no.1", "Jan"
    addValue 210, "no.1", "Feb"
    addValue 390, "no.1", "Mar"
    addValue 300, "no.2", "Jan"
    addValue 400, "no.2", "Feb"
    addValue 200, "no.2", "Mar"
}

def labels = ["Bugs", "Month", "Count"]
def options = [true, true, true]
def chart = ChartFactory.createLineChart(*labels, dataset,
                Orientation.VERTICAL, *options)
def swing = new SwingBuilder()
def contents = swing.panel(id:'canvas') { widget(new ChartPanel(chart)) }
slidePanel.add(contents)

data=['Lisa Rose': ['Lady in the Water': 2.5, 'Snakes on a Plane': 3.5,
      'Just My Luck': 3.0, 'Superman Returns': 3.5, 'You, Me and Dupree': 2.5,
      'The Night Listener': 3.0],
     'Gene Seymour': ['Lady in the Water': 3.0, 'Snakes on a Plane': 3.5,
      'Just My Luck': 1.5, 'Superman Returns': 5.0, 'The Night Listener': 3.0,
      'You, Me and Dupree': 3.5],
     'Michael Phillips': ['Lady in the Water': 2.5, 'Snakes on a Plane': 3.0,
      'Superman Returns': 3.5, 'The Night Listener': 4.0],
     'Claudia Puig': ['Snakes on a Plane': 3.5, 'Just My Luck': 3.0,
      'The Night Listener': 4.5, 'Superman Returns': 4.0,
      'You, Me and Dupree': 2.5],
     'Mick LaSalle': ['Lady in the Water': 3.0, 'Snakes on a Plane': 4.0,
      'Just My Luck': 2.0, 'Superman Returns': 3.0, 'The Night Listener': 3.0,
      'You, Me and Dupree': 2.0],
     'Jack Matthews': ['Lady in the Water': 3.0, 'Snakes on a Plane': 4.0,
      'The Night Listener': 3.0, 'Superman Returns': 5.0, 'You, Me and Dupree': 3.5],
     'Toby': ['Snakes on a Plane':4.5,'You, Me and Dupree':1.0,'Superman Returns':4.0]]

def users = []
def movies = [] as Set
data.each{k,v -> 
	users << [name: k]
	v.each{k2,v2 -> movies << k2}
}

table = swing.panel(layout: new BorderLayout()) {
  def tab = table(constraints:BorderLayout.CENTER) {
    tableModel(list: users) {
      propertyColumn(header:'Name', propertyName: 'name', editable: false)

      movies.each { movie ->
    	closureColumn(header:movie, read: {row -> return data[row.name][movie]});
      }
    }
  }
  widget(constraints:BorderLayout.NORTH, tab.tableHeader)
}

slidePanel.add(table)