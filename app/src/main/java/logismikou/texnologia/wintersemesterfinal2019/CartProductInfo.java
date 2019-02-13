package logismikou.texnologia.wintersemesterfinal2019;

public class CartProductInfo {

    public String name;
    public String description;
    public String image_url;
    public float price;
    public int quantity;

    public CartProductInfo() {
    }

    public CartProductInfo(String name, String description, String image_url, float price,
                           int quantity){

        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
