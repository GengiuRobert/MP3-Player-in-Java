module com.example.hope_it_works_now {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.hope_it_works_now to javafx.fxml;
    exports com.example.hope_it_works_now;
}