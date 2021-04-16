# SBML GitHub Actions Simulator
<p align=center><img src="https://github.com/avinal/sbscl-sim/actions/workflows/simulator.yml/badge.svg"></p> 

This is a prototype SBML simulator for GitHub Actions.

## How to use this simulator?

1. Fork this repo and clone to your system
    ```bash
        git clone https://github.com/username/sbscl-sim.git
    ```

2. Add your test files and config in [testfiles](/testfiles) directory as directed below.
    ```bash
        cd sbscl-sim/testfiles
        # create new test directory
        mkdir test2
        # config file must be name as directory name i.e test2.csv
        echo "test2/filename,stepsize,endtime,absTol,relTol" >> test2/test2.csv
        # add your input file to the directory
        cp testinput.xml test2/
    ```

3. Commit everything and push
    ```bash
        git add .
        git commit -m "new test added"
        git push origin master
    ```

4. Go to [https://github.com/username/sbscl-sim/actions](https://github.com/avinal/sbscl-sim/actions) and you should see the GitHub Actions executing

5. After simulation is complete you can check the test folder, the results will be uploaded automatically 

##  Example

[Simulation Example](./testfiles/test1)
