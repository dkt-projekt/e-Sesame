package eu.freme.broker.esesame.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import eu.freme.broker.esesame.exceptions.BadRequestException;
import eu.freme.broker.esesame.exceptions.ExternalServiceFailedException;
import eu.freme.broker.esesame.modules.SesameStorage;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 * The whole documentation about openNLP examples can be found in https://opennlp.apache.org/documentation/1.6.0/manual/opennlp.html
 *
 */

@Component
public class ESesameService {
    
    public ResponseEntity<String> storeEntitiesFromString(String storageName, String inputText, String inputDataMimeType)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ESesameService.checkNotNullOrEmpty(storageName, "No Storage specified");
        	ESesameService.checkNotNullOrEmpty(inputText, "No inputText specified");
        	
       		String nifResult = SesameStorage.storeTriplets(storageName, inputText, inputDataMimeType);
       		
           	return ESesameService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> storeEntitiesFromTriplet(String storageName, String subject, String predicate, String object, String namespace)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ESesameService.checkNotNullOrEmpty(subject, "subject");
        	ESesameService.checkNotNullOrEmpty(predicate, "predicate");
        	ESesameService.checkNotNullOrEmpty(object, "object");
        	ESesameService.checkNotNullOrEmpty(storageName, "No Storage specified");

        	String nifResult = SesameStorage.storeTriplet(storageName, subject, predicate, object, namespace);
       		
           	return ESesameService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> retrieveEntitiesFromString(String storageName, String inputRDFData)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ESesameService.checkNotNullOrEmpty(storageName, "Storage Name");
        	ESesameService.checkNotNullOrEmpty(inputRDFData, "inputRDFData");

       		String nifResult = SesameStorage.retrieveTriplets(storageName, inputRDFData);
       		
           	return ESesameService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromSPARQL(String storageName, String inputSPARQLQuery)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ESesameService.checkNotNullOrEmpty(storageName, "Storage Name");
        	ESesameService.checkNotNullOrEmpty(inputSPARQLQuery, "inputRDFData");

       		String nifResult = SesameStorage.retrieveTripletsFromSPARQL(storageName, inputSPARQLQuery);
       		
           	return ESesameService.successResponse(nifResult, "RDF/JSON");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromTriplet(String storageName, String subject, String predicate, String object)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	if( (subject==null || subject.equals("")) && (predicate==null || predicate.equals("")) && (object==null || object.equals("")) ){
                throw new BadRequestException("One parameter must be different from NULL or EMPTY.");
        	}
        	ESesameService.checkNotNullOrEmpty(storageName, "Storage Name");

       		String nifResult = SesameStorage.retrieveTriplets(storageName, subject, predicate, object);
       		
           	return ESesameService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }
    
    public static void checkNotNullOrEmpty (String param, String message) throws BadRequestException {
    	if( param==null || param.equals("") ){
            throw new BadRequestException("No "+message+" param specified");
    	}
    }

    public static ResponseEntity<String> successResponse(String body, String contentType) throws BadRequestException {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", contentType);
    	ResponseEntity<String> response = new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    	return response;
    }
}
