package logismikou.texnologia.wintersemesterfinal2019;

public class ProductInfo {

    public String description;
    public String image_url;
    public String name;
    public float price;
    public String shop_name;
    public String shop_image_url;
    public String reference;

    public ProductInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(ProductInfo.class)
    }

    public ProductInfo(String description, String image_url, String name, float price,
                       String shop_name, String shop_image_url, String reference){

        this.description = description;
        this.image_url = image_url;
        this.name = name;
        this.price = price;
        this.shop_name = shop_name;
        this.shop_image_url = shop_image_url;
        this.reference = reference;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

}
