package com.taskmanager.models;

/**
 * Classe représentant une catégorie de tâches.
 * Une catégorie permet de regrouper les tâches selon leur type ou leur contexte
 * et inclut des propriétés visuelles comme une couleur pour l'affichage.
 */
public class Category {
    // Attributs de la classe
    private int id;                 // Identifiant unique de la catégorie
    private String name;            // Nom de la catégorie (ex: "Travail", "Personnel")
    private String color;// Code couleur au format hexadécimal (ex: "#FF0000")
    private User user;

    // Constructeur par défaut
    public Category() {
    }

    // Constructeur avec paramètres
    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Constructeur complet
    public Category(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Méthode toString pour l'affichage dans les ComboBox
    @Override
    public String toString() {
        return this.name;
    }

    // Méthodes equals et hashCode pour la comparaison des catégories
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return 31 * id;
    }

}