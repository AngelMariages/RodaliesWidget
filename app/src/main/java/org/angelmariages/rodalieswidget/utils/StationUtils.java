/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.angelmariages.rodalieswidget.utils;

import android.graphics.Color;

import java.util.LinkedHashMap;

public final class StationUtils {

	private static final LinkedHashMap<String, String> nucli50 = new LinkedHashMap<String, String>(){{
		put("72400", "Aeroport");
		put("78505", "Aguilar de Segarra");
		put("73101", "Alcover");
		put("71502", "Altafulla-Tamarit");
		put("78407", "Anglesola");
		put("79600", "Arenys de Mar");
		put("71211", "Ascó");
		put("79404", "Badalona");
		put("77106", "Balenyà-Els Hostalets");
		put("77107", "Balenyà-Tona-Seva");
		put("78705", "Barberà del Vallès");
		put("78804", "Barcelona-Arc de Triomf");
		put("79009", "Barcelona-El Clot-Aragó");
		put("79400", "Barcelona-Estació de França");
		put("78806", "Barcelona-La Sagrera-Meridiana");
		put("71802", "Barcelona-Passeig de Gràcia");
		put("78805", "Barcelona-Plaça de Catalunya");
		put("78802", "Barcelona-Sant Andreu Arenal");
		put("79004", "Barcelona-Sant Andreu Comtal");
		put("71801", "Barcelona-Sants");
		put("78801", "Barcelona-Torre del Baró");
		put("78402", "Bell-lloc d'Urgell");
		put("78406", "Bellpuig");
		put("71708", "Bellvitge");
		put("79606", "Blanes");
		put("79302", "Bordils-Juià");
		put("77112", "Borgonyà");
		put("79412", "Cabrera de Mar-Vilassar de Mar");
		put("78503", "Calaf");
		put("71601", "Calafell");
		put("79502", "Caldes d'Estrac");
		put("79203", "Caldes de Malavella");
		put("79603", "Calella");
		put("79305", "Camallera");
		put("65403", "Camarles-Deltebre");
		put("65409", "Cambrils");
		put("65401", "Camp-redó");
		put("77301", "Campdevànol");
		put("79601", "Canet de Mar");
		put("71302", "Capçanes");
		put("79101", "Cardedeu");
		put("78605", "Castellbell i el Vilar-Monistrol de Mont");
		put("72210", "Castellbisbal");
		put("71705", "Castelldefels");
		put("78405", "Castellnou de Seana");
		put("79301", "Celrà");
		put("77105", "Centelles");
		put("79316", "Cerbère");
		put("78706", "Cerdanyola del Vallès");
		put("72503", "Cerdanyola-Universitat");
		put("78500", "Cervera");
		put("79314", "Colera");
		put("72303", "Cornellà");
		put("71604", "Cubelles");
		put("71603", "Cunit");
		put("71305", "Duesaigües-L'Argentera");
		put("79407", "El Masnou");
		put("72211", "El Papiol");
		put("71707", "El Prat de Llobregat");
		put("72201", "El Vendrell");
		put("71301", "Els Guiamets");
		put("72203", "Els Monjos");
		put("77103", "Figaró");
		put("79309", "Figueres");
		put("79303", "Flaçà");
		put("71210", "Flix");
		put("79205", "Fornells de la Selva");
		put("71703", "Garraf");
		put("71706", "Gavà");
		put("72208", "Gelida");
		put("79300", "Girona");
		put("78404", "Golmés");
		put("79100", "Granollers Centre");
		put("77006", "Granollers-Canovelles");
		put("79105", "Gualba");
		put("79107", "Hostalric");
		put("73002", "Juneda");
		put("65402", "L'Aldea-Amposta");
		put("65405", "L'Ametlla de Mar");
		put("65404", "L'Ampolla-Perelló-Deltebre");
		put("72202", "L'Arboç");
		put("73007", "L'Espluga de Francolí");
		put("72305", "L'Hospitalet de Llobregat");
		put("65407", "L'Hospitalet de l'Infant");
		put("77114", "La Farga de Bebié");
		put("73004", "La Floresta");
		put("77102", "La Garriga");
		put("72205", "La Granada");
		put("79011", "La Llagosta");
		put("77306", "La Molina");
		put("73100", "La Plana-Picamoixons");
		put("73010", "La Riba");
		put("73102", "La Selva del Camp");
		put("77310", "La Tor de Querol-Enveig");
		put("72206", "Lavern-Subirats");
		put("73003", "Les Borges Blanques");
		put("71307", "Les Borges del Camp");
		put("77100", "Les Franqueses del Vallès");
		put("79109", "Les Franqueses-Granollers Nord");
		put("79312", "Llançà");
		put("78400", "Lleida-Pirineus");
		put("79102", "Llinars del Vallès");
		put("79605", "Malgrat de Mar");
		put("77110", "Manlleu");
		put("78600", "Manresa");
		put("72209", "Martorell");
		put("71303", "Marçà-Falset");
		put("79500", "Mataró");
		put("79200", "Maçanet-Massanes");
		put("72300", "Molins de Rei");
		put("78403", "Mollerussa");
		put("79006", "Mollet-Sant Fost");
		put("77004", "Mollet-Santa Rosa");
		put("65408", "Mont-roig del Camp");
		put("73008", "Montblanc");
		put("78800", "Montcada Bifurcació");
		put("77002", "Montcada Ripollet");
		put("79005", "Montcada i Reixac");
		put("78708", "Montcada i Reixac-Manresa");
		put("78707", "Montcada i Reixac-Santa Maria");
		put("79405", "Montgat");
		put("79406", "Montgat Nord");
		put("79007", "Montmeló");
		put("71300", "Móra la Nova");
		put("76003", "Nulles-Bràfim");
		put("79408", "Ocata");
		put("79103", "Palautordera");
		put("77005", "Parets del Vallès");
		put("79604", "Pineda de Mar");
		put("77304", "Planoles");
		put("71704", "Platja de Castelldefels");
		put("65411", "Port Aventura");
		put("79315", "Portbou");
		put("71304", "Pradell");
		put("79409", "Premià de Mar");
		put("77309", "Puigcerdà");
		put("73001", "Puigverd de Lleida-Artesa de Lleida");
		put("78506", "Rajadell");
		put("71400", "Reus");
		put("71209", "Riba-roja d'Ebre");
		put("77303", "Ribes de Freser");
		put("79106", "Riells i Viabrea-Breda");
		put("77200", "Ripoll");
		put("71306", "Riudecanyes-Botarell");
		put("79204", "Riudellots");
		put("72100", "Roda de Barà");
		put("72101", "Roda de Mar");
		put("72501", "Rubí");
		put("78704", "Sabadell Centre");
		put("78709", "Sabadell Nord");
		put("78703", "Sabadell Sud");
		put("76001", "Salomó");
		put("65410", "Salou");
		put("79403", "Sant Adrià del Besòs");
		put("79501", "Sant Andreu de Llavaneres");
		put("79104", "Sant Celoni");
		put("72502", "Sant Cugat del Vallès");
		put("72301", "Sant Feliu de Llobregat");
		put("78501", "Sant Guim de Freixenet");
		put("72302", "Sant Joan Despí");
		put("79304", "Sant Jordi Desvalls");
		put("78502", "Sant Martí Sesgueioles");
		put("77104", "Sant Martí de Centelles");
		put("79306", "Sant Miquel de Fluvià");
		put("78610", "Sant Miquel de Gonteres-Viladecavalls");
		put("79602", "Sant Pol de Mar");
		put("77113", "Sant Quirze de Besora");
		put("72207", "Sant Sadurní d'Anoia");
		put("71600", "Sant Vicenç de Calders");
		put("78604", "Sant Vicenç de Castellet");
		put("77003", "Santa Perpètua de Mogoda");
		put("79608", "Santa Susanna");
		put("78504", "Seguers-Sant Pere Sallavinera");
		put("71602", "Segur de Calafell");
		put("79202", "Sils");
		put("71701", "Sitges");
		put("71500", "Tarragona");
		put("78700", "Terrassa");
		put("78710", "Terrassa Est");
		put("79607", "Tordera");
		put("77111", "Torelló");
		put("71503", "Torredembarra");
		put("65400", "Tortosa");
		put("77305", "Toses");
		put("78408", "Tàrrega");
		put("65314", "Ulldecona-Alcanar-La Sénia");
		put("77307", "Urtx-Alp");
		put("78606", "Vacarisses");
		put("78607", "Vacarisses-Torreblanca");
		put("76004", "Valls");
		put("77109", "Vic");
		put("71401", "Vila-seca");
		put("76002", "Vilabella");
		put("71709", "Viladecans");
		put("78609", "Viladecavalls");
		put("72204", "Vilafranca del Penedès");
		put("79311", "Vilajuïga");
		put("79308", "Vilamalla");
		put("71700", "Vilanova i la Geltrú");
		put("79410", "Vilassar de Mar");
		put("73009", "Vilaverd");
		put("73006", "Vimbodí");
		put("73005", "Vinaixa");
	}};

