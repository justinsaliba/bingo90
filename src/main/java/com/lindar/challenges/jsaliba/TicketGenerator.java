package com.lindar.challenges.jsaliba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.IntStream;

@SpringBootApplication
public class TicketGenerator implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(TicketGenerator.class);

	public static void main(String[] args) {
		SpringApplication.run(TicketGenerator.class, args);
	}

	private int strips;
	private boolean verbose;
	private int generations;

	@Override
	public void run(ApplicationArguments args) {
		parseInput(args);

		for (int i = 0; i < generations; i++) {
			long now = System.currentTimeMillis();
			this.generateStrips(strips, verbose);
			long then = System.currentTimeMillis();
			LOGGER.info("Took {} ms to generate {} ticket strips", then - now, strips);
		}
	}

	private void parseInput(ApplicationArguments args) {
		if (args.containsOption("strips")) {
			strips = Integer.parseInt(args.getOptionValues("strips").get(0));
		}
		else strips = 10000;

		if (args.containsOption("generations")) {
			generations = Integer.parseInt(args.getOptionValues("generations").get(0));
		}
		else generations = 1;

		if (args.containsOption("verbose")) {
			if (args.getOptionValues("verbose").size() > 0) {
				if (args.getOptionValues("verbose").get(0).trim().equalsIgnoreCase("true")) {
					verbose = true;
				}
				else verbose = false;
			}
			else verbose = true;
		}
		else verbose = false;
	}

	private void generateStrips(int strips, boolean verbose) {
		IntStream.range(0, strips).forEach(stripNumber -> {
			var strip = new TicketStrip();

			if (verbose) {
				LOGGER.info("Strip {}\n{}", stripNumber+1, strip);
			}
		});
	}
}
