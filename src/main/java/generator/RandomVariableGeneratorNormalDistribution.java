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
import java.util.Arrays;
import java.util.Random;

public class RandomVariableGeneratorNormalDistribution extends ApplicationFrame {

    // Число участков разбиения
    private static final int k = 15;
    private static final double interval = 6.0 / k;
    private static double[] dataPlot;
    private static double[] dataFunc;
    private static double mx = -2;
    private static double dx = 0.9;
    private static double a = -5;
    private static double b = 1;
    // Объем выборки
    private static int n;

    public RandomVariableGeneratorNormalDistribution(String title) {
        super(title);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Гистограмма частот",
                "x",
                "f",
                createDatasetFreq(),
                PlotOrientation.VERTICAL,
                true, true, false);
        StandardChartTheme theme = (StandardChartTheme) org.jfree.chart.StandardChartTheme.createJFreeTheme();
        theme.setRegularFont(new Font(Font.SERIF, Font.PLAIN, 9));
        theme.apply(barChart);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 507));

        JFreeChart barChart2 = ChartFactory.createBarChart(
                "Статистическая функция распределения",
                "x",
                "f",
                createDatasetFunc(),
                PlotOrientation.VERTICAL,
                true, true, false);

        theme.apply(barChart2);
        ChartPanel chartPanel1 = new ChartPanel(barChart2);
        chartPanel1.setPreferredSize(new java.awt.Dimension(800, 507));

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
            double inter = a + interval * i;
            dataset.addValue(dataPlot[i] * k, v, String.valueOf(inter));
        }

        return dataset;
    }

    //Вывод статистическая функции распределения
    private CategoryDataset createDatasetFunc() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataFunc.length; i++) {
            double inter = a + interval * i;
            dataset.addValue(dataFunc[i], v, String.valueOf(inter));
        }

        return dataset;
    }

    private static double[] rndCenter() {
        double[] m = new double[n];
        Random random = new Random();
        double s;
        double r;
        for (int i = 0; i < n; i++) {
            s = 0;
            for (int j = 0; j < 12; j++) {
                r = random.nextDouble();
                s += r;
            }
            m[i] = s - 6;
            m[i] = mx + m[i] * Math.sqrt(dx);
        }

        return m;
    }

    private static double[] rndApprox() {
        Random random = new Random();
        double[] m = new double[n];
        double r;
        double k = Math.sqrt(8.0 / Math.PI);
        for (int i = 0; i < n; i++) {
            r = random.nextDouble();
            m[i] = Math.log((1 + r) / (1 - r)) / k;
            r = random.nextDouble();
            if (r < 0.5) {
                m[i] = -m[i];
            }
            m[i] = mx + m[i] * Math.sqrt(dx);
        }

        return m;
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

    //получить статистические оценки
    private static void estimate(double[] values) {
        double m2 = 0;
        mx = 0;
        for (int i = 0; i < n; i++) {
            mx += values[i];
            m2 += values[i] * values[i];
        }
        mx /= n;
        m2 /= n;
        dx = (m2 - mx * mx) * n / (n - 1);
    }

    //Критерий Колмогорова
    public static double colmogorov(double[] mas) {
        int n = mas.length;
        double dMax = 0;
        Arrays.sort(mas);
        for (int i = 0; i < n; i++) {
            double dp = Math.abs((double) (i + 1) / n - func(mas[i], mx, dx));
            double dm = Math.abs(func(mas[i], mx, dx) - (double) i / n);
            if (dp > dMax) {
                dMax = dp;
            } else if (dm > dMax) {
                dMax = dm;
            }
        }
        return dMax * Math.sqrt(n);
    }

    private static double func(double x, double m, double d) {
        double s = 0.0;
        double step;
        if (x > m) {
            step = (x - m) / 1000.0;
            for (double i = m; i <= x; i += step) {
                s += 1.0 / (d * Math.sqrt(2 * Math.PI)) * Math.exp(-(Math.pow(i - m, 2)) / (2 * Math.pow(d, 2)));
            }
            s *= step;
            s += 0.5;

        } else {
            step = (m - x) / 1000.0;
            for (double i = x; i <= m; i += step) {
                s += 1.0 / (d * Math.sqrt(2 * Math.PI)) * Math.exp(-(Math.pow(i - m, 2)) / (2 * Math.pow(d, 2)));
            }
            s *= step;
            s = 0.5 - s;
        }

        return s;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Количество случайных величин n: ");
        n = Integer.parseInt(br.readLine());

        double[] valuesCenter = rndCenter();
        makeData(valuesCenter, a, b);
        RandomVariableGeneratorNormalDistribution chartFreqCenter = new RandomVariableGeneratorNormalDistribution("Центральная предельная теорема");
        chartFreqCenter.pack();
        RefineryUtilities.positionFrameOnScreen(chartFreqCenter, 0.5, 0.0);
        chartFreqCenter.setVisible(true);

        double[] valuesApprox = rndApprox();
        makeData(valuesApprox, a, b);
        RandomVariableGeneratorNormalDistribution chartFreqApprox = new RandomVariableGeneratorNormalDistribution("Метод аппроксимации");
        chartFreqApprox.pack();
        RefineryUtilities.positionFrameOnScreen(chartFreqApprox, 0.5, 1.0);
        chartFreqApprox.setVisible(true);

        estimate(valuesCenter);
        System.out.println("Центральная предельная теорема");
        System.out.println("Математическое ожидание:" + mx);
        System.out.println("Дисперсия:" + dx);
        System.out.println("Критерий Колмогорова: " + colmogorov(valuesCenter));
        estimate(valuesApprox);
        System.out.println("\nМетод аппроксимации");
        System.out.println("Математическое ожидание:" + mx);
        System.out.println("Дисперсия:" + dx);
        System.out.println("Критерий Колмогорова: " + colmogorov(valuesApprox));
    }
}
