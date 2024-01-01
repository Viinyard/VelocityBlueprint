package pro.vinyard.vb.engine.core.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class StringInput extends PromptType {

    @XmlElement(name = "defaultValue")
    private DefaultValue defaultValue;

    @XmlAttribute
    private boolean masked = false;

}