package pojo;

public class Fruit {
    private Integer id;
    private String name;
    private Integer inventory;
    private Integer price;


    public Fruit() {

    }

    public Fruit(String name, Integer inventory, Integer price) {
        this.name = name;
        this.inventory = inventory;
        this.price = price;
    }

    public Fruit(Integer id, String name, Integer inventory, Integer price) {
        this.id = id;
        this.name = name;
        this.inventory = inventory;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
