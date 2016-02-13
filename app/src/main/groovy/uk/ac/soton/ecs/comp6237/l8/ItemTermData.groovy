package uk.ac.soton.ecs.comp6237.l8;

import org.openimaj.feature.FloatFVComparison



public class ItemTermData {
	List<String> itemNames
	List<String> terms
	float[][] counts

	ItemTermData() {
		itemNames = []
		terms = []
	}

	public ItemTermData(String data) {
		def d = loadDataTable(ItemTermData.class.getResource(data))
		this.itemNames = d.rhead
		this.terms = d.chead
		this.counts = new float[itemNames.size()][terms.size()]

		for (int i=0; i<counts.length; i++) {
			for (int j=0; j<counts[i].length; j++) {
				counts[i][j] = d.data[i][j]
			}
		}
	}

	public static def loadDataTable(stream) {
		stream = stream.newReader()
		def colheadings = stream.readLine().split('\t').drop(1)
		def rowheadings = []
		def data=[]

		stream.eachLine{
			def parts = it.split('\t')
			rowheadings << parts[0]
			data << parts.drop(1).collect { it as float }
		}

		return [chead: colheadings, rhead: rowheadings, data: data]
	}

	public ItemTermData transpose() {
		ItemTermData tr = new ItemTermData()

		tr.itemNames = this.terms
		tr.terms = this.itemNames
		tr.counts = new float[tr.itemNames.size()][tr.terms.size()]

		for (int i=0; i<counts.length; i++)
			for (int j=0; j<counts[0].length; j++)
				tr.counts[j][i] = counts[i][j]

		return tr
	}

	public static void main(String [] args) {
		def data = new ItemTermData("moduledata.txt")

		def rnk = [:]
		def vis;
		data.itemNames.eachWithIndex {it, idx ->
			if (it.contains("COMP3204"))
				vis=idx
		}

		data.itemNames.eachWithIndex {it, idx ->
			def score = org.openimaj.feature.FloatFVComparison.EUCLIDEAN.compare(data.counts[vis], data.counts[idx])
			rnk[it] = score
		}

		rnk = rnk.sort{it.value}

		println rnk
	}
}
