package xyz.cesarbiker.rodalieswidget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public abstract class Utils {
    public static final int ORIGIN = 100;
    public static final int DESTINATION = 200;

    public static final String CLICK_LIST_VIEW = "xyz.cesarbiker.RodaliesWidget.clickListView";
    public static final String CLICK_ORIGEN_TEXT = "xyz.cesarbiker.RodaliesWidget.clickOrigenTextId_";
    public static final String CLICK_DESTI_TEXT = "xyz.cesarbiker.RodaliesWidget.clickDestiTextId_";
    public static final String ACTION_CLICK_UPDATE_BUTTON = "org.angelmariages.RodaliesWidget.clickUpdateButtonId_";
    public static final String ACTION_CLICK_SWAP_BUTTON = "org.angelmariages.RodaliesWidget.clickSwapButtonId_";
    public static final String ACTION_UPDATE_STATIONS = "org.angelmariages.RodaliesWidget.sendNewSettingsId_";

    public static final String ACTION_SEND_NEWSTATIONS = "org.angelmariages.RodaliesWidget.sendNewStations";
    public static final String ACTION_CLICK_STATIONS_TEXT = "org.angelmariages.RodaliesWidget.clickStationsText_";
    public static final String ACTION_CLICK_LIST_ITEM = "org.angelmariages.RodaliesWidget.clickListItem_";
    public static final String EXTRA_ORIGINorDESTINATION = "org.angelmariages.RodaliesWidget.originOrDestination";
    public static final String EXTRA_ORIGIN = "org.angelmariages.RodaliesWidget.extraOrigin";
    public static final String EXTRA_DESTINATION = "org.angelmariages.RodaliesWidget.extraDestination";
    public static final String SEND_NEWSETTINGSNOID = "xyz.cesarbiker.RodaliesWidget.sendNewSettings";
    public static final String EXTRA_WIDGET_ID = "xyz.cesarbiker.RodaliesWidget.extraWidgetId";
    public static final String EXTRA_RIDELENGTH = "org.angelmariages.RodaliesWidget.extraRideLength";

    public static final String EXTRA_NEWSTATIONS = "xyz.cesarbiker.RodaliesWidget.newSettings";
    public static final String EXTRA_ORIORDEST = "xyz.cesarbiker.RodaliesWidget.oriordest";
    private static final boolean LOGGING = true;
    public static final String PREFERENCE_KEY = "xyz.cesarbiker.RodaliesWidget.PREFERENCE_FILE_KEY_ID_";

    public static final String PREFERENCE_STRING_ORIGEN = "xyz.cesarbiker.RodaliesWidget.PREFERENCE_STRING_ORIGEN";

    public static final String PREFERENCE_STRING_DESTI = "xyz.cesarbiker.RodaliesWidget.PREFERENCE_STRING_DESTI";
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
            "Vic", "Viladecans", "Viladecavalls", "Vilafranca del Penedés", "Vilanova i la Geltrú", "Vilassar de Mar"};
    public static final String[] STATION_IDS = new String[]{"72400", "79600", "79404", "77106", "77107", "78705", "78804",
            "79009", "79400", "78806", "71802", "78805", "78802", "79004", "71801", "78801", "71708", "79606",
            "77112", "79412", "71601", "79502", "79603", "77301", "79601", "79101", "78605", "72210", "71705",
            "77105", "78706", "72503", "72303", "71604", "71603", "79407", "72211", "71707", "72201", "72203",
            "77103", "71703", "71706", "72208", "79100", "77006", "79105", "79107", "72202", "72305", "77114",
            "77102", "72205", "79011", "77306", "77310", "72206", "77100", "79109", "79102", "79200", "79605",
            "77110", "78600", "72209", "79500", "72300", "79006", "77004", "79005", "78708", "78707", "78800",
            "77002", "79405", "79406", "79007", "79408", "79103", "77005", "79604", "77304", "71704", "79409",
            "77309", "77303", "79106", "77200", "72501", "78704", "78709", "78703", "79403", "79501", "79104",
            "72502", "72301", "72302", "77104", "78610", "79602", "77113", "72207", "71600", "78604", "77003",
            "79608", "71602", "71701", "78700", "78710", "79607", "77111", "77305", "77307", "78606", "78607",
            "77109", "71709", "78609", "72204", "71700", "79410"};
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void log(String message) {
        if(LOGGING) {
            Log.d("RodaliesLog", message);
        }
    }

    public static void log(String message, String type) {
        if(LOGGING) {
            switch (type) {
                case "e":
                    Log.e("RodaliesLog", message);
                    break;
                case "w":
                    Log.w("RodaliesLog", message);
                    break;
                case "i":
                    Log.i("RodaliesLog", message);
                    break;
                default:
                    log(message);
            }
        }
    }

    public static int getIdFromIntent(Intent intent) {
        return intent.getIntExtra(Utils.EXTRA_WIDGET_ID, -1);
    }

    /*public static int getIdIntent(Intent intent, String action) {
        try {
            return Integer.valueOf(intent.getAction().substring(action.length()));
        } catch(NumberFormatException e) {
            return -1;
        }
    }*/

    public static void saveStations(Context context, int idWidget, String origen, String desti) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.PREFERENCE_KEY + String.valueOf(idWidget), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Utils.PREFERENCE_STRING_ORIGEN, origen);
        editor.putString(Utils.PREFERENCE_STRING_DESTI, desti);
        editor.apply();
    }

    public static String[] getStations(Context context, int idWidget) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.PREFERENCE_KEY + String.valueOf(idWidget), Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return new String[] {
                    sharedPreferences.getString(Utils.PREFERENCE_STRING_ORIGEN, null),
                    sharedPreferences.getString(Utils.PREFERENCE_STRING_DESTI, null)
            };
        }
        return null;
    }

    public static int getIDFromEstacio(String estacio) {
        for(int i = 0; i < STATION_NAMES.length; i++)
            if(STATION_NAMES[i].equals(estacio))
                return Integer.parseInt(STATION_IDS[i]);
        return -1;
    }

    public static String getEstacioFromID(int idestacio) {
        for(int i = 0; i < STATION_IDS.length; i++)
            if(STATION_IDS[i].equals(Integer.toString(idestacio)))
                return STATION_NAMES[i];
        return "Cap";
    }

    public static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        if(mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return mFirebaseAnalytics;
    }
}
