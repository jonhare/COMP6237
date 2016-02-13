//script to process a list of ecs module syllabus urls and build bow histograms

@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2.1' )
import org.ccil.cowan.tagsoup.Parser

def tagsoupParser = new Parser()
def slurper = new XmlSlurper(tagsoupParser)

def rawdata = [:]
def termDocStats = [:].withDefault {0}
def ndocs = 0

this.getClass().getResourceAsStream("/uk/ac/soton/ecs/comp6237/l8/modules.txt").eachLine {url ->
	def htmlParser = slurper.parse(url)

	def title = htmlParser.'**'.find{ it.@class = 'uos-sia-title' }.text()
	title = title.substring(0, title.indexOf('|')).trim()

	def terms = [:];
	htmlParser.'**'.findAll{ it.@id == "tab_pane_overview"}.each { syllabus ->
		processNode(syllabus, terms)
	}
	htmlParser.'**'.findAll{ it.@id == "tab_pane_aims"}.each { syllabus ->
		processNode(syllabus, terms)
	}
	htmlParser.'**'.findAll{ it.@id == "tab_pane_syllabus"}.each { syllabus ->
		processNode(syllabus, terms)
	}

	println title
	rawdata[title] = terms

	terms.each { k,v ->  termDocStats[k] += 1 }
	ndocs++
}

def titleterms = rawdata.keySet().collect{it.split("[^A-Z^a-z]+").collect{i1->i1.toLowerCase().trim()}}.flatten()

println titleterms

def filteredTermList = []
termDocStats.each { k,v ->
	def frac = v/ndocs
	if ((frac > 0.1 && frac < 0.7) || titleterms.contains(k))
		filteredTermList << k

	println k + " " + frac + " " + titleterms.contains(k)
}

def outfile = "./src/main/resources/uk/ac/soton/ecs/comp6237/l8/moduledata.txt" as File
outfile.delete()
outfile << "MODULE"
filteredTermList.each { outfile << "\t" + it }
outfile << "\n"

rawdata.each { module, terms ->
	outfile << module
	filteredTermList.each {
		if (terms[it])
			outfile << "\t" + terms[it]
		else
			outfile << "\t" + 0
	}
	outfile << "\n"
}

def processNode(node, terms) {
	processText(node.localText(), terms)
	node.depthFirst().findAll().each {
		processText(it.localText(), terms)
	}
}

def processText(text, terms) {
	text.each { t ->
		t.split('[^A-Z^a-z]+').each {
			if (it.length()>2) {
				if (!terms[it.toLowerCase()]) {
					terms[it.toLowerCase()] = 1
				} else {
					terms[it.toLowerCase()] = terms[it.toLowerCase()] + 1
				}
			}
		}
	}
}
