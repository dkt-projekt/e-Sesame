package eu.freme.broker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.freme.broker.esesame.api.ESesameService;


//@SpringBootApplication
//@ComponentScan("de.dkt.eservices.eopennlp.api")
@Configuration
public class ESesameConfig {
	
	@Bean
	public ESesameService getEntityApi(){
		return new ESesameService();
	}
	
}
