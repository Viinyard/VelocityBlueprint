package pro.vinyard.vb.engine.core.environment.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "environment")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Environment {

    @XmlElement
    private Properties properties;
}
