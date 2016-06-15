# e-Sesame

The e-Sesame module performs storage and retrieval of semantic information. It is a triple storage system that allows users to store and retrieve semantic information and even develop reasoning through its SPARQL interface.

## Storage of semantic information

The Storage of semantic information endpoint allows to store information provided in different types. 

### Endpoint

http://api.digitale-kuratierung.de/api/e-sesame/storeData

### Input
The API conforms to the general NIF API specifications. For more details, see: http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html
In addition to the input, informat and outformat parameters, the following parameters have to be set to perform Semantic Information Storage:  

`storageName`: name of the sesame (triple storage) where the information must be stored.

`storagePath`: (optional) path of the sesame (triple storage) where the information must be stored.

`storageCreate`: boolean value defining if the repository has to be created.

`inputDataFormat`: parameter that specifies the format in which the information is provided to the service. It can have three different values: `param`, `body`, or `triple`.

`inputDataMimeType`: in case that the service is receiving a string (`param` and `body` formats), this parameter specifies the mime type of the string (RDF/XML, JSON).

If the `inputDataFormat` is `param`: 
- `input`: the input is a string and is given as parameter. 

If the `inputDataFormat` is `body`: 
- `body`: the input is a string and is given in the body of the request. 

If the `inputDataFormat` is `triple`: the information to be stored is given as a triple defined by its three properties:  
- `subject`: subject of the triple.
- `predicate`: predicate of the triple.
- `object`: object of the triple.

`namespace`: ?????

### Output
A document in NIF format annotated with ......

Example cURL post for using the `semantic information storage`:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-sesame/storeData?storageName=sesame2&inputDataFormat=param&inputDataMimeType=text&namespace=nose&input=Welcome+to+Berlin+in+2016."`


## Retrieval of semantic information

The Retrieval of semantic information endpoint offers different ways of accessing semantic information. 

### Endpoint

http://api.digitale-kuratierung.de/api/e-sesame/retrieveData

### Input
The API conforms to the general NIF API specifications. For more details, see: http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html
In addition to the input, informat and outformat parameters, the following parameters have to be set to perform Semantic Information Retrieval:  

`storageName`: name of the sesame (triple storage) where the information must be stored.

`storagePath`: (optional) path of the sesame (triple storage) where the information must be stored.

`inputDataType`: parameter that specifies the format in which the query is provided to the service. It can have four different values: `NIF`, `entity`, `sparql` or `triple`.

If the `inputDataType` is `NIF`, `entity` or `sparql`: 
- `input`: the query is a string and is given as parameter. 

If the `inputDataType` is `triple`: the service will retrieve triples that fit one or more of the following elements:
- `subject`: subject of the triple.
- `predicate`: predicate of the triple.
- `object`: object of the triple.

### Output
A JSON string containing the retrieved triples.

Example cURL post for using the `semantic information storage`:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-sesame/retrieveData?storageName=sesame2&inputDataType=entity&input=Mendelsohn."`

