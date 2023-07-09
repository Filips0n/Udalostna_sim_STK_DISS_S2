package Statistics;

public class Arithmetic_Mean {
    int count = 0;
    double sum = 0;
    double sumSquared = 0;
    public void add(double value) {
        this.sum += value;
        this.sumSquared += Math.pow(value, 2);
        this.count++;
    }

    public double getMean() {
        double mean = sum/(double)count;
        if (Double.isNaN(mean)) {
            return 0;
        }
        return mean;
    }

    public void reset() {
        count = 0;
        sum = 0;
        sumSquared = 0;
    }

    private double getStandardDeviation() {
        //count = pocetReplikacii
//        return Math.sqrt(((1/(double)(count-1))*sumSquared)-Math.pow((((1/(double)(count-1))*sum)),2));
        return  Math.sqrt((sumSquared-(Math.pow(sum, 2)/(count)))/(count-1));
    }

    public double[] getConfidenceIntervalValues(String percent) {
        double[] values = new double[2];

        double tAlpha = getTAlpha(percent);
        values[0] = getMean()-((getStandardDeviation()*tAlpha)/Math.sqrt(count));
        values[1] = getMean()+((getStandardDeviation()*tAlpha)/Math.sqrt(count));

        return values;
    }

    private double getTAlpha(String percent) {
        switch (percent) {
            case "95":
                return 1.96;
            case "90":
                return 1.645;
        }
        return 0;
    }
}
