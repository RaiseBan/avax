import java.util.List;

public class StatisticalFunctions {
    public static Float calculateMean(List<Float> values) {
        float sum = 0.0F;
        for (float value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    // Функция для вычисления исправленного стандартного отклонения
    public static Float calculateCorrectedStdDev(List<Float> values) {
        float mean = calculateMean(values);
        float sumOfSquares = 0.0F;
        for (double value : values) {
            sumOfSquares += Math.pow(value - mean, 2);
        }
        return (float) Math.sqrt(sumOfSquares / (values.size() - 1));
    }
}
