package com.mycompany.xmljson_converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class Transformer {

    private static String json;
    private static int index = 0;
    private static int depth = 0;

    /**
     * PATH 1: XML to JSON
     * 
     *
     *
     */
    public static String convertWithJackson(String SXml) {
        try { 
            JacksonXmlModule module = new JacksonXmlModule();
            
            module.setXMLTextElementName("value");
            
            XmlMapper xmlMapper = new XmlMapper(module);
            Object data = xmlMapper.readValue(SXml, Object.class);
            
            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            return jsonMapper.writeValueAsString(data);
        } catch (Exception e) {
            return "Error: " + e.getMessage(); 
        }
    }  

    /**
     * PATH 2: JSON to XML
     *
     *
     */
    public static String convert(String jsonInput, String rootName) throws Exception {
        json = jsonInput.trim();
        index = 0;
        depth = 0;

        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);

        writer.writeStartDocument();
        writer.writeStartElement(rootName);
        depth++;

        skipWhitespace();
        if (index < json.length() && json.charAt(index) == '{') {
            index++; 
            parseObjectContent(writer);
        }

        depth--;
        writeNewLine(writer);
        writer.writeEndElement();
        writer.writeEndDocument();
        return sw.toString();
    }

    private static void parseJson(XMLStreamWriter writer, String currentKey) throws Exception {
        skipWhitespace();
        if (index >= json.length()) return;
        char c = json.charAt(index);

        if (c == '{') {
            index++; 
            parseObjectContent(writer);
        } else if (c == '[') {
            index++; 
            while (index < json.length() && json.charAt(index) != ']') {
                skipWhitespace();
                writeNewLine(writer);
                writer.writeStartElement(currentKey); 
                depth++;
                parseJson(writer, currentKey);
                depth--;
                if (json.charAt(index - 1) == '}') writeNewLine(writer);
                writer.writeEndElement();
                
                skipWhitespace();
                if (index < json.length() && json.charAt(index) == ',') index++;
                skipWhitespace();
            }
            index++; 
        } else if (c == '"') {
            writer.writeCharacters(parseString());
        } else {
            writer.writeCharacters(parseLiteral());
        }
    }

    private static void parseObjectContent(XMLStreamWriter writer) throws Exception {
        while (index < json.length() && json.charAt(index) != '}') {
            skipWhitespace();
            String key = parseString();
            
            if (key == null || key.trim().isEmpty()) key = "item";
            
            skipWhitespace();
            index++;
            skipWhitespace();

           
            if (json.charAt(index) == '[') {
                parseJson(writer, key); 
            } else {
                writeNewLine(writer);
                writer.writeStartElement(key);
                depth++;
                parseJson(writer, key);
                depth--;
                if (index > 0 && (json.charAt(index - 1) == '}' || json.charAt(index - 1) == ']')) {
                    writeNewLine(writer);
                }
                writer.writeEndElement();
            }
            
            skipWhitespace();
            if (index < json.length() && json.charAt(index) == ',') index++;
            skipWhitespace();
        }
        index++; 
    }

    // Methods For Help
    public static void writeNewLine(XMLStreamWriter writer) throws Exception {
        writer.writeCharacters("\n" + "  ".repeat(depth));
    }

    private static String parseString() {
        StringBuilder sb = new StringBuilder();
        if (index < json.length() && json.charAt(index) == '"') index++;
        while (index < json.length() && json.charAt(index) != '"') {
            sb.append(json.charAt(index++));
        }
        index++;
        return sb.toString();
    }

    private static String parseLiteral() {
        StringBuilder sb = new StringBuilder();
        while (index < json.length() && ",}] ".indexOf(json.charAt(index)) == -1) {
            sb.append(json.charAt(index++));
        }
        return sb.toString();
    }

    private static void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }
}
