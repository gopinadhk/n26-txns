package com.n26.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.statistics.IStatisticsRepository;
import com.n26.statistics.Statistics;

@RestController
public class StatisticsController {
	private final IStatisticsRepository statisticsRepository;

	public StatisticsController(final IStatisticsRepository statisticsRepository) {
		this.statisticsRepository = statisticsRepository;
	}

	/**
	 * Get the statistics
	 * 
	 * @return
	 */
	@GetMapping(value = "/statistics")
	public ResponseEntity<Statistics> getStatistics() {
		return ResponseEntity.ok(statisticsRepository.getStatistics());
	}

}
