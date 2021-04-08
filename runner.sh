#! /bin/bash

# git configuration
git config user.email "41898282+github-actions[bot]@users.noreply.github.com"
git config user.name "GitHub Actions"

remote_repo="https://${GITHUB_ACTOR}:${INPUT_GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY}.git" # remote repo address

# go to testfile directory
cd testfiles || exit

# download the latest jar
wget -nv https://github.com/avinal/sbscl-sim/releases/download/v1.1/proto-sim-1.1.jar

for testd in * ; do
    if [ -d "$testd" ]
    then
        simargs=$(cut -d "," --output-delimiter=" " -f 1- < "${testd}/${testd}.csv")
        java -jar proto-sim-1.1.jar ${simargs[@]}
        cd "$testd"
        {
            printf "# Simulation result for %s\n## Input Details\n\n- Filename: %s\n- Step Size: %s\n- Time End: %s\n- Relative Tolerance: %s\n- Absolute Tolerance: %s\n## Output \n\nTable: [%s](%s)\n<p align=center><img src=%s></p>\n" "${testd}" ${simargs[@]} "$(find . -type f -name "*.xls")" "$(find . -type f -name "*.xls")" "$(find . -type f -name "*.svg")"
        } > README.md
        cd ..
        git stage "${testd}/"
        git commit -m "simulated ${testd}"
    fi
done

git remote add publisher "${remote_repo}"

git checkout master

# push to github
git push publisher master
