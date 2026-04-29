package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<FacilityEntity, UUID> {
    boolean existsByUser(UserEntity app);
    List<FacilityEntity> findByUser(UserEntity user);

    /**
     * Find all facilities sorted by distance from a given location (using PostGIS ST_Distance)
     * Distance is calculated in meters and converted to kilometers
     *
     * @param longitude User's current longitude
     * @param latitude User's current latitude
     * @param maxDistanceMeters Maximum distance in meters to search (e.g., 50000 for 50km)
     * @return List of facilities sorted by distance (nearest first)
     */
    @Query(value = "SELECT f.* FROM turfclan_schema.t_facility f " +
            "WHERE ST_Distance(f.location, ST_Point(:longitude, :latitude, 4326)) <= :maxDistanceMeters " +
            "ORDER BY ST_Distance(f.location, ST_Point(:longitude, :latitude, 4326)) ASC",
            nativeQuery = true)
    List<FacilityEntity> findFacilitiesByDistance(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            @Param("maxDistanceMeters") Double maxDistanceMeters);

    /**
     * Find all facilities with distance information (for dynamic sorting)
     * Returns all facilities with their calculated distances
     *
     * @param longitude User's current longitude
     * @param latitude User's current latitude
     * @return List of facilities sorted by distance (nearest first)
     */
    @Query(value = "SELECT f.*, ST_Distance(f.location, ST_Point(:longitude, :latitude, 4326)) as distance_meters " +
            "FROM turfclan_schema.t_facility f " +
            "ORDER BY ST_Distance(f.location, ST_Point(:longitude, :latitude, 4326)) ASC",
            nativeQuery = true)
    List<FacilityEntity> findAllFacilitiesWithDistance(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude);
}
