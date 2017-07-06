package com.GDBS;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;


public class Main {

    static String URL = "http://www.gwentdb.com/cards?filter-display=1"; //Use the table view if you change this.
    static String urlNoPage = "http://www.gwentdb.com/cards?filter-display=1&page=";
    public static void main(String[] args) {
        try {
            getCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getCards() throws IOException {
        Document doc = Jsoup.connect(URL).get();
        int totalPagesToGet = Integer.parseInt(doc.select("li.b-pagination-item").last().previousElementSibling().text());
        String nextUrl;
        JSONArray cards = new JSONArray();
        for (int i = 1; i<=totalPagesToGet; i++)
        {
            Elements cardRows = doc.select("table.listing-cards-table").select("tbody").select("tr.card-row"); //gets the rows of the cards in the table
            Iterator iterator = cardRows.iterator();
            while(iterator.hasNext()) {
                JSONObject card = new JSONObject();
                Element cardRow = (Element)iterator.next();
                card.put("imageurl", cardRow.select("tr").attr("data-card-image-url"));
                card.put("Name", cardRow.select("td.col-name").text());
                card.put("Faction", cardRow.select("td.col-faction").text());
                card.put("Power", cardRow.select("td.col-power").text());
                card.put("Abilities", Jsoup.parse(cardRow.select("td.col-abilities").select("span").attr("title")).text());
                card.put("Row", cardRow.select("td.col-row").text());
                card.put("Type", cardRow.select("td.col-type").text());
                card.put("Loyalty", cardRow.select("td.col-loyalty").attr("title"));
                cards.add(card);
            }
            nextUrl = urlNoPage + (i+1);
            doc = Jsoup.connect(nextUrl).get();
        }
        saveCardsToJSON(cards);
    }

    public static void saveCardsToJSON(JSONArray cards) {
        try {
            FileWriter file = new FileWriter("cards.json");
            file.write(cards.toJSONString());
            file.close();
            System.out.println("Created JSON file \"cards.json\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
