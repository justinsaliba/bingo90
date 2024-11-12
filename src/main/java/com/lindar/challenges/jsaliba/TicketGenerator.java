package com.lindar.challenges.jsaliba;

import com.lindar.challenges.jsaliba.beans.TicketStrip;
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

	@Override
	public void run(ApplicationArguments args) {
		int strips = args.containsOption("strips") ? Integer.parseInt(args.getOptionValues("strips").get(0)) : 1;
		boolean verbose = args.containsOption("verbose") ? Boolean.parseBoolean(args.getOptionValues("verbose").get(0)) : false;
		int generations = args.containsOption("generations") ? Integer.parseInt(args.getOptionValues("generations").get(0)) : 1;

		for (int i = 0; i < generations; i++) {
			long now = System.currentTimeMillis();
			this.generateStrips(strips, verbose);
			long then = System.currentTimeMillis();
			LOGGER.info("Took {} ms to generate {} ticket strips", then - now, strips);
		}
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
