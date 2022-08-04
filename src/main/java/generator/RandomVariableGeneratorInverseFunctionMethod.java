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

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class RandomVariableGeneratorInverseFunctionMethod extends ApplicationFrame {

    // Число участков разбиения
    private static final int k = 25;
    private static double interval = 1.5 / k;
    private static double[] dataPlot;
    private static double[] dataFunc;
    private static double mx;
    private static double dx;
    private static double a = 0.0;
    private static double b = 1.5;
    // Объем выборки
    private static int n;

    public RandomVariableGeneratorInverseFunctionMethod(String applicationTitle, String chartTitle) {
        super(applicationTitle);
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
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
        setContentPane(chartPanel);
    }

    public RandomVariableGeneratorInverseFunctionMethod(String chartTitle) {
        super("Статистическая функция распределения");
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "x",
                "f",
                createDatasetFunc(),
                PlotOrientation.VERTICAL,
                true, true, false);

        StandardChartTheme theme = (StandardChartTheme) org.jfree.chart.StandardChartTheme.createJFreeTheme();
        theme.setRegularFont(new Font(Font.SERIF, Font.PLAIN, 9));
        theme.apply(barChart);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 507));
        setContentPane(chartPanel);
    }

    //Вывод гистограммы частот
    private CategoryDataset createDatasetFreq() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataPlot.length; i++) {
            double inter = interval * i;
            dataset.addValue(dataPlot[i] * k, v, String.valueOf(inter));
        }

        return dataset;
    }

    //Вывод статистическая функции распределения
    private CategoryDataset createDatasetFunc() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataFunc.length; i++) {
            double inter = interval * i;
            dataset.addValue(dataFunc[i], v, String.valueOf(inter));
        }

        return dataset;
    }

    private static double[] rnd() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Количество случайных величин n: ");
        n = Integer.parseInt(br.readLine());

        double[] m = new double[n];

        Random random = new Random();
        double r;
        double x;
        for (int i = 0; i < n; i++) {
            r = random.nextDouble();
            if (r < 0.5) {
                x = r;
            } else {
                x = Math.sqrt((r - 0.5) / 2) + 1;
            }
            m[i] = x;
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
            double dp = Math.abs((double) (i + 1) / n - func(mas[i]));
            double dm = Math.abs(func(mas[i]) - (double) i / n);
            if (dp > dMax) {
                dMax = dp;
            } else if (dm > dMax) {
                dMax = dm;
            }
        }
        return dMax * Math.sqrt(n);
    }

    private static double func(double x) {
        if (x >= 0 && x < 0.5) {
            return x;
        } else if (x >= 0.5 && x < 1) {
            return 0.5;
        } else if (x >= 1 && x < 1.5) {
            return 2 * Math.pow(x - 1, 2) + 0.5;
        } else if (x >= 1.5) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void main(String[] args) throws IOException {
        double[] values = rnd();
        makeData(values, a, b);
        estimate(values);
        RandomVariableGeneratorInverseFunctionMethod chart = new RandomVariableGeneratorInverseFunctionMethod("Гистограмма частот",
                "Гистограмма частот");
        chart.pack();
        RefineryUtilities.positionFrameOnScreen(chart, 0.10, 0.5);
        chart.setVisible(true);

        RandomVariableGeneratorInverseFunctionMethod chart2 = new RandomVariableGeneratorInverseFunctionMethod(
                "Статистическая функция распределения");
        chart2.pack();
        RefineryUtilities.positionFrameOnScreen(chart2, 0.90, 0.5);
        chart2.setVisible(true);

        System.out.println("Математическое ожидание:" + mx);
        System.out.println("Дисперсия:" + dx);
        System.out.println("Критерий Колмогорова: " + colmogorov(values));
    }
}
