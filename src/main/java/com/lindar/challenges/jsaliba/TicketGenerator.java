package com.lindar.challenges.jsaliba;

import com.lindar.challenges.jsaliba.beans.TicketStrip;
import com.lindar.challenges.jsaliba.beans.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
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
		try {
			parseInput(args);
		}
		catch (ValidationException e) {
			LOGGER.error(e.getMessage());
		}

		for (int i = 0; i < generations; i++) {
			long now = System.currentTimeMillis();
			this.generateStrips(strips, verbose);
			long then = System.currentTimeMillis();
			LOGGER.info("Took {} ms to generate {} ticket strips", then - now, strips);
		}
	}

	private void parseInput(ApplicationArguments args) {

		LOGGER.info("Parsing arguments: [{}]", args.getSourceArgs());

		if (args.containsOption("strips")) {
			if (args.getOptionValues("strips").isEmpty()) {
				throw new ValidationException("No strips given, please specify strips: 'strips=1000'");
			}
			final String stripsOption = args.getOptionValues("strips").get(0);
			strips = Integer.parseInt(stripsOption);
		}
		else strips = 10000;

		if (args.containsOption("generations")) {
			if (args.getOptionValues("generations").isEmpty()) {
				throw new ValidationException("No generations given, please specify generations: 'generations=1000'");
			}
			final String generationsOption = args.getOptionValues("generations").get(0);
			generations = Integer.parseInt(generationsOption);
		}
		else generations = 1;

		if (args.containsOption("verbose")) {
			final List<String> verboseOption = args.getOptionValues("verbose");
			if (!verboseOption.isEmpty()) {
                verbose = verboseOption.get(0).trim().equalsIgnoreCase("true");
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
