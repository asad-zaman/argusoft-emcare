package com.argusoft.who.emcare.web.location.dao;

import com.argusoft.who.emcare.web.location.model.LocationMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author jay
 */
public interface LocationMasterDao extends JpaRepository<LocationMaster, Integer> {

    @Query(value = "select * from location_master where parent = :id", nativeQuery = true)
    public List<LocationMaster> getChildLocation(@Param("id") Integer id);

    List<LocationMaster> findByType(String locationType);

    public Page<LocationMaster> findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(String searchString1, String searchString2, Pageable pageable);

    public List<LocationMaster> findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(String searchString1, String searchString2);

    List<LocationMaster> findByParent(Long locationId);

    @Query(value = "WITH RECURSIVE child AS \n" +
            "(SELECT * FROM location_master WHERE id = :id \n" +
            " UNION SELECT l.* FROM location_master l \n" +
            " INNER JOIN child s ON s.id = l.parent)\n" +
            " SELECT child.id FROM child;", nativeQuery = true)
    public List<Integer> getAllChildLocationId(@Param("id") Integer id);

    @Query(value = "WITH RECURSIVE child AS \n" +
            "(SELECT * FROM location_master WHERE id in :id \n" +
            " UNION SELECT l.* FROM location_master l \n" +
            " INNER JOIN child s ON s.id = l.parent)\n" +
            " SELECT child.id FROM child;", nativeQuery = true)
    public List<Integer> getAllChildLocationIdWithMultipalLocationId(@Param("id") List<Integer> id);

    @Query(value = "WITH RECURSIVE child AS (\n" +
            "SELECT * FROM location_master WHERE id = :id\n" +
            " UNION SELECT l.* FROM location_master l \n" +
            " INNER JOIN child s ON s.parent = l.id)\n" +
            " SELECT child.* FROM child;", nativeQuery = true)
    public List<LocationMaster> getAllParent(@Param("id") Integer id);

    @Query(value = "with alldata as (WITH RECURSIVE child AS (\n" +
            "            SELECT * FROM location_master WHERE id = :id\n" +
            "            UNION SELECT l.* FROM location_master l\n" +
            "            INNER JOIN child s ON s.parent = l.id)\n" +
            "            SELECT child.* FROM child order by child.id ASC)\n" +
            "select  STRING_AGG (alldata.name,'->')\n" +
            "         From alldata", nativeQuery = true)
    public String getNameHierarchy(@Param("id") Integer id);

    @Query(value = "select * from location_master where id in :ids offset :offset limit :limit ;", nativeQuery = true)
    public List<LocationMaster> getLocationByLocationIds(@Param("ids") List<Integer> ids, @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "select count(*) from location_master where id in :ids ;", nativeQuery = true)
    public Long getLocationByLocationIdsCount(@Param("ids") List<Integer> ids);


}
