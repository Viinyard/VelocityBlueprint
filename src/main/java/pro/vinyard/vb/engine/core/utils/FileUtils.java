package pro.vinyard.vb.engine.core.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;
import pro.vinyard.vb.engine.core.exception.VelocityBlueprintException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class FileUtils {

    /**
     * Create a file in the specified directory
     *
     * @param file        Internal file path to be copied
     * @param destination The destination file
     * @return The result message
     */
    public static File copyFile(String file, File destination) {
        try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (inputStream == null)
                throw new RuntimeException("Cannot find file " + file);
            Files.copy(inputStream, destination.toPath());

            return destination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> listFiles(File directory, String... extensions) {
        return org.apache.commons.io.FileUtils.listFiles(directory, extensions, true).stream().toList();
    }

    public static <T> T loadConfigurationFile(File xmlFile, Class<T> environmentClass, String schemaLocation) throws VelocityBlueprintException {
        try {
            JAXBContext context = JAXBContext.newInstance(environmentClass);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "true");

            Source schemaFile = new StreamSource(FileUtils.class.getClassLoader().getResourceAsStream(schemaLocation));
            Schema schema = schemaFactory.newSchema(schemaFile);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);

            return Optional.of(unmarshaller.unmarshal(xmlFile)).filter(environmentClass::isInstance).map(environmentClass::cast).orElseThrow(() -> new VelocityBlueprintException("Invalid configuration file"));
        } catch (JAXBException | SAXException e) {
            throw new VelocityBlueprintException(Optional.ofNullable(e.getCause()).map(Throwable::getMessage).or(() -> Optional.of(e).map(Exception::getMessage)).orElse("unknown"), e);
        }
    }

    public static <T> T loadConfigurationFile(String xml, Class<T> environmentClass, String schemaLocation) throws VelocityBlueprintException {
        try {
            JAXBContext context = JAXBContext.newInstance(environmentClass);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "true");

            Source schemaFile = new StreamSource(FileUtils.class.getClassLoader().getResourceAsStream(schemaLocation));
            Schema schema = schemaFactory.newSchema(schemaFile);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);

            return Optional.of(unmarshaller.unmarshal(new StringReader(xml))).filter(environmentClass::isInstance).map(environmentClass::cast).orElseThrow(() -> new VelocityBlueprintException("Invalid configuration file"));
        } catch (JAXBException | SAXException e) {
            throw new VelocityBlueprintException(Optional.ofNullable(e.getCause()).map(Throwable::getMessage).or(() -> Optional.of(e).map(Exception::getMessage)).orElse("unknown"), e);
        }
    }
}
