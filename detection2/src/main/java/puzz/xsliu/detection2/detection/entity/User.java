package puzz.xsliu.detection2.detection.entity;

import lombok.Data;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/10:05 AM
 * @author: lxs
 */
@Data
public class User {
    private Long id;
    private String nick;
    private String email;
    private String password;
    private String salt;
    private String header;


}