	private static final LinkedHashMap<String, String> nucli20 = new LinkedHashMap<String, String>(){{
		put("15205", "Ablaña");
		put("16403", "Aviles");
		put("16006", "Barros");
		put("15401", "Calzada Asturias");
		put("15120", "Campomanes");
		put("16302", "Cancienes");
		put("16010", "Ciaño");
		put("15210", "El Caleyo");
		put("16011", "El Entrego");
		put("16301", "Ferroñes");
		put("15410", "Gijon (Sanz Crespo");
		put("15121", "La Cobertoria");
		put("15217", "La Corredoria");
		put("16008", "La Felguera");
		put("15119", "La Frecha");
		put("15206", "La Pereda-Riosa");
		put("16402", "La Rocica");
		put("15209", "Las Segadas");
		put("15218", "Llamaquique");
		put("16408", "Los Campos");
		put("15300", "Lugo de LLanera");
		put("15212", "Lugones");
		put("15203", "Mieres-Puente");
		put("15303", "Monteana");
		put("16400", "Nubledo");
		put("15207", "Olloniego");
		put("15211", "Oviedo");
		put("16005", "Peña Rubia");
		put("15122", "Pola de Lena");
		put("15118", "Puente L.Fierros");
		put("16009", "Sama");
		put("16405", "San Juan de Nieva");
		put("16001", "Santa Eulalia M");
		put("15202", "Santullano");
		put("15302", "Serin");
		put("15208", "Soto de Rey");
		put("16002", "Tudela-Veguin");
		put("15200", "Ujo");
		put("15400", "Veriña");
		put("15301", "Villabona Astur");
		put("15305", "Villabona-Tabladiell");
		put("16401", "Villalegre");
		put("15123", "Villallana");
	}};

