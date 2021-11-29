#ant -buildfile build-ts.xml compile jar;

inputFilePath="in/EGFR_symm.G"
outDir="out/EGFR_symm-TS"

ant -v -buildfile build-ts.xml -DinputFilePath="$inputFilePath" -DoutDir="$outDir" -Dtilim=10 run
