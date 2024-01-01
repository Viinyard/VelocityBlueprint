package pro.vinyard.vb.engine.core.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Prompts {

    @XmlElement(name = "monoSelect")
    private List<MonoSelect> monoSelectList;

    @XmlElement(name = "multiSelect")
    private List<MultiSelect> multiSelectList;

    @XmlElement(name = "stringInput")
    private List<StringInput> stringInputList;

}