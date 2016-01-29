package de.dkt.eservices.esesame;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.common.tools.ResponseGenerator;
import de.dkt.eservices.esesame.modules.SesameStorage;
import eu.freme.common.conversion.rdf.JenaRDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 * The whole documentation about openNLP examples can be found in https://opennlp.apache.org/documentation/1.6.0/manual/opennlp.html
 *
 */

@Component
public class ESesameService {
    
	RDFConversionService rdfConversionService = new JenaRDFConversionService();

	private String storageLocation="/Users/jumo04/Documents/DFKI/DKT/dkt-test/testTimelining/sesameStorage/";
	
	public ESesameService() {
		SesameStorage.setStorageDirectory(storageLocation);
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
	
		ESesameService service = new ESesameService("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/");
//		System.out.println(service.retrieveEntitiesFromTriplet("testComplete/", null, null, null));
		ResponseEntity<String> response = service.retrieveEntitiesFromTriplet("parrotTest", null, null, null, null);
		System.out.println(response.getBody());

	}
	
    public ResponseEntity<String> storeEntitiesFromString(String storageName, String storagePath, boolean storageCreate, String inputText, String inputDataMimeType)
            throws ExternalServiceFailedException, BadRequestException, Exception {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "storage");
        	ParameterChecker.checkNotNullOrEmpty(inputText, "No inputText specified");

        	SesameStorage.setStorageCreate(storageCreate);
        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
        	String nifResult;
        	if(inputDataMimeType.equalsIgnoreCase("NIF")){
        		
        		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel();
        		try{
            		jenaModel = rdfConversionService.unserializeRDF(inputText, RDFSerialization.RDF_XML);
        		}
        		catch(Exception e){
            		jenaModel = rdfConversionService.unserializeRDF(inputText, RDFSerialization.TURTLE);
        		}

        		String docURI = NIFReader.extractDocumentURI(jenaModel);
        		
        		Map<String,Map<String,String>> list2 = NIFReader.extractEntitiesExtended(jenaModel);
        		Map<String,String> docList = NIFReader.extractDocumentInformation(jenaModel);
        		
                Model openrdfModel = new LinkedHashModel(); 
                ValueFactory factory = ValueFactoryImpl.getInstance();
                URI doc = factory.createURI(docURI);
                URI mentions = factory.createURI("http://dkt.dfki.de/mentions");
                URI isMentioned = factory.createURI("http://dkt.dfki.de/isMentioned");
                URI hasText = factory.createURI("http://dkt.dfki.de/hasText");
                URI isTypeOf = factory.createURI("http://dkt.dfki.de/isTypeOf");
                URI hasBirthDate = factory.createURI("http://dkt.dfki.de/hasBirthDate");
                URI hasDeathDate = factory.createURI("http://dkt.dfki.de/hasDeathDate");
                URI hasOrganizationType = factory.createURI("http://dkt.dfki.de/hasOrganizationType");
                URI hasNormalizedDate = factory.createURI("http://dkt.dfki.de/hasNormlizedDate");
                URI hasGeoPoint = factory.createURI("http://dkt.dfki.de/hasGeoPoint");
                URI hasExternalLink = factory.createURI("http://dkt.dfki.de/hasExternalLink");
                URI hasMeanDateRange = factory.createURI("http://dkt.dfki.de/hasMeanDateRange");
                URI hasCentralGeoPoint = factory.createURI("http://dkt.dfki.de/hasCentralGeoPoint");
                URI hasGeoStandardDevs = factory.createURI("http://dkt.dfki.de/hasGeoStandardDevs");

                if(docList!=null){
                	Set<String> keys = docList.keySet();
                	for (String k : keys) {
                    	if(k.equalsIgnoreCase(NIF.centralGeoPoint.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasCentralGeoPoint, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(NIF.geoStandardDevs.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasGeoStandardDevs, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(NIF.meanDateRange.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasMeanDateRange, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(NIF.isString.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasText, literalText);
		                	openrdfModel.add(st1);
                    	}
                	}
                }
                
                if(list2!=null){
                	Set<String> keys = list2.keySet();
                	for (String k : keys) {
						Map<String,String> map = list2.get(k);
						
	                    URI entURI = factory.createURI(k);
	                    
	                    Set<String> keys2 = map.keySet();
	                    for (String k2 : keys2) {
							
	                    	if(k2.equalsIgnoreCase(NIF.anchorOf.getURI())){
			                    Literal entityText = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasText, entityText);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.birthDate.getURI())){
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasBirthDate, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.deathDate.getURI())){
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasDeathDate, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.orgType.getURI())){
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasOrganizationType, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.normalizedDate.getURI())){
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasNormalizedDate, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.geoPoint.getURI())){
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasGeoPoint, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.entity.getURI())){
			                    URI uri = factory.createURI(map.get(k2));
		                    	Statement st4 = factory.createStatement(entURI, isTypeOf, uri);
		                    	openrdfModel.add(st4);
	                    	}
	                    	else if(k2.equalsIgnoreCase(ITSRDF.taIdentRef.getURI())){
			                    URI uri = factory.createURI(map.get(k2));
		                    	Statement st4 = factory.createStatement(entURI, hasExternalLink, uri);
		                    	openrdfModel.add(st4);
	                    	}
						}
	                    if(entURI!=null && doc!=null){
	                    	Statement st2 = factory.createStatement(doc, mentions, entURI);
	                    	openrdfModel.add(st2);
	                    	Statement st3 = factory.createStatement(entURI, isMentioned, doc);
	                    	openrdfModel.add(st3);
	                    }
					}
	//                return null;
	           		nifResult = SesameStorage.storeTripletsFromModel(storageName, openrdfModel);
                }
                else{
                	nifResult = inputText;
                }

                //	OLD VERSION EXTRACTING LESS INFORMATION OF ENTITIES.
