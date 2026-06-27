module com.barbershop.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens com.barbershop.app to javafx.fxml;   
    opens com.barbershop.app.controller to javafx.fxml;
    opens com.barbershop.app.model to javafx.fxml;

    exports com.barbershop.app;    
    exports com.barbershop.app.controller;  
    exports com.barbershop.app.model;
}
