package logismikou.texnologia.wintersemesterfinal2019;

public class CreditCard {

    public String card_number;
    public String card_holder;
    public String cvv;
    public String exp_month;
    public String exp_year;

    public CreditCard(){
        // Default constructor required for calls to DataSnapshot.getValue(CreditCard.class)
    }

    public CreditCard(String card_number, String card_holder, String cvv, String exp_month,
                      String exp_year){

        this.card_number = card_number;
        this.card_holder = card_holder;
        this.cvv = cvv;
        this.exp_month = exp_month;
        this.exp_year = exp_year;
    }
}
