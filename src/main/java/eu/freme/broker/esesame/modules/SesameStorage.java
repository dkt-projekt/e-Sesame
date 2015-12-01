package eu.freme.broker.esesame.modules;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.nativerdf.NativeStore;
import org.springframework.core.io.ClassPathResource;

import eu.freme.broker.esesame.exceptions.BadRequestException;
import eu.freme.broker.esesame.exceptions.ExternalServiceFailedException;
import eu.freme.broker.esesame.nif.NIF;
import info.aduna.iteration.Iterations;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
public class SesameStorage {

//	private static String storageDirectory = "triplestore/";
	private static String storageDirectory = "C:\\Users\\jmschnei\\Desktop\\dkt-test\\sesame\\";

	@SuppressWarnings("all")
	public static String storeTriplets(String storageName, String data, String dataFormat) throws ExternalServiceFailedException {
		try {
			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(cpr.getFile()));
			rep.initialize();

			InputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
			RDFFormat dFormat = RDFFormat.forMIMEType(dataFormat);
			Model inputModel = Rio.parse(in, "", dFormat, null);

			RepositoryConnection conn = rep.getConnection();
			try{
				conn.add(inputModel, null);
			}
			finally{
				conn.close();
				rep.shutDown();
			}
			return dataFormat + " has been correctly added to the tripletSTore: "+storageName;
		}
		catch (RepositoryException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RDFParseException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new BadRequestException(e.getMessage());
		}
	}

	@SuppressWarnings("all")
	public static String storeTriplet(String storageName, String sSubject, String sPredicate, String sObject, String sNameSpace) throws ExternalServiceFailedException {
		try{
			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
//			File fil = cpr.getFile();
			File fil = new File(storageDirectory + "" + storageName);
			if(!fil.exists()){
				fil.mkdir();
				System.out.println("Dir created:");
			}
			
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();

			ValueFactory f  = rep.getValueFactory();
			String namespace = (sNameSpace==null) ? "" : sNameSpace;//"http://dkt.dfki.de/";
			URI subject = f.createURI(namespace, sSubject);
			URI predicate = f.createURI(namespace, sPredicate);
			URI object = f.createURI(namespace, sObject);
//			URI subject = f.createURI(sSubject);
//			URI predicate = f.createURI(sPredicate);
//			URI object = f.createURI(sObject);

			RepositoryConnection conn = rep.getConnection();
			try{
				conn.add(subject, predicate, object);
			}
			finally{
				conn.close();
				rep.shutDown();
			}
			return "Triplet properly stored in: "+storageName;
		}
		catch (RepositoryException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
//		catch(FileNotFoundException e){
//			e.printStackTrace();
//			throw new ExternalServiceFailedException(e.getMessage());
//		}
//		catch(IOException e){
//			e.printStackTrace();
//			throw new ExternalServiceFailedException(e.getMessage());
//		}
	}

	@SuppressWarnings("all")
	public static String retrieveTriplets(String storageName, String sSubject, String sPredicate, String sObject) throws ExternalServiceFailedException {
		try{
			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
//			File fil = cpr.getFile();
			File fil = new File(storageDirectory + "" + storageName);
			if(!fil.exists()){
				throw new ExternalServiceFailedException("Triplet store does not exist.!!!");
			}
			
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();

			RepositoryConnection conn = rep.getConnection();
			try{
				ValueFactory f  = rep.getValueFactory();
				//				String namespace = "http://dkt.dfki.de/";
				//				URI subject = f.createURI(namespace, sSubject);
				//				URI predicate = f.createURI(namespace, sPredicate);
				//				URI object = f.createURI(namespace, sObject);
				URI subject = (sSubject==null) ? null : f.createURI(sSubject);
				URI predicate = (sPredicate==null) ? null : f.createURI(sPredicate);
				URI object = (sObject==null) ? null : f.createURI(sPredicate);
				
				RepositoryResult<Statement> statements =  conn.getStatements(subject, predicate, object, true);

				org.openrdf.model.Model model = Iterations.addAll(statements, new LinkedHashModel());

				model.setNamespace("rdf", org.openrdf.model.vocabulary.RDF.NAMESPACE);
				model.setNamespace("rdfs", RDFS.NAMESPACE);
				model.setNamespace("xsd", XMLSchema.NAMESPACE);
				model.setNamespace("foaf", FOAF.NAMESPACE);
				model.setNamespace("nif", NIF.getURI());

				StringWriter sw = new StringWriter();
				//				Rio.write(model, System.out, RDFFormat.TURTLE);
				Rio.write(model, sw, RDFFormat.TURTLE);
				return sw.toString();
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
//		catch(FileNotFoundException e){
//			e.printStackTrace();
//			throw new ExternalServiceFailedException(e.getMessage());
//		}
//		catch(IOException e){
//			e.printStackTrace();
//			throw new ExternalServiceFailedException(e.getMessage());
//		}
		catch (RepositoryException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RDFHandlerException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static String retrieveTriplets(String storageName, String inputRDFData) throws ExternalServiceFailedException {
		try{
			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(cpr.getFile(), ""));
			rep.initialize();

			RepositoryConnection conn = rep.getConnection();
			try{
				ValueFactory f  = rep.getValueFactory();

				org.openrdf.model.Model model;
				
				RepositoryResult<Statement> statements =  conn.getStatements(f.createURI(inputRDFData), null, null, true);
				model = Iterations.addAll(statements, new LinkedHashModel());

				RepositoryResult<Statement> statements2 =  conn.getStatements(null, f.createURI(inputRDFData), null, true);
				model.addAll(Iterations.addAll(statements2, new LinkedHashModel()));

				RepositoryResult<Statement> statements3 =  conn.getStatements(null, null, f.createURI(inputRDFData), true);
				model.addAll(Iterations.addAll(statements3, new LinkedHashModel()));

				model.setNamespace("rdf", org.openrdf.model.vocabulary.RDF.NAMESPACE);
				model.setNamespace("rdfs", RDFS.NAMESPACE);
				model.setNamespace("xsd", XMLSchema.NAMESPACE);
				model.setNamespace("foaf", FOAF.NAMESPACE);
				model.setNamespace("nif", NIF.getURI());

				StringWriter sw = new StringWriter();
				//Rio.write(model, System.out, RDFFormat.TURTLE);
				Rio.write(model, sw, RDFFormat.RDFXML);
				return sw.toString();
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RDFHandlerException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static String retrieveTripletsFromSPARQL(String storageName, String inputSPARQLData) throws ExternalServiceFailedException {
		try{
			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(cpr.getFile(), ""));
			rep.initialize();

			RepositoryConnection conn = rep.getConnection();
			try{
				StringWriter sw = new StringWriter();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				SPARQLResultsJSONWriter jsonWriter = new SPARQLResultsJSONWriter(bos);

//				String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, inputSPARQLData);
				tupleQuery.evaluate(jsonWriter);

				return new String(bos.toByteArray());
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(MalformedQueryException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(QueryEvaluationException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(TupleQueryResultHandlerException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception{
		
		System.out.println(SesameStorage.storeTriplet("triplet2", "http://dkt.dfki.de/file2.txt", "http://dkt.dfki.de/ontology#isPartOf", "http://dkt.dfki.de/file3.txt", ""));
		System.out.println(SesameStorage.retrieveTriplets("triplet2", null, null, null));
	
	}
}
