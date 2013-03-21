package rules.tradeorder.facts;

import java.util.Date;

public class TradeOrder {
    
    private Date orderOpenTime;
    private String status;
    
    public TradeOrder() {
    }
    
    public TradeOrder(Date openTime, String status) {
        this.orderOpenTime = openTime;
        this.status = status;
    }

    public Date getOrderOpenTime() {
        return orderOpenTime;
    }

    public void setOrderOpenTime(Date orderOpenTime) {
        this.orderOpenTime = orderOpenTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
