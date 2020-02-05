import java.sql.Date;

public class Transaction {
    public Long shopWeek;
    public Date shopDate;
    public Integer shopWeekday;
    public Integer shopHour;
    public Integer quantity;
    public Double spend;
    public String prodCode;
    public String prodCode10;
    public String prodCode20;
    public String prodCode30;
    public String prodCode40;
    public String custCode;
    public String custPriceSensetivity;
    public String custLifestage;
    public Long basketId;
    public String basketSize;
    public String basketPriceSensetivity;
    public String basketType;
    public String basketDominantMission;
    public String storeCode;
    public String storeFormat;
    public String storeRegion;

    public Transaction(Long shopWeek, Date shopDate, Integer shopWeekday, Integer shopHour, Integer quantity, Double spend, String prodCode, String prodCode10, String prodCode20, String prodCode30, String prodCode40, String custCode, String custPriceSensetivity, String custLifestage, Long basketId, String basketSize,
                       String basketPriceSensetivity, String basketType, String basketDominantMission, String storeCode, String storeFormat, String storeRegion) {
        this.shopWeek = shopWeek;
        this.shopDate = shopDate;
        this.shopWeekday = shopWeekday;
        this.shopHour = shopHour;
        this.quantity = quantity;
        this.spend = spend;
        this.prodCode = prodCode;
        this.prodCode10 = prodCode10;
        this.prodCode20 = prodCode20;
        this.prodCode30 = prodCode30;
        this.prodCode40 = prodCode40;
        this.custCode = custCode;
        this.custPriceSensetivity = custPriceSensetivity;
        this.custLifestage = custLifestage;
        this.basketId = basketId;
        this.basketSize = basketSize;
        this.basketPriceSensetivity = basketPriceSensetivity;
        this.basketType = basketType;
        this.basketDominantMission = basketDominantMission;
        this.storeCode = storeCode;
        this.storeFormat = storeFormat;
        this.storeRegion = storeRegion;
    }
}
