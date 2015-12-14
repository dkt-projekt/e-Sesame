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

import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.freme.broker.esesame.exceptions.BadRequestException;
import eu.freme.broker.esesame.exceptions.ExternalServiceFailedException;
import eu.freme.broker.esesame.modules.SesameStorage;
import eu.freme.broker.niftools.NIFReader;
import eu.freme.broker.tools.ParameterChecker;
import eu.freme.broker.tools.ResponseGenerator;
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

	private String storageLocation="/Users/jumo04/Documents/DFKI/DKT/dkt-test/testComplete/sesameStorage";
	
	public ESesameService() {
	}
	
	public ESesameService(String storageLocation) {
		super();
		this.storageLocation = storageLocation;
		SesameStorage.setStorageDirectory(storageLocation);
	}

	public static void main(String[] args) throws Exception {
//		String file = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/debug.txt";
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
//		String line = br.readLine();
//		String text = "";
//		while(line!=null){
//			text += " " + line;
//			line = br.readLine();
//		}
//		br.close();
//
//		ESesameService service = new ESesameService("/Users/jumo04/Documents/DFKI/DKT/dkt-test/Weka/storage/");
//		service.storeEntitiesFromString("test", text, "NIF");
	
		ESesameService service = new ESesameService();
		System.out.println(service.retrieveEntitiesFromTriplet("test1/", null, null, null));

	}
	
	
    public ResponseEntity<String> storeEntitiesFromString(String storageName, String inputText, String inputDataMimeType)
            throws ExternalServiceFailedException, BadRequestException, Exception {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "No Storage specified");
        	ParameterChecker.checkNotNullOrEmpty(inputText, "No inputText specified");
        	
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
                URI isTypeOf = factory.createURI("http://dkt.dfki.de/isTypeOf");
            
                for (String[] entity : list) {
                    URI entURI = factory.createURI(entity[0]);
                    Literal entityText = factory.createLiteral(entity[1]);
                    URI entTypeURI = factory.createURI(entity[2]);
                    Statement st1 = factory.createStatement(entURI, hasText, entityText);
                	openrdfModel.add(st1);
                    Statement st2 = factory.createStatement(doc, mentions, entURI);
                	openrdfModel.add(st2);
                    Statement st3 = factory.createStatement(entURI, isMentioned, doc);
                	openrdfModel.add(st3);
                    Statement st4 = factory.createStatement(entURI, isTypeOf, entTypeURI);
                	openrdfModel.add(st4);
				}
//                return null;
           		nifResult = SesameStorage.storeTripletsFromModel(storageName, openrdfModel);
        	}
        	else{
           		nifResult = SesameStorage.storeTriplets(storageName, inputText, inputDataMimeType);
        	}
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> storeEntitiesFromTriplet(String storageName, String subject, String predicate, String object, String namespace)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(subject, "subject");
        	ParameterChecker.checkNotNullOrEmpty(predicate, "predicate");
        	ParameterChecker.checkNotNullOrEmpty(object, "object");
        	ParameterChecker.checkNotNullOrEmpty(storageName, "No Storage specified");

        	String nifResult = SesameStorage.storeTriplet(storageName, subject, predicate, object, namespace);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> retrieveEntitiesFromString(String storageName, String inputRDFData)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");
        	ParameterChecker.checkNotNullOrEmpty(inputRDFData, "inputRDFData");

       		String nifResult = SesameStorage.retrieveTriplets(storageName, inputRDFData);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromSPARQL(String storageName, String inputSPARQLQuery)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");
        	ParameterChecker.checkNotNullOrEmpty(inputSPARQLQuery, "inputRDFData");

       		String nifResult = SesameStorage.retrieveTripletsFromSPARQL(storageName, inputSPARQLQuery);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/JSON");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromTriplet(String storageName, String subject, String predicate, String object)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	if( (subject==null || subject.equals("")) && (predicate==null || predicate.equals("")) && (object==null || object.equals("")) ){
                //throw new BadRequestException("One parameter must be different from NULL or EMPTY.");
                System.out.println("[WARNING] Empty parameters in this method [retrieveEntitiesFromTriplet] should be only used for testing purposes.\n"
                		+ " Consider that it retrieves the whole triple store and it can be resources and time consuming.");
        	}
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");

       		String nifResult = SesameStorage.retrieveTriplets(storageName, subject, predicate, object);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

}
