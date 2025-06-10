
package com.wastewise.pickup.repository;

import com.wastewise.pickup.model.PickUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CRUD operations on PickUp.
 */
@Repository
public interface PickUpRepository extends JpaRepository<PickUp, String> {

//    /**
//     * Find all pickups by status.
//     * @param status Pickup status filter.
//     * @return list of matching PickUps.
//     */
//    List<PickUp> findByStatus(com.wastewise.pickup.model.enums.PickUpStatus status);
    Optional<PickUp> findFirstByOrderByIdDesc();

}