	private static final LinkedHashMap<String, String> nucli60 = new LinkedHashMap<String, String>(){{
		put("13200", "Abando");
		put("13118", "Abaroa-San Miguel");
		put("13206", "Ametzola");
		put("13101", "Amurrio");
		put("13121", "Amurrio-Iparralde");
		put("13117", "Arakaldo");
		put("13108", "Arbide");
		put("13107", "Areta");
		put("13109", "Arrankudiaga");
		put("13111", "Arrigorriaga");
		put("13207", "Autonomia");
		put("13115", "Bakiola");
		put("13112", "Basauri");
		put("13113", "Bidebieta-Basauri");
		put("13400", "Desertu-Barakaldo");
		put("13501", "Galindo");
		put("13508", "Gallarta");
		put("13116", "Inarratxu");
		put("13402", "La Iberia");
		put("13119", "La Peña");
		put("13106", "Llodio");
		put("13103", "Luiaondo");
		put("13305", "Lutxana-Barakaldo");
		put("13120", "Miribilla");
		put("13506", "Muskiz");
		put("13303", "Olabeaga");
		put("13114", "Ollargan");
		put("13100", "Orduña");
		put("13504", "Ortuella");
		put("13404", "Peñota");
		put("13403", "Portugalete");
		put("13505", "Putxeta");
		put("13509", "Sagrada Familia");
		put("13102", "Salbio");
		put("13208", "San Mames");
		put("13104", "Santa Cruz Llodio");
		put("13405", "Santurtzi");
		put("13401", "Sestao");
		put("13502", "Trapaga");
		put("13110", "Ugao-Miraballes");
		put("13507", "Urioste");
		put("13503", "Valle Trapaga-Trapaga");
		put("13205", "Zabalburu");
		put("13304", "Zorrotza");
	}};

