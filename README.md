# COMP6237
COMP6237 "Data Mining" Lecture Material

This Github repository stores the lecture materials for Jon's part of the [COMP6237](https://secure.ecs.soton.ac.uk/module/COMP6237) Data Mining module at the [University of Southampton](http://www.soton.ac.uk). The lectures presentations themselves are interactive slidesets created using [OpenIMAJ](http://www.openimaj.org).

From this page you can get the source-code for the presentations, which you can build yourself following the instructions below. If you just want to run the presentations for yourself, you can download the latest version of the pre-compiled runnable jar from [here](http://jenkins.ecs.soton.ac.uk/job/COMP6237/lastSuccessfulBuild/artifact/app/target/COMP6237-1.0-SNAPSHOT-jar-with-dependencies.jar). More details, as well as PDF versions of the handouts and slides, are available [here](http://comp6237.ecs.soton.ac.uk/).

##Operating the presentations
The launcher program that opens when you run the jar is self explanatory, but once you've opened a presentation or demo you can make it full screen by pressing "f" (press again to exit). For the presentations you can use the left and right arrow keys to navigate. Note that on some of the interactive slides, you might need to click on the slide background for the arrow keys to work if you clicked on any controls other than buttons.

##Building & running the code
You need to have [Apache Maven](http://maven.apache.org) installed to build the code (it should work with Maven 2 or Maven 3). Fork or clone the repository (or download a source [tarball](https://github.com/jonhare/COMP6237/tarball/master)/[zipball](https://github.com/jonhare/COMP6237/zipball/master)) & then from the command line navigate to the `app` directory within the source tree. Run `mvn install assembly:assembly` to build the presentation, and use `java -jar target/COMP6237-1.0-SNAPSHOT-jar-with-dependencies.jar` to launch the main interface.

