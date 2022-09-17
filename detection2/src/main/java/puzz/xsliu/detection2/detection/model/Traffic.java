package puzz.xsliu.detection2.detection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "traffic")
public class Traffic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "root_no")
    private String rootNo;
    private String year;
    private Long xxhc;
    private Long zxhc;
    private Long dxhc;
    private Long tdhc;
    private Long jzxc;
    private Long zxkc;
    private Long dkc;

}
