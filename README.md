# LS3
Latent Semantic Analysis-based Similarity Search for Process Models

### Description
The LS3 Java API can be used to query process models stored as PNML files. It is developed as part of my doctoral thesis at the [Institute AIFB](http://www.aifb.kit.edu/web/Hauptseite/en) of the [Karlsruhe Institute of Technology](http://www.kit.edu/english/index.php) (KIT). As it is a research prototype, please reference it in your publications as described below.

### Code and Used Libraries
An example on how to use the LS3 library can be found [here](https://github.com/ASchoknecht/LS3/wiki/LS3-Usage). An already compiled version can be downloaded [here](http://butler.aifb.kit.edu/asc/LS3/LS3-1.0-jar-with-dependencies.jar).

The code is stored as a maven project with pom.xml included for the external library dependencies. The following external libraries are used in the LS3 API:

* [Stanford CoreNLP](http://stanfordnlp.github.io/CoreNLP/)
* [JDOM2](http://www.jdom.org/)
* [Google Collections](https://mvnrepository.com/artifact/com.google.collections/google-collections/1.0)
* [Apache Commons Math3](http://commons.apache.org/proper/commons-math/)
* [Apache Commons IO](https://commons.apache.org/proper/commons-io/)
* [Porter Stemmer](https://mvnrepository.com/artifact/gov.sandia.foundry/porter-stemmer)

### Citation
If you want to cite this API please use the following references:


> Schoknecht, A. und A. Oberweis (2017)<br/>
> **LS3: Latent Semantic Analysis-based Similarity Search for Process Models**<br/>
> Enterprise Modelling and Information Systems Architectures 12(2), S. 1-22<br/>
> DOI: [10.18417/emisa.12.2] (http://dx.doi.org/10.18417/emisa.12.2).

> Andreas Schoknecht, Nicolai Fischer, Andreas Oberweis<br/>
> **Process Model Search using Latent Semantic Analysis**<br/>
> Business Process Management Workshops, Rio de Janeiro, Brasilien<br/>
> M. Dumas und B. Fantinato Marcelo. Bd. 281. Lecture Notes in Business Information Processing, Springer, S. 283–295<br/>
> DOI: [10.1007/978-3-319-58457-7_21] (http://dx.doi.org/10.1007/978-3-319-58457-7_21).

### Licensing
Licensed under the GNU General Public License v3.

Copyright 2017 by Andreas Schoknecht <andreas_schoknecht@web.de>

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