	private static final LinkedHashMap<String, String> nucli31 = new LinkedHashMap<String, String>(){{
		put("51205", "AEROPUERTO DE JEREZ");
		put("51406", "BAHIA SUR");
		put("51405", "CADIZ");
		put("51407", "CORTADURA");
		put("51400", "EL PUERTO DE SANTA MARIA");
		put("51409", "ESTADIO");
		put("51300", "JEREZ DE LA FRONTERA");
		put("51415", "LAS ALETAS");
		put("51401", "PUERTO REAL");
		put("51402", "SAN FERNANDO");
		put("51414", "SAN SEVERIANO");
		put("51404", "SEGUNDA AGUADA");
		put("51416", "UNIVERSIDAD");
		put("51417", "VALDELAGRANA");
	}};

	private static final LinkedHashMap<String, String> nucli10 = new LinkedHashMap<String, String>(){{
		put("98305", "Aeropuerto-T4");
		put("70103", "Alcala de Henares");
		put("70107", "Alcala de Henares-Universidad");
		put("19003", "Alcobendas-S.Sebast. de los Reyes");
		put("35605", "Alcorcon");
		put("12002", "Alpedrete");
		put("35600", "Aluche");
		put("60200", "Aranjuez");
		put("10001", "Aravaca");
		put("70002", "Asamblea de Madrid-Entrevias");
		put("18000", "Atocha");
		put("70105", "Azuqueca");
		put("17009", "Cantoblanco Universidad");
		put("12006", "Cercedilla");
		put("17000", "Chamartin");
		put("60105", "Ciempozuelos");
		put("12004", "Collado Mediano");
		put("17005", "Colmenar Viejo");
		put("70108", "Coslada");
		put("12023", "Cotos");
		put("35603", "Cuatro Vientos");
		put("18004", "Delicias");
		put("35702", "Doce de Octubre");
		put("10010", "El Barrial-C.Com.Pozuelo");
		put("60109", "El Casar");
		put("10203", "El Escorial");
		put("17003", "El Goloso");
		put("70003", "El Pozo");
		put("35609", "Embajadores");
		put("35601", "Fanjul");
		put("17001", "Fuencarral");
		put("35002", "Fuenlabrada");
		put("98003", "Fuente de la Mora");
		put("10104", "Galapagar-La Navata");
		put("37011", "Getafe -Sector Tres");
		put("37002", "Getafe Centro");
		put("60102", "Getafe Industrial");
		put("70200", "Guadalajara");
		put("35012", "Humanes");
		put("70111", "La Garena");
		put("35010", "La Serna");
		put("35608", "Laguna");
		put("35602", "Las Aguilas");
		put("37010", "Las Margaritas Universidad");
		put("10101", "Las Matas");
		put("35610", "Las Retamas");
		put("10005", "Las Rozas");
		put("10202", "Las Zorreras");
		put("35001", "Leganes");
		put("12005", "Los Molinos");
		put("12001", "Los Negrales");
		put("10007", "Majadahonda");
		put("70104", "Meco");
		put("18003", "Mendez Alvaro");
		put("97200", "Mirasierra - Paco de Lucia");
		put("35606", "Mostoles");
		put("35607", "Mostoles el Soto");
		put("18002", "Nuevos Ministerios");
		put("35703", "Orcasitas");
		put("37012", "Parla");
		put("35011", "Parque Polvoranca");
		put("10100", "Pinar de las Rozas");
		put("60103", "Pinto");
		put("18005", "Piramides");
		put("97100", "Pitis");
		put("10002", "Pozuelo");
		put("10000", "Principe Pio");
		put("35704", "Puente Alcocer");
		put("12020", "Puerto de Navacerrada");
		put("97201", "Ramon y Cajal");
		put("18001", "Recoletos");
		put("10205", "Robledo de Chavela");
		put("60107", "San Cristobal de los Angeles");
		put("60101", "San Cristobal Industrial");
		put("70101", "San Fernando");
		put("35604", "San Jose de Valderas");
		put("10201", "San Yago");
		put("70109", "Santa Eugenia");
		put("10206", "Santa Maria de la Alameda");
		put("18101", "Sol");
		put("70112", "Soto del Henares");
		put("70102", "Torrejon de Ardoz");
		put("10103", "Torrelodones");
		put("17004", "Tres Cantos");
		put("19001", "Universidad P.Comillas");
		put("98304", "Valdebebas");
		put("19002", "ValdelasFuentes");
		put("60104", "Valdemoro");
		put("70001", "Vallecas");
		put("70100", "Vicalvaro");
		put("10200", "Villalba");
		put("37001", "Villaverde Alto");
		put("60100", "Villaverde Bajo");
		put("10204", "Zarzalejo");
		put("35009", "Zarzaquemada");
	}};

