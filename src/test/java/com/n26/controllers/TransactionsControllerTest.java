package com.n26.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.n26.exceptions.TransactionExpiredException;
import com.n26.exceptions.TransactionInFutureException;
import com.n26.transactions.ITransactionsRepository;
import com.n26.transactions.Transaction;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(controllers = TransactionsController.class)
@MockBean(value = ITransactionsRepository.class)
public class TransactionsControllerTest {
	private MockMvc mockMvc;
	@Autowired
	private TransactionsController transactionsController;
	@Autowired
	private ITransactionsRepository transactionsRepository;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(transactionsController).build();
		reset(transactionsRepository);
	}

	@Test
	public void shouldReturnCreatedOnSuccess() throws Exception {

		mockMvc.perform(
				post("/transactions").content("{\"amount\":\"12.3343\",\"timestamp\":\"2018-11-11T14:33:09.159Z\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		verify(transactionsRepository, times(1)).addTransaction(any(Transaction.class));
		verifyNoMoreInteractions(transactionsRepository);
	}

	@Test
	public void shouldReturnNoContentWhenTransactionIsTooOld() throws Exception {
		doThrow(new TransactionExpiredException()).when(transactionsRepository).addTransaction(any(Transaction.class));
		mockMvc.perform(
				post("/transactions").content("{\"amount\":\"12.3343\",\"timestamp\":\"2018-11-11T13:45:08.149Z\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		verify(transactionsRepository, times(1)).addTransaction(any(Transaction.class));
		verifyNoMoreInteractions(transactionsRepository);

	}

	@Test
	public void shouldReturnUnprocessableEntityOnFutureDate() throws Exception {
		doThrow(new TransactionInFutureException()).when(transactionsRepository).addTransaction(any(Transaction.class));
		mockMvc.perform(
				post("/transactions").content("{\"amount\":\"27.96\",\"timestamp\":\"2018-11-30T14:35:09.149Z\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
		verify(transactionsRepository, times(1)).addTransaction(any(Transaction.class));
		verifyNoMoreInteractions(transactionsRepository);

	}

	@Test
	public void deleteTransactions() throws Exception {
		mockMvc.perform(delete("/transactions").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		verify(transactionsRepository, times(1)).deleteTransactions();
		verifyNoMoreInteractions(transactionsRepository);
	}

	@Test
	public void shouldReturnUnprocessableEntity_InvalidTimeformat() throws Exception {
		mockMvc.perform(post("/transactions").content("{\"timestamp\":\"10/31/2018 10:11 PM\",\"amount\":\"342.01\"}")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity());
		verify(transactionsRepository, times(0)).addTransaction(any(Transaction.class));
		verifyNoMoreInteractions(transactionsRepository);

	}

	@Test
	public void shouldReturnUnprocessableEntity_BadAmount() throws Exception {
		mockMvc.perform(
				post("/transactions").content("{\"amount\":\"147-96\",\"timestamp\":\"2018-11-11T13:45:09.149Z\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
		verify(transactionsRepository, times(0)).addTransaction(any(Transaction.class));
		verifyNoMoreInteractions(transactionsRepository);
	}

	@Test
	public void shouldReturnBadRequestOnInvalidJson() throws Exception {
		mockMvc.perform(post("/transactions").content("Hello world!").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verify(transactionsRepository, times(0)).addTransaction(any(Transaction.class));
		verifyNoMoreInteractions(transactionsRepository);

	}

}
