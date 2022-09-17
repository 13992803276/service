package puzz.xsliu.detection2.detection.process.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import puzz.xsliu.detection2.detection.entity.Damage;

import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:01 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuantifyResultMessage extends Message{
    private static final long serialVersionUID = -154284953097309329L;

    private Long id;

    private String path;

    private List<Damage> damages;

    private String damageInfo;
}
