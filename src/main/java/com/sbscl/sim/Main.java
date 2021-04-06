package com.sbscl.sim;

public class Main {
    public static void main(String[] args) {
        String fileName = null;
        double stepSize = 0d;
        double timeEnd = 0d;
        double absTol = 0d;
        double relTol = 0d;
        try {

            fileName = args[0];
            stepSize = Double.parseDouble(args[1]);
            timeEnd = Double.parseDouble(args[2]);
            absTol = Double.parseDouble(args[3]);
            relTol = Double.parseDouble(args[4]);
        } catch (Exception e) {
            System.err.println(e);
        }

        SBMLSimulator sbmlSimulator = new SBMLSimulator(fileName, stepSize, timeEnd, absTol, relTol);
        try {
            sbmlSimulator.simulate();
        } catch (Exception e) {
            System.err.println(e);
        }
        sbmlSimulator.getModelExcel(fileName);
        sbmlSimulator.getModelSVG(fileName, "Output SVG");
    }
}