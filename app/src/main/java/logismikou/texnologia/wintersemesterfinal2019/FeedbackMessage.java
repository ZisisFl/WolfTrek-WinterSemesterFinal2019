package logismikou.texnologia.wintersemesterfinal2019;

public class FeedbackMessage {

    public String user;
    public String timestamp;
    public String store_suggestion;
    public String general_suggestion;

    public FeedbackMessage(){
        // Default constructor required for calls to DataSnapshot.getValue(CreditCard.class)
    }

    public FeedbackMessage(String user, String timestamp, String store_suggestion,
                           String general_suggestion){

        this.user = user;
        this.timestamp = timestamp;
        this.store_suggestion = store_suggestion;
        this.general_suggestion = general_suggestion;
    }
}
