package bigcookingdata_engine.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bigcookingdata_engine.business.data.user.User;
import org.json.*;
import org.parboiled.common.ImmutableList;

import bigcookingdata_engine.business.data.recipe.Ingredient;
import bigcookingdata_engine.business.data.recipe.Recipe;
import bigcookingdata_engine.business.data.recipe.Step;
import bigcookingdata_engine.business.data.recipe.Utensil;

public abstract class Neo4J implements java.sql.Connection {

	private static java.sql.Connection conn = null;

	public static void main(String[] args) throws Exception {
		ArrayList<Recipe> al = new ArrayList<>();
		ArrayList<Utensil> sl = new ArrayList<>();

		int[] i = { 1};
		//createUser("aa@aa.aa", "", "a");
		//System.out.println(connection("a", "a"));
	}

	public static ArrayList<Recipe> getRecipesByIngred(String... ingred) throws SQLException, JSONException {
		List<String> ing = ImmutableList.of(ingred);

		ArrayList<Recipe> al = new ArrayList<>();

		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			String match = "MATCH ";
			String q1 = "(r:Recipe)-[:IS_COMPOSED_TO]->";
			String returne = " RETURN r";

			String q2 = "";
			String q3 = "";

			if (ing.size() == 1)
				q2 = "(Ingredient{nameIngred : '" + ing + "'})";

			else if (ing.size() > 1) {
				for (String i : ing) {
					q3 += q1 + "(" + i.replaceAll(" ", "") + ":Ingredient{nameIngred : '" + i + "'}),";
				}
			}

			System.out.println(match + q2 + q3.substring(0, q3.length() - 1) + returne);
			java.sql.ResultSet rs = stmt.executeQuery(match + q2 + q3.substring(0, q3.length() - 1) + returne);
			while (rs.next()) {
				String result = rs.getString(1);
				System.out.println(result);
				JSONObject json = new JSONObject(result);
				Recipe recipe = new Recipe();
				recipe.setTimeTotal(json.getString("timetotal"));
				// erreur ici recipe.setCategorie((String)
				// json.getString("categorie").replaceAll(",",""
				// ).replaceAll("[\\[\\]]", "").replaceAll("'", ""));
				recipe.setLevel(json.getInt("level"));
				recipe.setNbOfPerson(json.getInt("number_of_person"));
				recipe.setTimeCooking(json.getString("timecooking"));
				//recipe.setRating((float) json.getDouble("rating"));
				recipe.setTimePrepa(json.getString("timeprepa"));
				recipe.setTitle(json.getString("title"));
				recipe.setIdRecipe(json.getInt("idRecipe"));
				recipe.setBudget(json.getInt("budget"));
				al.add(recipe);

			}
		}
		return al;
	}

	public static ArrayList<Step> getSteps(int idRecip) throws SQLException, JSONException {
		ArrayList<Step> stepList = new ArrayList<>();
		Step step = new Step();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (r:Recipe{idRecipe:'" + idRecip + "'})<-[:IS_STEP]-(s:Step) RETURN distinct s.idStep,s.numberStep,s.detailStep order by (s.numberStep)";
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			System.out.println(query);
			while (rs.next()) {
				step.setIdStep(Integer.parseInt(rs.getString(1)));
				step.setNumberStep(Integer.parseInt(rs.getString(2)));
				step.setDescStep(rs.getString(2));
				stepList.add(step);
			}
		}
		return stepList;
	}

	public static ArrayList<Utensil> getUtensil(int idRecip) throws SQLException, JSONException {

		ArrayList<Utensil> utensilList = new ArrayList<>();
		Utensil utensil = new Utensil();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "match (r:Recipe{idRecipe:'" + idRecip + "'})-[:USE]->(u:Utensil) return u";
		System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				JSONObject json = new JSONObject(result);
				utensil.setIdUtensil(Integer.parseInt(json.getString("idUtensil")));
				utensil.setNameUtensil(json.getString("nameUtensil"));

				utensilList.add(utensil);
			}
		}
		return utensilList;

	}

	public static void createUser(String name, String mail, String password) throws SQLException {
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		//int keyId;
		//keyId+1;
		String query = "CREATE (n:User { nameUser: '" + name + "', mail: '" + mail + "',password: '" + password + "'})";
		System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {

			java.sql.ResultSet rs = stmt.executeQuery(query);

		}
	}

	public static User connection(String name, String pwd) {
        User user = new User();
        String query = "MATCH (u:User {nameUser:'" + name + "', password:'" + pwd + "'}) return u";
        System.out.println(query);

        // Connect
        conn = SingletonConnectionNeo4j.getDbConnection().conn;

        // Querying
        try (java.sql.Statement stmt = conn.createStatement()) {
            java.sql.ResultSet rs = stmt.executeQuery(query);
            if (!rs.next())
                return null;

            String r = rs.getString(1);
            JSONObject json = new JSONObject(r);
            user.setName(json.getString("nameUser"));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

        public static void userLike(String name, String surname, String ingredient, int score) throws SQLException {
		String query = "MATCH (a:User),(b:Ingredient{nameIngred:'" + ingredient + "'})" + "WHERE a.nameUser = '" + name
				+ "' AND a.surname='" + surname + "'" + "CREATE (a)-[r:LIKE{score:'" + score + "'}]->(b);";
		System.out.println(query);
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {

			java.sql.ResultSet rs = stmt.executeQuery(query);

		}
	}

	public static ArrayList<Recipe> getRecipes(int[] idRecipe) {

		ArrayList<Recipe> al = new ArrayList<>();

		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		try (java.sql.Statement stmt = conn.createStatement()) {
			for (int i = 0; i < idRecipe.length; i++) {
				String query = "MATCH (r:Recipe{idRecipe:'" + idRecipe[i] + "'}) RETURN r";
				java.sql.ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					String result = rs.getString(1);
					//System.out.println(result);
					JSONObject json = new JSONObject(result);
					Recipe recipe = new Recipe();
					String title = json.getString("title");
					String timetotal = json.getString("timetotal");
					String timeprepa = json.getString("timeprepa");
					String timecooking = json.getString("timecooking");
					int level = json.getInt("level");
					int number_of_person = json.getInt("number_of_person");
					int budget = json.getInt("budget");
					
					recipe.setIdRecipe(json.getInt("idRecipe"));
					recipe.setTitle(title);
					recipe.setTimeTotal(timetotal);
					recipe.setTimePrepa(timeprepa);
					recipe.setTimeCooking(timecooking);
					recipe.setLevel(level);
					recipe.setNbOfPerson(number_of_person);
					recipe.setBudget(budget);
					
					if (json.has("rating")) {
						double rating = json.getDouble("rating");
						recipe.setRating((float)rating);
					}
					else {
						double rating;
						rating=0;
					}
					
					if (json.has("categorie")){
						String categorie = json.getString("categorie");
						recipe.setCategorie(categorie.replaceAll(",","").replaceAll("[\\[\\]]", "").replaceAll("'", ""));
					}
					else {
						String categorie;
						categorie = "None";
					}
					al.add(recipe);
				}
			}
		} catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return al;
	}

	public static int[] getRecipesByIngred(int[] idIngred) {

		int[] recipes_id = {};
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query="";
		if(idIngred.length==1)
				query="MATCH (r:Recipe)-[:IS_COMPOSED_TO]->(i:Ingredient{idIngred : '" + idIngred[0] + "'}) RETURN r";
		else{
			// Querying
			
				String match = "MATCH ";
				String q1 = "(r:Recipe)-[:IS_COMPOSED_TO]->";
				String returne = " RETURN r";
	
				String q2 = "";
				String q3 = "";
				for (int i = 0; i < idIngred.length; i++) {
					if (idIngred.length == 1)
						q2 = q1 + "(Ingredient{idIngred : '" + idIngred[i] + "'})";
	
					else if (idIngred.length > 1) {
	
						q3 += q1 + "(i" + i + ":Ingredient{idIngred : '" + idIngred[i] + "'}),";
					}
				}
				query=match + q2 + q3.substring(0, q3.length() - 1) + returne;

			}
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				//System.out.println(result);
				JSONObject json = new JSONObject(result);
				int id = json.getInt("idRecipe");
				recipes_id = Arrays.copyOf(recipes_id, recipes_id.length + 1);
				recipes_id[recipes_id.length - 1] = id;

			}
		} catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipes_id;
	}

	public static int[] getRecipesByCluster(int cluster) {

		int[] recipes_id = {};
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (r:Recipe{label:'"+cluster+"'}) return r";
		//System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				//System.out.println(result);
				JSONObject json = new JSONObject(result);
				int id = json.getInt("idRecipe");
				recipes_id = Arrays.copyOf(recipes_id, recipes_id.length + 1);
				recipes_id[recipes_id.length - 1] = id;
			}
		} catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipes_id;
	}

	public static ArrayList<Ingredient> getIngreds(int[] idIngred) throws SQLException, JSONException {

		ArrayList<Ingredient> ingredList = new ArrayList<>();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			for (int j = 0; j < idIngred.length; j++) {
				String query = "match (i:Ingredient{idIngred:'"+ idIngred[j] + "'}) return i";
				System.out.println(query);
				java.sql.ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					Ingredient ing = new Ingredient();
					String result = rs.getString(1);
					System.out.println(result);
					JSONObject json = new JSONObject(result);
					ing.setId(json.getInt("idIngred"));
					ing.setName(json.getString("nameIngred"));
					
					if (json.has("quantity")) {
						int quantity = json.getInt("quantity");
						ing.setQuantity(quantity);
					}
					else {
						int quantity;
						quantity=0;
					}
			
					if (json.has("prefix")) {
						String prefix = json.getString("prefix");
						ing.setPrefix(prefix);
					}
					else {
						String prefix;
						prefix="None";
					}
					ingredList.add(ing);
				}
			}
			return ingredList;

		}
	}

	public static void ratingIngred(String name, int id_ingr, int value)  {
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (user:User{nameUser:'"+name+"'}), (ingred:Ingredient{idIngred:'"+id_ingr+"'}) CREATE (user)-[:LIKE{rateIngred:"+value+"}]->(ingred)";
		System.out.println(query);

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
				java.sql.ResultSet rs = stmt.executeQuery(query);

		} catch (SQLException e) {
            e.printStackTrace();
        }
    }


	public static HashMap<Integer, Integer> getRatingIngred(String name) {
		
	    HashMap<Integer,Integer> map = new HashMap<>();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query ="MATCH (user:User{nameUser:'"+name+"'})-[lri:LIKE]->(ingred:Ingredient) with ingred, lri.rateIngred as rateIngred\n" + 
				"return ingred.idIngred,rateIngred";

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
				java.sql.ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					String idIngred = rs.getString(1);
					String score = rs.getString(2);
					map.put(Integer.parseInt(idIngred),Integer.parseInt(score));
					}
				} catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
	}

	public static void ratingCluster(String name, int id_cluster, int value) throws SQLException {
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query ="MATCH (u:User{nameUser:'"+name+"'})\n" + 
						"MATCH (c:Cluster{idCluster:'"+id_cluster+"'})\n" + 
						"CREATE (u)-[:LIKE{rateCluster:"+value+"}]->(c)\n" + 
						"";
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
		} 
	}

	public static ArrayList<Integer> getRatingClusters(String name) {
		ArrayList<Integer> list = new ArrayList<>();
		HashMap<Integer, Integer> map = new HashMap<>();

		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query ="MATCH (user:User{nameUser:'"+name+"'})-[lri:LIKE]->(cluster:Cluster) with cluster, lri.rateCluster as rateCluster\n" + 
				"return cluster.idCluster,rateCluster";
		//System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
				java.sql.ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					String idCluster = rs.getString(1);
					String score = rs.getString(2);
					map.put(Integer.parseInt(idCluster), Integer.parseInt(score));
					}
				//System.out.println(map.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}

				for (int i = 0 ; i<30 ; i++){
		            if (map.containsKey(i))
		                list.add(map.get(i));
		            else
                        list.add(0);
                }

		return list;
	}
	
	public void calculEuclideanDistance(int id_user) throws SQLException{

		  

		
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query ="MATCH (u1:user)-[x:LIKE]->(i:ingredient),"
		 			+"(u2:user)-[y:LIKE]->(i)"
		 			+"WHERE id(u1)<id(u2)"
		 			+" WITH sqrt(sum((x.score-y.score)^2)) AS euc , u1, u2"
		+" MERGE (u1)-[d:DISTANCE]->(u2)"
		 +"SET d.euclidean=euc;";
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
		}
	}
	
	
	
	public HashMap<Integer, Integer> getSimilarUser(int id_user){
		return null;
		
	}
	
	/**
	 * Methode pour la suggestion des recettes
	 * 
	 * @param name
	 * @return
	 */
	public static Ingredient getIngeByName(String name) {
		java.sql.Connection conn = null;
		String r = null;
		// connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		Ingredient i = new Ingredient();
		// requête
		String req = "match (i:Ingredient{nameIngred:'" + name + "'}) return i;";

		try {
			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(req);
			while (rs.next()) {
				r = rs.getString(1);
				JSONObject json = new JSONObject(r);
				i.setName(name);
				i.setId(json.getInt("idIngred"));
			}
		} catch (Exception e) {
		}

		return i;
	}
}


