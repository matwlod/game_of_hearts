package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public class Game {

    private final int numberOfplayers=4;
    private final int randomNumberOfPointsMIN=10;
    private final int randomNumberOfPointsMAX=100;

    private final int numberOfCardsInHand=13;
    private Deck deck;
    private ArrayList<ArrayList<Card>> hands = new ArrayList<>();

    private ArrayList<Card> table = new ArrayList<>();

    private ArrayList<String> nicknames = new ArrayList<>();

    private ArrayList<Float> points = new ArrayList<>();
    private int numberOfRounds = 7;
    private ArrayList<Float> rulesPoints = new ArrayList<>();
    private ArrayList<String> rules = new ArrayList<>();
    private ArrayList<ArrayList<Lewa>> taken = new ArrayList<>();
    private int roundNumber=1;
    private int playerTurn=0;

    private int cardsPlaced=0;

    private int turnCount=1;


    private List<ClientHandler> players=null;
    private char turnColor;

    public Game(List<ClientHandler> p){
        players=p;
        deck = new Deck();
        deck.shuffleDeck();
        loadRules("rules.xml");

        for (int i=0;i<numberOfplayers;i++){
            hands.add(new ArrayList<Card>());
            taken.add(new ArrayList<Lewa>());
            table.add(null);
            points.add(0.f);
        }
        for (ClientHandler c:players){
            nicknames.add(c.username);
        }
        giveCards();
        updatePlayers();
    }

    public void updatePlayers(){
        for (ClientHandler p:players){
            p.sendMessage(createUpdateJSON(p));
        }
    }

    public boolean playCard(String card,ClientHandler player){
        int id=players.indexOf(player);
        if(id!=playerTurn) return false;
        if(cardsPlaced==0){
            if(roundNumber==2||roundNumber==5||roundNumber==7)
            {
                if ((!noColor(id,'D')||!noColor(id,'S')||!noColor(id,'C'))&&card.charAt(0)=='H'){
                    return false;
                }
            }
            turnColor=card.charAt(0);
        }
        Card toRemove=null;
        boolean valid=false;
        for (Card c:hands.get(id)){
            if (Objects.equals(c.showCard(), card)){
                if (turnColor==card.charAt(0)||noColor(id,turnColor)){
                toRemove = c;
                valid=true;}
            }
        }
        if (valid){
            table.set(id, toRemove);
            hands.get(id).remove(toRemove);
        }
        if (!valid) return false;
        playerTurn++;
        cardsPlaced++;
        if(playerTurn==numberOfplayers)playerTurn=0;
        if(cardsPlaced==numberOfplayers){
            endTurn();
        }else {
            updatePlayers();
        }
        return true;
    }
    private boolean noColor(int playerID,char color){
        for (Card c:hands.get(playerID)){
          if(c.getColor().charAt(0)==color) return false;
        }
        return true;
    }

    private void endTurn(){
        turnCount++;
        int winner=getWinner();
        taken.get(winner).add(new Lewa(new ArrayList<>(table),roundNumber,turnCount-1));
        for (int i=0;i<numberOfplayers;i++)
        {
        table.set(i, null);
        }
        playerTurn=winner;
        cardsPlaced=0;
        if (turnCount>numberOfCardsInHand){
            turnCount=1;
            deck.fillDeck();
            deck.shuffleDeck();
            giveCards();
            for (int i=0;i<numberOfplayers;i++){
                for (Lewa l:taken.get(i)){
                    points.set(i, points.get(i)+l.calculatePointsHardCoded(rules.get(roundNumber-1), rulesPoints.get(roundNumber-1)));
                }}
            roundNumber++;
            if(roundNumber>numberOfRounds){
                endGame(0);
                return;
            }
            taken = new ArrayList<>();
            for (int i=0;i<numberOfplayers;i++){
            taken.add(new ArrayList<Lewa>());}

            playerTurn=(roundNumber-1)%numberOfplayers;
        }
        updatePlayers();
    }
    public void endGame(int code){
        /**
         * ends game
         * @param code 0 if game ended naturally,1 if game was aborted
         */
        if (code==1){
            for (ClientHandler c:players){
                c.sendMessage("GAME ENDED\n"+ "PLAYER LEFT THE GAME - GAME ABORTED");
            }

        }

        StringBuilder leaderboards= new StringBuilder();
        for (int i=0;i<numberOfplayers;i++){
            leaderboards.append(nicknames.get(i)).append("- Points =").append(points.get(i).toString()).append("\n");
        }
        for (ClientHandler c:players){
            c.sendMessage("GAME ENDED\n"+ leaderboards);
        }
        for (String n:Server.lobbies.keySet()){
            if(Server.lobbies.get(n).getGame()==this){
                Server.lobbies.remove(n);
            }
        }


    }
    public void skipRound(){
        turnCount=1;
        roundNumber++;
        for (int i=0;i<numberOfplayers;i++){
        table.set(i, null);}
        cardsPlaced=0;
        for (ArrayList<Card> h:hands){
            h.clear();
        }
        for (int i=0;i<numberOfplayers;i++){
            int random = RandomGenerator.getDefault().nextInt(randomNumberOfPointsMIN,randomNumberOfPointsMAX);
            points.set(i,points.get(i)+random);
        }
        if(roundNumber>numberOfRounds){
            endGame(0);
            return;
        }
        taken.clear(); //= new ArrayList<>();
        for (int i=0;i<numberOfplayers;i++){
        taken.add(new ArrayList<Lewa>());
        }
        deck.emptyDeck();
        deck.fillDeck();
        deck.shuffleDeck();
        giveCards();
        playerTurn=(roundNumber-1)%numberOfplayers;
        updatePlayers();
    }

    private int getWinner(){
        int winner=playerTurn;
        int max=0;
        for (int i=0;i<numberOfplayers;i++){
            if (table.get(i).getColor().charAt(0)==turnColor)
                if (table.get(i).getIntValue()>max){
                    winner=i;
                    max=table.get(i).getIntValue();
                }
        }
        return winner;
    }
    private String createUpdateJSON(ClientHandler player){
        int id=players.indexOf(player);
        JSONObject out = new JSONObject();
        out.put("type","update");
        out.put("playerTurn",this.playerTurn);
        out.put("yourID",id);
        out.put("cards",getCards(id));
        out.put("table",getTable());
        out.put("takenAmount",getAmountTaken());
        out.put("points",points);
        out.put("nicknames", new JSONArray(nicknames));
        return out.toString();
    }
    private ArrayList<String> getCards(int playerID){
        ArrayList<String> out = new ArrayList<>();
        for (Card c: hands.get(playerID)){
            out.add(c.showCard());
        }
        return  out;
    }
    private ArrayList<String> getTable(){
        ArrayList<String> out = new ArrayList<>();
        for (Card c: table){
            if (c!=null)
                out.add(c.showCard());
            else
                out.add("null");
        }
        return  out;
    }

    public void giveCards(){
        int i=0;
        while (!deck.isEmpty()){
            hands.get(i).add(deck.getCard());
            i++;
            if (i==numberOfplayers) i=0;
        }
    }

    private ArrayList<Integer> getAmountTaken(){
        ArrayList<Integer> out = new ArrayList<>();
        for(ArrayList<Lewa> a :taken){
            out.add(a.size());
        }
        return out;
    }
    public void loadRules(String filePath) {
        try {
            File inputFile = new File(filePath);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            numberOfRounds = Integer.parseInt(doc.getElementsByTagName("numberOfRounds").item(0).getTextContent());

            NodeList roundList = doc.getElementsByTagName("round");
            for (int i = 0; i < roundList.getLength(); i++) {
                Element roundElement = (Element) roundList.item(i);

                float points = Float.parseFloat(roundElement.getElementsByTagName("points").item(0).getTextContent());
                String rule = roundElement.getElementsByTagName("rules").item(0).getTextContent();

                rulesPoints.add(points);
                rules.add(rule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//   public void debug(){
//        int i=0;
//        System.out.println(numberOfRounds);
//        System.out.println(roundNumber);
//        System.out.println(turnCount);
//        for(ArrayList<Lewa> a:taken){
//            System.out.print(i);
//            for (Lewa l:a){
//                l.show();
//            }
//            System.out.println("\n");
//            i++;
//        }
//    }