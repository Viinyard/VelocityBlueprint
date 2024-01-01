package pro.vinyard.vb.engine.core.model.entities;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Model {

    @XmlAttribute
    private String name;

    @XmlElement
    private Properties properties;

    @XmlElement
    private Directives directives;

    @XmlElement
    private Prompts prompts;

}