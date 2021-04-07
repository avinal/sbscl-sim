#! /bin/bash

# git configuration
git config user.email "41898282+github-actions[bot]@users.noreply.github.com"
git config user.name "GitHub Actions"

remote_repo="https://${GITHUB_ACTOR}:${INPUT_GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY}.git" # remote repo address

# go to testfile directory
cd testfiles || exit

# download the latest jar
wget -nv https://github.com/avinal/sbscl-sim/releases/download/v1.0-SNAPSHOT/proto-sim-1.0-SNAPSHOT.jar

for testd in * ; do
    if [ -d "$testd" ]
    then
        simargs=$(cat "${testd}/${testd}.txt")
        java -jar proto-sim-1.0-SNAPSHOT.jar ${simargs}
        mkdir -p "${testd}"/output
        mv -- *.svg *.xls "${testd}"/output/
        git stage "${testd}"/output/*
        git commit -m "simulated ${testd}"
    fi
done

git remote add publisher "${remote_repo}"

git checkout ${INPUT_BRANCH}

# push to github
git push publisher ${INPUT_BRANCH}
