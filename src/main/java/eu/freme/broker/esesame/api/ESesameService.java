package eu.freme.broker.esesame.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.freme.broker.esesame.exceptions.BadRequestException;
import eu.freme.broker.esesame.exceptions.ExternalServiceFailedException;
import eu.freme.broker.esesame.modules.SesameStorage;
import eu.freme.broker.niftools.NIFReader;
import eu.freme.common.conversion.rdf.JenaRDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 * The whole documentation about openNLP examples can be found in https://opennlp.apache.org/documentation/1.6.0/manual/opennlp.html
 *
 */

@Component
public class ESesameService {
    
	RDFConversionService rdfConversionService = new JenaRDFConversionService();

	public static void main(String[] args) throws Exception {
		String file = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/debug.txt";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
		String line = br.readLine();
		String text = "";
		while(line!=null){
			text += " " + line;
			line = br.readLine();
		}
		br.close();

		ESesameService service = new ESesameService();
		
		service.storeEntitiesFromString("", text, "NIF");
		
	}
	
	
    public ResponseEntity<String> storeEntitiesFromString(String storageName, String inputText, String inputDataMimeType)
            throws ExternalServiceFailedException, BadRequestException, Exception {
        try {
//        	ESesameService.checkNotNullOrEmpty(storageName, "No Storage specified");
//        	ESesameService.checkNotNullOrEmpty(inputText, "No inputText specified");
        	
        	String nifResult;
        	if(inputDataMimeType.equalsIgnoreCase("NIF")){
        		
        		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel();
        		jenaModel = rdfConversionService.unserializeRDF(inputText, RDFSerialization.RDF_XML);

        		String docURI = NIFReader.extractDocumentURI(jenaModel);
        		List<String[]> list = NIFReader.extractEntities(jenaModel);

                Model openrdfModel = new LinkedHashModel(); 
                ValueFactory factory = ValueFactoryImpl.getInstance();
                URI doc = factory.createURI(docURI);
                URI mentions = factory.createURI("http://dkt.dfki.de/mentions");
                URI isMentioned = factory.createURI("http://dkt.dfki.de/isMentioned");
                URI hasText = factory.createURI("http://dkt.dfki.de/hasText");
            
                for (String[] entity : list) {
                    URI entURI = factory.createURI(entity[0]);
                    Literal entityText = factory.createLiteral(entity[1]);
                    Statement st1 = factory.createStatement(entURI, hasText, entityText);
                	openrdfModel.add(st1);
                    Statement st2 = factory.createStatement(doc, mentions, entURI);
                	openrdfModel.add(st2);
                    Statement st3 = factory.createStatement(entURI, isMentioned, doc);
                	openrdfModel.add(st3);
				}
//                return null;
           		nifResult = SesameStorage.storeTripletsFromModel(storageName, openrdfModel);
        	}
        	else{
           		nifResult = SesameStorage.storeTriplets(storageName, inputText, inputDataMimeType);
        	}
       		
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
