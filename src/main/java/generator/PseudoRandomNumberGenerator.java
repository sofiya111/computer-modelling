package generator;

import org.apache.commons.math3.special.Erf;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.util.Arrays;

public class PseudoRandomNumberGenerator extends ApplicationFrame {
    // Объем выборки
    private static final int n = 1600;
    // Число участков разбиения
    private static final int k = 18;
    // Вспомогательные данные
    private static final int a = 171;
    private static final int b = 177;
    private static final int c = 2;
    private static final int d = 30269;
    private static int y = 295;
    private static double interval = (double) 1 / k;
    // Количество первоначальных датчиков
    //private static final int k = 1;

    private static double[] dataPlot;
    private static double[] dataFunc;
    private static double mx;
    private static double dx;
    private static double countSeries;

    public PseudoRandomNumberGenerator(String applicationTitle, String chartTitle) {
        super(applicationTitle);
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "x",
                "f",
                createDatasetFreq(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(660, 367));
        setContentPane(chartPanel);
    }

    public PseudoRandomNumberGenerator(String chartTitle) {
        super("Статистическая функция распределения");
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "x",
                "f",
                createDatasetFunc(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(660, 367));
        setContentPane(chartPanel);
    }

    //Вывод гистограммы частот
    private CategoryDataset createDatasetFreq() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataPlot.length; i++) {
            dataset.addValue(dataPlot[i] * k, v, String.valueOf(interval * i));
        }

        return dataset;
    }

    //Вывод статистическая функции распределения
    private CategoryDataset createDatasetFunc() {
        final String v = "";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataFunc.length; i++) {
            dataset.addValue(dataFunc[i], v, String.valueOf(interval * i));
        }

        return dataset;
    }

    //Генератор случайных чисел
    private static double rnd() {
        double s = 0.0;
        int yN = (int) Math.abs(a * (y % b) - c * (double) y / b);
        y = yN;
        double x = (double) yN / d;
        s += x;

        return Math.abs(s - (int) s);
    }

    //формировать набор данных
    private static double[] generateData() {
        double[] values = new double[n];
        // получение случайных чисел
        for (int i = 0; i < n; i++) {
            values[i] = rnd();
        }

        return values;
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

    //Критерий Пирсона
    public static double pirson(double[] mas, int k) {
        double[] hits = calcHits(mas, k);
        long n = mas.length;
        double p = (double) 1 / k;
        double xi = 0;
        for (int i = 0; i < k; i++) {
            xi += Math.pow(hits[i] - n * p, 2) / (n * p);
        }
        return xi;
    }

    //Число попаданий случайной величины в интервалы
    private static double[] calcHits(double[] mas, int k) {
        double h = (double) 1 / k;
        double lBorder = 0;
        double rBorder = lBorder + h;
        double[] hits = new double[k];
        for (int i = 0; i < k; i++) {
            hits[i] = calcAmount(mas, lBorder, rBorder);
            lBorder += h;
            rBorder += h;
        }
        return hits;
    }

    //Количество попаданий случайной величины в интервал
    private static int calcAmount(double[] mas, double lBorder, double rBorder) {
        int count = 0;
        for (double el : mas) {
            if (lBorder <= el && el < rBorder) {
                count++;
            }
        }
        return count;
    }

    //Критерий Колмогорова
    public static double colmogorov(double[] mas) {
        int n = mas.length;
        double dMax = 0;
        Arrays.sort(mas);
        for (int i = 0; i < n; i++) {
            double dp = Math.abs((double) (i + 1) / n - calcFunc(mas[i]));
            double dm = Math.abs(calcFunc(mas[i]) - (double) i / n);
            if (dp > dMax) {
                dMax = dp;
            } else if (dm > dMax) {
                dMax = dm;
            }
        }
        return dMax * Math.sqrt(n);
    }

    //Функция распределения
    private static double calcFunc(double x) {
        if (x <= 0) {
            return 0;
        } else if (x > 0 && x < 1) {
            return x;
        } else {
            return 1;
        }
    }

    //Тест длины серий единиц
    public static double seriesOne(double[] x, int n, double p) {
        int k = 0;
        int count = 0;
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            if (x[i] < p) {
                y[i] = 0;
                count++;
            } else {
                y[i] = 1;

            }
        }
        for (int i = 1; i < n; i++) {
            if ((y[i - 1] == 0) && (y[i] == 1)) {
                k++;
            }
        }
        if (y[n - 1] == 0) {
            k++;
        }

        countSeries = k;
        return (double) count / k;
    }

    private static double checkLeft(double p) {
        double mz = (1 - p) / p + 1;
        double dz = p / Math.pow((1 - p), 2);
        return mz - 3.3 * Math.sqrt(dz / countSeries);

    }

    private static double checkRight(double p) {
        double mz = (1 - p) / p +1;
        double dz = p / Math.pow((1 - p), 2);
        return mz + 3.3 * Math.sqrt(dz / countSeries);

    }

    public static void main(String[] args) {
        double[] values = generateData();
        makeData(values, 0.0, 1.0);
        estimate(values);
        System.out.println("Критерий Пирсона: " + pirson(values, k));
        System.out.println("Тест длины серий нулей: " + seriesOne(values, n, 0.4));
        System.out.println("Левая граница: " + checkLeft(0.4));
        System.out.println("Правая граница: " + checkRight(0.4));
        System.out.println("Критерий Колмогорова: " + colmogorov(values));
    }

    private static double func(double x) {
         return function_laplas((x - mx) / dx);
    }

    public static double function_laplas(double x) {
        double multiplication; // подсчет значения аргумента в произведении
        double multiproduct;  //
        double previousMultiproduct = 1000; // сохраняет значение предыдущего произведения для подсчета величины ошибки
        double sum = 0.0; // значение суммы произведений
        for (int i = 0; i < 1000000; i++) {
            double product = 1.0;  //
            for (int k = 1; k <= i; k++) {
                multiplication = -x * x / (2 * k);
                product *= multiplication;
            }
            multiproduct = x * product / (Math.sqrt(2) * (2 * i + 1));
            if (Math.abs(multiproduct - previousMultiproduct) < 0.000000001) {
                break; // проверка на величину ошибки,разница 2х рядом стоящих слагаемых должна быть < определенной эпсилон
            }
            sum += multiproduct;
            previousMultiproduct = multiproduct;
        }

        return sum / Math.sqrt(Math.PI) + 0.5;
    }
}
