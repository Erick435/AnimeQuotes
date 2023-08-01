package com.erick.animequoteapp;



public class Quote {

    private String title;
    private String character;
    private String quote;
    private boolean isFavorite;

    // constructor for quote strings from SharedPreferences
    public Quote(String quoteString) {
        String[] parts = quoteString.split("\n");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Quote string has wrong format: " + quoteString);
        }
        this.title = parts[0].split(": ")[1];
        this.character = parts[1].split(": ")[1];
        this.quote = parts[2].split(": ")[1];
        this.isFavorite = false;
    }

    // constructor for new quotes from the API
    public Quote(String title, String character, String quote) {
        this.title = title;
        this.character = character;
        this.quote = quote;
        this.isFavorite = false;  // Quotes are not favorites by default
    }

    // getters and setters...

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public boolean isFavorite() {
        return this.isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n" +
                "Character: " + character + "\n" +
                "Quote: " + quote + "\n" +
                "IsFavorite: " + isFavorite;
    }
}
