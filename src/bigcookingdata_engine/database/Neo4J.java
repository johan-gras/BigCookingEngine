package bigcookingdata_engine.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bigcookingdata_engine.business.data.user.User;
import org.json.*;
import org.parboiled.common.ImmutableList;

import com.sun.javafx.css.CalculatedValue;

import bigcookingdata_engine.business.data.recipe.Ingredient;
import bigcookingdata_engine.business.data.recipe.Recipe;
import bigcookingdata_engine.business.data.recipe.Step;
import bigcookingdata_engine.business.data.recipe.TopIngred;
import bigcookingdata_engine.business.data.recipe.Utensil;

public abstract class Neo4J implements java.sql.Connection {

	private static java.sql.Connection conn = null;

	public static void main(String[] args) throws Exception {
		ArrayList<Recipe> al = new ArrayList<>();
		ArrayList<Utensil> sl = new ArrayList<>();
		ArrayList<String> l = new ArrayList<>();
		// getIngreFromFridge("sofiane@sofiane.fr");
		// l.add("Sel");
		// l.add("fromage");
		// getRecipeFromFrigo(l);
		// createUser("rezki", "rez", "jo", "26");
		//getRecipesById(11);
		// ratingIngred("rez", 12, 5);
		// ratingIngred("rez", 22, 9);
		// ratingIngred("rez", 32, 9);
		//ratingCluster("sofiane@gmail.com", 6, 5);
		// calculEuclideanDistance();
		// getSimilarUser("aiss");
		// ratingRecipe("max", 40, 7);
		// ratingRecipe("max", 432, 7);
		// ratingRecipe("max",654, 7);
		//System.out.println(getBestRecipe("aissam@gmail.com"));
		getBestRecipe("aissam@gmail.com").toString();
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
				// recipe.setRating((float) json.getDouble("rating"));
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
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		// String query = "match (s:Step)-[:IS_STEP]->(:Recipe{idRecipe:'" + idRecip +
		// "'}) return s";

		String query = "match (s:Step)-[:IS_STEP]->(:Recipe{idRecipe:'" + idRecip + "'}) "
				+ "with toInt(s.numberStep) AS nb, s " + "return s order by nb";
		System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			// System.out.println(query);
			while (rs.next()) {
				Step step = new Step();
				String result = rs.getString(1);
				JSONObject json = new JSONObject(result);
				step.setIdStep(json.getInt("idStep"));
				step.setNumberStep(json.getInt("numberStep"));
				step.setDescStep(json.getString("detailStep"));
				stepList.add(step);
				System.out.println(json.getString("detailStep"));
			}
		}
		return stepList;
	}

	/**
	 * récupération des ingrédiant By Id Recette
	 * 
	 * @paraescStepm idRecip
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static ArrayList<Ingredient> getIngredientByIdRecipe(int idRecip) throws SQLException, JSONException {
		ArrayList<Ingredient> listIng = new ArrayList<>();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (r:Recipe{idRecipe:'" + idRecip + "'})-[:IS_COMPOSED_TO]->(i:Ingredient) return i";
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Ingredient ing = new Ingredient();
				String result = rs.getString(1);
				JSONObject json = new JSONObject(result);
				ing.setId(json.getInt("idIngred"));
				ing.setName(json.getString("nameIngred"));
				listIng.add(ing);
			}
		}
		return listIng;
	}

	public static ArrayList<Utensil> getUtensil(int idRecip) throws SQLException, JSONException {

		ArrayList<Utensil> utensilList = new ArrayList<>();
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
				Utensil utensil = new Utensil();
				JSONObject json = new JSONObject(result);
				utensil.setIdUtensil(Integer.parseInt(json.getString("idUtensil")));
				utensil.setNameUtensil(json.getString("nameUtensil"));
				utensilList.add(utensil);
			}
		}
		return utensilList;

	}

	public static void createUser(String name, String mail, String password, String poids) throws SQLException {
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		// int keyId;
		// keyId+1;
		String query = "MERGE (n:User { nameUser: '" + name + "', mail: '" + mail + "',password: '" + password
				+ "', poids: '" + poids + "' })";
		System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {

			java.sql.ResultSet rs = stmt.executeQuery(query);

		}
	}

	public static User connection(String mail, String pwd) {
		User user = new User();
		String query = "MATCH (u:User {mail:'" + mail + "', password:'" + pwd + "'}) return u";
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
			user.setMail(json.getString("mail"));
			user.setName(json.getString("nameUser"));
			user.setWeight(json.getString("poids"));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	public static void userLike(String name, String mail, String ingredient, int score) throws SQLException {
		String query = "MATCH (a:User),(b:Ingredient{nameIngred:'" + ingredient + "'})" + "WHERE a.nameUser = '" + name
				+ "' AND a.mail='" + mail + "'" + "MERGE (a)-[r:LIKE{score:'" + score + "'}]->(b);";
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
					// System.out.println(result);
					ArrayList<Step> listStep = new ArrayList<>();
					ArrayList<Utensil> listUtensil = new ArrayList<>();
					ArrayList<Ingredient> inggr = new ArrayList<>();
					inggr = Neo4J.getIngredientByIdRecipe(idRecipe[i]);
					listStep = Neo4J.getSteps(idRecipe[i]);
					listUtensil = Neo4J.getUtensil(idRecipe[i]);
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
					recipe.setSteps(listStep);
					recipe.setUtensils(listUtensil);
					recipe.setIngredients(inggr);
					if (json.has("rating")) {
						double rating = json.getDouble("rating");
						recipe.setRating((float) rating);
					} else {
						double rating;
						rating = 0;
					}

					if (json.has("categorie")) {
						String categorie = json.getString("categorie");
						recipe.setCategorie(
								categorie.replaceAll(",", "").replaceAll("[\\[\\]]", "").replaceAll("'", ""));
					} else {
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
		String query = "";
		if (idIngred.length == 1)
			query = "MATCH (r:Recipe)-[:IS_COMPOSED_TO]->(i:Ingredient{idIngred : '" + idIngred[0] + "'}) RETURN r";
		else {
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
			query = match + q2 + q3.substring(0, q3.length() - 1) + returne;

		}
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				// System.out.println(result);
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
		String query = "MATCH (r:Recipe{label:'" + cluster + "'}) return r";
		// System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				// System.out.println(result);
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

	public static int[] getRecipesByCategorie(String categorie) {

		int[] recipes_id = {};
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (r:Recipe)-[IS_CATEGORIZE]-(categorie:Categorie{nameCategorie:'" + categorie
				+ "'}) return r limit 30";
		System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				// System.out.println(result);
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
				String query = "match (i:Ingredient{idIngred:'" + idIngred[j] + "'}) return i";
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
					} else {
						int quantity;
						quantity = 0;
					}

					if (json.has("prefix")) {
						String prefix = json.getString("prefix");
						ing.setPrefix(prefix);
					} else {
						String prefix;
						prefix = "None";
					}
					ingredList.add(ing);
				}
			}
			return ingredList;

		}
	}

	public static void ratingIngred(String mail, int id_ingr, int value) {
		// Connection
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (user:User{mail:'" + mail + "'}), (ingred:Ingredient{idIngred:'" + id_ingr
				+ "'}) CREATE (user)-[:LIKE{rateIngred:" + value + "}]->(ingred)";
		System.out.println(query);

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static HashMap<Integer, Integer> getRatingIngred(String name) {

		HashMap<Integer, Integer> map = new HashMap<>();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (user:User{nameUser:'" + name
				+ "'})-[lri:LIKE]->(ingred:Ingredient) with ingred, lri.rateIngred as rateIngred\n"
				+ "return ingred.idIngred,rateIngred";

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			if (rs == null)
				System.out.println("RS NULL");
			while (rs.next()) {
				String idIngred = rs.getString(1);
				String score = rs.getString(2);
				map.put(Integer.parseInt(idIngred), Integer.parseInt(score));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static ArrayList<TopIngred> getIngredByTop3(String name) {

		HashMap<String, Integer> map = new HashMap<>();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (user:User{nameUser:'" + name
				+ "'})-[lri:LIKE]->(ingred:Ingredient) with ingred, lri.rateIngred as rateIngred\n"
				+ "return DISTINCT ingred.nameIngred,rateIngred ORDER BY rateIngred limit 3";
		ArrayList<TopIngred> TopList = new ArrayList<>();
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			if (rs == null)
				System.out.println("RS NULL");
			while (rs.next()) {
				TopIngred top = new TopIngred();
				String nameIngred = rs.getString(1);
				String score = rs.getString(2);
				top.setNamIng(nameIngred);
				top.setRating(score);
				TopList.add(top);
				// map.put(nameIngred, Integer.parseInt(score));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return TopList;
	}

	public static void ratingRecipe(String mail, int idRecip, int score) throws SQLException {
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (u:User{mail:'" + mail + "'})\n" + "MATCH (r:Recipe{idRecipe:'" + idRecip + "'})\n"
				+ "MERGE (u)-[l:LIKE]->(r) SET l.score='" + score + "' ";
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
		}
	}

	public static void ratingCluster(String mail, int id_cluster, int value) throws SQLException {
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (u:User{mail:'" + mail + "'})\n" + "MATCH (c:Cluster{idCluster:'" + id_cluster
				+ "'})\n" + "MERGE (u)-[:LIKE{rateCluster:" + value + "}]->(c)\n" + "";
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
		String query = "MATCH (user:User{nameUser:'" + name
				+ "'})-[lri:LIKE]->(cluster:Cluster) with cluster, lri.rateCluster as rateCluster\n"
				+ "return cluster.idCluster,rateCluster";
		System.out.println(query);
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String idCluster = rs.getString(1);
				String score = rs.getString(2);
				System.out.println(idCluster);
				System.out.println(score);
				map.put(Integer.parseInt(idCluster), Integer.parseInt(score));
			}
			// System.out.println(map.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 30; i++) {
			if (map.containsKey(i))
				list.add(map.get(i));
			else
				list.add(0);
		}

		return list;
	}

	public static void calculEuclideanDistance() throws SQLException {

		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (u1:User)-[x:LIKE]->(i:Ingredient)," + "(u2:User)-[y:LIKE]->(i)" + "WHERE id(u1)<id(u2)"
				+ " WITH sqrt(sum((toInt(x.rateIngred)-toInt(y.rateIngred))^2)) AS euc , u1, u2"
				+ " MERGE (u1)-[d:DISTANCE]->(u2) MERGE (u2)-[d1:DISTANCE]-(u1) SET d.euclidean=euc SET d1.euclidean=euc;";

		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			System.out.println(query);
		}
	}

	public static ArrayList<String> getSimilarUser(String mail) throws JSONException, SQLException {
		calculEuclideanDistance();
		ArrayList<String> list = new ArrayList<>();
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;

		String query = "match (u1:User{mail:'" + mail + "'})-[d:DISTANCE]->(u2:User) "
				+ "with toInt(d.euclidean) AS distance , u2 " + "return u2, distance ORDER BY distance limit 3";
		// Querying
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				System.out.println(result);
				JSONObject json = new JSONObject(result);
				// System.out.println(json.getString("mail"));
				list.add(json.getString("mail"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;

	}

	public static ArrayList<Integer> getBestRecipe(String mail) {

		ArrayList<String> list = new ArrayList<>();
		ArrayList<Integer> result = new ArrayList<>();

		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		try {
			list = getSimilarUser(mail);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			String query = "MATCH (u:User{mail:'" + list.get(i) + "'}),(r:Recipe),(u)-[l:LIKE]->(r) "
					+ "return r.idRecipe ORDER BY toInt(l.score) DESC limit 3";
			System.out.println(query);
			// Querying
			try (java.sql.Statement stmt = conn.createStatement()) {
				java.sql.ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					// rs.getString(1);
					System.out.println(rs.getString(1));
					result.add(Integer.parseInt(rs.getString(1)));
				}

				// System.out.println(map.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	public static int getIngeByName(String name) {
		java.sql.Connection conn = null;
		String r = null;
		int id = -1;
		// connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		// requête
		String req = "match (i:Ingredient{nameIngred:'" + name + "'}) return i;";
		System.out.println(req);
		try {
			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(req);
			while (rs.next()) {
				r = rs.getString(1);
				JSONObject json = new JSONObject(r);
				id = json.getInt("idIngred");
			}
		} catch (Exception e) {
		}

		return id;
	}

	public static Ingredient getRandomIngred() throws SQLException, JSONException {

		Ingredient i = new Ingredient();
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (i:Ingredient),(u:User)-[l:LIKE]-(i1:Ingredient)" + "WITH i,rand() AS rand, i1 "
				+ "ORDER BY rand WHERE i <> i1 AND NOT i.photoIngred CONTAINS 'unique' return i LIMIT 1";
		String r = null;
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				r = rs.getString(1);
				JSONObject json = new JSONObject(r);
				i.setId(Integer.parseInt(json.getString("idIngred")));
				i.setName(json.getString("nameIngred"));
				i.setPhoto(json.getString("photoIngred"));
			}
			// System.out.println(map.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public static ArrayList<Ingredient> getRatingIngred2(String name) throws JSONException {

		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		String query = "MATCH (user:User{nameUser:'" + name + "'})-[lri:LIKE]->(ingred:Ingredient) return lri, ingred";
		ArrayList<Ingredient> lisIng = new ArrayList<>();
		// Querying
		System.out.println("querrryy"+query);
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(query);
			if (rs == null)
				System.out.println("RS NULL");
			while (rs.next()) {
				Ingredient i = new Ingredient();
				String score = rs.getString(1);
				String ingred = rs.getString(2);
				JSONObject scoreJson = new JSONObject(score);
				JSONObject ingredJson = new JSONObject(ingred);
				i.setId(Integer.parseInt(ingredJson.getString("idIngred")));
				i.setName(ingredJson.getString("nameIngred"));
				String photo = ingredJson.getString("photoIngred").replace("C:\\Users\\Admin\\", "");
				i.setPhoto(photo);
				i.setScore(scoreJson.getString("rateIngred"));
				lisIng.add(i);
				System.out.println(i.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lisIng;
	}

	public static ArrayList<Recipe> getRecipeFromFrigo(ArrayList<String> ListeIngred) throws JSONException {

		/**
		 * Construction de la requête
		 */
		String q = "";
		
		if(ListeIngred.size()!=0) {
	 q = "MATCH (a:Recipe)-[:IS_COMPOSED_TO]->(i:Ingredient)\n" + "WHERE i.nameIngred IN [";
		for (int i = 0; i < ListeIngred.size() - 1; i++) {
			q += "'" + ListeIngred.get(i) + "'";
			q += ",";
		}
		q += "'" + ListeIngred.get(ListeIngred.size() - 1) + "'] RETURN a limit 3";
		}else {
			q="MATCH (a:Recipe)-[:IS_COMPOSED_TO]->(i:Ingredient{idIngred : '2'})";
		}
		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		ArrayList<Recipe> listRecipe = new ArrayList<>();
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(q);
			if (rs == null)
				System.out.println("RS NULL");
			while (rs.next()) {
				Recipe r = new Recipe();
				String recipe = rs.getString(1);
				JSONObject recipeJson = new JSONObject(recipe);
				r.setTitle(recipeJson.getString("title"));
				r.setIdRecipe(recipeJson.getInt("idRecipe"));
				System.out.println(r);
				listRecipe.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listRecipe;
	}

	public static void addIngredientToFrigo(String idIngred, String mailUser) throws JSONException, SQLException {

		/**
		 * Construction de la requête
		 */
		String q = "MATCH (u:User{mail:'" + mailUser + "'}),(i:Ingredient{idIngred:'" + idIngred
				+ "'}) MERGE (u)-[:HAS_IN_FRIDGE]->(i)";

		// Connect
		System.out.println(q);
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		java.sql.Statement stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery(q);

		System.out.println("relation frigo ajout�");

	}

	public static ArrayList<Ingredient> getIngreFromFridge(String mailUser) throws JSONException, SQLException {

		/**
		 * Construction de la requête
		 */
		String q = "MATCH (u:User{mail:'" + mailUser + "'})-[:HAS_IN_FRIDGE]->(i) return i";

		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		ArrayList<Ingredient> listIngred = new ArrayList<>();
		try (java.sql.Statement stmt = conn.createStatement()) {
			java.sql.ResultSet rs = stmt.executeQuery(q);
			if (rs == null)
				System.out.println("RS NULL");
			while (rs.next()) {
				Ingredient i = new Ingredient();
				String recipe = rs.getString(1);
				JSONObject ingredJson = new JSONObject(recipe);
				i.setId(ingredJson.getInt("idIngred"));
				i.setName(ingredJson.getString("nameIngred"));
				i.setPhoto(ingredJson.getString("photoIngred"));
				listIngred.add(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listIngred;
	}

	public static Recipe getRecipesById(int idRecipe) {

		Recipe recipe = new Recipe();

		// Connect
		SingletonConnectionNeo4j sc = SingletonConnectionNeo4j.getDbConnection();
		conn = sc.conn;
		try (java.sql.Statement stmt = conn.createStatement()) {

			String query = "MATCH (r:Recipe{idRecipe:'" + idRecipe + "'}) RETURN r";
			java.sql.ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String result = rs.getString(1);
				JSONObject json = new JSONObject(result);
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
					recipe.setRating((float) rating);
				}

				if (json.has("categorie")) {
					String categorie = json.getString("categorie");
					recipe.setCategorie(categorie.replaceAll(",", "").replaceAll("[\\[\\]]", "").replaceAll("'", ""));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return recipe;
	}

}
