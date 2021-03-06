package bigcookingdata_engine.business.data.user;

/**
 * class User
 * 
 * @author sofiane-hamiti
 *
 */
public class User {
	/**
	 * user fields
	 */
	private int id;

	private String name;
	private String surname;
	private String sexe;
	private String weight;
	private String mail;
	/**
	 * Constructor without fields
	 */
	public User() {
		super();
	}

	public User(int id, String name, String surname, String sexe, String weight) {
		super();
		this.name = name;
		this.surname = surname;
		this.sexe = sexe;
		this.weight = weight;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String string) {
		this.weight = string;
	}

	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", surname=" + surname + ", sexe=" + sexe + ", weight=" + weight + "]";
	}

}
