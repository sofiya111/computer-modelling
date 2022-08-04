package generator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.JPanel;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class SimulationRandomWandering extends ApplicationFrame {

    public static final int M = 8;
    private static final int k = 15;
    private static final double interval = 30.0 / k;
    private static double left = 0;
    private static double right = 30;
    private static double[] dataPlot;
    private static double[] dataFunc;
    private static int n;
    private static double o;
    private static double mu;

    public SimulationRandomWandering(String title) {
        super(title);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Гистограмма частот",
                "x",
                "f",
                createDatasetFreq(),
                PlotOrientation.VERTICAL,
                true, true, false);
        StandardChartTheme theme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
        theme.setRegularFont(new Font(Font.SERIF, Font.PLAIN, 9));
        theme.apply(barChart);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        JFreeChart barChart2 = ChartFactory.createBarChart(
                "Статистическая функция распределения",
                "x",
                "f",
                createDatasetFunc(),
                PlotOrientation.VERTICAL,
                true, true, false);

        theme.apply(barChart2);
        ChartPanel chartPanel1 = new ChartPanel(barChart2);
        chartPanel1.setPreferredSize(new java.awt.Dimension(800, 500));

        JPanel panel = new JPanel();
        panel.add(chartPanel);
        panel.add(chartPanel1);
        panel.add(chartPanel1);
        setContentPane(panel);
    }

    //Вывод гистограммы частот
    private CategoryDataset createDatasetFreq() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < dataPlot.length; i++) {
            double inter = left + interval * i;
            dataset.addValue(dataPlot[i] * k, v, String.valueOf(inter));
        }

        return dataset;
    }

    //Вывод статистическая функции распределения
    private CategoryDataset createDatasetFunc() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataFunc.length; i++) {
            double inter = left + interval * i;
            dataset.addValue(dataFunc[i], v, String.valueOf(inter));
        }

        return dataset;
    }

    //Получить массивы плотности и функции распределения
    private static void makeData(double[] values, double min, double max) {
        double delta = (max - min) / k;
        dataPlot = new double[k];
        dataFunc = new double[k];
        for (int i = 0; i < n; i++) {
            int j = (int) ((values[i] - min) / delta);
            if (j >= k) {
                j = k - 1;
            } else if (j < 0) {
                j = 0;
            }
            dataPlot[j]++;
        }
        for (int i = 0; i < k; i++) {
            dataPlot[i] /= n;
        }
        dataFunc[0] = dataPlot[0];
        for (int i = 1; i < k; i++) {
            dataFunc[i] = dataFunc[i - 1] + dataPlot[i];
        }
    }

    private static double[] averageDistance(int n) {
        Random random = new Random();
        double[] values = new double[n];
        double l;
        for (int i = 0; i < n; i++) {
            int x = 0;
            int y = 0;
            l = 0;
            for (int j = 0; j < M; j++) {
                double r = random.nextDouble();
                if (r < 0.25) {
                    x++;
                } else if (r < 0.5) {
                    x--;
                } else if (r < 0.75) {
                    y--;
                } else {
                    y++;
                }
                l += Math.sqrt(x * x + y * y);
            }
            values[i] = l;
        }

        return values;
    }

    private static void getDiagram(double[] values, String title, double verticalPercent) {
        makeData(values, left, right);
        SimulationRandomWandering chartFreqWeibull = new SimulationRandomWandering(title);
        chartFreqWeibull.pack();
        RefineryUtilities.positionFrameOnScreen(chartFreqWeibull, 0.5, verticalPercent);
        chartFreqWeibull.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите количество пчел\nN = ");
        n = Integer.parseInt(br.readLine());
        double[] values = averageDistance(n);
        getDiagram(values, "Двумерное случайное блуждание", 0.5);
    }
}
