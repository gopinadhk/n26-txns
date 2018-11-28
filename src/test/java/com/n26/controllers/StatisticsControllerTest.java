package com.n26.controllers;

import static java.math.BigDecimal.ZERO;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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

import com.n26.statistics.IStatisticsRepository;
import com.n26.statistics.Statistics;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(controllers = StatisticsController.class)
@MockBean(value = IStatisticsRepository.class)
public class StatisticsControllerTest {
	private MockMvc mockMvc;
	@Autowired
	private StatisticsController statisticsController;
	@Autowired
	private IStatisticsRepository statisticsRepository;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
		reset(statisticsRepository);
	}

	@Test
	public void shouldReturnEmptyStatisticsOnEmptyRepo() throws Exception {
		doReturn(new Statistics(ZERO, ZERO, ZERO, ZERO, 0L)).when(statisticsRepository).getStatistics();
		mockMvc.perform(get("/statistics").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(
						"{\"sum\": \"0.00\", \"avg\": \"0.00\", \"max\":\"0.00\", \"min\":\"0.00\", \"count\":0}"));
	}

	@Test
	public void shouldReturnValidStatistics() throws Exception {
		doReturn(new Statistics(new BigDecimal("12.0"), new BigDecimal("4.0"), new BigDecimal("7.0"),
				new BigDecimal("1.0"), 3L)).when(statisticsRepository).getStatistics();
		mockMvc.perform(get("/statistics").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(
						"{\"sum\": \"12.00\", \"avg\": \"4.00\", \"max\":\"7.00\", \"min\":\"1.00\", \"count\":3}"));
	}

}
