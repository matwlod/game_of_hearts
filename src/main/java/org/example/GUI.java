package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Objects;

public class GUI {

    private static JFrame frame;

    private JPanel cardsPanel;


    private PrintWriter out = null;

    private static void showText(Container container, int x, int y, String text,int size) {
        JLabel textLabel = new JLabel(text);
        Font labelFont = textLabel.getFont();
        textLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, size));
        textLabel.setBounds(x, y, 400, 30); // Set width and height as needed
        textLabel.setVisible(true);

        container.add(textLabel);
    }
    private static void removeTextAtPosition(int x, int y) {
        Component[] components = frame.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                Rectangle bounds = component.getBounds();
                if (bounds.x == x && bounds.y == y) {
                    frame.remove(component);
                    break;
                }
            }
        }
    }
    public GUI(PrintWriter o) {
        this.out = o;


        frame = new JFrame("Card Game GUI");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        cardsPanel = new JPanel();




        int off=30;
        int t=-35;
        showText(frame,300,500,"YOUR CARDS",20);
        frame.setLayout(new BorderLayout());

        frame.add(cardsPanel, BorderLayout.SOUTH);


        frame.setVisible(true);
    }

    public void updateGUI(String jsonString) {
        try {

            JSONObject json = new JSONObject(jsonString);
            JSONArray cardsArray = json.getJSONArray("cards");
            JSONArray tableArray = json.getJSONArray("table");
            JSONArray pointsArray = json.getJSONArray("points");
            JSONArray takenArray = json.getJSONArray("takenAmount");
            JSONArray nickArray = json.getJSONArray("nicknames");
            int off=50;
            cardsPanel.removeAll();

            for (int i = 0; i < pointsArray.length(); i++){
               removeTextAtPosition(500, i * 20);
            }
            for (int i = 0; i < tableArray.length(); i++) {
                removeTextAtPosition(200+off*2*i, 200);
            }
            removeTextAtPosition(0, 0);
            removeTextAtPosition(0, 0);


            for (int i = 0; i < pointsArray.length(); i++){
                showText(frame,500, i * 20,nickArray.get(i)+":"+pointsArray.get(i)+" taken: "+takenArray.get(i),20);
            }
            for (int i = 0; i < tableArray.length(); i++) {

                if (Objects.equals(tableArray.get(i).toString(), "null")){
                    showText(frame,200+off*2*i,200," ",30);
                }
                else {
                showText(frame,200+off*2*i,200, getSymbolForCard((String) tableArray.get(i)),40);
                }
            }

            if(Objects.equals(json.get("yourID"), json.get("playerTurn"))){
                showText(frame,0,0,"Your turn",30);
            }
            else {
                showText(frame,0,0," ",20);
            }


            for (int i = 0; i < cardsArray.length(); i++) {
                String card = cardsArray.getString(i);
                JButton cardButton = new JButton(getSymbolForCard(card));
                cardButton.addActionListener(new CardButtonListener(card));
                cardsPanel.add(cardButton);
            }

            frame.revalidate();
            frame.repaint();
        } catch (JSONException e) {
            e.printStackTrace();
        }}





    private class CardButtonListener implements ActionListener {
        private String card;

        public CardButtonListener(String card) {
            this.card = card;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Client.playCard(out, "play " + card);

        }
    }

    private String getSymbolForCard(String card) {
        return card.replace("H","♥").replace("S","♠").replace("C","♣").replace("D","♦");

    }
    public void closeWindow() {


        if (out != null) {
            out.close();
        }

        frame.dispose();
    }
}
