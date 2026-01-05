module com.mycompany.xmljson_converter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.xml; //
    // ADD THESE TWO LINES
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;

    // This allows Jackson to "see" your classes to convert them
    opens com.mycompany.xmljson_converter to javafx.fxml;
    exports com.mycompany.xmljson_converter;
}
