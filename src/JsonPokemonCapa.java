import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonPokemonCapa {

	private static String gen = "1";
	private static String verString = "red-blue";
	private static String url = "https://pokeapi.co/api/v2/generation/"+gen;


	TreeMap<Integer,String> pokes;
	TreeMap<Integer,ArrayList<String[]>> pokeCapa;

	public JsonPokemonCapa() {
		pokes = recupUrls();
		pokeCapa = createCapas();
	}



	public static TreeMap<Integer,String> recupUrls() {
    	String urlJson = recupJSON(url);
    	TreeMap<Integer,String> urls =new TreeMap<>();
        try {
			JSONObject obj = (JSONObject) new JSONParser().parse(urlJson);
			JSONArray pokemon_species = (JSONArray) obj.get("pokemon_species");
			String url= "";
			for (Object p : pokemon_species) {
				JSONObject jsonObj = (JSONObject) p;
				url = (String) jsonObj.get("url");
				String info[] = getPokemonNomUrl(recupJSON(url),(String) jsonObj.get("name"));
				urls.put(Integer.parseInt(info[0]),info[1]);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

        return urls;
    }

    public static String[] getPokemonNomUrl(String json,String nom) {
		String[] info = new String[2];
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(json);
			String id = obj.get("id").toString();
			info[0] = id;
			JSONArray varieties = (JSONArray) obj.get("varieties");
			for (Object v : varieties) {
				JSONObject jsonObj2 = (JSONObject) v;
				JSONObject poke = (JSONObject) jsonObj2.get("pokemon");
				String nomPoke = (String) poke.get("name");
				if(nomPoke.equals(nom)) {
					info[1] = (String) poke.get("url");
				}
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return info;
	}

    public static ArrayList<String[]> implementationCapSet(String url) {
    	ArrayList<String[]> moves = new ArrayList<>();
    	try {
			JSONObject obj = (JSONObject) new JSONParser().parse(recupJSON(url));
			JSONArray movesJSON = (JSONArray) obj.get("moves");
			for (Object m : movesJSON) {
				JSONObject jsonObj = (JSONObject) m;
				JSONArray versionDetail = (JSONArray) jsonObj.get("version_group_details");
				for (Object vd : versionDetail) {
					JSONObject jsonObj2 = (JSONObject) vd;
					JSONObject version = (JSONObject)jsonObj2.get("version_group");
					String ver = (String) version.get("name");
					if(ver.equals(verString)) {
						String[] move = new String[3];
						JSONObject moveJSON = (JSONObject) jsonObj.get("move");
						move[0] = (String) moveJSON.get("name");
						move[1] = jsonObj2.get("level_learned_at").toString();
						move[2] = (String) moveJSON.get("url");
						moves.add(move);
					}
				}
			}
    	} catch (ParseException e) {

		}
    	return moves;

    }


    public static void triCapa(ArrayList<String[]> capa) {
    	try{
	    	for(int i = 0;i<capa.size();i++) {
	    		System.out.println("Tri de: "+capa.get(i)[0]);
	    		JSONObject obj = (JSONObject) new JSONParser().parse(recupJSON(capa.get(i)[2]));
	    		JSONObject catClass = (JSONObject) obj.get("damage_class");
	    		String cat = (String) catClass.get("name");
	    		if(cat.equals("status")) {
	    			capa.remove(i);
	    			i--;
	    		}
	    		else{
	    			JSONArray names = (JSONArray) obj.get("names");
	    			for (Object m : names) {
	    				JSONObject jsonObj = (JSONObject) m;
	    				JSONObject languaugeJson = (JSONObject) jsonObj.get("language");
	    				String langue = (String) languaugeJson.get("name");
	    				if(langue.equals("fr") ){
	    					String[] info = capa.get(i);
							info[0] = (String) jsonObj.get("name");
	    					info[2] = null;
							capa.set(i,info);
	    				}
	    			}
	    		}
	    	}
	    } catch (ParseException e){
	    	e.printStackTrace();
	    }
    }

    public static void triCapaS(ArrayList<String[]> capa) {
    	System.out.println("Tri des doubles");
    	for(int i =0;i<capa.size();i++) {
    		for(int j =0;j<capa.size();j++) {
    			if(j!=i && capa.get(i)[0].equals(capa.get(j)[0])) {
    				if(Integer.parseInt(capa.get(i)[1]) < Integer.parseInt(capa.get(j)[1])) {
    					capa.remove(j);
    	    			i--;
    				}
    				else {
    					capa.remove(i);
    	    			i--;
    				}
    				break;
    			}
    		}
    	}
    		
    }
    
    
    public static String recupJSON(String urlString) {
		String inputStr ="";
		try {
            URL hp = new URL(urlString);
            HttpURLConnection hpCon = (HttpURLConnection) hp.openConnection();
            hpCon.connect();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(hpCon.getInputStream()));
            StringBuilder responseStrBuilder = new StringBuilder();

            while ((inputStr = streamReader.readLine()) != null) {
            	responseStrBuilder.append(inputStr);
            }
            inputStr = responseStrBuilder.toString();

            streamReader.close();
            hpCon.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return inputStr;
	}

    public void printToString() {
    	for(int p: pokes.keySet()) {
    		System.out.println("ID: "+p+"; "+pokes.get(p));
    	}
    }

    public static void printCapas(ArrayList<String[]> capa) {
    	for(String[] i: capa) {
    		System.out.println(i[0]);
    		System.out.println(i[1]);
    	}
    }

    public TreeMap<Integer,ArrayList<String[]>> createCapas(){
    	TreeMap<Integer,ArrayList<String[]>> pokeCapa = new TreeMap<>();
    	for(int i : pokes.keySet()) {
    		ArrayList<String[]> capas = implementationCapSet(pokes.get(i));
    		System.out.println("\nPokemon: "+i);
    		triCapaS(capas);
    		triCapa(capas);
    		pokeCapa.put(i,capas);
    	}
    	return pokeCapa;
    }

    public void printAll() {
    	for(int i : pokeCapa.keySet()) {
    		System.out.println("ID: "+ i +"\nCapacites:");
    		printCapas(pokeCapa.get(i));
    	}
    }

    public void createJson() {
    	FileWriter file;
		try {
			file = new FileWriter("moves.json");
    	JSONArray pok = new JSONArray();
    	for(int i : pokeCapa.keySet()) {
    		JSONObject pokes = new JSONObject();
    		pokes.put("id", i);
    		JSONArray capas = new JSONArray();
    		for(String[] c: pokeCapa.get(i)) {
    			JSONObject capa = new JSONObject();
    			capa.put("name",c[0]);
        		capa.put("level",c[1]);
        		capas.add(capa);
        	}
    		pokes.put("Capacites", capas);
    		pok.add(pokes);
    	}
    	JSONObject obj = new JSONObject();
    	obj.put("capacites_sets", pok);
		file.write(obj.toJSONString());
		file.flush();
		file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
    	JsonPokemonCapa p1 = new JsonPokemonCapa();
    	p1.printAll();
    	p1.createJson();
    	System.out.println("Ecriture Réussi");
    }
}
