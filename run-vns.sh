#ant -buildfile build-vns.xml compile jar;

inputFilePath="in/EGFR_symm.G"
outDir="out/EGFR_symm-VNS"

ant -v -buildfile build-vns.xml -DinputFilePath="$inputFilePath" -DoutDir="$outDir" -Dtilim=10 run
