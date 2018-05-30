package bigcookingdata_engine.business.data.recipe;

public class Ingredient {
    private int id;
    private String complement;
    private String prefix;
    private String name;
    private int quantity;

    public Ingredient(){

    }

    public Ingredient(int id, String complement, String prefix, String name, int quantity) {
        this.id = id;
        this.complement = complement;
        this.prefix = prefix;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}