	private static final LinkedHashMap<String, String> nucli32 = new LinkedHashMap<String, String>(){{
		put("54505", "Aeropuerto");
		put("54407", "Aljaima");
		put("54405", "Alora");
		put("54511", "Benalmadena (A.Miel)");
		put("54410", "Campanillas");
		put("54408", "Cartama");
		put("54513", "Carvajal");
		put("54510", "El Pinillo");
		put("54516", "Fuengirola");
		put("54503", "Guadalhorce");
		put("54508", "La Colina");
		put("54518", "Los Alamos");
		put("54515", "Los Boliches");
		put("54412", "Los Prados(Alora)");
		put("54517", "Málaga-Centro Alameda");
		put("54413", "Malága-María Zambrano");
		put("54501", "Malaga-Victoria Kent");
		put("54519", "Montemar Alto");
		put("54406", "Pizarra");
		put("54520", "Plaza Mayor");
		put("54506", "San Julian");
		put("54514", "Torreblanca");
		put("54509", "Torremolinos");
		put("54512", "Torremuelle");
	}};

	private static final LinkedHashMap<String, String> nucli41 = new LinkedHashMap<String, String>(){{
		put("07004", "Aguilas");
		put("07007", "Aguilas el Labradorcico");
		put("62100", "Albatera-Catral");
		put("06008", "Alcantarilla-Los Romanos");
		put("06002", "Alhama de Murcia");
		put("60911", "Alicante/Alacant Termino");
		put("06100", "Almendricos");
		put("62001", "Beniel");
		put("62003", "Callosa de Segura");
		put("62101", "Crevillente");
		put("62102", "Elche Carrus/Elx Carrus");
		put("62103", "Elche Parque/Elx Parc");
		put("62108", "Elche-Mercancias");
		put("07003", "Jaravia");
		put("06004", "La Hoya");
		put("06001", "Librilla");
		put("06005", "Lorca San Diego");
		put("06006", "Lorca-Sutullena");
		put("61200", "Murcia del Carmen");
		put("61101", "Murcia Mercancias");
		put("62002", "Orihuela");
		put("06007", "Puerto Lumbreras");
		put("07001", "Pulpi");
		put("62109", "Sant Gabriel");
		put("60913", "Sant Vicent Centre");
		put("62104", "Torrellano");
		put("06003", "Totana");
		put("60914", "Universidad de Alicante");
	}};

