package de.dkt.eservices.esesame.modules;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
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
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
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

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import info.aduna.iteration.Iterations;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
public class SesameStorage {

	static Logger logger = Logger.getLogger(SesameStorage.class);

	private static String storageDirectory;

	private static boolean storageCreate;

	@SuppressWarnings("all")
	public static String storeTripletsFromModel(String storageName, Model model) throws ExternalServiceFailedException {
		try {
			File f = null;
			if(storageCreate){
				f = FileFactory.generateOrCreateDirectoryInstance(storageDirectory + storageName);
			}
			else{
				f = FileFactory.generateFileInstance(storageDirectory + storageName);
			}
			Repository rep = new SailRepository(new NativeStore(f));
			rep.initialize();
			RepositoryConnection conn = rep.getConnection();
			try{
				for (Statement st : model) {
					conn.add(st.getSubject(),st.getPredicate(),st.getObject());
				}
			}
			finally{
				conn.close();
				rep.shutDown();
			}
			return "Model has been correctly added to the tripleSTore: "+storageName;
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new BadRequestException(e.getMessage());
		}
	}

	@SuppressWarnings("all")
	public static String storeTriplets(String storageName, String data, String dataFormat) throws ExternalServiceFailedException {
		Repository rep = null; 
		try {
			File f = null;
			if(storageCreate){
				f = FileFactory.generateOrCreateDirectoryInstance(storageDirectory + storageName);
			}
			else{
				f = FileFactory.generateFileInstance(storageDirectory + storageName);
			}			
			rep = new SailRepository(new NativeStore(f));
			rep.initialize();

			Model inputModel;

			InputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
			RDFFormat dFormat = RDFFormat.forMIMEType(dataFormat);
			inputModel = Rio.parse(in, "", dFormat, null);

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
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RDFParseException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			String msg = "";
			if(storageCreate){
				msg = "No storageCreate parameter stablish and ";
			}
			msg += e.getMessage();
			logger.error(msg);
			throw new BadRequestException(msg);
		}
		finally{
			try {
				rep.shutDown();
			} catch (RepositoryException e) {
				logger.error(e.getMessage());
				throw new ExternalServiceFailedException("Error at stoping the sesame repository");
			}
		}
	}

