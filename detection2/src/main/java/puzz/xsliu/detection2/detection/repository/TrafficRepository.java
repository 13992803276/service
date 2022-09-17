package puzz.xsliu.detection2.detection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puzz.xsliu.detection2.detection.model.Traffic;

import java.util.List;

/**
 * @author gaoll
 */
public interface TrafficRepository extends JpaRepository<Traffic, Integer> {

    //List<Traffic> findallByYear(String year);

    //List<Traffic> findAllByRootNo(String rootNo);
}