	private static final LinkedHashMap<String, String> nucli62 = new LinkedHashMap<String, String>(){{
		put("14235", "Arenas de Iguña");
		put("14206", "Barcena");
		put("14219", "Boo");
		put("14218", "Guarnizo");
		put("14203", "Lantueno-Santiur.");
		put("14211", "Las Caldas de Be.");
		put("14209", "Las Fraguas");
		put("14232", "Lombera");
		put("14210", "Los Corrales Buelna");
		put("14220", "Maliaño");
		put("14207", "Molledo-Portolin");
		put("14221", "Muriedas");
		put("14237", "Muriedas-Bahia");
		put("14231", "Nueva Montaña");
		put("14217", "Parbayon");
		put("14204", "Pesquera");
		put("14234", "Pujayo");
		put("14202", "Reinosa");
		put("14216", "Renedo");
		put("14233", "Rio Ebro");
		put("14208", "Santa Cruz Iguña");
		put("14223", "Santander");
		put("14214", "Sierrapando");
		put("14213", "Torrelavega");
		put("14230", "Valdecilla");
		put("14212", "Viernoles");
		put("14236", "Vioño");
		put("14215", "Zurita");
	}};

	private static final LinkedHashMap<String, String> nucli61 = new LinkedHashMap<String, String>(){{
		put("11409", "Alegia de Oria");
		put("11505", "Andoain");
		put("11504", "Andoain-Centro");
		put("11502", "Anoeta");
		put("11513", "Ategorrieta");
		put("11404", "Beasain");
		put("11503", "Billabona-Zizurkil");
		put("11305", "Brincola");
		put("11511", "Donostia-San Sebastian");
		put("11512", "Gros");
		put("11508", "Hernani");
		put("11507", "Hernani-Centro");
		put("11514", "Herrera");
		put("11408", "Ikaztegieta");
		put("11522", "Intxaurrondo");
		put("11600", "Irun");
		put("11406", "Itsasondo");
		put("11306", "Legazpi");
		put("11407", "Legorreta");
		put("11516", "Lezo-Renteria");
		put("11510", "Loiola");
		put("11509", "Martutene");
		put("11405", "Ordizia");
		put("11402", "Ormaiztegui");
		put("11515", "Pasaia");
		put("11500", "Tolosa");
		put("11501", "Tolosa Centro");
		put("11506", "Urnieta");
		put("11518", "Ventas");
		put("11400", "Zumarraga");
	}};

	private static final LinkedHashMap<String, String> nucli30 = new LinkedHashMap<String, String>(){{
		put("40121", "Alcolea del Rio");
		put("40118", "Arenillas");
		put("51111", "Bellavista");
		put("43005", "Benacazon");
		put("50702", "Brenes");
		put("43002", "Camas");
		put("51112", "Cantaelgallo");
		put("50701", "Cantillana");
		put("51050", "Cartuja");
		put("40113", "Cazalla-Constantina");
		put("51104", "Don Rodrigo");
		put("51103", "Dos Hermanas");
		put("50704", "El Cañamo");
		put("51051", "Estadio Olimpico");
		put("40114", "Fabrica de Pedroso");
		put("50602", "Guadajoz");
		put("51113", "Jardines de Hercules");
		put("50703", "La Rinconada");
		put("51202", "Las Cabezas de San Juan");
		put("51203", "Lebrija");
		put("50600", "Lora del Rio");
		put("50700", "Los Rosales");
		put("51010", "Padre Pio Palmete");
		put("51009", "Palacio de Congresos");
		put("40115", "Pedroso");
		put("43027", "Salteras");
		put("51100", "San Bernardo");
		put("43000", "San Jeronimo");
		put("43004", "Sanlucar la Mayor");
		put("51003", "Santa Justa");
		put("40122", "Tocina");
		put("51200", "Utrera");
		put("43026", "Valencina-Santiponce");
		put("43003", "Villanueva del Ariscal y Olivares");
		put("40119", "Villanueva del Rio y Minas");
		put("51110", "Virgen del Rocio");
	}};

