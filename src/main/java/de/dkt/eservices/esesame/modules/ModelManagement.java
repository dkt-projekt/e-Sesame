package de.dkt.eservices.esesame.modules;

import java.io.StringWriter;

import org.openrdf.model.Model;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.TIME;

public class ModelManagement {

	public static void addNamespaces(Model model){
		model.setNamespace("rdf", org.openrdf.model.vocabulary.RDF.NAMESPACE);
		model.setNamespace("rdfs", RDFS.NAMESPACE);
		model.setNamespace("xsd", XMLSchema.NAMESPACE);
		model.setNamespace("foaf", FOAF.NAMESPACE);
		model.setNamespace("nif", NIF.getURI());
		model.setNamespace("dbo", DBO.uri);
		model.setNamespace("geo", GEO.uri);
		model.setNamespace("time", TIME.uri);
	}
	
	public static String model2String (Model m,RDFFormat format) throws RDFHandlerException {
		StringWriter sw = new StringWriter();
		Rio.write(m, sw, format);
		return sw.toString();
	}
}
