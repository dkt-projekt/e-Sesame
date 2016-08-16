package de.dkt.eservices.esesame;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;

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
        			String msg = "Exception: input doesn't match input type";
        			System.out.println(msg);
        			logger.error(msg);
        			throw new BadRequestException(msg);
            		//jenaModel = rdfConversionService.unserializeRDF(inputText, RDFSerialization.TURTLE);
        		}

                Model openrdfModel = new LinkedHashModel(); 
                ValueFactory factory = ValueFactoryImpl.getInstance();
        		StmtIterator it = jenaModel.listStatements();
        		while(it.hasNext()){
        			com.hp.hpl.jena.rdf.model.Statement st = it.next();
                    URI sub = factory.createURI(st.getSubject().getURI());
                    URI predicate = factory.createURI(st.getPredicate().getURI());
                    RDFNode node = st.getObject();
                    Value obj = null;
                    if(node.isLiteral()){
                    	obj = factory.createLiteral(node.asLiteral().getString());
                    }
                    else{
                    	obj = factory.createURI(node.asResource().getURI());
                    }
        			Statement st2 = factory.createStatement(sub, predicate, obj); 
//            		Literal literalText = factory.createLiteral(docList.get(k));
                	openrdfModel.add(st2);
        		}
        		
        		nifResult = SesameStorage.storeTripletsFromModel(storageName, openrdfModel);
        	}
        	else{
           		nifResult = SesameStorage.storeTriplets(storageName, inputText, inputDataMimeType);
        	}
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
