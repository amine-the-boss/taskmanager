module com.taskmanager.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jbcrypt;
    // Remove the jBCrypt requirement from here
    requires annotations;

    opens com.taskmanager to javafx.fxml;
    opens com.taskmanager.controllers to javafx.fxml;
    opens com.taskmanager.models to javafx.base;

    exports com.taskmanager;
    exports com.taskmanager.controllers;
    exports com.taskmanager.models;
    exports com.taskmanager.dao;
}