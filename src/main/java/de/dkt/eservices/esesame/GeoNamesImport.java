package de.dkt.eservices.esesame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.activemq.filter.function.replaceFunction;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLParser;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLParserFactory;

import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.DFKINIF;
import de.dkt.common.niftools.GEO;
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

public class GeoNamesImport {

	public static void storeFileFromGeonames(String filePath, String storageName, String storagePath, boolean storageCreate) throws Exception {
		Date d1 = new Date();
		
		SesameStorage.setStorageCreate(storageCreate);
		SesameStorage.setStorageDirectory(storagePath);
		
		File f = FileFactory.generateFileInstance(filePath);

		LineNumberReader lnr = new LineNumberReader(new FileReader(f));
		lnr.skip(Long.MAX_VALUE);
		int ln = lnr.getLineNumber() + 1 ;
		lnr.close();
		
		BufferedReader br = FileFactory.generateBufferedReaderInstance(filePath, "utf-8");

        Model model = new LinkedHashModel(); 
        ValueFactory factory = ValueFactoryImpl.getInstance();
        
        int counter=1;
        String line = br.readLine();
//        line = br.readLine();
//        String lineNext = br.readLine();
		while(line!=null){
			if(counter%10000==0){
				System.out.println(filePath + " : " + counter+"/"+ln);
				SesameStorage.storeTripletsFromModel(storageName, model);
				model = new LinkedHashModel(); 
			}
			counter++;
			
			String parts[] = line.split("\t");
			String geonameId = parts[0];
			String name = parts[1];
			String asciiname = parts[2];
			String alternatesnames = parts[3];
			String latitude = parts[4];
			String longitude = parts[5];
			String fClass = parts[6];
			String fCode = parts[7];
			String contryCode = parts[8];
			String cc2 = parts[9];
			String admin1 = parts[10];
			String admin2 = parts[11];
			String admin3 = parts[12];
			String admin4 = parts[13];
			String population = parts[14];
			String elevation = parts[15];
			String dem = parts[16];
			String timezone = parts[17];
			String modificationdate = parts[18];
			
            URI doc = factory.createURI("http://sws.geonames.org/"+geonameId+"/");
            URI hasText = factory.createURI("http://dkt.dfki.de/hasText");
            URI hasLatitude = factory.createURI(GEO.latitude.getURI());
            URI hasLongitude = factory.createURI(GEO.longitude.getURI());

            Literal literalText = factory.createLiteral(latitude);
            Statement st1 = factory.createStatement(doc, hasLatitude, literalText);
            model.add(st1);
            literalText = factory.createLiteral(longitude);
            st1 = factory.createStatement(doc, hasLongitude, literalText);
            model.add(st1);
            literalText = factory.createLiteral(name);
            st1 = factory.createStatement(doc, hasText, literalText);
            model.add(st1);

            line = br.readLine();
		}
		SesameStorage.storeTripletsFromModel(storageName, model);
//		SesameStorage.storeTriplet("hyperlinking1", parts[0], parts[1], parts[2], "");
		br.close();
		Date d2 = new Date();
		System.out.println(""+(d2.getTime()-d1.getTime())+" milliseconds for processing file: "+filePath);
	}
	
	public static void showRepositoryInformation(String storageName, String storagePath){
		Date d1 = new Date();
		SesameStorage.setStorageDirectory(storagePath);
		
		String output = SesameStorage.retrieveTriplets(storageName, null, null, null);
		System.out.println("Repository information: "+output);

		Date d2 = new Date();
		System.out.println(""+(d2.getTime()-d1.getTime())+" milliseconds for processing information of repository: "+storageName);
	}

	public static void main(String[] args) throws Exception {
		Date d1 = new Date();

		GeoNamesImport.storeFileFromGeonames("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/Geonames/allCountries.txt", "geoFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
		GeoNamesImport.showRepositoryInformation("geoFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/");
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc2NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc3NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc4NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc5NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc6NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc7NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc8NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc9NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);
//		DBPediaTest.storeNIFFile("/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/HyperlinkingDocuments/docsimtest/doc10NIF2.txt", "hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/",true);

//		System.out.println("\n\n\n\n\n===========================================");
//		DBPediaTest.showRepositoryInformation("hyperlinking12","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/");
		
//		Map<String,Float> map = GeoNamesImport.checkPathes("hyperlinkingFinal","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/", "http://dkt.dfki.de/documents/nifdoc1", 14);
////		Map<String,Float> map = DBPediaTest.checkPathes("test2","/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/", "http://de.dkt.sesame/ontology/doc1", 1);
//		
//		System.out.println("============MAP RESULT========");
//		Set<String> keys = map.keySet();
//		for (String k : keys) {
//			System.out.println("\t "+k+" : "+map.get(k));
//		}
//		System.out.println("<<<<<<<<<<<<MAP RESULT>>>>>>>>>>");
//		Date d2 = new Date();
//		System.out.println("It took: "+(d2.getTime()-d1.getTime())+" milliseconds to charge DBPEDIA in english");
	}

}
