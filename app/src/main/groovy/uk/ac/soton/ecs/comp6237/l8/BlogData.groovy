package uk.ac.soton.ecs.comp6237.l8;


public class BlogData {
	List<String> blogNames
	List<String> terms
	float[][] counts

	public BlogData() {
		def d = loadDataTable(BlogData.class.getResource("blogdata.txt"))
		this.blogNames = d.rhead
		this.terms = d.chead
		this.counts = new float[blogNames.size()][terms.size()]

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

	public static BlogData loadSmallData(int size) {
		BlogData bd = new BlogData()
		bd.blogNames = bd.blogNames.subList(0, size)
		bd.counts = Arrays.copyOfRange(bd.counts, 0, size)
		return bd
	}
}
