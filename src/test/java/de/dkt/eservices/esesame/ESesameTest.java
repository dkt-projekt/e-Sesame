package de.dkt.eservices.esesame;


import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;

/**
 * @author 
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ESesameTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;
	String indexPath = "";
	
	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
		
		String OS = System.getProperty("os.name");
		if(OS.startsWith("Mac")){
			indexPath = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/testTimelining/sesameStorage/";
		}
		else if(OS.startsWith("Windows")){
			indexPath = "C:/tests/luceneindexes/";
		}
		else if(OS.startsWith("Linux")){
			indexPath = "/home/sabine/Schreibtisch/test/";
			
		}
	}
	
	private HttpRequestWithBody baseRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-sesame/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody storageRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-sesame/storeData";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody retrievalRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-sesame/retrieveData";
		return Unirest.post(url);
	}
	
	@Test
	public void test1_SanityCheck() throws UnirestException, IOException,
			Exception {

		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

	}
	
	@Test
	public void test2_StoreTripletLocalFilesystemStorage() throws UnirestException, IOException,Exception {
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", indexPath)
				.queryString("storageCreate", true)
				.queryString("inputDataFormat", "triple")
				.queryString("inputDataMimeType", "NOT IMPORTANT")
				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.queryString("namespace", "")
				.asString();

		Assert.assertEquals(200, response.getStatus());
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals("<http://de.dkt.sesame/ontology/doc1> "
				+ "<http://de.dkt.sesame/ontology/mentions> "
				+ "<http://de.dkt.sesame/ontology/doc2>"
				+ " has been properly included in tripleSTORE: test2",response.getBody());

	}

	@Test
	public void test3_StoreStringLocalFilesystemStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test3")
				.queryString("storagePath", indexPath)
				.queryString("storageCreate", true)
				.queryString("input", TestConstants.inputData)
				.queryString("inputDataFormat", "param")
				.queryString("inputDataMimeType", "text/turtle")
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

		HttpResponse<String> response2 = storageRequest()
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test4")
				.queryString("storagePath", indexPath)
				.queryString("storageCreate", true)
				.queryString("input", TestConstants.inputData)
				.queryString("inputDataFormat", "body")
				.queryString("inputDataMimeType", "text/turtle")
				.body(TestConstants.inputData)
				.asString();

		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
	}

	@Test
	public void test4_StoreTripletLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				.queryString("inputDataFormat", "triple")
				.queryString("inputDataMimeType", "NOT IMPORTANT")
				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.queryString("namespace", "")
				.asString();
////
////		System.out.println("BODY: "+response.getBody());
////		System.out.println("STATUS:" + response.getStatus());
//
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(""
				+ "<http://de.dkt.sesame/ontology/doc1> "
				+ "<http://de.dkt.sesame/ontology/mentions> "
				+ "<http://de.dkt.sesame/ontology/doc2>"
				+ " has been properly included in tripleSTORE: test2",response.getBody());

	}

	@Test
	public void test5_StoreStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("storageName", "sesame2")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				//.queryString("input", TestConstants.inputDataRDF3)
				.queryString("inputDataFormat", "body")
//				.queryString("inputDataMimeType", "text/turtle")
//				.queryString("inputDataMimeType", "application/rdf+xml")
				.queryString("inputDataMimeType", "application/ld+json")
				.body(TestConstants.inputDataJsonLd)
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
 
		System.out.println("----------------RESPONSE----------------------------------------");
		System.out.println(response.getBody());
		
	}
	
	@Test
	public void test51_StoreStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("storageName", "sesame2")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				//.queryString("input", TestConstants.inputDataRDF3)
				.queryString("inputDataFormat", "body")
				.queryString("inputDataMimeType", "text/turtle")
//				.queryString("inputDataMimeType", "application/rdf+xml")
//				.queryString("inputDataMimeType", "application/ld+json")
				.body(TestConstants.inputData)
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
 
		System.out.println("----------------RESPONSE----------------------------------------");
		System.out.println(response.getBody());
		
	}
	
	@Test
	public void test52_StoreStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("storageName", "sesame2")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				//.queryString("input", TestConstants.inputDataRDF3)
				.queryString("inputDataFormat", "body")
//				.queryString("inputDataMimeType", "text/turtle")
				.queryString("inputDataMimeType", "application/rdf+xml")
//				.queryString("inputDataMimeType", "application/ld+json")
				.body(TestConstants.inputDataRDF3)
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
 
		System.out.println("----------------RESPONSE----------------------------------------");
		System.out.println(response.getBody());
		
	}
	
	@Test
	public void test6_RetrieveTripletLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"\n"
				+"\txmlns:dbo=\"http://dbpedia.org/ontology/\"\n"
				+"\txmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos/\"\n"
				+"\txmlns:time=\"http://www.w3.org/2006/time#\">\n"
				
				+"\n"
			+"<rdf:Description rdf:about=\"http://de.dkt.sesame/ontology/doc1\">\n"
			+"	<mentions xmlns=\"http://de.dkt.sesame/ontology/\" rdf:resource=\"http://de.dkt.sesame/ontology/doc2\"/>\n"
			+"</rdf:Description>\n"
			+"\n"
			+"</rdf:RDF>"
			+ "";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("outformat", "text/turtle")
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "triple")
				.queryString("inputData", "")
				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());

		System.out.println("BODY OUTPUT: "+response.getBody());
		
		response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "")
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "triple")
				.queryString("inputData", "")
//				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());

		response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "")
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "triple")
				.queryString("inputData", "")
//				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());

	}

	@Test
	public void test7_RetrieveEntityStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"\n"
				+"\txmlns:dbo=\"http://dbpedia.org/ontology/\"\n"
				+"\txmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos/\"\n"
				+"\txmlns:time=\"http://www.w3.org/2006/time#\">\n"
				+"\n"
			+"<rdf:Description rdf:about=\"http://de.dkt.sesame/ontology/doc1\">\n"
			+"	<mentions xmlns=\"http://de.dkt.sesame/ontology/\" rdf:resource=\"http://de.dkt.sesame/ontology/doc2\"/>\n"
			+"</rdf:Description>\n"
			+"\n"
			+"</rdf:RDF>"
			+ "";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "http://de.dkt.sesame/ontology/doc1")
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", indexPath)
				.queryString("inputDataType", "entity")
				.queryString("inputData", "")
//				.queryString("subject", "")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());

		response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "http://de.dkt.sesame/ontology/mentions")
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", indexPath)
				.queryString("inputDataType", "entity")
				.queryString("inputData", "")
//				.queryString("subject", "")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());
		
		response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "http://de.dkt.sesame/ontology/doc2")
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", indexPath)
				.queryString("inputDataType", "entity")
				.queryString("inputData", "")
//				.queryString("subject", "")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());
	}
	
	@Test
	public void test8_RetrieveSparqlStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "" +
				"<?xml version='1.0' encoding='UTF-8'?>\n"+
		"<sparql xmlns='http://www.w3.org/2005/sparql-results#'>\n"+
		"	<head>\n"+
		"		<variable name='p'/>\n"+
		"		<variable name='o'/>\n"+
		"	</head>\n"+
		"	<results>\n"+
		"		<result>\n"+
		"			<binding name='p'>\n"+
		"				<uri>http://de.dkt.sesame/ontology/mentions</uri>\n"+
		"			</binding>\n"+
		"			<binding name='o'>\n"+
		"				<uri>http://de.dkt.sesame/ontology/doc2</uri>\n"+
		"			</binding>\n"+
		"		</result>\n"+
		"	</results>\n"+
		"</sparql>\n";
		
		String entityURI = "http://de.dkt.sesame/ontology/doc1";
		String sparqlQuery = "select ?p ?o where {\n" +
		        " <"+entityURI+"> ?p ?o \n" +
//		        " ?s <"+entityURI+"> ?o.\n" +
//		        " ?s ?p <"+entityURI+"> \n" +
		        "}";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", sparqlQuery)
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "sparql")
				.queryString("inputData", "")
//				.queryString("subject", "")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());
	}

	@Test
	public void test9_RetrieveNIFStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"\n"
				+"\txmlns:dbo=\"http://dbpedia.org/ontology/\"\n"
				+"\txmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos/\"\n"
				+"\txmlns:time=\"http://www.w3.org/2006/time#\">\n"
				+"\n"
			+"<rdf:Description rdf:about=\"http://de.dkt.sesame/ontology/doc1\">\n"
			+"	<mentions xmlns=\"http://de.dkt.sesame/ontology/\" rdf:resource=\"http://de.dkt.sesame/ontology/doc2\"/>\n"
			+"</rdf:Description>\n"
			+"\n"
			+"</rdf:RDF>"
			+ "";
		
		String nifQuery = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
				+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
				+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
				+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
				+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
				+"\n"
				+"<http://dkt.dfki.de/examples/#char=399,403>\n"
				+"        a                  nif:RFC5147String , nif:String ;\n"
				+"        nif:anchorOf       \"July\"^^xsd:string ;\n"
				+"        nif:beginIndex     \"399\"^^xsd:nonNegativeInteger ;\n"
				+"        nif:endIndex       \"403\"^^xsd:nonNegativeInteger ;\n"
				+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
				+"        itsrdf:taIdentRef  <http://de.dkt.sesame/ontology/doc4> ;\n"
				+"        itsrdf:taClassRef  <http://de.dkt.sesame/ontology/doc4> .\n"
				+"\n"
				+"<http://dkt.dfki.de/examples/#char=0,813>\n"
				+"        a                  nif:RFC5147String , nif:String , nif:Context ;\n"
				+"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;\n"
				+"        nif:endIndex       \"813\"^^xsd:nonNegativeInteger ;\n"
				+"        nif:isString       \"\"\"1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\n\"\"\"^^xsd:string ;\n"
				+"        nif:meanDateRange  \"02110711030000_16631213030000\"^^xsd:string .\n"
				+"\n"
			    + "<http://dkt.dfki.de/examples/#char=277,282>\n" +
			      "        a                     nif:RFC5147String , nif:String ;\n" +
			      "        nif:anchorOf          \"Spain\"^^xsd:string ;\n" +
			      "        nif:beginIndex        \"277\"^^xsd:nonNegativeInteger ;\n" +
			      "        nif:endIndex          \"282\"^^xsd:nonNegativeInteger ;\n" +
			      "        nif:entity            []  ;\n" +
			      "        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" +
			      "        itsrdf:taIdentRef     <http://de.dkt.sesame/ontology/doc1> ;\n"+
	      		  "        itsrdf:taClassRef     <http://de.dkt.sesame/ontology/doc1> .\n";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", nifQuery)
				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "NIF")
				.queryString("inputData", "")
//				.queryString("subject", "")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());
	}
}
