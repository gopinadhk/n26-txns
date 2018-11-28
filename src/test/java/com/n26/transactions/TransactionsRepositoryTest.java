package com.n26.transactions;

import static java.math.BigDecimal.ZERO;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.junit.Test;

import com.n26.exceptions.TransactionExpiredException;
import com.n26.exceptions.TransactionInFutureException;
import com.n26.statistics.Statistics;

public class TransactionsRepositoryTest {

	@Test(expected = TransactionExpiredException.class)
	public void shouldThrowExceptionForTooOldTransaction()
			throws TransactionExpiredException, TransactionInFutureException {
		final TransactionsRepository transactionsRepository = new TransactionsRepository();
		try {
			transactionsRepository
					.addTransaction(new Transaction(new BigDecimal("1.0"), ZonedDateTime.now().minusSeconds(120)));
		} catch (TransactionInFutureException e) {
			assertThat(transactionsRepository.getTransactions().size(), is(0));
			throw e;
		}
		fail("no exception thrown");
	}

	@Test(expected = TransactionInFutureException.class)
	public void shouldThrowExceptionForFutureTransaction()
			throws TransactionExpiredException, TransactionInFutureException {
		final TransactionsRepository transactionsRepository = new TransactionsRepository();
		try {
			transactionsRepository
					.addTransaction(new Transaction(new BigDecimal("1.0"), ZonedDateTime.now().plusSeconds(20)));
		} catch (TransactionInFutureException e) {
			assertThat(transactionsRepository.getTransactions().size(), is(0));
			throw e;
		}
		fail("no exception thrown");
	}

	@Test
	public void testValidTransaction() throws TransactionExpiredException, TransactionInFutureException {
		final TransactionsRepository transactionsRepository = new TransactionsRepository();
		Transaction transaction = new Transaction(new BigDecimal("1.0"), ZonedDateTime.now().minusSeconds(10));
		transactionsRepository.addTransaction(transaction);
		final ArrayList<Transaction> actualTransactions = transactionsRepository.getTransactions();
		assertThat(actualTransactions.size(), is(1));
		assertThat(actualTransactions, contains(transaction));
	}

	@Test
	public void deleteTransactions() throws TransactionExpiredException, TransactionInFutureException {
		final TransactionsRepository transactionsRepository = new TransactionsRepository();
		final Transaction transaction = new Transaction(new BigDecimal("1.0"), ZonedDateTime.now().minusSeconds(10));
		transactionsRepository.addTransaction(transaction);
		transactionsRepository.deleteTransactions();
		final ArrayList<Transaction> actualTransactions = transactionsRepository.getTransactions();
		assertThat(actualTransactions.size(), is(0));
	}

	@Test
	public void returnsEmptyStatistics() {
		final TransactionsRepository transactionsRepository = new TransactionsRepository();
		final Statistics expectedStats = new Statistics(ZERO, ZERO, ZERO, ZERO, 0L);
		final Statistics actualStatistics = transactionsRepository.getStatistics();
		assertThat(actualStatistics, equalTo(expectedStats));

	}

	@Test
	public void testCalculateStatistics() throws TransactionExpiredException, TransactionInFutureException {
		final TransactionsRepository transactionsRepository = new TransactionsRepository();
		transactionsRepository
				.addTransaction(new Transaction(new BigDecimal("4.0"), ZonedDateTime.now().minusSeconds(10)));
		transactionsRepository
				.addTransaction(new Transaction(new BigDecimal("1.0"), ZonedDateTime.now().minusSeconds(9)));
		transactionsRepository
				.addTransaction(new Transaction(new BigDecimal("7.0"), ZonedDateTime.now().minusSeconds(8)));

		final Statistics expectedStats = new Statistics(new BigDecimal("12.0"), new BigDecimal("4.0"),
				new BigDecimal("7.0"), new BigDecimal("1.0"), 3L);
		final Statistics actualStatistics = transactionsRepository.getStatistics();
		assertThat(actualStatistics, equalTo(expectedStats));

	}
}
