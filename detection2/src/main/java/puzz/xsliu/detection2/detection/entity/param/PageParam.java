package puzz.xsliu.detection2.detection.entity.param;

import lombok.Data;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/6:34 PM
 * @author: lxs
 */
@Data
public class PageParam {
    private static int DEFAULT_PAGE_SIZE = 12;
    private Integer pageIndex;
    private Integer pageSize;

    public void buildPage(int pageIndex, int pageSize){
        if (pageSize <= 0){
            this.pageSize = DEFAULT_PAGE_SIZE;
        }else{
            this.pageSize = pageSize;
        }
        if (pageIndex <= 0){
            pageIndex = 1;
        }

        this.pageIndex = (pageIndex - 1) * this.pageSize;
    }
}
