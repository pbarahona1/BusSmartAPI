package BuSmart.APIBuSmart.Repositories;

import BuSmart.APIBuSmart.Entities.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<EstadoEntity, Long>{
}
