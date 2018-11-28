package com.n26.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Statistics {

	private final BigDecimal sum;
	private final BigDecimal avg;
	private final BigDecimal max;
	private final BigDecimal min;
	private final long count;

	public Statistics(final BigDecimal sum, final BigDecimal avg, final BigDecimal max, final BigDecimal min,
			final long count) {
		this.sum = sum.setScale(2, RoundingMode.HALF_UP);
		this.avg = avg.setScale(2, RoundingMode.HALF_UP);
		this.max = max.setScale(2, RoundingMode.HALF_UP);
		this.min = min.setScale(2, RoundingMode.HALF_UP);
		this.count = count;
	}

	public String getSum() {
		return sum.toPlainString();
	}

	public String getAvg() {
		return avg.toPlainString();
	}

	public String getMax() {
		return max.toPlainString();
	}

	public String getMin() {
		return min.toPlainString();
	}

	public long getCount() {
		return count;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Statistics that = (Statistics) o;
		return count == that.count && Objects.equals(sum, that.sum) && Objects.equals(avg, that.avg)
				&& Objects.equals(max, that.max) && Objects.equals(min, that.min);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sum, avg, max, min, count);
	}

	@Override
	public String toString() {
		return "Statistics [sum=" + getSum() + ", avg=" + getAvg() + ", max=" + getMax() + ", min=" + getMin()
				+ ", count=" + getCount() + "]";
	}

}
