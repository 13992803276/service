package puzz.xsliu.detection2.detection.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/6:19 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageResult<E> extends Result<List<E>>{
    private static final long serialVersionUID = 7103577191111948681L;
    private boolean next;

    private long pageIndex;

    private int pageSize;

    private long count;



    public static <E> PageResult<E> ofSuccess(List<E> data, int totalCount, long pageIndex, int pageSize){
        PageResult<E> result = new PageResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);
        result.setCount(totalCount);
        long count = pageIndex * pageSize;
        result.setNext(count < totalCount);
        return  result;
    }

}
