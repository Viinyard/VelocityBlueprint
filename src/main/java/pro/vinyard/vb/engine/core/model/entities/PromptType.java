package pro.vinyard.vb.engine.core.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PromptType implements Comparable<PromptType> {

    @XmlAttribute
    private String value;

    @XmlAttribute
    private Integer order = 0;

    @Override
    public int compareTo(PromptType o) {
        return this.order.compareTo(o.order);
    }
}
