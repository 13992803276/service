package puzz.xsliu.detection2.detection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lexu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficExportData {
    private String fileName;
    private String[] head;
    private List<String[]> data;
}
