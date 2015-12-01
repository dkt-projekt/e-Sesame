/**
 * Copyright (C) 2015 3pc, Art+Com, Condat, Deutsches Forschungszentrum 
 * für Künstliche Intelligenz, Kreuzwerke (http://)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.freme.broker.esesame.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.freme.broker.eservices.BaseRestController;
import eu.freme.broker.esesame.exceptions.BadRequestException;
import eu.freme.broker.esesame.exceptions.ExternalServiceFailedException;
import eu.freme.broker.tools.ExceptionHandlerService;
import eu.freme.broker.tools.NIFParameterFactory;
import eu.freme.broker.tools.NIFParameterSet;
import eu.freme.broker.tools.RDFELinkSerializationFormats;
import eu.freme.broker.tools.RDFSerializationFormats;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConversionService;


@RestController
public class ESesameServiceStandAlone extends BaseRestController {
	
	@Autowired
	ESesameService service;
	
	@Autowired
	RDFConversionService rdfConversionService;

	@Autowired
	NIFParameterFactory nifParameterFactory;

	@Autowired
	RDFSerializationFormats rdfSerializationFormats;

	@Autowired
	RDFELinkSerializationFormats rdfELinkSerializationFormats;
	
	@Autowired
	ExceptionHandlerService exceptionHandlerService;

	@RequestMapping(value = "/e-sesame/storeData", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> storeData(
			@RequestParam(value = "storageName", required = false) String storageName,
			@RequestParam(value = "inputDataFormat", required = false) String inputDataFormat,
			@RequestParam(value = "inputDataMimeType", required = false) String inputDataMimeType,
			@RequestParam(value = "inputData", required = false) String inputData,
			@RequestParam(value = "subject", required = false) String subj,
			@RequestParam(value = "predicate", required = false) String pred,
			@RequestParam(value = "object", required = false) String obj,
			@RequestParam(value = "namespace", required = false) String nam,
            @RequestBody(required = false) String postBody) throws Exception {

		ESesameService.checkNotNullOrEmpty(inputDataFormat, "input data type");
		ESesameService.checkNotNullOrEmpty(inputDataMimeType, "input data type");
		ESesameService.checkNotNullOrEmpty(storageName, "storage Name");
//		ESesameService.checkNotNullOrEmpty(inputDataType, "input Data");
        
        try {
        	if(subj!=null && pred!=null && obj!=null){
        		return service.storeEntitiesFromTriplet(storageName, subj, pred, obj, nam);
        	}
        	else{
        		if(inputDataFormat.equalsIgnoreCase("param")){
        			return service.storeEntitiesFromString(storageName, inputData, inputDataMimeType);
        		}
        		else if(inputDataFormat.equalsIgnoreCase("body")){
        			return service.storeEntitiesFromString(storageName, postBody, inputDataMimeType);
        		}
        		else{
        			throw new BadRequestException("Input data is not in the proper format ...");
        		}
        	}
        } catch (BadRequestException e) {
            throw e;
        } catch (ExternalServiceFailedException e) {
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
			@RequestParam(value = "inputDataType", required = false) String inputDataType,
			@RequestParam(value = "inputData", required = false) String inputData,
			@RequestParam(value = "subj", required = false) String subj,
			@RequestParam(value = "pred", required = false) String pred,
			@RequestParam(value = "obj", required = false) String obj,
            @RequestBody(required = false) String postBody) throws Exception {

		ESesameService.checkNotNullOrEmpty(inputDataType, "input data type");
		ESesameService.checkNotNullOrEmpty(inputDataType, "storage Name");
//		ESesameService.checkNotNullOrEmpty(inputDataType, "input Data");
        
        try {
        	
            NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, true);

        	if(subj!=null || pred!=null || obj!=null){
        		return service.retrieveEntitiesFromTriplet(storageName, subj, pred, obj);
        	}
        	else{
                String textForProcessing = null;

                if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
                    // input is sent as value of the input parameter
                    textForProcessing = nifParameters.getInput();
        			//rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
                } else {
                    //inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
                	textForProcessing = postBody;
                    if (textForProcessing == null) {
                        throw new eu.freme.broker.exception.BadRequestException("No text to process.");
                    }
                }
            	
        		if(inputDataType.equalsIgnoreCase("entity")){
        			return service.retrieveEntitiesFromString(storageName, textForProcessing);
        		}
        		else if(inputDataType.equalsIgnoreCase("sparql")){
        			return service.retrieveEntitiesFromSPARQL(storageName, textForProcessing);
        		}
        		else{
        			throw new BadRequestException("Input data is not in the proper format ...");
        		}
        	}
        } catch (BadRequestException e) {
            throw e;
        } catch (ExternalServiceFailedException e) {
            throw e;
        }
	}

}
