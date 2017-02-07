package org.angelmariages.rodalieswidget.utils;

public final class StationUtils {
	public static final String[] STATION_NAMES = new String[]{"Aeroport", "Arenys de Mar", "Badalona", "Balenyà-Els Hostalets", "Balenyà-Tona-Seva",
			"Barberà del Vallès", "Barcelona-Arc de Triomf", "Barcelona-El Clot-Aragó", "Barcelona-Estació de França",
			"Barcelona-La Sagrera-Meridiana", "Barcelona-Passeig de Gracia", "Barcelona-Plaça de Catalunya",
			"Barcelona-Sant Andreu Arenal", "Barcelona-Sant Andreu Comtal", "Barcelona-Sants", "Barcelona-Torre del Baró",
			"Bellvitge", "Blanes", "Borgonyà", "Cabrera de Mar-Vilassar de Mar", "Calafell", "Caldes d'Estrac", "Calella",
			"Campdevànol", "Canet de Mar", "Cardedeu", "Castellbell i el Vilar-Monistrol de Mont",
			"Castellbisbal", "Castelldefels", "Centelles", "Cerdanyola del Valĺès", "Cerdanyola-Universitat",
			"Cornellà", "Cubelles", "Cunit", "El Masnou", "El Papiol", "El Prat de Llobregat", "El Vendrell",
			"Els Monjos", "Figaro", "Garraf", "Gavà", "Gelida", "Granollers Centre", "Granollers-Canovelles",
			"Gualba", "Hostalric", "L'Arboç", "L'Hospitalet de Llobregat", "La Farga de Bebié", "La Garriga",
			"La Granada", "La Llagosta", "La Molina", "La Tor de Querol-Enveig", "Lavern-Subirats", "Les Franqueses del Vallès",
			"Les Franqueses-Granollers Nord", "Llinars del VallÌs", "Maçanet-Massanes", "Malgrat de Mar", "Manlleu", "Manresa",
			"Martorell", "Mataró", "Molins de Rei", "Mollet-Sant Fost", "Mollet-Santa Rosa", "Montcada i Reixac",
			"Montcada i Reixac-Manresa", "Montcada i Reixac-Santa Maria", "Montcada Bifurcació", "Montcada Ripollet",
			"Montgat", "Montgat Nord", "Montmeló", "Ocata", "Palautordera", "Parets del Vallès", "Pineda de Mar", "Planoles",
			"Platja de Castelldefels", "Premià de Mar", "Puigcerdà", "Ribes de Freser", "Riells i Viabrea-Breda", "Ripoll",
			"Rubí", "Sabadell Centre", "Sabadell Nord", "Sabadell Sud", "Sant Adrià del Besòs", "Sant Andreu de Llavaneres",
			"Sant Celoni", "Sant Cugat del Vallés", "Sant Feliu de Llobregat", "Sant Joan Despí", "Sant Martí de Centelles",
			"Sant Miquel de Gonteres-Viladecavalls", "Sant Pol de Mar", "Sant Quirze de Besora", "Sant Sadurní d'Anoia",
			"Sant Vicenç de Calders", "Sant Vicenç de Castellet", "Santa Perpetua de Mogoda", "Santa Susanna", "Segur de Calafell",
			"Sitges", "Terrassa", "Terrassa Est", "Tordera", "Torelló", "Toses", "Urtx-Alp", "Vacarisses", "Vacarisses-Torreblanca",
			"Vic", "Viladecans", "Viladecavalls", "Vilafranca del Penedés", "Vilanova i la Geltrú", "Vilassar de Mar"
	};

	private static final int[] STATION_IDS = new int[]{72400, 79600, 79404, 77106, 77107, 78705, 78804,
			79009, 79400, 78806, 71802, 78805, 78802, 79004, 71801, 78801, 71708, 79606,
			77112, 79412, 71601, 79502, 79603, 77301, 79601, 79101, 78605, 72210, 71705,
			77105, 78706, 72503, 72303, 71604, 71603, 79407, 72211, 71707, 72201, 72203,
			77103, 71703, 71706, 72208, 79100, 77006, 79105, 79107, 72202, 72305, 77114,
			77102, 72205, 79011, 77306, 77310, 72206, 77100, 79109, 79102, 79200, 79605,
			77110, 78600, 72209, 79500, 72300, 79006, 77004, 79005, 78708, 78707, 78800,
			77002, 79405, 79406, 79007, 79408, 79103, 77005, 79604, 77304, 71704, 79409,
			77309, 77303, 79106, 77200, 72501, 78704, 78709, 78703, 79403, 79501, 79104,
			72502, 72301, 72302, 77104, 78610, 79602, 77113, 72207, 71600, 78604, 77003,
			79608, 71602, 71701, 78700, 78710, 79607, 77111, 77305, 77307, 78606, 78607,
			77109, 71709, 78609, 72204, 71700, 79410
	};

	public static int getIDFromName(String stationName) {
		for (int i = 0; i < STATION_NAMES.length; i++)
			if (STATION_NAMES[i].equals(stationName))
				return STATION_IDS[i];
		return -1;
	}

	public static String getNameFromID(int stationID) {
		for (int i = 0; i < STATION_IDS.length; i++)
			if (STATION_IDS[i] == stationID)
				return STATION_NAMES[i];
		return null;
	}
}
