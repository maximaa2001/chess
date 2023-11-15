module com.maks.chess {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;


    opens com.maks.chess to javafx.fxml;
    exports com.maks.chess;
}