//        		List<String[]> list = NIFReader.extractEntities(jenaModel);
//                if(list!=null){
//	                for (String[] entity : list) {
//	                    URI entURI = (entity[0]!=null) ? factory.createURI(entity[0]) : null;
//	                    Literal entityText = (entity[1]!=null) ? factory.createLiteral(entity[1]) : null;
//	                    URI entTypeURI = (entity[2]!=null) ? factory.createURI(entity[2]) : null;
//	                    if(entURI!=null && entityText!=null){
//		                    Statement st1 = factory.createStatement(entURI, hasText, entityText);
//		                	openrdfModel.add(st1);
//	                    }
//	                    if(entURI!=null && doc!=null){
//	                    	Statement st2 = factory.createStatement(doc, mentions, entURI);
//	                    	openrdfModel.add(st2);
//	                    	Statement st3 = factory.createStatement(entURI, isMentioned, doc);
//	                    	openrdfModel.add(st3);
//	                    }
//	                    if(entURI!=null && entTypeURI!=null){
//	                    	Statement st4 = factory.createStatement(entURI, isTypeOf, entTypeURI);
//	                    	openrdfModel.add(st4);
//	                    }
//					}
//	//                return null;
//	           		nifResult = SesameStorage.storeTripletsFromModel(storageName, openrdfModel);
//                }
//                else{
//                	nifResult = inputText;
//                }
        	}
        	else{
           		nifResult = SesameStorage.storeTriplets(storageName, inputText, inputDataMimeType);
        	}
       		nifResult = inputText;
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> storeEntitiesFromTriplet(String storageName, String storagePath, boolean storageCreate, String subject, String predicate, String object, String namespace)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(subject, "subject");
        	ParameterChecker.checkNotNullOrEmpty(predicate, "predicate");
        	ParameterChecker.checkNotNullOrEmpty(object, "object");
        	ParameterChecker.checkNotNullOrEmpty(storageName, "No Storage specified");

        	SesameStorage.setStorageCreate(storageCreate);
        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
        	String nifResult = SesameStorage.storeTriplet(storageName, subject, predicate, object, namespace);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> retrieveEntitiesFromString(String storageName, String storagePath, String inputRDFData)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");
        	ParameterChecker.checkNotNullOrEmpty(inputRDFData, "inputRDFData");

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTriplets(storageName, inputRDFData);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromSPARQL(String storageName, String storagePath, String inputSPARQLQuery)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");
        	ParameterChecker.checkNotNullOrEmpty(inputSPARQLQuery, "inputRDFData");

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTripletsFromSPARQL(storageName, inputSPARQLQuery);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/JSON");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromTriplet(String storageName, String storagePath, String subject, String predicate, String object)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	if( (subject==null || subject.equals("")) && (predicate==null || predicate.equals("")) && (object==null || object.equals("")) ){
                //throw new BadRequestException("One parameter must be different from NULL or EMPTY.");
                System.out.println("[WARNING] Empty parameters in this method [retrieveEntitiesFromTriplet] should be only used for testing purposes.\n"
                		+ " Consider that it retrieves the whole triple store and it can be resources and time consuming.");
        	}
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTriplets(storageName, subject, predicate, object);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }

    public ResponseEntity<String> retrieveEntitiesFromNIF(String storageName, String storagePath, String nifData)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");
        	ParameterChecker.checkNotNullOrEmpty(nifData, "inputNIFData");

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTripletsFromNIF(storageName, nifData);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }
    
    public ResponseEntity<String> retrieveEntitiesFromNIFIterative(String storageName, String storagePath, String nifData, int iterations)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name");
        	ParameterChecker.checkNotNullOrEmpty(nifData, "inputNIFData");

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTripletsFromNIFIterative(storageName, nifData, iterations);
       		
           	return ResponseGenerator.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} 
    }
}