	private static final LinkedHashMap<String, String> nucli40 = new LinkedHashMap<String, String>(){{
		put("65005", "Albuixech");
		put("66211", "Aldaia");
		put("64203", "Alfafar - Benetusser");
		put("64105", "Algemesí");
		put("67216", "Algimia");
		put("65209", "Almassora");
		put("65202", "Almenara");
		put("64104", "Alzira");
		put("64107", "Benifaio - Almussafes");
		put("66206", "Buñol");
		put("65207", "Burriana - Alquerias del Niño Perdido");
		put("64103", "Carcaixent");
		put("65300", "Castello de la Plana");
		put("64201", "Catarroja");
		put("67211", "Caudiel");
		put("66208", "Cheste");
		put("66207", "Chiva");
		put("66210", "Circuit R. Tormo");
		put("69104", "Cullera");
		put("65007", "El Puig");
		put("66203", "El Rebollar");
		put("69101", "El Romani");
		put("67221", "Estivella - Albalat dels Tarongers");
		put("69110", "Gandia");
		put("67223", "Gilet");
		put("67212", "Jerica - Viver");
		put("64006", "L'Alcudia de Crespins");
		put("64007", "L'Enova - Manuel");
		put("65203", "La Llosa");
		put("64102", "La Pobla Llarga");
		put("65201", "Les Valls");
		put("66209", "Loriguilla - Reva");
		put("65006", "Massalfassar");
		put("64202", "Massanassa");
		put("64003", "Moixent");
		put("65205", "Moncofa");
		put("64005", "Montesa");
		put("67213", "Navajas");
		put("65206", "Nules - La Vilavella");
		put("69111", "Platja i Grau de Gandia");
		put("65008", "Puçol");
		put("66202", "Requena");
		put("65001", "Roca - Cuper");
		put("65200", "Sagunt");
		put("66201", "San Antonio de Requena");
		put("67214", "Segorbe - Arrabal");
		put("67215", "Segorbe - Ciudad");
		put("66204", "Siete Aguas");
		put("64200", "Silla");
		put("69102", "Sollana");
		put("67217", "Soneja");
		put("69103", "Sueca");
		put("69105", "Tavernes de la Valldigna");
		put("66200", "Utiel");
		put("65003", "Valencia - Cabanyal");
		put("65002", "Valencia - F.S.L.");
		put("66212", "Valencia - Sant Isidre");
		put("65000", "Valencia Nord");
		put("64004", "Vallada");
		put("66205", "Venta Mina");
		put("65208", "Vila - Real");
		put("64100", "Xativa");
		put("69107", "Xeraco");
		put("65204", "Xilxes");
		put("66214", "Xirivella - Alqueries");
		put("69200", "Xirivella - L'Alter");
	}};

	private static final LinkedHashMap<String, String> nucli70 = new LinkedHashMap<String, String>(){{
		put("70800", "Casetas");
		put("71100", "Miraflores");
		put("70801", "Utebo");
		put("04040", "Zaragoza Delicias");
		put("70806", "Zaragoza Portillo");
		put("70807", "Zaragoza-Goya");
	}};

	public static final LinkedHashMap<Integer, LinkedHashMap<String, String>> nuclis = new LinkedHashMap<Integer, LinkedHashMap<String, String>>() {{
		put(20, nucli20);
		put(50, nucli50);
		put(60, nucli60);
		put(31, nucli31);
		put(10, nucli10);
		put(32, nucli32);
		put(41, nucli41);
		put(62, nucli62);
		put(61, nucli61);
		put(30, nucli30);
		put(40, nucli40);
		put(70, nucli70);
	}};

	public static String getIDFromName(String stationName, int nucliID) {
		LinkedHashMap<String, String> selectedNucli = nuclis.get(nucliID);

		if (selectedNucli.containsValue(stationName)) {
			for (String stationID : selectedNucli.keySet()) {
				if (selectedNucli.get(stationID).equalsIgnoreCase(stationName))
					return stationID;
			}
		}
		return null;
	}

	public static String getNameFromID(String stationID, int nucliID) {
		LinkedHashMap<String, String> selectedNucli = nuclis.get(nucliID);

		if(stationID == null || selectedNucli == null) return null;
		return selectedNucli.containsKey(stationID) ? selectedNucli.get(stationID) : null;
	}

