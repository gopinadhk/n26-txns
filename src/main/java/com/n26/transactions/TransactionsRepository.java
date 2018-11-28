package com.n26.transactions;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Repository;

import com.n26.exceptions.TransactionExpiredException;
import com.n26.exceptions.TransactionInFutureException;
import com.n26.statistics.IStatisticsRepository;
import com.n26.statistics.Statistics;

@Repository
public class TransactionsRepository implements ITransactionsRepository, IStatisticsRepository {

	private final Queue<Transaction> transactions = new ConcurrentLinkedQueue<>();

	@Override
	public void addTransaction(final Transaction transaction)
			throws TransactionInFutureException, TransactionExpiredException {
		final ZonedDateTime now = ZonedDateTime.now();
		if (transaction.getTimestamp().isAfter(now)) {
			throw new TransactionInFutureException();
		}
		final ZonedDateTime oneMinuteAgo = now.minusSeconds(60);
		if (transaction.getTimestamp().isBefore(oneMinuteAgo)) {
			throw new TransactionExpiredException();
		}
		transactions.add(transaction);
		cleanupOldTransactions();
	}

	@Override
	public void deleteTransactions() {
		transactions.clear();
	}

	@Override
	public Statistics getStatistics() {
		cleanupOldTransactions();
		final List<Transaction> transactions = getTransactions();

		long count = transactions.size();
		final BigDecimal sum = transactions.stream().map(Transaction::getAmount).reduce(ZERO, BigDecimal::add);
		final BigDecimal avg = (count == 0) ? ZERO : sum.divide(new BigDecimal(count), MathContext.DECIMAL32);
		final BigDecimal max = transactions.stream().map(Transaction::getAmount).max(BigDecimal::compareTo)
				.orElse(ZERO);
		final BigDecimal min = transactions.stream().map(Transaction::getAmount).min(BigDecimal::compareTo)
				.orElse(ZERO);
		return new Statistics(sum, avg, max, min, count);
	}

	ArrayList<Transaction> getTransactions() {
		return new ArrayList<>(this.transactions);
	}

	private void cleanupOldTransactions() {

		final ZonedDateTime oneMinuteAgo = ZonedDateTime.now().minusSeconds(60);
		transactions.removeIf(transaction -> transaction.getTimestamp().isBefore(oneMinuteAgo));

	}
}
