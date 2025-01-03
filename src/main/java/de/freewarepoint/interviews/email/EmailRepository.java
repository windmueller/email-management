package de.freewarepoint.interviews.email;

import de.freewarepoint.interviews.email.entities.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "emails", path = "emails")
public interface EmailRepository extends CrudRepository<Email, Long> {

    List<Email> findByFrom(@Param("from") String fromHeader);

    List<Email> findByIdIn(@Param("ids") Long[] ids);

}
