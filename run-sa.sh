#ant -buildfile build-sa.xml compile jar;

inputFilePath="in/EGFR_symm.G"
outDir="out/EGFR_symm-SA"

ant -v -buildfile build-sa.xml -DinputFilePath="$inputFilePath" -DoutDir="$outDir" -Dtilim=10 run
