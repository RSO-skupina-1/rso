package si.fri.rso.katalogdestinacij.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.metrics.annotation.Timed;

import si.fri.rso.katalogdestinacij.lib.KatalogDestinacij;
import si.fri.rso.katalogdestinacij.models.converters.KatalogDestinacijConverter;
import si.fri.rso.katalogdestinacij.models.entities.KatalogDestinacijEntity;


@RequestScoped
public class KatalogDestinacijBean {

    private Logger log = Logger.getLogger(KatalogDestinacijBean.class.getName());

    @Inject
    private EntityManager em;

    public List<KatalogDestinacij> getKatalogDestinacij() {

        TypedQuery<KatalogDestinacijEntity> query = em.createNamedQuery(
                "KatalogDestinacijEntity.getAll", KatalogDestinacijEntity.class);

        List<KatalogDestinacijEntity> resultList = query.getResultList();

        return resultList.stream().map(KatalogDestinacijConverter::toDto).collect(Collectors.toList());

    }

    public List<KatalogDestinacij> getKatalogDestinacijFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, KatalogDestinacijEntity.class, queryParameters).stream()
                .map(KatalogDestinacijConverter::toDto).collect(Collectors.toList());
    }

    public KatalogDestinacij getKatalogDestinacij(Integer id) {

        KatalogDestinacijEntity katalogDestinacijEntity = em.find(KatalogDestinacijEntity.class, id);

        if (katalogDestinacijEntity == null) {
            throw new NotFoundException();
        }

        KatalogDestinacij katalogDestinacij = KatalogDestinacijConverter.toDto(katalogDestinacijEntity);

        return katalogDestinacij;
    }

    public KatalogDestinacij createKatalogDestinacij(KatalogDestinacij katalogDestinacij) {

        KatalogDestinacijEntity katalogDestinacijEntity = KatalogDestinacijConverter.toEntity(katalogDestinacij);

        try {
            beginTx();
            em.persist(katalogDestinacijEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (katalogDestinacijEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return KatalogDestinacijConverter.toDto(katalogDestinacijEntity);
    }

    public KatalogDestinacij putImageMetadata(Integer id, KatalogDestinacij katalogDestinacij) {

        KatalogDestinacijEntity c = em.find(KatalogDestinacijEntity.class, id);

        if (c == null) {
            return null;
        }

        KatalogDestinacijEntity updatedKatalogDestinacijEntity = KatalogDestinacijConverter.toEntity(katalogDestinacij);

        try {
            beginTx();
            updatedKatalogDestinacijEntity.setId(c.getId());
            updatedKatalogDestinacijEntity = em.merge(updatedKatalogDestinacijEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return KatalogDestinacijConverter.toDto(updatedKatalogDestinacijEntity);
    }

    public boolean deleteImageMetadata(Integer id) {

        KatalogDestinacijEntity imageMetadata = em.find(KatalogDestinacijEntity.class, id);

        if (imageMetadata != null) {
            try {
                beginTx();
                em.remove(imageMetadata);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
