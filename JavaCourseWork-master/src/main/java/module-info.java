module com.example.coursework {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires java.desktop;
    requires java.sql;

    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires mysql.connector.j;

    requires javafx.graphics;
    requires jbcrypt;


    opens com.example.coursework to javafx.fxml;
    exports com.example.coursework;
    exports com.example.coursework.consoleCourseWork;
    opens com.example.coursework.consoleCourseWork to javafx.fxml;
    opens com.example.coursework.fxControllers to javafx.fxml;
    exports com.example.coursework.fxControllers;
    opens com.example.coursework.model to org.hibernate.orm.core;
    exports com.example.coursework.model;
    exports com.example.coursework.fxTablesParameters;
    opens com.example.coursework.fxTablesParameters to javafx.fxml;

}