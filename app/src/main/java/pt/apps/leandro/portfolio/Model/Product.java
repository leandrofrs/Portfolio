package pt.apps.leandro.portfolio.Model;


public class Product {

    private Long id;
    private String name;
    private String price_buy;
    private String price_sell;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice_buy() {
        return price_buy;
    }

    public void setPrice_buy(String price_buy) {
        this.price_buy = price_buy;
    }

    public String getPrice_sell() {
        return price_sell;
    }

    public void setPrice_sell(String price_sell) {
        this.price_sell = price_sell;
    }
}


