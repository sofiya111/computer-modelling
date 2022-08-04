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

public class RandomVariableGeneratorWeibullExp extends ApplicationFrame {

    // Число участков разбиения
    private static final int k = 15;
    private static final double interval = 4.0 / k;
    private static double left = 0;
    private static double right = 4;
    private static final double[] p = {0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7};

    private static double[] dataPlot;
    private static double[] dataFunc;
    private static double mx;
    private static double dx;
    private static double c;
    private static double b;
    private static double lambda;

    // Объем выборки
    private static int n;

    public RandomVariableGeneratorWeibullExp(String title) {
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
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 300));

        JFreeChart barChart2 = ChartFactory.createBarChart(
                "Статистическая функция распределения",
                "x",
                "f",
                createDatasetFunc(),
                PlotOrientation.VERTICAL,
                true, true, false);

        theme.apply(barChart2);
        ChartPanel chartPanel1 = new ChartPanel(barChart2);
        chartPanel1.setPreferredSize(new java.awt.Dimension(600, 300));

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

    private static double[] rndWeibull() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Параметры распределения для распределения Вейбулла\nс: ");
        c = Double.parseDouble(br.readLine());
        System.out.println("b: ");
        b = Double.parseDouble(br.readLine());
        Random random = new Random();
        double r;
        double[] m = new double[n];
        for (int i = 0; i < n; i++) {
            r = random.nextDouble();
            m[i] = b * Math.pow(-Math.log(r), 1 / c);
        }

        return m;
    }

    private static double[] rndLog() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Параметры распределения для экспоненциального распределения\nlambda: ");
        lambda = Double.parseDouble(br.readLine());
        Random random = new Random();
        double r;
        double[] m = new double[n];
        for (int i = 0; i < n; i++) {
            r = random.nextDouble();
            m[i] = -Math.log(r) / lambda;
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
    private static void estimateWeibull() {
        mx = b / c * gamma(1 / c);
        dx = Math.pow(b, 2) / c * (2 * gamma(2 / c) - 1 / c * Math.pow(gamma(1 / c), 2));
    }

    //получить статистические оценки
    private static void estimateExp() {
        mx = 1 / lambda;
        dx = 1 / Math.pow(lambda, 2);
    }

    //вычисление гамма-функции приближение Ланцоша
    public static double gamma(double x) {
        int g = 7;
        if (x < 0.5) {
            return Math.PI / (Math.sin(Math.PI * x) * gamma(1 - x));
        }
        x -= 1;
        double a = p[0];
        double t = x + g + 0.5;
        for (int i = 1; i < p.length; i++) {
            a += p[i] / (x + i);
        }

        return Math.sqrt(2 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;
    }

    //Критерий Колмогорова
    public static double colmogorov(double[] mas, Law law) {
        int n = mas.length;
        double dMax = 0;
        Arrays.sort(mas);
        for (int i = 0; i < n; i++) {
            double dp = Math.abs((double) (i + 1) / n - getLaw(mas[i], law));
            double dm = Math.abs(getLaw(mas[i], law) - (double) i / n);
            if (dp > dMax) {
                dMax = dp;
            } else if (dm > dMax) {
                dMax = dm;
            }
        }
        return dMax * Math.sqrt(n);
    }

    private static double getLaw(double x, Law law) {
        if (law == Law.WEIBULL) {
            return funcWeibull(x);
        }
        if (law == Law.EXP) {
            return funcLog(x);
        }

        return x;
    }

    private static double funcWeibull(double x) {
        return 1 - Math.exp(-Math.pow(x / b, c));
    }

    private static double funcLog(double x) {
        if (x >= 0) {
            return 1 - Math.exp(-lambda * x);
        } else {
            return 0;
        }
    }

    private static void getDiagram(double[] values, String title, double verticalPercent) {
        makeData(values, left, right);
        RandomVariableGeneratorWeibullExp chartFreqWeibull = new RandomVariableGeneratorWeibullExp(title);
        chartFreqWeibull.pack();
        RefineryUtilities.positionFrameOnScreen(chartFreqWeibull, 0.5, verticalPercent);
        chartFreqWeibull.setVisible(true);
    }

    private static void getEstimate(double[] values, Law law) {
        System.out.println("Математическое ожидание:" + mx);
        System.out.println("Дисперсия:" + dx);
        System.out.println("Критерий Колмогорова: " + colmogorov(values, law));
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Количество случайных величин n: ");
        n = Integer.parseInt(br.readLine());

        double[] valuesWeibull = rndWeibull();
        double[] valuesLog = rndLog();
        getDiagram(valuesWeibull, "Распределение Вейбулла", 0);
        getDiagram(valuesLog, "Экспоненциального распределение", 0.5);

        estimateWeibull();
        System.out.println("Распределение Вейбулла");
        getEstimate(valuesWeibull, Law.WEIBULL);
        estimateExp();
        System.out.println("\nЭкспоненциального распределение");
        getEstimate(valuesLog, Law.EXP);
    }
}
