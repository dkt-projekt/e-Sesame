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
	
//	@Test
//	public void test2_StoreTripletLocalFilesystemStorage() throws UnirestException, IOException,Exception {
//		HttpResponse<String> response = storageRequest()
//				.queryString("informat", "text")
//				.queryString("input", "hello world")
//				.queryString("outformat", "turtle")
//				.queryString("storageName", "test2")
//				.queryString("storagePath", indexPath)
//				.queryString("storageCreate", true)
//				.queryString("inputDataFormat", "triple")
//				.queryString("inputDataMimeType", "NOT IMPORTANT")
//				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
//				.queryString("namespace", "")
//				.asString();
//
//		Assert.assertEquals(200, response.getStatus());
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals("<http://de.dkt.sesame/ontology/doc1> "
//				+ "<http://de.dkt.sesame/ontology/mentions> "
//				+ "<http://de.dkt.sesame/ontology/doc2>"
//				+ " has been properly included in tripleSTORE: test2",response.getBody());
//
//	}

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
	
String inputDataTest = "	@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
"		@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
"		@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
"		@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
"		@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
"		@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
"		@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
"		@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
"		@prefix time:  <http://www.w3.org/2006/time#> .\n" +
"\n" +
"		<http://dkt.dfki.de/documents/doc1#char=0,41506>\n" +
"		        a                        nif:String , nif:Context , nif:RFC5147String ;\n" +
"		        dktnif:averageLatitude   \"56.585965447154436\"^^xsd:double ;\n" +
"		        dktnif:averageLongitude  \"12.676017615176164\"^^xsd:double ;\n" +
"		        dktnif:meanDateEnd       \"2159-12-17T04:39:22\"^^xsd:dateTime ;\n" +
"		        dktnif:meanDateStart     \"1709-11-14T04:39:22\"^^xsd:dateTime ;\n" +
"		        dktnif:standardDeviationLatitude\n" +
"		                \"3.7822891901650997\"^^xsd:double ;\n" +
"		        dktnif:standardDeviationLongitude\n" +
"		                \"12.835737456070387\"^^xsd:double ;\n" +
"		        nif:beginIndex           \"0\"^^xsd:nonNegativeInteger ;\n" +
"		        nif:endIndex             \"41506\"^^xsd:nonNegativeInteger ;\n" +
//"		        nif:isString             \"﻿**************************** Page 1***************************\\\\r\\\\nA runestone is typically a raised stone with a runic in- scription, but the term can also be applied to inscrip- tions on boulders and on bedrock. The tradition began in the 4th century and lasted into the 12th century,\"^^xsd:string .\n";
"		        nif:isString             \"﻿**************************** Page 1****************************\\\\r\\\\n\\\\r\\\\n  Runestone \\\\r\\\\n\\\\r\\\\n  The Lingsberg Runestone , Sweden, known as U 240 An early runestone: the Möjbro Runestone from Hagby (first\\\\r\\\\n\\\\r\\\\n  A runestone is typically a raised stone with a runic in- scription, but the term can also be applied to inscrip- tions on boulders and on bedrock. The tradition began in the 4th century and lasted into the 12th century, "
+ "but most of the runestones date from the late Viking Age . Most runestones are located in Scandinavia , but there are also scattered runestones in locations "
+ "that were visited by Norsemen during the Viking Age. Runestones are often memorials todeadmen. Runestoneswereusuallybrightly colouredwhenerected, "
+ "thoughthisisnolongerevidentas the colour has worn off. \\\\r\\\\n\\\\r\\\\n  1 History \\\\r\\\\n\\\\r\\\\n  The tradition of raising stones that had runic inscriptions first "
+ "appeared in the 4th and 5th century, in Norway and Sweden, and these early runestones were usually placed \\\\r\\\\n\\\\r\\\\n  pearedinthe8thand9thcenturies,"
+ "andthereareabout50 \\\\r\\\\n\\\\r\\\\n  Most runestones were erected during the period 950- 1100 CE , and then they were mostly raised in Sweden ,\\\\r\\\\n\\\\r\\\\n  placed near "
+ "Möjebro), Uppland , Sweden. As with other early runic inscriptions, (e.g. Kylver Stone from about 300 - 400 CE) this is written from right to left, while later "
+ "Runestones were written from left to right. The text is “Frawaradaz anahaha is laginaz”. [1] \\\\r\\\\n\\\\r\\\\n  and to a lesser degree in Denmark and Norway . [2] The "
+ "tradition is mentioned in both Ynglinga saga and Hávamál : \\\\r\\\\n\\\\r\\\\n  For men of consequence a mound should be raised to their memory, and for all other warriors "
+ "who had been distinguished for man- hood a standing stone, a custom that remained long after Odin 's time. —The Ynglinga saga [5] \\\\r\\\\n\\\\r\\\\n  What may have "
+ "increased the spread of runestones was an event in Denmark in the 960s. King Harald Bluetooth hadjustbeenbaptisedandinordertomarkthearrivalofa neworderandanewage,"
+ " hecommandedtheconstruction\\\\r\\\\n\\\\r\\\\n  1\\\\r\\\\n\\\\r\\\\n  next to graves. [2] [3] The earliest Danish runestones ap-\\\\r\\\\n\\\\r\\\\nrunestones from the Migration Period in Scandinavia. "
+ "[4]\\\\r\\\\n\\\\r\\\\nof a runestone . [7] The inscription reads\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 2****************************\\\\r\\\\n\\\\r\\\\n  2 DISTRIBUTION\\\\r\\\\n\\\\r\\\\n  "
+ "The Snoldelev stone , one of the oldest runestones in Denmark \\\\r\\\\n\\\\r\\\\n  King Haraldr ordered this monument made in memory of Gormr , his father, and in memory of Þyrvé , "
+ "his mother; that Haraldr who won for himself all of Denmark and Norway and made \\\\r\\\\n\\\\r\\\\n  The runestone has three sides of which two are deco- rated with images. On one side, there is an animal that is the prototype of the runic animals that would be com- monly engraved on runestones, and on another side there is Denmark’s oldest depiction of Jesus . Shortly after this stone had been made, something happened in Scandi- navia’s runic tradition. Scores of chieftains and power- ful Norse clans consciously tried to imitate King Harald, and from Denmark a runestone wave spread northwards through Sweden. In most districts, the fad died out af- ter a generation, but, in the central Swedish provinces of Uppland and Södermanland , the fashion lasted into the \\\\r\\\\n\\\\r\\\\n  2 Distribution \\\\r\\\\n\\\\r\\\\n  runic inscriptions in Scandinavia. [3] There are also rune- stonesinotherpartsoftheworldasthetraditionofraising runestones followed the Norsemen wherever they went, from the Isle of Man ( Manx Runestones ) in the west to\\\\r\\\\n\\\\r\\\\n  Distribution of runestones in Sweden, the country with the highest density. Runestones / km 2 : >10 5-9 1-4 <1 Lacks runestones \\\\r\\\\n\\\\r\\\\n  the Black Sea in the east ( Berezan' Runestone ), "
+ "and from \\\\r\\\\n\\\\r\\\\n  The runestones are unevenly distributed in Scandinavia: Denmark has 250 runestones, Norway has 50 while Ice- \\\\r\\\\n\\\\r\\\\n  and 2,500 [3] [7] depending on definition. The Swedish dis- trict of Uppland has the highest concentration with as many as 1,196 inscriptions in stone, whereas Söderman- \\\\r\\\\n\\\\r\\\\n  Outside of Scandinavia, the Isle of Man stands out with\\\\r\\\\n\\\\r\\\\n  the Danes Christian . [7] [8]\\\\r\\\\n\\\\r\\\\n12th century. [7]\\\\r\\\\n\\\\r\\\\nJämtland in the north to Schleswig in the south. [2]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\nThere are about 3,000 runestones among the about 6,000\\\\r\\\\n\\\\r\\\\nlandhasnone. [4] Swedenhasasmanyasbetween1,700 [4]\\\\r\\\\n\\\\r\\\\nland is second with 391. [7]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 3****************************\\\\r\\\\n\\\\r\\\\n  3\\\\r\\\\n\\\\r\\\\n  century. [9] Scattered runestones have also been found in \\\\r\\\\n\\\\r\\\\n  the exception of the runestone on Berezan' , there are no runestones in Eastern Europe , which probably is due to a lack of available stones and the fact that the local pop- ulation probably did not treat the foreigners’ stones with \\\\r\\\\n\\\\r\\\\n  Runestones were placed on selected spots in the land- scape, suchas assemblylocations , roads, bridgeconstruc- tions, and fords. In medieval "
+ "churches, there are often runestones that have been inserted as construction mate- rial, and it is debated whether they were originally part of the church location or had been moved there. In southern Scania , runestones can be tied to large estates that also had churches constructed on their land. In the Mälaren Valley , the runestones appear to be placed so that they mark essential parts of the domains of an estate, such as courtyard, grave field , and borders to neighbouring es- tates. Runestones usually appear as single monuments and more rarely as pairs. In some cases , they are part \\\\r\\\\n\\\\r\\\\n  However, although scholars know where 95% of all rune- stones were discovered, only about 40% were discov- ered in their original location. The remainder have been found in churches, roads, bridges, graves, farms, and \\\\r\\\\n\\\\r\\\\n  the stones were not moved very far from their original \\\\r\\\\n\\\\r\\\\n  2.1 Effect of religion \\\\r\\\\n\\\\r\\\\n  In many districts, 50% of the stone inscriptions have traces of Christianity, but, in Uppland, which has the highest concentration of runic inscriptions in the world, about 70% of the 1,196 stone inscriptions are explic- itly Christian, which is shown by engraved crosses or added "
+ "Christian prayers, and only a few runestones are \\\\r\\\\n\\\\r\\\\n  Scholars have suggested that the reason why so many Christian runestones were raised in Uppland is that the district was the focal point in the conflict between Norse paganism and the newly Christianized King of Sweden . It is possible that the chieftains tried to demonstrate their allegiance to the king and to display their Christian faith to the world and to God by adding Christian crosses and prayers on their runestones. What speaks against this the- ory is the fact that Norway, Denmark, and Götaland did not have any corresponding development in the runestone tradition. Moreover, not a single runestone declares that \\\\r\\\\n\\\\r\\\\n  ally, the runestones appear to show that the conversion \\\\r\\\\n\\\\r\\\\n  According to another theory, it was a social fashion that was popular among certain clans, but not among all of\\\\r\\\\n\\\\r\\\\n  The Stenkvista runestone in Södermanland, Sweden, shows Thor’s lightning hammer instead of a cross. Only two such rune- stones are known. [13] \\\\r\\\\n\\\\r\\\\n  gun to raise runestones , neighbouring clans emulated them . However, in parts where these clans were less in- fluential, the runestone raising did not reach the same \\\\r\\\\n\\\\r\\\\n  "
+ "Viking expeditions and the considerable amassment of wealth in the district. At this time, Swedish chief- tains near Stockholm had created considerable fortunes through trade and pillaging both in the East and in the West. They had seen the Danish Jelling stones or they had been inspired by Irish high crosses and other \\\\r\\\\n\\\\r\\\\n  The runestones show the different ways in which Chris- tianity changed Norse society, and one of the greatest changes involved no longer burying the deceased on the clan’s grave field among his ancestors. Instead, he was \\\\r\\\\n\\\\r\\\\n  stone would serve as a memorial at the homestead, [18] but for certain families, there was less change as they had \\\\r\\\\n\\\\r\\\\n  3 Inscriptions \\\\r\\\\n\\\\r\\\\n  Themainpurposeofarunestonewastomarkterritory,to explain inheritance, to boast about constructions, to bring glory to dead kinsmen and to tell of important events. In somepartsofUppland,therunestonesalsoappeartohave\\\\r\\\\n\\\\r\\\\n  its 30 runestones from the 9th century and early 11th\\\\r\\\\n\\\\r\\\\nEngland, Ireland, Scotland and the Faroe Islands . [3] With\\\\r\\\\n\\\\r\\\\nmuch respect. [10]\\\\r\\\\n\\\\r\\\\nof larger monuments together with other raised stones. [2]\\\\r\\\\n\\\\r\\\\nwater routes. [11] On the other hand, scholars agree that\\\\r\\\\n\\\\r\\\\nsites. "
+ "[12]\\\\r\\\\n\\\\r\\\\npopularity. [16] Several scholars have pointed out the long\\\\r\\\\n\\\\r\\\\nnot Christian. [7]\\\\r\\\\n\\\\r\\\\nmonuments. [7]\\\\r\\\\n\\\\r\\\\nburied in the cemetery of the church, [17] while the rune-\\\\r\\\\n\\\\r\\\\nchurches built adjoining the family grave field. [19]\\\\r\\\\n\\\\r\\\\nthere was any relationship towards the king. [14] Addition-\\\\r\\\\n\\\\r\\\\nwas a rather peaceful process. [15]\\\\r\\\\n\\\\r\\\\nthem. [14] Once some clans in southern Uppland had be-\\\\r\\\\n\\\\r\\\\nfunctioned as social and economical markers. [14]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 4****************************\\\\r\\\\n\\\\r\\\\n  4 3 INSCRIPTIONS \\\\r\\\\n\\\\r\\\\n  3.1 Stone raisers \\\\r\\\\n\\\\r\\\\n  Most runestones were raised by men and only one rune- stone in eight is raised by a single woman, while at least 10% are raised by a woman together with several men. It is common that the runestones were raised by sons and widows of the deceased, but they could also be raised by sisters and brothers. It is almost only in Uppland, Sö- dermanland, and Öland that women raised runestones to- getherwithmalerelatives. Itisnotknownwhymanypeo- ple such as sisters, brothers, uncles, parents, housecarls , and business partners can be enumerated on runestones, but it is possible that it is because they are part of "
+ "the \\\\r\\\\n\\\\r\\\\n  3.2 Those commemorated \\\\r\\\\n\\\\r\\\\n  A vast majority, 94%, are raised in memory of men, but, contrary to common perception, the vast majority of the runestones are raised in memory of people who died at home. The most famous runestones and those that peo- ple tend to think of are those that tell of foreign voy- \\\\r\\\\n\\\\r\\\\n  and they were raised in usually memory of those not hav- ing returned from Viking expeditions and not as tributes \\\\r\\\\n\\\\r\\\\n  roughly the same message as the majority of the rune- stones, which is that people wanted to commemorate one \\\\r\\\\n\\\\r\\\\n  3.2.1 Expeditions in the East \\\\r\\\\n\\\\r\\\\n  The first man who scholars know fell on the eastern route\\\\r\\\\n\\\\r\\\\n  The Mask Stone (DR 66) found in Aarhus, Denmark commemo- rates a battle between two kings and features a stylized depiction of a mask. \\\\r\\\\n\\\\r\\\\n  Virtually all the runestones from the late Viking Age make use of the same formula. The text tells in memory of whom the runestone is raised, who raised it, and often how the deceased and the one who raised the runestone are related to each other. Also, the inscription can tell the social status of the dead person, possible foreign voy- age, place of death, and also a prayer, as in the following\\\\r\\\\n\\\\r\\\\n  "
+ "wasthe EastGeat Eyvindrwhosefateismentionedonthe \\\\r\\\\n\\\\r\\\\n  Styggr/Stigr made this monument in memory of Eyvindr, his son. He fell in the east with \\\\r\\\\n\\\\r\\\\n  It is unfortunate for historians that the stones rarely re- \\\\r\\\\n\\\\r\\\\n  Västergötland , we are informed only that they died dur- ing a war campaign in the East: “Gulli/Kolli raised this stone in memory of his wife’s brothers Ásbjôrn and Juli, very good valiant men. And they died in the east in the \\\\r\\\\n\\\\r\\\\n  laconically states on the Dalum Runestone : “Tóki and his brothers raised this stone in memory of their brothers.\\\\r\\\\n\\\\r\\\\n  And Danr and Húskarl and Sveinn had the stone erected in memory of Ulfríkr, their fa- ther’s father. He had taken two payments in England . May God and God’s mother help the\\\\r\\\\n\\\\r\\\\n  The single country that is mentioned on most runestone is the Byzantine Empire , which at the time comprised most of Asia Minor and the Balkans , as well as a part of South- ern Italy. If a man died in the Byzantine Empire, no mat- ter how he had died or in which province, the event was mentionedlaconicallyas“hediedinGreece”. Sometimes\\\\r\\\\n\\\\r\\\\n  inheritors. [20]\\\\r\\\\n\\\\r\\\\nages, but they comprise only c. 10% of all runestones, [20]\\\\r\\\\n\\\\r\\\\nto those having returned. [22] These runestones contain\\\\r\\\\n\\\\r\\\\nor several dead kinsmen. [20]\\\\r\\\\n\\\\r\\\\n9th century Kälvesten Runestone . [20] The epitath reads:\\\\r\\\\n\\\\r\\\\nEivísl. Víkingr coloured and Grímulfr. [22] [23]\\\\r\\\\n\\\\r\\\\nexample, [20] the Lingsberg Runestone U 241 :\\\\r\\\\n\\\\r\\\\nveal where the men died. [22] On the Smula Runestone in\\\\r\\\\n\\\\r\\\\nretinue”. [22] [24] Another runemaster in the same province\\\\r\\\\n\\\\r\\\\nOne died in the west, another in the east”. [22] [25]\\\\r\\\\n\\\\r\\\\nsouls of the father and son. [20] [21]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 5****************************\\\\r\\\\n\\\\r\\\\n  3.2 Those commemorated 5 \\\\r\\\\n\\\\r\\\\n  dermanland says: “Inga raised this stone in memory of Óleifr, her ... He ploughed his stern to the east, and met \\\\r\\\\n\\\\r\\\\n  Other Norsemen died in Gardariki (Russia and Ukraine) such as Sigviðr on the Esta Runestone who his son Ingifastr reported had flew in Novgorod ( Holmgarðr ): “He fell in Holmgarðr, the ship’s leader with the \\\\r\\\\n\\\\r\\\\n  from home and it appears that there were close contacts with Estonia due to many personal names such as Æist- fari (“traveller to Estonia”), Æistulfr (“Wolf of Estoni- ans”) and Æistr (“Estonian”). One of the runestones that report of deaths in Estonia is the Ängby Runestone which \\\\r\\\\n\\\\r\\\\n  There were many ways to die as reported by the rune- \\\\r\\\\n\\\\r\\\\n  drowned during a voyage to Livonia , [22] and the Sjonhem Runestone tells that the Gotlander Hróðfúss was killed in a treacherous way by what was probably a people in \\\\r\\\\n\\\\r\\\\n  eastern voyages are the Ingvar Runestones which tell of Ingvar the Far-Travelled 's expedition to Serkland , i.e., the Muslim world. It ended in tragedy as none of the\\\\r\\\\n\\\\r\\\\n  The Kälvesten Runestone , Sweden more than 25 runestones that were raised in its memory\\\\r\\\\n\\\\r\\\\n  3.2.2 Expeditions in the West\\\\r\\\\n\\\\r\\\\n  The Djulafors Runestone , Sweden \\\\r\\\\n\\\\r\\\\n  anexceptioncouldbemadeforSouthernItaly, whichwas known as the land of the Lombards , such as Inga’s Óleifr who, it is presumed, was a member of the Varangian Guard , and about whom the Djulafors Runestone in Sö-\\\\r\\\\n\\\\r\\\\n  The Yttergärde Runestone , Sweden \\\\r\\\\n\\\\r\\\\n  Other Vikings travelled westwards. The Anglo-Saxon rulerspaidlargesums, Danegelds ,toVikings,whomostly\\\\r\\\\n\\\\r\\\\n  his end in the land of the Lombards.” [22] [26]\\\\r\\\\n\\\\r\\\\nseamen.” [22] [27] There were others who died not as far\\\\r\\\\n\\\\r\\\\ntells that a Björn had died in Vironia ( Virland ). [22]\\\\r\\\\n\\\\r\\\\nstones. The Åda Runestone reports that Bergviðr\\\\r\\\\n\\\\r\\\\nthe Balkans . [28] The most famous runestones that tell of\\\\r\\\\n\\\\r\\\\ntells of any survivor. [29]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 6****************************\\\\r\\\\n\\\\r\\\\n  6 3 INSCRIPTIONS \\\\r\\\\n\\\\r\\\\n  Husby-SjuhundrainUppland, diedwhenhewashalf-way to England, as explained on the runestone that was raised \\\\r\\\\n\\\\r\\\\n  toEngland”. [34] [35] OtherVikings,suchasGuðvérdidnot only attack England, but also Saxony , as reported by the \\\\r\\\\n\\\\r\\\\n  There are in total about 30 runestones that tell of peo- \\\\r\\\\n\\\\r\\\\n  Some of them are very laconic and only tell that the \\\\r\\\\n\\\\r\\\\n  The Valleberga Runestone , Sweden, reports that two Vikings had died in London. \\\\r\\\\n\\\\r\\\\n  came from Denmark and who arrived to the English shores during the 990s and the first decades of the 11th century. What may be part of a Danegeld has been found submerged in a creek in Södra Betby in Södermanland, Sweden. At the location, there is also a runestone with the text: \\\"[...] "
+ "raise the stone in memory of Jôrundr, his\\\\r\\\\n\\\\r\\\\n  It is not unlikely that the voyage westwards is connected \\\\r\\\\n\\\\r\\\\n  more explicit with the Danegelds. Ulf of Borresta who \\\\r\\\\n\\\\r\\\\n  as reported on the Yttergärde Runestone : \\\\r\\\\n\\\\r\\\\n  And Ulfr has taken three payments in England. Thatwasthelastthat Tosti paid. Then Þorketill \\\\r\\\\n\\\\r\\\\n  Tosti may have been the Swedish chieftain Skoglar Tosti who is otherwise only mentioned by Snorri Sturluson in Heimskringla andwhoSnorrireportstohavebeena“great warrior” who “was out for long periods of time on war expeditions”. Þorketill was Thorkell the Tall , one of the most famous Viking chieftains, and who often stayed in England. Knútr is no one else but Canute the Great , who \\\\r\\\\n\\\\r\\\\n  Canute sent home most of the Vikings who had helped him conquer England, but he kept a strong bodyguard, the Þingalið . It was considered to be a great honour to be part of this force, and, on the Häggeby Runestone in Uppland, it is reported that Geiri “sat in the Assem- \\\\r\\\\n\\\\r\\\\n  stone mentions Þjalfi “who was with Knútr”. [29] [33] Some Swedish Vikings wanted nothing else but to travel with DanessuchasThorkellandCanutetheGreat,buttheydid not make it to their destinations. Sveinn, who came from\\\\r\\\\n\\\\r\\\\n  Modern runestone on Adelsö near Stockholm, Sweden \\\\r\\\\n\\\\r\\\\n  3.3 Conversion \\\\r\\\\n\\\\r\\\\n  Swedish men who travelled to Denmark, England, or Saxony and the Byzantine Empire played an important \\\\r\\\\n\\\\r\\\\n  two runestones tell of men baptized in Denmark, such as the runestone in Amnö, which says “He died in chris- \\\\r\\\\n\\\\r\\\\n  given on another runestone in Vallentuna near Stockholm that tells that two sons waited until they were on their death beds before they converted: “They died in (their) \\\\r\\\\n\\\\r\\\\n  clothes, hvitavaðir , were given to pagan Scandinavians when they were baptized, and in Uppland there are at least seven stones that tell of convertees having died in \\\\r\\\\n\\\\r\\\\n  The language used by the missionaries appears on several runestones, and they suggest that the missionaries used a \\\\r\\\\n\\\\r\\\\n  pression “light and paradise” is presented on three rune- stones, of which two are located in Uppland and a third on the Danish island Bornholm . The runestone U 160 in Risbyle says “May God and God’s mother help his spirit\\\\r\\\\n\\\\r\\\\n  in his memory : “He died in Jútland . He meant to travel\\\\r\\\\n\\\\r\\\\nGrinda Runestone in Södermanland: [36]\\\\r\\\\n\\\\r\\\\nplewhowenttoEngland, [36] seethe EnglandRunestones .\\\\r\\\\n\\\\r\\\\nViking was buried in London , or in Bath, Somerset . [36]\\\\r\\\\n\\\\r\\\\nson, who was in the west with Ulfr, Hákon’s son.” [29] [30]\\\\r\\\\n\\\\r\\\\nwith the English silver treasure. [29] Other runestones are\\\\r\\\\n\\\\r\\\\nlived in Vallentuna travelled westwards several times, [29]\\\\r\\\\n\\\\r\\\\npaid. Then Knútr paid. [29] [31]\\\\r\\\\n\\\\r\\\\npart in the introduction of Christianity in Sweden , [38] and\\\\r\\\\n\\\\r\\\\ntening robes in Denmark.” [39] [40] A similar message is\\\\r\\\\n\\\\r\\\\nbecame king of England in 1016. [29]\\\\r\\\\n\\\\r\\\\nchristening robes.” [36] [41] Christening robes or baptismal\\\\r\\\\n\\\\r\\\\nsuch robes. [39] [42]\\\\r\\\\n\\\\r\\\\nbly’s retinue in the west”, [29] [32] and the Landeryd Rune-\\\\r\\\\n\\\\r\\\\nrather uniform language when they preached. [38] The ex-\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 7****************************\\\\r\\\\n\\\\r\\\\n  3.5 As sources 7\\\\r\\\\n\\\\r\\\\n  and soul; grant him light and paradise.” [38] [43] and the Bornholm runestone also appeals to Saint Michael : “May Christ and Saint Michael help the souls of Auðbjôrn and \\\\r\\\\n\\\\r\\\\n  Christian terminology was superimposed on the earlier pagan, and so Paradise substituted Valhalla , invocations to Thor and magic charms were replaced with Saint \\\\r\\\\n\\\\r\\\\n  Michael, who was the leader of the army of Heaven, subsumed Odin 's role as the psychopomp , and led the \\\\r\\\\n\\\\r\\\\n  vocations to Saint Michael on one runestone in Uppland, one on Gotland , on three on Bornholm and on one on \\\\r\\\\n\\\\r\\\\n  There is also the Bogesund runestone that testifies to the change that people were no longer buried at the family’s grave field: “He died in Eikrey (?). He is buried in the \\\\r\\\\n\\\\r\\\\n  3.4 Other types of runestones\\\\r\\\\n\\\\r\\\\n  of himself, the cleverest of men. May God help the soul of Vigmund, the ship captain. Vigmund and \\\\r\\\\n\\\\r\\\\n  Åfrid carved this memorial while he lived.” • Frösö Runestone : “Östman Gudfast’s son made the \\\\r\\\\n\\\\r\\\\n  • Dr 212: “Eskill Skulkason had this stone raised to \\\\r\\\\n\\\\r\\\\n  made;” • U 164 : “Jarlabanki had this stone put up in his own sake. And he owned the whole of Täby by himself. May God help his soul.” \\\\r\\\\n\\\\r\\\\n  Other runestones, as evidenced in two of the previous threeinscriptions,memorializethepiousactsofrelatively new Christians. In these, we can see the kinds of good works people who could afford to commission runestones undertook. Other inscriptions hint at religious beliefs. For example, one reads: \\\\r\\\\n\\\\r\\\\n  • U 160 : “Ulvshattil and Gye and Une ordered this\\\\r\\\\n\\\\r\\\\n  The Jelling stones which triggered the great runestone trend in Scandinavia\\\\r\\\\n\\\\r\\\\n  He lived in Skolhamra. God and God’s Mother save his spirit and soul, endow him with light and par- adise.” \\\\r\\\\n\\\\r\\\\n  Although most runestones were set up to perpetuate the memories of men, many speak of women, often repre- sented as conscientious landowners and pious Christians: \\\\r\\\\n\\\\r\\\\n  • Sö 101 : “Sigrid, Alrik’s mother, Orm’s daughter of Sigoerd, for his soul” \\\\r\\\\n\\\\r\\\\n  as important members of extended families: \\\\r\\\\n\\\\r\\\\n  • Br Olsen;215: “Mael-Lomchon and the daughter\\\\r\\\\n\\\\r\\\\n  The Kingittorsuaq Runestone from Greenland \\\\r\\\\n\\\\r\\\\n  Another interesting class of runestone is rune-stone-as- self promotion. Bragging was a virtue in Norse society, a habit in which the heroes of sagas often indulged, and is exemplified in runestones of the time. Hundreds of people had stones carved with the purpose of advertising theirownachievementsorpositivetraits. Afewexamples will suffice: \\\\r\\\\n\\\\r\\\\n  • U1011 : “Vigmundhadthisstonecarvedinmemory\\\\r\\\\n\\\\r\\\\n  cross in memory of Mael-Muire, his fostermother. It is better to leave a good fosterson than a bad son” \\\\r\\\\n\\\\r\\\\n  and as much-missed loved ones: \\\\r\\\\n\\\\r\\\\n  • N 68 : “Gunnor, Thythrik’s daughter, made a bridge skilful girl in Hadeland.” \\\\r\\\\n\\\\r\\\\n  3.5 As sources \\\\r\\\\n\\\\r\\\\n  The only existing Scandinavian texts dating to the period \\\\r\\\\n\\\\r\\\\n  coins) are found amongst the runic inscriptions, some of whichwerescratchedontopiecesofwoodormetalspear- heads, but for the most part they have been found on ac-\\\\r\\\\n\\\\r\\\\n  Gunnhildr into light and paradise.” [38] [44]\\\\r\\\\n\\\\r\\\\nMichael, Christ , God , and the Mother of God . [38] Saint\\\\r\\\\n\\\\r\\\\nbridge, and he Christianized Jämtland”\\\\r\\\\n\\\\r\\\\nhimself. Ever will stand this memorial that Eskill\\\\r\\\\n\\\\r\\\\ndead Christians to “light and paradise”. [45] There are in-\\\\r\\\\n\\\\r\\\\nLolland . [38]\\\\r\\\\n\\\\r\\\\nlifetime. And he made this causeway for his soul’s\\\\r\\\\n\\\\r\\\\nchurchyard.” [18] [46]\\\\r\\\\n\\\\r\\\\nstone erected in memory of Ulv, their good father.\\\\r\\\\n\\\\r\\\\nmade this bridge for her husband Holmgers, father\\\\r\\\\n\\\\r\\\\nof Dubh-Gael, whom Adils had to wife, raised this\\\\r\\\\n\\\\r\\\\ninmemoryofherdaughterAstrid. Shewasthemost\\\\r\\\\n\\\\r\\\\nbefore 1050 [47] (besides a few finds of inscriptions on\\\\r\\\\n\\\\r\\\\ntual stones. [48] In addition, the runestones usually remain\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 8****************************\\\\r\\\\n\\\\r\\\\n  8 4 IMAGERY\\\\r\\\\n\\\\r\\\\n  in their original form [47] and at their original locations, [49] and so their importance as historical sources cannot be \\\\r\\\\n\\\\r\\\\n  The inscriptions seldom provide solid historical evidence of events and identifiable people but instead offer in- sight into the development of language and poetry, kin- ship, and habits of name-giving, settlement, depictions from Norsepaganism , place-namesandcommunications, Viking as well as trading expeditions, and, not least, the \\\\r\\\\n\\\\r\\\\n  dinavian historians their main resource of information concerning early Scandinavian society, not much can be learned by studying the stones individually. The wealth of information that the stones provide can be found in the different movements and reasons for erecting the stones, in each region respectively. Approximately ten percent of the known runestones announce the travels and deaths of men abroad. These runic inscriptions coincide with certain Latin sources, such as the Annals of St. Bertin andthewritingsof LiudprandofCremona ,whichcontain valuable information on Scandinavians/ Rus’ who visited \\\\r\\\\n\\\\r\\\\n  4 Imagery\\\\r\\\\n\\\\r\\\\n  In the left part of the inscription lies Regin, who is be- headed with all his smithying tools around him. To the right of Regin, Sigurd is sitting and he has just burnt his thumb on the dragon’s heart that he is roasting. He is putting the thumb in his mouth and begins to understand the language of the marsh-tits that are sitting in the tree. They warn him of Regin’s schemes. Sigurd’s horse Grani \\\\r\\\\n\\\\r\\\\n  Another important personage from the legend of the Nibelungs is Gunnarr . On the Västerljung Runestone , there are three sides and one of them shows a man whose arms and legs are encircled by snakes. He is holding his arms stretched out gripping an object that may be a harp, \\\\r\\\\n\\\\r\\\\n  pears to be depicting an older version of the Gunnarr leg- end in which he played the harp with his fingers, which \\\\r\\\\n\\\\r\\\\n  4.2 Norse myths \\\\r\\\\n\\\\r\\\\n  The Norse god who was most popular was Thor , [55] and the AltunaRunestone in Uppland showsThor’sfishingex-\\\\r\\\\n\\\\r\\\\n  A drawing of the Ramsund inscription, in the province of Söder- manland, Sweden \\\\r\\\\n\\\\r\\\\n  Main articles: Urnes style , Runestone styles and Runemaster \\\\r\\\\n\\\\r\\\\n  The inscription is usually arranged inside a band, which often has the shape of a serpent, a dragon or a quadruped \\\\r\\\\n\\\\r\\\\n  4.1 Norse legends \\\\r\\\\n\\\\r\\\\n  It appears from the imagery of the Swedish runestones that the most popular Norse legend in the area was that \\\\r\\\\n\\\\r\\\\n  runestones , but the most famous of them is the Ramsund inscription . The inscription itself is of a common kind that tells of the building of a bridge, but the ornamen- tation shows Sigurd sitting in a pit thrusting his sword, forged by Regin , through the body of the dragon, which alsoformstherunicbandinwhichtherunesareengraved.\\\\r\\\\n\\\\r\\\\n  Two centuries later, the Icelander Snorri Sturluson would write: “The Midgarth Serpent bit at the ox-head and the hook caught in the roof of its mouth. When it felt that, it started so violently that both Thor’s fists went smack against the gunwale. Then Thor got angry, assumed all his godly strength, and dug his heels so sturdily that his feet went right through the bottom of the boat and he \\\\r\\\\n\\\\r\\\\n  The Altuna Runestone has also included the foot that \\\\r\\\\n\\\\r\\\\n  Itappearsthat Ragnarök isdepictedonthe Ledbergstone in Östergötland . On one of its sides it shows a large war- rior with a helmet, and who is bitten at his feet by a beast. This beast is, it is presumed, Fenrir , the brother of the Midgard Serpent, and who is attacking Odin . On the bottom of the illustration, there is a prostrate man who is holding out his hands and who has no legs. There is a close parallel from an illustration at Kirk Douglas on the Isle of Man. The Manx illustration shows Odin with a spear and with one of his ravens on his shoulders, and Odin is attacked in the same way as he is on the Ledberg stone. Adding to the stone’s spiritual content is a magic formula that was known all across the world of the pagan \\\\r\\\\n\\\\r\\\\n  On one of the stones from the Hunnestad Monument in Scania , there is an image of a woman riding a wolf using snakes as reins. The stone may be an illustration of the giantess Hyrrokin (“fire-wrinkled”), who was summoned bythegodstohelplaunch Baldr 'sfuneralship Hringhorni , which was too heavy for them. It was the same kind of wolf thatisreferredtoasthe“Valkyriehorse”onthe Rök\\\\r\\\\n\\\\r\\\\n  overstated. [47]\\\\r\\\\n\\\\r\\\\nis also shown tethered to the tree. [53]\\\\r\\\\n\\\\r\\\\nspread of Christianity . [50] Though the stones offer Scan-\\\\r\\\\n\\\\r\\\\nbut that part is damaged due to flaking. [53] The image ap-\\\\r\\\\n\\\\r\\\\nappears in the archaic eddic poem Atlakviða . [54]\\\\r\\\\n\\\\r\\\\nByzantium. [51]\\\\r\\\\n\\\\r\\\\npeditionwhenhetriedtocapturethe MidgardSerpent . [56]\\\\r\\\\n\\\\r\\\\nbraced them on the sea bed.” (Jansson’s translation). [57]\\\\r\\\\n\\\\r\\\\nwent through the planks. [58]\\\\r\\\\n\\\\r\\\\nbeast. [2]\\\\r\\\\n\\\\r\\\\nNorsemen. [58]\\\\r\\\\n\\\\r\\\\nof Sigurd the dragon slayer. [52] He is depicted on several\\\\r\\\\n\\\\r\\\\nRunestone . [58]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 9****************************\\\\r\\\\n\\\\r\\\\n  Odin attacked by Fenrir on the Ledberg stone , Sweden\\\\r\\\\n\\\\r\\\\n  9 \\\\r\\\\n\\\\r\\\\n  A runestone from the church of Resmo on Öland has been re- painted. It is presently at the Swedish Museum of National An- tiquities in Stockholm . \\\\r\\\\n\\\\r\\\\n  in Hávamál , Odin says: “So do I write / and colour the \\\\r\\\\n\\\\r\\\\n  cup were runes of every kind / Written and reddened, I \\\\r\\\\n\\\\r\\\\n  There are several runestones where it is declared that they were originally painted. A runestone in Söderman- land says “Here shall these stones stand, reddened with \\\\r\\\\n\\\\r\\\\n  says \\\"Ásbjörn carved and Ulfr painted” [59] [64] and a third runestone in Södermanland says \\\"Ásbjôrn cut the stone, \\\\r\\\\n\\\\r\\\\n  times, the original colours have been preserved unusu- ally well, and especially if the runestones were used as construction material in churches not very long after they had been made. One runestone in the church of Köping on Öland was discovered to be painted all over, and the colour of the words was alternating between black and\\\\r\\\\n\\\\r\\\\n  5 Colour \\\\r\\\\n\\\\r\\\\n  Today, most runestones are painted with falu red , since the colour red makes it easy to discern the ornamenta- tion, and it is appropriate since red paint was also used on \\\\r\\\\n\\\\r\\\\n  Norse wordsfor“writinginrunes”was fá anditoriginally\\\\r\\\\n\\\\r\\\\n  The most common paints were red ochre , red lead , soot , calcium carbonate , and other earth colours , which were bound with fat and water. It also appears that the Vikings imported white lead , green malachite and blue azurite \\\\r\\\\n\\\\r\\\\n  croscope , chemists have been able to analyse traces of colours on runestones, and in one case, they discov- ered bright red vermilion , which was an imported luxury colour. However, the dominating colours were white and\\\\r\\\\n\\\\r\\\\n  runes” [6] [59] andin GuðrúnarkviðaII , Gudrun says“Inthe\\\\r\\\\n\\\\r\\\\ncould not read them”. [61] [62]\\\\r\\\\n\\\\r\\\\nrunes”, [59] [63] a second runestone in the same province\\\\r\\\\n\\\\r\\\\npainted as a marker, bound with runes”. [60] [65] Some-\\\\r\\\\n\\\\r\\\\nred . [59]\\\\r\\\\n\\\\r\\\\nfrom Continental Europe . [59] By using an electron mi-\\\\r\\\\n\\\\r\\\\nrunes during the Viking Age. [59] In fact, one of the Old\\\\r\\\\n\\\\r\\\\nmeant“topaint”in Proto-Norse ( faihian ). [60] Moreoever,\\\\r\\\\n\\\\r\\\\nred lead. [66] There are even accounts where runes were\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 10****************************\\\\r\\\\n\\\\r\\\\n  10 8 NOTES\\\\r\\\\n\\\\r\\\\n  reddened with blood as in Grettis saga , where the Völva Þuríðr cut runes on a tree root and coloured them with her own blood to kill Grettir, and in Egils saga where Egill Skallagrímsson cut ale runes on a drinking horn and painted them with his own blood to see if the drink was \\\\r\\\\n\\\\r\\\\n  6 Preservation and care \\\\r\\\\n\\\\r\\\\n  The exposed runestones face several threats to the in- scribed rock surface. In Sweden, lichen grows at approximately 2 mm per year. In more ideal conditions it can grow considerably faster. Manyrunestonesareplacedalongsideroadsandroaddust causes lichen to grow faster, making lichen a major prob- lem. The lichen’s small root strands break through the rock, and blast off tiny pieces, making the rock porous, and over time degrade the inscriptions. Algae and moss \\\\r\\\\n\\\\r\\\\n  Water entering the cracks and crevices of the stone can cause whole sections to fall off either by freezing or by a combination of dirt, organic matter, and moisture, which \\\\r\\\\n\\\\r\\\\n  Proper preservation techniques slow down the rate of degradation. One method to combat the lichen, algae and moss problem is to smear in fine grained moist clay over the entire stone. This is then left to sit for a few weeks, \\\\r\\\\n\\\\r\\\\n  7 See also \\\\r\\\\n\\\\r\\\\n  • Hero stone • Alliterative verse • Eltang stone • List of runestones • Old Norse orthography • Picture stone • Stele • Valknut • Viking Runestones • Varangian Runestones \\\\r\\\\n\\\\r\\\\n  8 Notes \\\\r\\\\n\\\\r\\\\n  [1] “Om lifvet i Sverige under hednatiden” by Oscar Mon- telius (1905), pp. 81-82.\\\\r\\\\n\\\\r\\\\n  [2] “Runsten”, Nationalencyklopedin (1995), volume 16, pp. \\\\r\\\\n\\\\r\\\\n  91-92. [3] Zilmer 2005:38 [4] Olstad, Lisa (2002-12-16). “Ein minnestein for å hedre \\\\r\\\\n\\\\r\\\\n  seg sjølv” . forskning.no. Retrieved 2008-04-20. [5] Ynglinga saga in English translation, at Northvegr. [6] Bellows 1936:44 [7] Harrison & Svensson 2007:192 [8] Entry DR 42 in Rundata . [9] Page 1995:207-244 [10] Pritsak 1987:306 [11] Sawyer, B. 2000:26 [12] Zilmer 2005:39 [13] Larsson 1999:176 [14] Harrison & Svensson 2007:195 [15] Jansson 1987:120 [16] Harrison & Svensson 2007:195ff [17] Jansson 1987:116 [18] Jansson 1987:118 [19] Jansson 1987:119 [20] Harrison & Svensson 2007:196 [21] The entry U 241 in Rundata . [22] Harrison & Svensson 2007:197 [23] The entry Ög 8 in Rundata . [24] The entry Vg 184 in Rundata . [25] The entry Vg 197 in Rundata . [26] The entry Sö 65 in Rundata . [27] The entry Sö 171 in Rundata . [28] Harrison & Svensson 2007:197ff [29] Harrison & Svensson 2007:198 [30] The entry Sö 260 in Rundata . [31] The entry U 344 in Rundata . [32] The entry U 668 in Rundata . [33] The entry Ög 111 in Rundata . [34] Harrison & Svensson 2007:198ff [35] The entry U 539 in Rundata . [36] Harrison & Svensson 2007:199 [37] The entry Sö 166 in Rundata .\\\\r\\\\n\\\\r\\\\n  poisoned. [67]\\\\r\\\\n\\\\r\\\\nalso cause the rock to become porous and crumble. [68]\\\\r\\\\n\\\\r\\\\ncan cause a hollowing effect under the stone surface. [68]\\\\r\\\\n\\\\r\\\\nwhich suffocates the organic matter and kills it. [68]\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 11****************************\\\\r\\\\n\\\\r\\\\n  11\\\\r\\\\n\\\\r\\\\n  [38] Jansson 1987:113 \\\\r\\\\n\\\\r\\\\n  [39] Jansson 1987:112 \\\\r\\\\n\\\\r\\\\n  [40] Entry U 699 in Rundata . \\\\r\\\\n\\\\r\\\\n  [41] The entry U 243 in Rundata . \\\\r\\\\n\\\\r\\\\n  [42] Amonkinthe AbbeyofSt. Gall tellsofagroupofNorse- men who visited the court of the Frankish king Louis the Pious . They agreed to get baptized and were given valu- able baptismal robes, but, as there were not enough robes, the robes were cut up and divided among the Norsemen. One of the Vikings then exclaimed that he had got bap- tized 20 times and he had always received beautiful pota- toes, but this time he got rags that better fit a herdsman than a warrior. (Harrison & Svensson 2007:199) \\\\r\\\\n\\\\r\\\\n  [43] Entry U 160 in Rundata . \\\\r\\\\n\\\\r\\\\n  [44] Entry DR 399 in Rundata . \\\\r\\\\n\\\\r\\\\n  [45] Jansson 1987:114 \\\\r\\\\n\\\\r\\\\n  [46] Entry U 170 in Rundata . \\\\r\\\\n\\\\r\\\\n  [47] Pritsak 1987:307 \\\\r\\\\n\\\\r\\\\n  [48] Sawyer, B. 2000:1 \\\\r\\\\n\\\\r\\\\n  [49] Pritsak 1987:308 \\\\r\\\\n\\\\r\\\\n  [50] Sawyer, B. 2000:3 \\\\r\\\\n\\\\r\\\\n  [51] Sawyer, P. 1997:139 \\\\r\\\\n\\\\r\\\\n  [52] Jansson 1987:144 \\\\r\\\\n\\\\r\\\\n  [53] Jansson 1987:145 \\\\r\\\\n\\\\r\\\\n  [54] Jansson 1987:146 \\\\r\\\\n\\\\r\\\\n  [55] Jansson 1987:149 \\\\r\\\\n\\\\r\\\\n  [56] Jansson 1987:150 \\\\r\\\\n\\\\r\\\\n  [57] Jansson 1987:151ff \\\\r\\\\n\\\\r\\\\n  [58] Jansson 1987:152 \\\\r\\\\n\\\\r\\\\n  [59] Harrison & Svensson 2007:208 \\\\r\\\\n\\\\r\\\\n  [60] Jansson 1987:156 \\\\r\\\\n\\\\r\\\\n  [61] Jansson 1987:153 \\\\r\\\\n\\\\r\\\\n  [62] Bellows 1936:459 \\\\r\\\\n\\\\r\\\\n  [63] Entry Sö 206 in Rundata . \\\\r\\\\n\\\\r\\\\n  [64] Entry Sö 347 in Rundata . \\\\r\\\\n\\\\r\\\\n  [65] Entry Sö 213 in Rundata . \\\\r\\\\n\\\\r\\\\n  [66] Harrison & Svensson 2007:209 \\\\r\\\\n\\\\r\\\\n  [67] Jansson 1987:154 \\\\r\\\\n\\\\r\\\\n  [68] Snaedal & Åhlen 2004:33-34\\\\r\\\\n\\\\r\\\\n  9 References \\\\r\\\\n\\\\r\\\\n  • Bellows,HenryA. (1936). ThePoeticEdda . Prince- \\\\r\\\\n\\\\r\\\\n  • Harrison, D. & Svensson, K. (2007). Vikingaliv . \\\\r\\\\n\\\\r\\\\n  • Nationalencyklopedin (1995), volume 16, pp. 91– \\\\r\\\\n\\\\r\\\\n  • Jansson, Sven B. F. (1987), Runes in Sweden , Gid- \\\\r\\\\n\\\\r\\\\n  • Larsson, MatsG.(1999). Svitjod–ResortillSveriges \\\\r\\\\n\\\\r\\\\n  • Page, Raymond I. (1995). Runes and Runic Inscrip- Runes . Parsons, D. (ed). Woodbridge: Boydell \\\\r\\\\n\\\\r\\\\n  Press. ISBN 978-0-85115-387-2 • Pritsak, O. (1987). The Origin of Rus’. Cambridge, \\\\r\\\\n\\\\r\\\\n  the Harvard Ukrainian Research Institute. • Sawyer, Birgit. (2000). The Viking-Age Rune- dieval Scandinavia . Oxford: Oxford University \\\\r\\\\n\\\\r\\\\n  Press . ISBN 0-19-926221-7 • Sawyer, P. (1997). The Oxford Illustrated History of \\\\r\\\\n\\\\r\\\\n  0-19-285434-8 • Snaedal, T. & Åhlen, M. (2004). Svenska Runor . \\\\r\\\\n\\\\r\\\\n  366-8 • Stocklund, Marie; et al., eds. (2006), Runes and \\\\r\\\\n\\\\r\\\\n  Museum Tusculanum Press, ISBN 87-635-0428-6 • Zilmer, Kristel (diss. 2005), “He Drowned in (PDF), Tartu University Press, ISBN 9949-11-089- 0 Check date values in: |date= ( help ) \\\\r\\\\n\\\\r\\\\n  10 External links \\\\r\\\\n\\\\r\\\\n  • The Jelling Project - Information about Jelling and \\\\r\\\\n\\\\r\\\\n  • PhotosofrunestonesandimagestonesfromGotland\\\\r\\\\n\\\\r\\\\n  ton University Press, Princeton, New York.\\\\r\\\\n\\\\r\\\\nFälth & Hässler, Värnamo. ISBN 91-27-35725-2\\\\r\\\\n\\\\r\\\\n92.\\\\r\\\\n\\\\r\\\\nlunds, ISBN 91-7844-067-X\\\\r\\\\n\\\\r\\\\nUrsprung . Atlantis. ISBN 91-7486-421-1\\\\r\\\\n\\\\r\\\\ntions: Collected Essays on Anglo-Saxon and Viking\\\\r\\\\n\\\\r\\\\nMass.: Distributed by Harvard University Press for\\\\r\\\\n\\\\r\\\\nStones: Custom and Commemoration in Early Me-\\\\r\\\\n\\\\r\\\\ntheVikings . Oxford: OxfordUniversityPress. ISBN\\\\r\\\\n\\\\r\\\\nRiksantikvarieämbetet, 33 & 34. ISBN 91-7209-\\\\r\\\\n\\\\r\\\\nTheir Secrets: Studies in Runology , Copenhagen:\\\\r\\\\n\\\\r\\\\nHolmr’s Sea\\\": Baltic Traffic in Early Nordic Sources\\\\r\\\\n\\\\r\\\\nthe runestones\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n**************************** Page 12****************************\\\\r\\\\n\\\\r\\\\n  12 11 TEXT AND IMAGE SOURCES, CONTRIBUTORS, AND LICENSES \\\\r\\\\n\\\\r\\\\n  11 Text and image sources, contributors, and licenses \\\\r\\\\n\\\\r\\\\n  11.1 Text • Runestone Source: https://en.wikipedia.org/wiki/Runestone?oldid=703660771 Contributors: Rmhermen, Heron, Olivier, Michael Hardy, Wereon, Lupo, Wiglaf, PRB,Gracefool, Billposer, Rdnk, Zondor, Vsmith, FlorianBlaschke, Dbachmann, Bender235, Shanes, Firespeaker, Jonathunder, Jeltz, Mceder, Anittas, Jävligsvengelska, Briangotts, Cbdorsett, Graham87, Rjwilmsi, Salleman, Vegaswikian, Ligulem, Nimur, Srleffler, Kresspahl, Borgx, Eirik, CodeGeneratR, Hairy Dude, Longbow4u, Gaius Cornelius, Lanceka, Veledan, Bloodofox, Adamrush, Rmky87, Jkelly, Deville, Petri Krohn, Spliffy, Victor falk, Sardanaphalus, SmackBot, Saihtam, Eskimbot, Wakuran, Pieter Kuiper, DMS, OrangeDog, OrphanBot, Rrburke, Addshore, Bolivian Unicyclist, BIL, Drphilharmonic, Dogears, Thesmothete, John, Grumpy444grumpy~enwiki, Stelio, Asatruer, Iridescent, Zarex, Rwflammang, Texcarson, Otto4711, Caliga10, Sigo, Thijs!bot, Lutskovp, LinaMishima, Spencer, Jenny Wong, Mrund, Berig, CommonsDelinker, Verdatum, Andejons, Wikimandia, VolkovBot, LokiClock, Mce- wan, TXiKiBoT, Serg!o, JhsBot, Mintop, Keilana, Gbbinning, JL-Bot, CaptainJae, Deanlaw, The Thing That Should Not Be, OlHen, Alexbot, SchreiberBike, Jutienna^, Algkalv, Noctibus, Addbot, Holt, Defineka, Luckas-bot, Yobot, Zheliel, Yngvadottir, AnomieBOT, Citation bot, Amaury, Thorguds, Tetraedycal, Citation bot 1, Dront, TjBot, Ripchip Bot, WildBot, EmausBot, WikitanvirBot, Look2See1, GoingBatty, ClueBot NG, Helpful Pixie Bot, Arnavchaudhary, Uhlan, Dansk65, Gprobins, Canatian, Makecat-bot, Lynn Boyett, Rhino- Mind, Monkbot, Norsedoc and Anonymous: 93 \\\\r\\\\n\\\\r\\\\n  11.2 Images • File:Aarhus_mask_stone.jpg Source: https://upload.wikimedia.org/wikipedia/commons/3/3b/Aarhus_mask_stone.jpg License: CC SA-2.5 licence as communicated to me via e-mail. I've forwarded that e-mail to permissions@wikimedia.org Originally uploaded under \\\\r\\\\n\\\\r\\\\n  the same name to English Wikipedia by me. Haukurth 16:26, 31 August 2007 (UTC) Original artist: Lars Zwemmer • File:Commons-logo.svg Source: https://upload.wikimedia.org/wikipedia/en/4/4a/Commons-logo.svg License: CC-BY-SA-3.0 Contribu- • File:Early_Runic_stone_Hagby_Möjebro_Uppland_Sweden_-_right_to_left_script.jpg Source: https://upload.wikimedia.org/ \\\\r\\\\n\\\\r\\\\n  domain Contributors: Om lifvet i Sverige under hednatiden Original artist: Oscar Montelius • File:Fenris_Ledbergsstenen_20041231.jpg Source: https://upload.wikimedia.org/wikipedia/commons/7/7c/Fenris_Ledbergsstenen_ • File:Gron-rune-kingigtorssuaq.jpg Source: https://upload.wikimedia.org/wikipedia/commons/8/8e/Gron-rune-kingigtorssuaq.jpg Li- • File:Kalle_Dahlberg_modern_runestone.jpg Source: https://upload.wikimedia.org/wikipedia/commons/8/83/Kalle_Dahlberg_ • File:Rune_stone_density-km2-Sweden.svg Source: https://upload.wikimedia.org/wikipedia/commons/a/ac/Rune_stone_ • File:Runenstein_Blauzahn_2.jpg Source: https://upload.wikimedia.org/wikipedia/commons/e/ef/Runenstein_Blauzahn_2.jpg License: • File:Runestone_from_Snoldelev,_East_Zealand,_Denmark.jpg Source: https://upload.wikimedia.org/wikipedia/commons/8/87/ \\\\r\\\\n\\\\r\\\\n  Bloodofox • File:Sigurd.svg Source: https://upload.wikimedia.org/wikipedia/commons/4/4f/Sigurd.svg License: Public domain Contributors: From • Original jpeg uploaded to Commons from the Swedish Wikipedia by Gizmo II • File:Sö_111,_Stenkvista.jpg Source: https://upload.wikimedia.org/wikipedia/commons/d/da/S%C3%B6_111%2C_Stenkvista.jpg Li- • File:Sö_65,_Djulefors.jpg Source: https://upload.wikimedia.org/wikipedia/commons/d/da/S%C3%B6_65%2C_Djulefors.jpg License: • File:U_240,_Lingsberg.JPG Source: https://upload.wikimedia.org/wikipedia/commons/9/96/U_240%2C_Lingsberg.JPG License: CC • File:U_344,_Orkesta.JPG Source: https://upload.wikimedia.org/wikipedia/commons/2/24/U_344%2C_Orkesta.JPG License: CC BY • File:Vallebergastenen_lund_2006.jpg Source: https://upload.wikimedia.org/wikipedia/commons/a/a4/Vallebergastenen_lund_2006. • File:Ög_8,_Västra_Steninge.jpg Source: https://upload.wikimedia.org/wikipedia/commons/2/2c/%C3%96g_8%2C_V%C3%A4stra_ • File:Öl_Fv1911;274B,_Resmo.jpg Source: https://upload.wikimedia.org/wikipedia/commons/8/87/%C3%96l_Fv1911%3B274B%2C_ \\\\r\\\\n\\\\r\\\\n  11.3 Content license • Creative Commons Attribution-Share Alike 3.0\\\\r\\\\n\\\\r\\\\n  Nixdorf, Mic, Glenn, Raven in Orbit, Charles Matthews, Magnus.de, AnonMoos, Wetman, Rogper~enwiki, ChrisO~enwiki, Sam Spade,\\\\r\\\\n\\\\r\\\\nBY-SA 2.5 Contributors: Cropped from: photo306261 . Photograph taken by Lars Zwemmer and released by him under the CC-BY-\\\\r\\\\n\\\\r\\\\ntors: ? Original artist: ?\\\\r\\\\n\\\\r\\\\nwikipedia/commons/c/c2/Early_Runic_stone_Hagby_M%C3%B6jebro_Uppland_Sweden_-_right_to_left_script.jpg License: Public\\\\r\\\\n\\\\r\\\\n20041231.jpg License: GPL Contributors: ? Original artist: ?\\\\r\\\\n\\\\r\\\\ncense: Public domain Contributors: http://www.arild-hauge.com/gron-greenland.htm Original artist: Runemaster is unknown\\\\r\\\\n\\\\r\\\\nmodern_runestone.jpg License: Public domain Contributors: Own work Original artist: Tobias Radeskog\\\\r\\\\n\\\\r\\\\ndensity-km2-Sweden.svg License: Public domain Contributors: Sveriges Nationalatlas, p 45 Original artist: Koyos\\\\r\\\\n\\\\r\\\\nCC BY-SA 2.0 de Contributors: Own work (selbst erstelltes Foto) Original artist: Jürgen Howaldt\\\\r\\\\n\\\\r\\\\nRunestone_from_Snoldelev%2C_East_Zealand%2C_Denmark.jpg License: CC BY-SA 3.0 Contributors: Own work Original artist:\\\\r\\\\n\\\\r\\\\nthe Nordisk familjebok Original artist:\\\\r\\\\n\\\\r\\\\ncense: GFDL Contributors: Own work Original artist: Berig\\\\r\\\\n\\\\r\\\\nCC BY 3.0 Contributors: Own work Original artist: Berig\\\\r\\\\n\\\\r\\\\nBY 2.5 Contributors: Own work Original artist: Berig\\\\r\\\\n\\\\r\\\\n2.5 Contributors: Own work Original artist: Berig\\\\r\\\\n\\\\r\\\\njpg License: CC BY-SA 3.0 Contributors: Own work Original artist: Hedning\\\\r\\\\n\\\\r\\\\nSteninge.jpg License: CC BY 2.5 Contributors: Own work Original artist: Berig\\\\r\\\\n\\\\r\\\\nResmo.jpg License: GFDL Contributors: Own work Original artist: Berig\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\\\\r\\\\n\"^^xsd:string .\n";

		    	@Test
		    	public void test00_Storage() throws UnirestException, IOException,Exception {
		    		
//		    		HttpResponse<String> response = storageRequest()
					String url = "http://dev.digitale-kuratierung.de/api/e-sesame/storeData";
					HttpResponse<String> response = Unirest.post(url)
		    				.queryString("informat", "turtle")
		    				.queryString("outformat", "turtle")
		    				.queryString("storageName", "hyperlinking141411414141")
//		    				.queryString("storagePath", "storage")
		    				.queryString("storageCreate", true)
		    				//.queryString("input", TestConstants.inputDataRDF3)
		    				.queryString("inputDataFormat", "body")
		    				.queryString("inputDataMimeType", "text/turtle")
//		    				.queryString("inputDataMimeType", "application/rdf+xml")
//		    				.queryString("inputDataMimeType", "application/ld+json")
		    				.body(inputDataTest)
		    				.asString();
		    		System.out.println("----------------RESPONSE----------------------------------------");
		    		System.out.println(response.getBody());
		    		assertTrue(response.getStatus() == 200);
		    		assertTrue(response.getBody().length() > 0);
		     
		    		
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
//				.queryString("informat", "text/plain")
//				.queryString("input", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("outformat", "text/turtle")
//				.queryString("outformat", "application/rdf+xml")
				.queryString("storageName", "test2")
//				.queryString("storagePath", "storage")
				.queryString("inputDataFormat", "triple")
//				.queryString("inputData", "")
				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
				.asString();
		
		Assert.assertEquals(response.getStatus(), 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput,response.getBody());

		System.out.println("BODY OUTPUT: "+response.getBody());
		
//		response = retrievalRequest()
//				.queryString("informat", "text/plain")
//				.queryString("input", "")
//				.queryString("outformat", "application/rdf+xml")
//				.queryString("storageName", "test2")
//				.queryString("storagePath", "storage")
//				.queryString("inputDataType", "triple")
//				.queryString("inputData", "")
////				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
////				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
//				.asString();
//		Assert.assertEquals(response.getStatus(), 200);
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals(expectedOutput,response.getBody());
//
//		response = retrievalRequest()
//				.queryString("informat", "text/plain")
//				.queryString("input", "")
//				.queryString("outformat", "application/rdf+xml")
//				.queryString("storageName", "test2")
//				.queryString("storagePath", "storage")
//				.queryString("inputDataType", "triple")
//				.queryString("inputData", "")
////				.queryString("subject", "http://de.dkt.sesame/ontology/doc1")
////				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
//				.asString();
//		Assert.assertEquals(response.getStatus(), 200);
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals(expectedOutput,response.getBody());

	}

//	@Test
//	public void test7_RetrieveEntityStringLocalClasspathStorage() throws UnirestException, IOException,Exception {
//		
//		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//				+"<rdf:RDF\n"
//				+"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
//				+"\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
//				+"\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
//				+"\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"
//				+"\txmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"\n"
//				+"\txmlns:dbo=\"http://dbpedia.org/ontology/\"\n"
//				+"\txmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos/\"\n"
//				+"\txmlns:time=\"http://www.w3.org/2006/time#\">\n"
//				+"\n"
//			+"<rdf:Description rdf:about=\"http://de.dkt.sesame/ontology/doc1\">\n"
//			+"	<mentions xmlns=\"http://de.dkt.sesame/ontology/\" rdf:resource=\"http://de.dkt.sesame/ontology/doc2\"/>\n"
//			+"</rdf:Description>\n"
//			+"\n"
//			+"</rdf:RDF>"
//			+ "";
//		
//		HttpResponse<String> response = retrievalRequest()
//				.queryString("informat", "text/plain")
//				.queryString("input", "http://de.dkt.sesame/ontology/doc1")
//				.queryString("outformat", "application/rdf+xml")
//				.queryString("storageName", "test2")
//				.queryString("storagePath", indexPath)
//				.queryString("inputDataType", "entity")
//				.queryString("inputData", "")
////				.queryString("subject", "")
////				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
////				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
//				.asString();
//		Assert.assertEquals(response.getStatus(), 200);
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals(expectedOutput,response.getBody());
//
//		response = retrievalRequest()
//				.queryString("informat", "text/plain")
//				.queryString("input", "http://de.dkt.sesame/ontology/mentions")
//				.queryString("outformat", "application/rdf+xml")
//				.queryString("storageName", "test2")
//				.queryString("storagePath", indexPath)
//				.queryString("inputDataType", "entity")
//				.queryString("inputData", "")
////				.queryString("subject", "")
////				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
////				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
//				.asString();
//		Assert.assertEquals(response.getStatus(), 200);
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals(expectedOutput,response.getBody());
//		
//		response = retrievalRequest()
//				.queryString("informat", "text/plain")
//				.queryString("input", "http://de.dkt.sesame/ontology/doc2")
//				.queryString("outformat", "application/rdf+xml")
//				.queryString("storageName", "test2")
//				.queryString("storagePath", indexPath)
//				.queryString("inputDataType", "entity")
//				.queryString("inputData", "")
////				.queryString("subject", "")
////				.queryString("object", "http://de.dkt.sesame/ontology/doc2")
////				.queryString("predicate", "http://de.dkt.sesame/ontology/mentions")
//				.asString();
//		Assert.assertEquals(response.getStatus(), 200);
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals(expectedOutput,response.getBody());
//	}
	
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
				.queryString("inputDataFormat", "sparql")
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
				.queryString("inputDataFormat", "NIF")
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
