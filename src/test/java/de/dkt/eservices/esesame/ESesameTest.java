package de.dkt.eservices.esesame;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import eu.freme.common.conversion.rdf.RDFConstants;

/**
 * @author 
 */

public class ESesameTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;

	String inputData = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=494,505>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"5 September\"^^xsd:string ;\n"
+"        nif:beginIndex     \"494\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"505\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=00010905000000_00010906000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=399,403>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"July\"^^xsd:string ;\n"
+"        nif:beginIndex     \"399\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"403\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=00010701000000_00010702000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=407,416>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"September\"^^xsd:string ;\n"
+"        nif:beginIndex     \"407\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"416\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=00010901000000_00010902000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=0,813>\n"
+"        a                  nif:RFC5147String , nif:String , nif:Context ;\n"
+"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"813\"^^xsd:nonNegativeInteger ;\n"
+"        nif:isString       \"\"\"1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\n\"\"\"^^xsd:string ;\n"
+"        nif:meanDateRange  \"02110711030000_16631213030000\"^^xsd:string .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=795,811>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"end of September\"^^xsd:string ;\n"
+"        nif:beginIndex     \"795\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"811\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=00010920000000_00010930000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=0,4>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"1936\"^^xsd:string ;\n"
+"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"4\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360101000000_19370101000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=156,163>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"21 July\"^^xsd:string ;\n"
+"        nif:beginIndex     \"156\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"163\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360721000000_19360722000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=598,610>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"13 September\"^^xsd:string ;\n"
+"        nif:beginIndex     \"598\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"610\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=00010913000000_00010914000000> .\n"
+"\n"
+"<http://dkt.dfki.de/examples/#char=58,65>\n"
+"        a                  nif:RFC5147String , nif:String ;\n"
+"        nif:anchorOf       \"20 July\"^^xsd:string ;\n"
+"        nif:beginIndex     \"58\"^^xsd:nonNegativeInteger ;\n"
+"        nif:endIndex       \"65\"^^xsd:nonNegativeInteger ;\n"
+"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n"
+"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360720000000_19360721000000> .";
	
	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
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
	public void testESesameBasic() throws UnirestException, IOException,
			Exception {

		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

	}
	
	@Test
	public void testESesameCheckStoreTripletLocalFilesystemStorage() throws UnirestException, IOException,Exception {

		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
//				.queryString("storagePath", null)
				.queryString("storageCreate", true)
				.queryString("inputDataFormat", "NOT IMPORTANT")
				.queryString("inputDataMimeType", "NOT IMPORTANT")
				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.queryString("namespace", "")
				.asString();

		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		assertTrue(response.getBody().equalsIgnoreCase(""
				+ "<http://de.dkt.sesame/ontology/doc1> "
				+ "<http://de.dkt.sesame/ontology/mentions> "
				+ "<http://de.dkt.sesame/ontology/doc2>"
				+ ""));

	}

	@Test
	public void testESesameCheckStoreStringLocalFilesystemStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test3")
//				.queryString("storagePath", null)
				.queryString("storageCreate", true)
				.queryString("inputData", inputData)
				.queryString("inputDataFormat", "param")
				.queryString("inputDataMimeType", "NIF")
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

		HttpResponse<String> response2 = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test4")
//				.queryString("storagePath", null)
				.queryString("storageCreate", true)
				.queryString("inputData", inputData)
				.queryString("inputDataFormat", "body")
				.queryString("inputDataMimeType", "NIF")
				.body(inputData)
				.asString();

		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
	}

	@Test
	public void testESesameCheckStoreTripletLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				.queryString("inputDataFormat", "NOT IMPORTANT")
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
		assertTrue(response.getBody().equalsIgnoreCase(""
				+ "<http://de.dkt.sesame/ontology/doc1> "
				+ "<http://de.dkt.sesame/ontology/mentions> "
				+ "<http://de.dkt.sesame/ontology/doc2>"
				+ ""));

	}

	@Test
	public void testESesameCheckStoreStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		HttpResponse<String> response = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test3")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				.queryString("inputData", inputData)
				.queryString("inputDataFormat", "param")
				.queryString("inputDataMimeType", "NIF")
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

		HttpResponse<String> response2 = storageRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test4")
				.queryString("storagePath", "storage")
				.queryString("storageCreate", true)
				.queryString("inputData", inputData)
				.queryString("inputDataFormat", "body")
				.queryString("inputDataMimeType", "NIF")
				.body(inputData)
				.asString();

		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
	}
	
	@Test
	public void testESesameCheckRetrieveTripletLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\">\n"
				+"\n"
			+"<rdf:Description rdf:about=\"http://de.dkt.sesame/ontology/doc1\">\n"
			+"	<mentions xmlns=\"http://de.dkt.sesame/ontology/\" rdf:resource=\"http://de.dkt.sesame/ontology/doc2\"/>\n"
			+"</rdf:Description>\n"
			+"\n"
			+"</rdf:RDF>"
			+ "";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "triplet")
				.queryString("inputData", "")
				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());

		response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", "")
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "triplet")
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
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
				.queryString("inputDataType", "triplet")
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
	public void testESesameCheckRetrieveEntityStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\">\n"
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
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
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
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
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
				.queryString("outformat", "turtle")
				.queryString("storageName", "test2")
				.queryString("storagePath", "storage")
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
	public void testESesameCheckRetrieveSparqlStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\">\n"
				+"\n"
			+"<rdf:Description rdf:about=\"http://de.dkt.sesame/ontology/doc1\">\n"
			+"	<mentions xmlns=\"http://de.dkt.sesame/ontology/\" rdf:resource=\"http://de.dkt.sesame/ontology/doc2\"/>\n"
			+"</rdf:Description>\n"
			+"\n"
			+"</rdf:RDF>"
			+ "";
		
		String entityURI = "http://de.dkt.sesame/ontology/doc1";
		String sparqlQuery = "select ?s ?p ?o where {\n" +
		        " <"+entityURI+"> ?p ?o \n" +
//		        " ?s <"+entityURI+"> ?o.\n" +
//		        " ?s ?p <"+entityURI+"> \n" +
		        "}";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", sparqlQuery)
				.queryString("outformat", "turtle")
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
	public void testESesameCheckRetrieveNIFStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
		
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<rdf:RDF\n"
				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\">\n"
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
				+"        itsrdf:taIdentRef  <http://de.dkt.sesame/ontology/doc4> .\n"
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
			      "        itsrdf:taIdentRef     <http://de.dkt.sesame/ontology/doc1> .\n";
		
		HttpResponse<String> response = retrievalRequest()
				.queryString("informat", "text/plain")
				.queryString("input", nifQuery)
				.queryString("outformat", "turtle")
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
