package puzz.xsliu.detection2.detection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportTrafficGroup {
    @Column(name = "root_no")
    private String rootNo;
    private String year;
    private String type;
    private String xxhc;
    private String zxhc;
    private String dxhc;
    private String tdhc;
    private String jzxc;
    private String zxkc;
    private String dkc;
    private String total;
}