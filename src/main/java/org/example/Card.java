package org.example;

import java.util.Objects;

public class Card {
    private String color;
    private String value;

    public int getIntValue() {
        return intValue;
    }

    private int intValue;

    public String getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }

    public Card(String c, String v){
            color=c;
            value=v;
            intValue=calculateVal();
    }
    public String showCard(){
        return color+value;
    }

    private int calculateVal(){
        int val = switch (value) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            case null, default -> Integer.parseInt(value);
        };

        return val;
    }


}
