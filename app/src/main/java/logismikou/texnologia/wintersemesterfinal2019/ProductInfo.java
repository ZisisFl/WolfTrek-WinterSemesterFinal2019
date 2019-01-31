package logismikou.texnologia.wintersemesterfinal2019;

public class ProductInfo {

    public String description;
    public String image_url;
    public String name;
    public float price;
    public String shop_name;

    public ProductInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(ProductInfo.class)
    }

    public ProductInfo(String description, String image_url, String name, float price,
                       String shop_name){

        this.description = description;
        this.image_url = image_url;
        this.name = name;
        this.price = price;
        this.shop_name = shop_name;
    }
}
