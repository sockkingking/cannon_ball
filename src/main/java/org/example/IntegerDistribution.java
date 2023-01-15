package org.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class IntegerDistribution {
    public static class Bound {
        private int num;
        private int min;
        private int max;
        private int prize;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bound bound = (Bound) o;

            if (num != bound.num) return false;
            if (min != bound.min) return false;
            if (max != bound.max) return false;
            return prize == bound.prize;
        }

        @Override
        public int hashCode() {
            int result = num;
            result = 31 * result + min;
            result = 31 * result + max;
            result = 31 * result + prize;
            return result;
        }

        public Bound(int num, int prize) {
            this.num = num;
            this.prize = prize;
        }

        public Bound(int num, int min, int max, int prize) {
            this.num = num;
            this.min = min;
            this.max = max;
            this.prize = prize;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getPrize() {
            return prize;
        }

        public void setPrize(int prize) {
            this.prize = prize;
        }
    }

    private static final String COMMA = ",";
    private static final String EQUAL = "=";
    private int boundNumber = 100;
    private List<Bound> bounds = new LinkedList<>();
    private final String distribution;
    public IntegerDistribution(String distribution) {
        this.distribution = distribution;
    }

    public IntegerDistribution(int boundNumber, String distribution, List<Bound> bounds) {
        this.boundNumber = boundNumber;
        this.distribution = distribution;
        this.bounds = bounds;
    }

    public List<Bound> getBounds() {
        return bounds;
    }

    public int getBoundNumber() {
        return boundNumber;
    }

    public String getDistribution() {
        return distribution;
    }

    public void parseDistributionConfig() {
        if (isInvalidDistribution(this.distribution)) {
            throw new IllegalArgumentException("invalid distribution config");
        }
        /// check if there is a percentage lower than 1%
        resetRandomNumber(this.distribution);

        AtomicInteger index = new AtomicInteger(0);
        Arrays.stream(this.distribution.split(COMMA))
                .map(s -> {
                    String[] distributionAndValue = s.split(EQUAL);
                    int num = (int) (Float.parseFloat(distributionAndValue[0]) * this.boundNumber);
                    int prize = Integer.parseInt(distributionAndValue[1]);
                    return new Bound(num, prize);
                })
                .sorted(Comparator.comparing(Bound::getNum))
                .forEach(bound -> {
                    bound.setMin(index.get() + 1);
                    bound.setMax(index.addAndGet(bound.getNum()));
                    this.bounds.add(bound);
                });
    }

    private boolean isInvalidDistribution(String distribution) {
        return distribution == null || distribution.isBlank();
    }

    private void resetRandomNumber(String distribution) {
        Optional<Double> minOpt = Arrays.stream(distribution.split(COMMA))
                .map(s -> {
                    String[] distributionAndValue = s.trim().split(EQUAL);
                    return Double.parseDouble(distributionAndValue[0].trim());
                }).min(Comparator.comparing(Double::doubleValue));

        if (minOpt.isEmpty()) {
            throw new IllegalArgumentException("invalid distribution config");
        }

        double min = minOpt.get();
        int count = 1;
        while (min < 1) {
            min *= 10;
            count *= 10;
        }

        if (count > 1) {
            this.boundNumber = count;
        }
    }

    public Integer getRandom() {
        Random random = new Random();
        int percent = random.ints(1, this.boundNumber)
                .findFirst()
                .orElse(0);

        for (Bound bound : bounds) {
            if (bound.getMax() >= percent && bound.getMin() <= percent) {
                return bound.getPrize();
            }
        }
        return 0;
    }
}
