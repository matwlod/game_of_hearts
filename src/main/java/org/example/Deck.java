package org.example;

import java.util.ArrayList;
import java.util.*;
public class Deck {
    private ArrayList<Card> cards=new ArrayList<>();
    public Deck(){
        fillDeck();
    }
    void fillDeck(){
        for (int i=2;i<11;i++){
            cards.add(new Card("C",Integer.toString(i)));
            cards.add(new Card("D",Integer.toString(i)));
            cards.add(new Card("H",Integer.toString(i)));
            cards.add(new Card("S",Integer.toString(i)));
        }
        cards.add(new Card("C","J"));
        cards.add(new Card("D","J"));
        cards.add(new Card("H","J"));
        cards.add(new Card("S","J"));

        cards.add(new Card("C","Q"));
        cards.add(new Card("D","Q"));
        cards.add(new Card("H","Q"));
        cards.add(new Card("S","Q"));

        cards.add(new Card("C","K"));
        cards.add(new Card("D","K"));
        cards.add(new Card("H","K"));
        cards.add(new Card("S","K"));

        cards.add(new Card("C","A"));
        cards.add(new Card("D","A"));
        cards.add(new Card("H","A"));
        cards.add(new Card("S","A"));
    }
    void showDeck(){
        for(Card card : cards){
            card.showCard();
            System.out.print(",");
        }
        System.out.print("\n");
    }
    void shuffleDeck(){
        Collections.shuffle(cards);
    }
    public boolean isEmpty(){
        return cards.isEmpty();
    }
    int getDecklen(){
        return cards.size();
    }
    public Card getCard(){
        return cards.remove(cards.size() - 1);
    }

    public void emptyDeck(){
        cards.clear();
    }

}
