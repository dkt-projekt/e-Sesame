package de.dkt.eservices.esesame;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.niftools.TIME;
import de.dkt.common.tools.ParameterChecker;
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
    
	Logger logger = Logger.getLogger(ESesameService.class);

	RDFConversionService rdfConversionService = new JenaRDFConversionService();

	private String storageLocation="/Users/jumo04/Documents/DFKI/DKT/dkt-test/testTimelining/sesameStorage/";
	
	public ESesameService() {
		String OS = System.getProperty("os.name");
		if(OS.startsWith("Mac")){
			storageLocation = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/testTimelining/sesameStorage/";
		}
		else if(OS.startsWith("Windows")){
			storageLocation = "C:/tests/sesame/";
		}
		else if(OS.startsWith("Linux")){
			storageLocation = "/opt/storage/sesameStorage/";
		}
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
		String response = service.retrieveEntitiesFromTriplet("parrotTest", null, null, null, null,"text/turtle");
		System.out.println(response);
	}
	
    public String storeEntitiesFromString(String storageName, String storagePath, boolean storageCreate, String inputText, String inputDataMimeType)
            throws ExternalServiceFailedException, BadRequestException, Exception {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "storage", logger);
        	ParameterChecker.checkNotNullOrEmpty(inputText, "No inputText specified", logger);

        	SesameStorage.setStorageCreate(storageCreate);
        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
        	String nifResult;
        	if((inputDataMimeType.equalsIgnoreCase("application/rdf+xml")||inputDataMimeType.equalsIgnoreCase("text/turtle")||inputDataMimeType.equalsIgnoreCase("application/ld+json"))){
        		
        		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel();
        		try{
            		jenaModel = rdfConversionService.unserializeRDF(inputText, RDFSerialization.fromValue(inputDataMimeType));
        		}
        		catch(Exception e){
        			System.out.println("input doesn't match input type");
            		//jenaModel = rdfConversionService.unserializeRDF(inputText, RDFSerialization.TURTLE);
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
                URI hasType = factory.createURI("http://dkt.dfki.de/hasType");
                URI hasBirthDate = factory.createURI("http://dkt.dfki.de/hasBirthDate");
                URI hasDeathDate = factory.createURI("http://dkt.dfki.de/hasDeathDate");
                URI hasOrganizationType = factory.createURI("http://dkt.dfki.de/hasOrganizationType");
                URI hasIntervalStarts = factory.createURI(TIME.intervalStarts.getURI());
                URI hasIntervalFinishes = factory.createURI(TIME.intervalFinishes.getURI());
                URI hasExternalLink = factory.createURI("http://dkt.dfki.de/hasExternalLink");
                URI isExternalLinkOf = factory.createURI("http://dkt.dfki.de/isExternalLinkOf");
                URI hasMeanDateStart = factory.createURI(DKTNIF.meanDateStart.getURI());
                URI hasMeanDateEnd = factory.createURI(DKTNIF.meanDateEnd.getURI());
                //URI hasCentralGeoPoint = factory.createURI("http://dkt.dfki.de/hasCentralGeoPoint");
                URI hasLatitudeAverage = factory.createURI(DKTNIF.averageLatitude.getURI());
                URI hasLongitudeAverage = factory.createURI(DKTNIF.averageLongitude.getURI());
                URI hasLatitude = factory.createURI(GEO.latitude.getURI());
                URI hasLongitude = factory.createURI(GEO.longitude.getURI());
                URI hasLatitudeStandardDevs = factory.createURI(DKTNIF.standardDeviationLatitude.getURI());
                URI hasLongitudeStandardDevs = factory.createURI(DKTNIF.standardDeviationLongitude.getURI());

                if(docList!=null){
                	Set<String> keys = docList.keySet();
                	for (String k : keys) {
                		/*
                    	if(k.equalsIgnoreCase(NIF.centralGeoPoint.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasCentralGeoPoint, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	*/
                    	if(k.equalsIgnoreCase(DKTNIF.averageLatitude.getURI())){
                    		Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasLatitudeAverage, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(DKTNIF.averageLongitude.getURI())){
                    		Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasLongitudeAverage, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	/*
                    	else if(k.equalsIgnoreCase(NIF.geoStandardDevs.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasGeoStandardDevs, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	*/
                    	else if(k.equalsIgnoreCase(DKTNIF.standardDeviationLatitude.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, factory.createURI(DKTNIF.standardDeviationLatitude.getURI()), literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(DKTNIF.standardDeviationLongitude.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasLongitudeStandardDevs, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(DKTNIF.meanDateStart.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasMeanDateStart, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(DKTNIF.meanDateEnd.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasMeanDateEnd, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(TIME.intervalStarts.getURI())){
		                    Literal literalText = factory.createLiteral(docList.get(k));
		                    Statement st1 = factory.createStatement(doc, hasIntervalStarts, literalText);
		                	openrdfModel.add(st1);
                    	}
                    	else if(k.equalsIgnoreCase(TIME.intervalFinishes.getURI())){
                    		Literal literalText = factory.createLiteral(docList.get(k));
                    		Statement st1 = factory.createStatement(doc, hasIntervalFinishes, literalText);
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
	                    	else if(k2.equalsIgnoreCase(DBO.birthDate.getURI())){
	                    		NIFWriter.addPrefixToModel(jenaModel, "dbo", DBO.uri);
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasBirthDate, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(DBO.deathDate.getURI())){
	                    		NIFWriter.addPrefixToModel(jenaModel, "dbo", DBO.uri);
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasDeathDate, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.orgType.getURI())){
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasOrganizationType, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(GEO.latitude.getURI())){
	                    		NIFWriter.addPrefixToModel(jenaModel, "geo", GEO.uri);
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasLatitude, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(GEO.longitude.getURI())){
	                    		NIFWriter.addPrefixToModel(jenaModel, "geo", GEO.uri);
			                    Literal text = factory.createLiteral(map.get(k2));
			                    Statement st1 = factory.createStatement(entURI, hasLongitude, text);
			                	openrdfModel.add(st1);
	                    	}
	                    	else if(k2.equalsIgnoreCase(NIF.entity.getURI())){
			                    URI uri = factory.createURI(map.get(k2));
		                    	Statement st4 = factory.createStatement(entURI, isTypeOf, uri);
		                    	openrdfModel.add(st4);
		                    	Statement st5 = factory.createStatement(uri, hasType, entURI);
		                    	openrdfModel.add(st5);
	                    	}
	                    	else if(k2.equalsIgnoreCase(ITSRDF.taIdentRef.getURI())){
			                    URI uri = factory.createURI(map.get(k2));
		                    	Statement st4 = factory.createStatement(entURI, hasExternalLink, uri);
		                    	openrdfModel.add(st4);
		                    	Statement st5 = factory.createStatement(uri, isExternalLinkOf, entURI);
		                    	openrdfModel.add(st5);
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
        	}
        	else{
           		nifResult = SesameStorage.storeTriplets(storageName, inputText, inputDataMimeType);
        	}
//       		nifResult = inputText;

       		//TODO ???
       		
       		return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
        	throw e;
    	} catch (ExternalServiceFailedException e2) {
        	logger.error(e2.getMessage());
    		throw e2;
    	}
    }

    public String storeEntitiesFromTriplet(String storageName, String storagePath, boolean storageCreate, String subject, String predicate, String object, String namespace)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(subject, "subject", logger);
        	ParameterChecker.checkNotNullOrEmpty(predicate, "predicate", logger);
        	ParameterChecker.checkNotNullOrEmpty(object, "object", logger);
        	ParameterChecker.checkNotNullOrEmpty(storageName, "No Storage specified", logger);

        	SesameStorage.setStorageCreate(storageCreate);
        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
        	String nifResult = SesameStorage.storeTriplet(storageName, subject, predicate, object, namespace);
       		
           	return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} catch (ExternalServiceFailedException e2) {
        	logger.error(e2.getMessage());
    		throw e2;
    	}
    }

    public String retrieveEntitiesFromSPARQL(String storageName, String storagePath, String inputSPARQLQuery, String outformat)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name", logger);
        	ParameterChecker.checkNotNullOrEmpty(inputSPARQLQuery, "inputRDFData", logger);

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTripletsFromSPARQL(storageName, inputSPARQLQuery, outformat);
       		
           	return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} 
    }


    public String retrieveEntitiesFromString(String storageName, String storagePath, String inputRDFData, String outformat)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name", logger);
        	ParameterChecker.checkNotNullOrEmpty(inputRDFData, "inputRDFData", logger);

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTriplets(storageName, inputRDFData, outformat);
       		
           	return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} 
    }
    
    public String retrieveEntitiesFromTriplet(String storageName, String storagePath, String subject, String predicate, String object, String outformat)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	if( (subject==null || subject.equals("")) && (predicate==null || predicate.equals("")) && (object==null || object.equals("")) ){
                //throw new BadRequestException("One parameter must be different from NULL or EMPTY.");
                System.out.println("[WARNING] Empty parameters in this method [retrieveEntitiesFromTriplet] should be only used for testing purposes.\n"
                		+ " Consider that it retrieves the whole triple store and it can be resources and time consuming.");
        	}
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name", logger);

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTriplets(storageName, subject, predicate, object, outformat);
           	return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} 
    }

    public String retrieveEntitiesFromNIF(String storageName, String storagePath, String nifData, String outformat)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name", logger);
        	ParameterChecker.checkNotNullOrEmpty(nifData, "inputNIFData", logger);

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTripletsFromNIF(storageName, nifData, outformat);
           	return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} 
    }
    
    public String retrieveEntitiesFromNIFIterative(String storageName, String storagePath, String nifData, int iterations)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ParameterChecker.checkNotNullOrEmpty(storageName, "Storage Name", logger);
        	ParameterChecker.checkNotNullOrEmpty(nifData, "inputNIFData", logger);

        	if(storagePath!=null && !storagePath.equalsIgnoreCase("")){
        		if(!storagePath.endsWith(File.separator)){
        			storagePath += File.separator;
        		}
        		SesameStorage.setStorageDirectory(storagePath);
        	}
        	
       		String nifResult = SesameStorage.retrieveTripletsFromNIFIterative(storageName, nifData, iterations);
       		return nifResult;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} 
    }
}
