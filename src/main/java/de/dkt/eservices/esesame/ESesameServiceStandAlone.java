package de.dkt.eservices.esesame;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.dkt.common.tools.ParameterChecker;
import de.dkt.common.tools.ResponseGenerator;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

@RestController
public class ESesameServiceStandAlone extends BaseRestController {
	
	Logger logger = Logger.getLogger(ESesameServiceStandAlone.class);

	@Autowired
	ESesameService service;

	@RequestMapping(value = "/e-sesame/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
	@RequestMapping(value = "/e-sesame/storeData", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> storeData(
			@RequestParam(value = "storageName", required = false) String storageName,
			@RequestParam(value = "storagePath", required = false) String storagePath,
			@RequestParam(value = "storageCreate", required = false) boolean storageCreate,
			@RequestParam(value = "inputDataFormat", required = false) String inputDataFormat,
			@RequestParam(value = "inputDataMimeType", required = false) String inputDataMimeType,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "subject", required = false) String subj,
			@RequestParam(value = "predicate", required = false) String pred,
			@RequestParam(value = "object", required = false) String obj,
			@RequestParam(value = "namespace", required = false) String nam,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkNotNullOrEmpty(inputDataFormat, "input data format", logger);
		ParameterChecker.checkNotNullOrEmpty(inputDataMimeType, "input data type", logger);
		ParameterChecker.checkNotNullOrEmpty(storageName, "storage Name", logger);

        try {
        	String nifResult = null;
    		if(inputDataFormat.equalsIgnoreCase("triple")){
            	if(subj!=null && pred!=null && obj!=null){
            		nifResult = service.storeEntitiesFromTriplet(storageName, storagePath, storageCreate, subj, pred, obj, nam);
            	}
        		else{
        			logger.error("Input triple is NULL");
        			throw new BadRequestException("Input triple is NULL");
        		}
    		}
    		else if(inputDataFormat.equalsIgnoreCase("param")){
    			nifResult = service.storeEntitiesFromString(storageName, storagePath, storageCreate, input, inputDataMimeType);
    		}
    		else if(inputDataFormat.equalsIgnoreCase("body")){
    			nifResult = service.storeEntitiesFromString(storageName, storagePath, storageCreate, postBody, inputDataMimeType);
    		}
    		else{
    			logger.error("Input data is not in the proper format ...");
    			throw new BadRequestException("Input data is not in the proper format ...");
    		}
           	return ResponseGenerator.successResponse(nifResult, "text/plain");
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
        } catch (ExternalServiceFailedException e) {
        	logger.error(e.getMessage());
        	throw e;
        }
	}
	
	@RequestMapping(value = "/e-sesame/retrieveData", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> retrieveData(
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,

			@RequestParam(value = "storageName", required = false) String storageName,
			@RequestParam(value = "storagePath", required = false) String storagePath,
			@RequestParam(value = "inputDataType", required = false) String inputDataType,
			@RequestParam(value = "subject", required = false) String subj,
			@RequestParam(value = "predicate", required = false) String pred,
			@RequestParam(value = "object", required = false) String obj,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkNotNullOrEmpty(storageName, "storage Name", logger);
        
        try {
    		if(inputDataType.equalsIgnoreCase("triple")){
    			if(subj!=null || pred!=null || obj!=null){
    				String nifResult = service.retrieveEntitiesFromTriplet(storageName, storagePath, subj, pred, obj, outformat);
    				System.out.println("NIF RESULT: "+nifResult);
    				return ResponseGenerator.successResponse(nifResult, outformat);
    			}
    			else{
        			logger.error("Input triple is NULL");
        			throw new BadRequestException("Input triple is NULL");
    			}
        	}
        	else{
        		ParameterChecker.checkNotNullOrEmpty(inputDataType, "input data type", logger);
                NIFParameterSet nifParameters = this.normalizeNif(input, acceptHeader, contentTypeHeader, allParams, true);
                String textForProcessing = null;

                if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
                    // input is sent as value of the input parameter
                    textForProcessing = nifParameters.getInput();
        			//rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
                } else {
                    //inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
                	textForProcessing = postBody;
                    if (textForProcessing == null) {
                        logger.error("No text to process.");
                        throw new BadRequestException("No text to process.");
                    }
                }
            	String nifResult = null;
        		if(inputDataType.equalsIgnoreCase("NIF")){
        			nifResult = service.retrieveEntitiesFromNIF(storageName, storagePath, textForProcessing, outformat);
        		}
        		else if(inputDataType.equalsIgnoreCase("entity")){
        			nifResult = service.retrieveEntitiesFromString(storageName, storagePath, textForProcessing, outformat);
        		}
        		else if(inputDataType.equalsIgnoreCase("sparql")){
        			nifResult = service.retrieveEntitiesFromSPARQL(storageName, storagePath, textForProcessing, outformat);
        		}
        		else{
        			logger.error("Input data is not in the proper format ...");
        			throw new BadRequestException("Input data is not in the proper format ...");
        		}
               	return ResponseGenerator.successResponse(nifResult, outformat);
        	}
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
        } catch (ExternalServiceFailedException e) {
        	logger.error(e.getMessage());
            throw e;
        }
	}

}