	@SuppressWarnings("all")
	public static String storeTriplet(String storageName, String sSubject, String sPredicate, String sObject, String sNameSpace) throws ExternalServiceFailedException {
		try{
			File fil = null;
			if(storageCreate){
				fil = FileFactory.generateOrCreateDirectoryInstance(storageDirectory + storageName);
			}
			else{
				fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			}
			
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();

			ValueFactory f  = rep.getValueFactory();
			String namespace = (sNameSpace==null) ? "" : sNameSpace;//"http://dkt.dfki.de/";
			URI subject = f.createURI(namespace, sSubject);
			URI predicate = f.createURI(namespace, sPredicate);
			URI object = null;
			Literal lObject = null;
			boolean isResource = true;
			if(!sObject.startsWith("http") || sObject.startsWith("\"")){
				isResource=false;
				lObject = f.createLiteral(sObject); 
			}
			else{
				object = f.createURI(namespace, sObject);
			}
			ValueFactoryImpl vfi = new ValueFactoryImpl();
			RepositoryConnection conn = rep.getConnection();
			try{
				if(isResource){
					conn.add(subject, predicate, object);
				}
				else{
					conn.add(subject, predicate, lObject);
				}
			}
			finally{
				conn.close();
				rep.shutDown();
			}
			return "<"+subject+"> <"+predicate+"> <"+object+"> has been properly included in tripleSTORE: "+storageName;
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	@SuppressWarnings("all")
	public static String retrieveTriplets(String storageName, String sSubject, String sPredicate, String sObject, String outformat) throws ExternalServiceFailedException {
		try{
			File fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			if(!fil.exists()){
				throw new ExternalServiceFailedException("Triple store does not exist.!!!");
			}
			
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();
			RepositoryConnection conn = rep.getConnection();
			try{
				ValueFactory f  = rep.getValueFactory();
				URI subject = (sSubject==null) ? null : f.createURI(sSubject);
				URI predicate = (sPredicate==null) ? null : f.createURI(sPredicate);
				URI object = (sObject==null) ? null : f.createURI(sObject);
				
				RepositoryResult<Statement> statements =  conn.getStatements(subject, predicate, object, true);

				Model model = Iterations.addAll(statements, new LinkedHashModel());
				ModelManagement.addNamespaces(model);
				RDFFormat format = RDFFormat.forMIMEType(outformat);
				return ModelManagement.model2String(model, format);

			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(IOException e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RDFHandlerException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static String retrieveTriplets(String storageName, String inputRDFData, String outformat) throws ExternalServiceFailedException {
		try{
//			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);

			File fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(fil, ""));
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
				ModelManagement.addNamespaces(model);
				RDFFormat format = RDFFormat.forMIMEType(outformat);
				return ModelManagement.model2String(model, format);
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RDFHandlerException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static String retrieveTripletsFromSPARQL(String storageName, String inputSPARQLData, String outformat) throws ExternalServiceFailedException {
		try{
//			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
			File fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();
			RepositoryConnection conn = rep.getConnection();
			try{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				SPARQLResultsXMLWriter xmlWriter = new SPARQLResultsXMLWriter(bos);
//				SPARQLResultsJSONWriter jsonWriter = new SPARQLResultsJSONWriter(bos);
//				String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
				//System.out.println("QUERY: "+inputSPARQLData);
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, inputSPARQLData);
//				tupleQuery.evaluate(jsonWriter);
				tupleQuery.evaluate(xmlWriter);
				//return new String(bos.toByteArray());
				return bos.toString("UTF-8"); // PB: trying this for encoding issue. Works locally, so unfortunately have to commit to test
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(MalformedQueryException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(QueryEvaluationException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(TupleQueryResultHandlerException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}
	
	public static List<BindingSet> retrieveTQRTripletsFromSPARQL(String storageName, String inputSPARQLData) throws ExternalServiceFailedException {
		try{
			List<BindingSet> list = new LinkedList<BindingSet>();
//			ClassPathResource cpr = new ClassPathResource(storageDirectory + storageName);
			File fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();

			RepositoryConnection conn = rep.getConnection();
			try{
//				SPARQLResultsJSONWriter jsonWriter = new SPARQLResultsJSONWriter(bos);
//				String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
				//System.out.println("QUERY: "+inputSPARQLData);
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, inputSPARQLData);
//				tupleQuery.evaluate(jsonWriter);
				TupleQueryResult result = tupleQuery.evaluate();
				while(result.hasNext()){
					list.add(result.next());
				}
//				System.out.println("JULIAN: "+result.hasNext());
				return list;
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(MalformedQueryException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(QueryEvaluationException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}
	
	public static String retrieveTripletsFromNIF(String storageName, String nifData, String outformat) throws ExternalServiceFailedException {
		try{
			File fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();

			RepositoryConnection conn = rep.getConnection();
			try{
				ValueFactory f  = rep.getValueFactory();
				Model model = null;
				
				com.hp.hpl.jena.rdf.model.Model nifModel = null;
				try{
					nifModel = NIFReader.extractModelFromFormatString(nifData, RDFSerialization.TURTLE);
				}
				catch(Exception e){
					nifModel = NIFReader.extractModelFromFormatString(nifData, RDFSerialization.RDF_XML);
				}
				
				List<String> entities = NIFReader.extractTaIdentRefsFromModel(nifModel);

				for (String k: entities) {
//					System.out.println("ENTITY: ");
//					for (String string : strings) {
//						System.out.println("\t"+string);
//					}
					String entityText = k;
					RepositoryResult<Statement> statements =  conn.getStatements(f.createURI(entityText), null, null, true);
					if(model==null)
						model = Iterations.addAll(statements, new LinkedHashModel());
					else
						model.addAll(Iterations.addAll(statements, new LinkedHashModel()));
					RepositoryResult<Statement> statements2 =  conn.getStatements(null, f.createURI(entityText), null, true);
					if(model==null)
						model = Iterations.addAll(statements2, new LinkedHashModel());
					else
						model.addAll(Iterations.addAll(statements2, new LinkedHashModel()));
					RepositoryResult<Statement> statements3 =  conn.getStatements(null, null, f.createURI(entityText), true);
					if(model==null)
						model = Iterations.addAll(statements3, new LinkedHashModel());
					else
						model.addAll(Iterations.addAll(statements3, new LinkedHashModel()));
//					Rio.write(model, System.out, RDFFormat.TURTLE);
				}
				ModelManagement.addNamespaces(model);
				RDFFormat format = RDFFormat.forMIMEType(outformat);
				return ModelManagement.model2String(model, format);			}
			catch(Exception e){
				e.printStackTrace();
				conn.close();
				rep.shutDown();
				throw new BadRequestException("Input NIF is not TURTLE format.");
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static String retrieveTripletsFromNIFIterative(String storageName, String nifData, int iterations) throws ExternalServiceFailedException {
		try{
			File fil = FileFactory.generateFileInstance(storageDirectory + storageName);
			Repository rep = new SailRepository(new NativeStore(fil, ""));
			rep.initialize();

			RepositoryConnection conn = rep.getConnection();
			try{
				ValueFactory f  = rep.getValueFactory();
				Model model = null;
				
				com.hp.hpl.jena.rdf.model.Model nifModel = null;
				try{
					nifModel = NIFReader.extractModelFromFormatString(nifData, RDFSerialization.TURTLE);
				}
				catch(Exception e){
					nifModel = NIFReader.extractModelFromFormatString(nifData, RDFSerialization.RDF_XML);
				}
				
				Map<String,Map<String,String>> entities = NIFReader.extractEntitiesExtended(nifModel);

				Set<String> keys = entities.keySet();
				
				for (String k: keys) {
//					System.out.println("ENTITY: ");
//					for (String string : strings) {
//						System.out.println("\t"+string);
//					}
					List<String> uris = new LinkedList<String>();
					
					Map<String,String> attributes = entities.get(k);
					Set<String> keys2 = attributes.keySet();

					//System.out.println("ADDING TO URIS: "+k);
					uris.add(k);
					for (String k2 : keys2) {
						String object = attributes.get(k2);
						if(object.startsWith("http") || object.startsWith("<http")){
							if(k2.equalsIgnoreCase(ITSRDF.taIdentRef.getURI()) || 
									k2.equalsIgnoreCase(NIF.referenceContext.getURI())){
								uris.add(object);
								//System.out.println("ADDING TO URIS: "+object);
							}
						}
						else{
							ValueFactory vf = new ValueFactoryImpl();
							Value v = vf.createLiteral(object);
							RepositoryResult<Statement> statements =  null;
							statements = conn.getStatements(null, f.createURI(k2), v, true);
//							if(k2.equalsIgnoreCase(NIF.birthDate.getURI())){
//								statements = conn.getStatements(null, f.createURI(k2), v, true);
//							}
//							else if(k2.equalsIgnoreCase(NIF.deathDate.getURI())){
//								statements = conn.getStatements(null, f.createURI(k2), f.createURI(object), true);
//							}
//							else if(k2.equalsIgnoreCase(NIF.geoPoint.getURI())){
//								statements = conn.getStatements(null, f.createURI(k2), f.createURI(object), true);
//							}
//							else if(k2.equalsIgnoreCase(NIF.anchorOf.getURI())){
//								statements = conn.getStatements(null, f.createURI(k2), f.createURI(object), true);
//							}
//							else if(k2.equalsIgnoreCase(NIF.normalizedDate.getURI())){
//								statements = conn.getStatements(null, f.createURI(k2), f.createURI(object), true);
//							}
							if(statements!=null && statements.hasNext()){
								if(model==null)
									model = Iterations.addAll(statements, new LinkedHashModel());
								else
									model.addAll(Iterations.addAll(statements, new LinkedHashModel()));
							}
						}
					}
					
					for (String u : uris) {
						//System.out.println(u);
						RepositoryResult<Statement> statements =  conn.getStatements(f.createURI(u), null, null, true);
						if(model==null)
							model = Iterations.addAll(statements, new LinkedHashModel());
						else
							model.addAll(Iterations.addAll(statements, new LinkedHashModel()));
						RepositoryResult<Statement> statements2 =  conn.getStatements(null, f.createURI(u), null, true);
						if(model==null)
							model = Iterations.addAll(statements2, new LinkedHashModel());
						else
							model.addAll(Iterations.addAll(statements2, new LinkedHashModel()));
						RepositoryResult<Statement> statements3 =  conn.getStatements(null, null, f.createURI(u), true);
						if(model==null)
							model = Iterations.addAll(statements3, new LinkedHashModel());
						else
							model.addAll(Iterations.addAll(statements3, new LinkedHashModel()));
					}
//					Rio.write(model, System.out, RDFFormat.TURTLE);
				}
				ModelManagement.addNamespaces(model);
				return ModelManagement.model2String(model, RDFFormat.TURTLE);
			}
			catch(Exception e){
				logger.error(e.getMessage());
				throw new BadRequestException("Input NIF is not TURTLE format.");
			}
			finally{
				conn.close();
				rep.shutDown();
			}
		}
		catch(FileNotFoundException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch(IOException e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
		catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static String getStorageDirectory() {
		return storageDirectory;
	}

	public static void setStorageDirectory(String storageDirectory) {
		SesameStorage.storageDirectory = storageDirectory;
	}

	public static boolean isStorageCreate() {
		return storageCreate;
	}

	public static void setStorageCreate(boolean storageCreate) {
		SesameStorage.storageCreate = storageCreate;
	}

	public static void main(String[] args) throws Exception{
		
//		System.out.println(SesameStorage.storeTriplet("triplet2", "http://dkt.dfki.de/file2.txt", "http://dkt.dfki.de/ontology#isPartOf", "http://dkt.dfki.de/file3.txt", ""));
		
		
		SesameStorage.setStorageDirectory("/Users/jumo04/git/e-Sesame/target/test-classes/storage/");
//		SesameStorage.setStorageDirectory("storage/");
		System.out.println(SesameStorage.retrieveTriplets("test2/", "http://de.dkt.sesame/ontology/doc1", null, null, "application/rdf+xml"));
//		System.out.println(SesameStorage.retrieveTriplets("test2", null, null, null, "application/rdf+xml"));
//	
//		String sparqlQuery = 
//				"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos/>\n" +
//				"PREFIX sws: <http://sws.geonames.org/>\n" +
//				"SELECT ?lat ?long WHERE { \n" +
//				"?geoNameId geo:lat ?lat . \n" +
//				"?geoNameId geo:long ?long . \n" +
//				"FILTER (?geoNameId = <http://sws.geonames.org/2755003/>) \n" +
//				"}";	
//			SesameStorage.setStorageDirectory("C:\\Users\\pebo01\\workspace\\e-Sesame\\target\\test-classes\\ontologies\\");
////			List<BindingSet> sets = SesameStorage.retrieveTQRTripletsFromSPARQL("geoFinal", sparqlQuery);
////			
////			String lat = null;
////			String lon = null;
////			for (BindingSet bs : sets) {
////				lat = bs.getValue("lat").toString();
////				lon = bs.getValue("long").toString();
////			}
////			
////			System.out.println("LATITUDE:" + lat);
////			System.out.println("LONGITUDE:" + lon);
//			System.out.println("OUTPUT DEBUG: "+SesameStorage.retrieveTripletsFromSPARQL("geoFinal", sparqlQuery));
				
	}
}
