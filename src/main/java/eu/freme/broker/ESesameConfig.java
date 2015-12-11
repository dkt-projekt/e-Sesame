package eu.freme.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.freme.broker.esesame.api.ESesameService;


//@SpringBootApplication
//@ComponentScan("de.dkt.eservices.eopennlp.api")
@Configuration
public class ESesameConfig {
	
	@Value("${sesame.storage}")
	String storageLocation;
	
	@Bean
	public ESesameService getEntityApi(){
		return new ESesameService(storageLocation);
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	
}
