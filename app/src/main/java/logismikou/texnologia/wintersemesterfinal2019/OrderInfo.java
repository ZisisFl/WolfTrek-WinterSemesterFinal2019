package logismikou.texnologia.wintersemesterfinal2019;

public class OrderInfo {

    String order_id;
    String time_stamp;
    String qr_code;

    public OrderInfo(){

    }

    public OrderInfo(String order_id, String time_stamp, String qr_code){

        this.order_id = order_id;
        this.time_stamp = time_stamp;
        this.qr_code = qr_code;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }
}
