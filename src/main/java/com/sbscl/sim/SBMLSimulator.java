package com.sbscl.sim;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.math.ode.DerivativeException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.svg.SVGGraphics2D;
import org.jfree.svg.SVGUtils;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.AdaptiveStepsizeIntegrator;
import org.simulator.math.odes.DESSolver;
import org.simulator.math.odes.MultiTable;
import org.simulator.math.odes.MultiTable.Block.Column;
import org.simulator.math.odes.RosenbrockSolver;
import org.simulator.sbml.EquationSystem;
import org.simulator.sbml.SBMLinterpreter;

public class SBMLSimulator implements PropertyChangeListener {
    private String fileName = null;
    private double stepSize = 0d;
    private double timeEnd = 0d;
    private double absTol = 0d;
    private double relTol = 0d;
    private MultiTable solution;
    private static final double TOLERANCE_FACTOR = 1E-3;
    private static final Logger logger = Logger.getLogger(SBMLSimulator.class.getName());
    private static final String RESULT = "result";

    public SBMLSimulator(String fileName, double stepSize, double timeEnd, double absTol, double relTol) {
        this.fileName = fileName;
        this.absTol = TOLERANCE_FACTOR * absTol;
        this.relTol = TOLERANCE_FACTOR * relTol;
        this.timeEnd = timeEnd;
        this.stepSize = stepSize;
    }

    public SBMLSimulator() {
    }

    public void simulate()
            throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {

        SBMLDocument sbmlDocument = (new SBMLReader()).readSBML(fileName);
        Model model = sbmlDocument.getModel();
        DESSolver desSolver = new RosenbrockSolver();
        desSolver.setStepSize(stepSize);
        EquationSystem equationSystem = new SBMLinterpreter(model);

        if (desSolver instanceof AbstractDESSolver) {
            desSolver.setIncludeIntermediates(false);
        }

        if (desSolver instanceof AdaptiveStepsizeIntegrator) {
            ((AdaptiveStepsizeIntegrator) desSolver).setAbsTol(absTol);
            ((AdaptiveStepsizeIntegrator) desSolver).setRelTol(relTol);
        }

        solution = desSolver.solve(equationSystem, equationSystem.getInitialValues(), 0d, timeEnd, new SBMLSimulator());
    }

    public void getModelExcel(String outFile) {
        if (solution == null) {
            return;
        }
        JTable jTable = new JTable(solution);
        try {
            TableModel tableModel = jTable.getModel();
            FileWriter fileWriter = new FileWriter(outFile + ".xls");

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                fileWriter.write(tableModel.getColumnName(i) + "\t");
            }

            fileWriter.write("\n");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    fileWriter.write(tableModel.getValueAt(i, j).toString() + "\t");
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public void getModelSVG(String outFile, String title) {
        if (solution == null) {
            return;
        }

        JFreeChart jFreeChart = ChartFactory.createLineChart(title, "time", "concentration (nM)", createDataset(),
                PlotOrientation.VERTICAL, true, true, false);

        // ChartPanel chartPanel = new ChartPanel(jFreeChart);
        // chartPanel.setPreferredSize(new java.awt.Dimension(1920, 1080));
        // PlotMultiTable plotMultiTable = new PlotMultiTable(solution, title);
        // plotMultiTable.pack();
        // plotMultiTable.setSize(1920, 1080);
        // JComponent jComponent = (JComponent) plotMultiTable.getComponent(0);
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(1920, 1080);
        jFreeChart.draw(svgGraphics2D, new java.awt.Rectangle(0, 0, 1920, 1080));
        try {
            SVGUtils.writeToSVG(new File(outFile + ".svg"), svgGraphics2D.getSVGElement());
        } catch (Exception e) {
            System.err.println(e);
        }

        // jComponent.paint(svgGraphics2D);
        // try {
        // SVGUtils.writeToSVG(new File(outFile + ".svg"),
        // svgGraphics2D.getSVGElement());
        // } catch (IOException e) {
        // System.err.println(e);
        // }
        // plotMultiTable.dispose();

    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (int i = 1; i < solution.getColumnCount(); i++) {
            Column col = solution.getColumn(i);
            int time_step = 0;
            for (Iterator<Double> iter = col.iterator(); iter.hasNext(); time_step++) {
                data.addValue(iter.next().doubleValue(), col.getColumnName(),
                        String.valueOf(solution.getTimePoint(time_step)));
            }
        }
        return data;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(RESULT)) {
            logger.info(Arrays.toString((double[]) propertyChangeEvent.getNewValue()));
        } else {
            logger.info(propertyChangeEvent.getNewValue().toString());
        }
    }
}