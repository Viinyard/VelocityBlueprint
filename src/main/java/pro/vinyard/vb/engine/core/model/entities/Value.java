package pro.vinyard.vb.engine.core.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Value {

    @XmlAttribute
    private String key;

    @XmlAttribute
    private boolean selected = false;

    @XmlAttribute
    private boolean enabled = true;

    @XmlValue
    private String content;

}