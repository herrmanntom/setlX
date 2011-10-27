#!/bin/bash

tmpFile=$(mktemp)

manualTestFiles=""

while IFS= read path
  do
    filename="${path#example_setlX_code/}"

    readCount=$(grep -c "read()" $path)
    getCount=$(grep -c "get()" $path)

    if [ $readCount -gt 0 -o $getCount -gt 0 -o $filename == "converted_Setl2_code/other_programs/2.18_A_n_infinite_loop.stlx" ]
      then
        manualTestFiles="$manualTestFiles$filename, "
        continue
    fi

    echo -n "executing file '$filename' ..."

    # execute with setlX
    cd interpreter; ./setlX --predictableRandom ../$path > $tmpFile; cd ..

    diff $path.reference $tmpFile > diff.result
    if [ $? -eq 1 ]
      then
        echo " failed ... (see diff.result)"
        break
    else
        echo -e " successful!\n"
    fi

done < <( find -L example_setlX_code -name "*.stlx" )

if [ ! -s diff.result ]
  then
    rm -f diff.result
fi

manualTestFiles="${manualTestFiles%, }"
echo "The following files need user input and must be tested manually: $manualTestFiles"

