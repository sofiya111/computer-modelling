package generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class ModelingMethodMonteCarlo {

    private static final double t = 1.96; // значение квартиля для надёжности b = 0,95

    private static void simulationResult(double p1, double p2, double p3, int n) {
        Random random = new Random();
        int nA = 0;
        int nB = 0;
        int nC = 0;
        double r;
        for (int i = 0; i < n; i++) {
            r = random.nextDouble();
            if (r < p1) {
                nA++;
            } else {
                r = random.nextDouble();
                if (r < p2) {
                    nB++;
                } else {
                    r = random.nextDouble();
                    if (r < p3) {
                        nA++;
                    } else {
                        nC++;
                    }
                }
            }
        }
        double pA = (double) nA / n;
        double pB = (double) nB / n;
        double pC = 1 - (double) nC / n;
        double[] interval = getInterval(pA, n);
        System.out.println("Искомые вероятности");
        System.out.println("P(A) = " + pA);
        System.out.println("I = [" + interval[0] + ";" + interval[1] + "]");
        System.out.println("P(B) = " + pB);
        interval = getInterval(pB, n);
        System.out.println("I = [" + interval[0] + ";" + interval[1] + "]");
        System.out.println("P(C) = " + pC);
        interval = getInterval(pC, n);
        System.out.println("I = [" + interval[0] + ";" + interval[1] + "]");
    }

    private static double[] getInterval(double p, double n) {
        double q = Math.sqrt((p * (1 - p)) / n);
        return new double[]{p - q * t, p + q * t};
    }

    private static void analyticalCalc(double p1, double p2, double p3) {
        double pA = p1 + (1 - p1) * (1 - p2) * p3;
        double pB = (1 - p1) * p2;
        double pC = 1 - (1 - p1) * (1 - p2) * (1 - p3);
        System.out.println("Аналитический расчет");
        System.out.println("P(A) = " + pA);
        System.out.println("P(B) = " + pB);
        System.out.println("P(С) = " + pC);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Вероятность того, что истребитель собьет бомбардировщик при первом попадании\nр1: ");
        double p1 = Double.parseDouble(br.readLine());
        System.out.println("Вероятность того, что бомбардировщик собьет истребитель при попадании\nр2: ");
        double p2 = Double.parseDouble(br.readLine());
        System.out.println("Вероятность того, что истребитель собьет бомбардировщик при втором попадании\nр3: ");
        double p3 = Double.parseDouble(br.readLine());
        System.out.println("Количество экспериментов N: ");
        int n = Integer.parseInt(br.readLine());

        simulationResult(p1, p2, p3, n);
        analyticalCalc(p1, p2, p3);
    }
}
