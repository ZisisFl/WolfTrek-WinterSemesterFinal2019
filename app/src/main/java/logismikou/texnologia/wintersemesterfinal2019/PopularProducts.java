package logismikou.texnologia.wintersemesterfinal2019;

public class PopularProducts {

    public String image_url;
    public String name;
    public float price;
    public String reference;

    public PopularProducts(){
        // Default constructor required for calls to DataSnapshot.getValue(PopularProducts.class)
    }

    public PopularProducts(String image_url, String name, float price, String reference){

        this.image_url = image_url;
        this.name = name;
        this.price = price;
        this.reference = reference;
    }
}