	public static final LinkedHashMap<Integer, String> nucliIDs = new LinkedHashMap<Integer, String>(){{
		put(20, "Asturias");//20
		put(50, "Barcelona");//50
		put(60, "Bilbao");//60
		put(31, "Cádiz");//31
		put(10, "Madrid");//10
		put(32, "Málaga");//32
		put(41, "Murcia/Alicante");//41
		put(62, "Santander");//62
		put(61, "San Sebastián");//61
		put(30, "Sevilla");//30
		put(40, "Valencia");//40
		put(70, "Zaragoza");//70
	}};

	public static int getIDFromNucli(String nucliName) {
		if(nucliIDs.containsValue(nucliName)) {
			for (Integer nucliID : nucliIDs.keySet()) {
				if(nucliIDs.get(nucliID).equalsIgnoreCase(nucliName))
					return nucliID;
			}
		}

		return -1;
	}

	public static String getNameFromNucliID(int nucliID) {
		return nucliIDs.containsKey(nucliID) ? nucliIDs.get(nucliID) : null;
	}

	@SuppressWarnings("unused")
	public enum ColorLines {
		//Barcelona
		R150("#79BDE8", Color.WHITE),
		R250("#00A650", Color.WHITE),
		R350("#EF3E33", Color.WHITE),
		R450("#F9A13A", Color.WHITE),
		R750("#B77CB6", Color.WHITE),
		R850("#8B0066", Color.WHITE),
		R1150("#0069AA", Color.WHITE),
		R1250("#FFDD00", Color.BLACK),
		R1350("#EA4498", Color.WHITE),
		R1450("#6658A6", Color.WHITE),
		R1550("#948671", Color.WHITE),
		R1650("#B30738", Color.WHITE),
		RG150("#1A75CE", Color.WHITE),
		RT150("#00C3B5", Color.WHITE),
		RT250("#E56BC2", Color.WHITE),
		//Asturias
		C120("#EB4A44", Color.WHITE),
		C220("#02A760", Color.WHITE),
		C320("#0763AC", Color.WHITE),
		//Bilbao
		C160("#EB4A44", Color.WHITE),
		C260("#02A760", Color.WHITE),
		C360("#0763AC", Color.WHITE),
		//Cadiz
		C131("#EB4A44", Color.WHITE),
		C1A31("#EB4A44", Color.WHITE),
		//Madrid
		C110("#64ADDE", Color.WHITE),
		C210("#088C29", Color.WHITE),
		C310("#0055A0", Color.WHITE),
		C410("#0F318C", Color.WHITE),
		C4A("#0F318C", Color.WHITE),
		C4B("#0F318C", Color.WHITE),
		C510("#F9B410", Color.WHITE),
		C710("#D70020", Color.WHITE),
		C810("#088C29", Color.WHITE),
		C8A10("#7D3494", Color.WHITE),
		C8B10("#179447", Color.WHITE),
		C910("#EE5513", Color.WHITE),
		C1010("#93BC0E", Color.WHITE),
		//Malaga
		C132("#4588D0", Color.WHITE),
		C232("#00712E", Color.WHITE),
		//Murcia/Alicante
		C141("#459FD4", Color.WHITE),
		C241("#019959", Color.WHITE),
		C341("#9F4797", Color.WHITE),
		//Santander
		C162("#EA4747", Color.WHITE),
		//San sebastian
		C161("#EA4747", Color.WHITE),
		//Sevilla
		C130("#4D8FC3", Color.WHITE),
		C230("#007B35", Color.WHITE),
		C330("#FF171B", Color.WHITE),
		C430("#931A8B", Color.WHITE),
		C530("#00328B", Color.WHITE),
		//Valencia
		C140("#4E8FC2", Color.WHITE),
		C240("#FFB20F", Color.WHITE),
		C340("#921A8A", Color.WHITE),
		C440("#FF171C", Color.WHITE),
		C540("#008B29", Color.WHITE),
		C640("#00328B", Color.WHITE),
		//Zaragoza
		C170("#E4484B", Color.WHITE);

		private final String s;
		private final int c;
		ColorLines(String s, int c) { this.s = s; this.c = c; }
		public String getBColor() { return s; }
		public int getTColor() { return c; }
	}
}
