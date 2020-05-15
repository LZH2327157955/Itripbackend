package cn.itrip.search.beans.vo.hotel;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

public class ItripHotelVO implements Serializable {

    @Field("id")
    private Long id;

    @Field("hotelName")
    private String hotelName;

    @Field("address")
    private String address;

    @Field("hotelLevel")
    private Integer hotelLevel;

    @Field("redundantCityName")
    private String redundantCityName;

    @Field("redundantProvinceName")
    private String redundantProvinceName;

    @Field("redundantCountryName")
    private String redundantCountryName;

    @Field("maxPrice")
    private Double maxPrice;

    @Field("minPrice")
    private Double minPrice;

    @Field("extendPropertyNames")
    private String extendPropertyNames;

    @Field("extendPropertyPics")
    private String extendPropertyPics;

    @Field("tradingAreaNames")
    private String tradingAreaNames;

    @Field("featureNames")
    private String featureNames;

    @Field("isOkCount")
    private Integer isOkCount;
    @Field("commentCount")
    private Integer commentCount;
    @Field("avgScore")
    private Double avgScore;

    @Field("imgUrl")
    private String imgUrl;

//省略getter/setter
}
