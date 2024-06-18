module org.example.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;


    opens org.example.example to javafx.fxml;
    exports org.example.example;
}