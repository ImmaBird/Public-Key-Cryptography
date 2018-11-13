# solve for paths
$startDir=pwd
$bin=$startDir.toString()+"/bin"
$inFiles=$startDir.toString()+"/TestFiles"
$outFiles=$startDir.toString()+"/TestOutput"
$outFilesEnc=$startDir.toString()+"/TestOutputEnc"

# create the 10 byte test file
cd $bin
java A $inFiles/A.test

# run encrypt on test files
java RSA_WIT $inFiles/a.PNG $outFilesEnc/a.PNG.enc $outFiles/a.PNG
java RSA_WIT $inFiles/A.test $outFilesEnc/A.test.enc $outFiles/A.test
java RSA_WIT $inFiles/pic.JPG $outFilesEnc/pic.JPG.enc $outFiles/pic.JPG
java RSA_WIT $inFiles/pic.PNG $outFilesEnc/pic.PNG.enc $outFiles/pic.PNG
java RSA_WIT $inFiles/plaintext.txt $outFilesEnc/plaintext.txt.enc $outFiles/plaintext.txt
echo ""

# check to make sure the output is the same as the input
echo "diffs-----------------------------------------------------------------------"
echo ""
echo "a.PNG"
diff $inFiles/a.PNG $outFiles/a.PNG
echo ""
echo "A.test"
diff $inFiles/A.test $outFiles/A.test
echo ""
echo "pic.JPG"
diff $inFiles/pic.JPG $outFiles/pic.JPG
echo ""
echo "pic.PNG"
diff $inFiles/pic.PNG $outFiles/pic.PNG
echo ""
echo "plaintext.txt"
diff $inFiles/plaintext.txt $outFiles/plaintext.txt

# cd back to start directory
cd $startDir