package eu.freme.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import eu.freme.broker.tools.StarterHelper;

@SpringBootApplication
@Import(ESesameConfig.class)
public class Starter {
    public static void main(String[] args) {
		String[] newArgs = StarterHelper.addProfile(args, "broker");

        SpringApplication.run(Starter.class, newArgs);
    }
}