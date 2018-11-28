package com.n26.transactions;

import com.n26.exceptions.TransactionExpiredException;
import com.n26.exceptions.TransactionInFutureException;

public interface ITransactionsRepository {

	void addTransaction(final Transaction transaction) throws TransactionExpiredException, TransactionInFutureException;

	void deleteTransactions();
}
