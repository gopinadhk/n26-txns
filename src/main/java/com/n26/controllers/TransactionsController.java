package com.n26.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.n26.exceptions.TransactionExpiredException;
import com.n26.exceptions.TransactionInFutureException;
import com.n26.transactions.ITransactionsRepository;
import com.n26.transactions.Transaction;

@RestController
public class TransactionsController {
	private static final Logger LOG = LoggerFactory.getLogger(TransactionsController.class);
	private final ITransactionsRepository transactionsRepository;

	@Autowired
	public TransactionsController(final ITransactionsRepository transactionsRepository) {
		this.transactionsRepository = transactionsRepository;
	}

	/**
	 * Add a new transaction
	 * 
	 * @param transaction
	 * @return
	 */
	@RequestMapping(value = "/transactions", method = RequestMethod.POST)
	public ResponseEntity<?> addTransaction(@RequestBody @Valid final Transaction transaction) {
		System.out.println("========transaction===="+transaction);
		LOG.debug("added transaction {}", transaction);
		try {
			transactionsRepository.addTransaction(transaction);
			System.out.println("========Created===="+CREATED);
			return new ResponseEntity(CREATED);
		} catch (TransactionExpiredException expired) {
			System.out.println("========NO_CONTENT===="+NO_CONTENT);
			return new ResponseEntity(NO_CONTENT);
		} catch (TransactionInFutureException future) {
			System.out.println("========UNPROCESSABLE_ENTITY===="+UNPROCESSABLE_ENTITY);
			return new ResponseEntity(UNPROCESSABLE_ENTITY);
		} catch(Exception e) {
			System.out.println("========CREATED===="+CREATED);
			return new ResponseEntity(CREATED);
		}
	}

	/**
	 * Used to delete all the transactions
	 */
	@RequestMapping(value = "/transactions", method = RequestMethod.DELETE)
	@ResponseStatus(NO_CONTENT)
	public void deleteTransactions() {
		transactionsRepository.deleteTransactions();
	}

	@ExceptionHandler
	void handleHttpMessageConversionException(final HttpMessageConversionException e, HttpServletResponse response)
			throws IOException {
		// I'd argue not parsable fields are of type bad request as well, but following
		// specification here.
		// In the office I'd ask for clarification and perhaps find a cleaner and less
		// specific solution.
		// until then its this
		final Throwable cause = e.getCause();
		if ((cause instanceof InvalidFormatException || cause instanceof DateTimeParseException)) {
			response.sendError(HttpStatus.UNPROCESSABLE_ENTITY.value());
		} else {
			response.sendError(HttpStatus.BAD_REQUEST.value());
		}
	}

}
