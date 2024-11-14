package org.example;

import java.util.ArrayList;
import java.util.Objects;

public class Lewa {

    private ArrayList<Card> cards = null;
    private int round;
    private int turn;
    public Lewa(ArrayList<Card> c,int rountNr,int turnNr){
        cards=c;
        round=rountNr;
        turn=turnNr;
    }
    public float calculatePoints(String rules,float points){
        String color = String.valueOf(rules.charAt(0));
        String values=rules.substring(1);
        float total=0f;
        for (Card c: cards){
            if((Objects.equals(c.getColor(), color) || color.equals("*"))&&(Objects.equals(c.getValue(), values) || values.equals("*")))total+=points;
        }

        return total;
    }
    public float calculatePointsHardCoded(String rules,float points){
        if(round==1) return calculatePoints("**",-5);
        if(round==2) return calculatePoints("H*",-20);
        if(round==3) return calculatePoints("*Q",-60);
        if(round==4) return calculatePoints("*K",-30)+calculatePoints("*J",-30);
        if(round==5) return calculatePoints("HK",-150);
        if(round==6&&turn==7) return -75;
        if(round==6&&turn==13) return -75;

        if(round==7){
            float total=0;
            total += calculatePoints("**",-5);
            total += calculatePoints("H*",-20);
            total += calculatePoints("*Q",-60);
            total += calculatePoints("*K",-30)+calculatePoints("*J",-30);
            total += calculatePoints("HK",-150);
            if(round==6&&turn==7) total+=-75;
            if(round==6&&turn==13) total+=-75;
            return total;

        }
        return 0;


    }
    public void show(){
        for(Card c:cards){
            System.out.print(c.showCard()+" ");

        }
        System.out.println();
    }
}
