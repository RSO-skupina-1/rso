package si.fri.rso.katalogdestinacij.models.converters;

import si.fri.rso.katalogdestinacij.lib.KatalogDestinacij;
import si.fri.rso.katalogdestinacij.models.entities.KatalogDestinacijEntity;

public class KatalogDestinacijConverter {

    public static KatalogDestinacij toDto(KatalogDestinacijEntity entity) {

        KatalogDestinacij dto = new KatalogDestinacij();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setLocation(entity.getLocation());
        dto.setAccessibility(entity.getAccessibility());
        dto.setInfrastructure(entity.getInfrastructure());
        dto.setPrice(entity.getPrice());
        //dto.setActivities(entity.getActivities());

        return dto;

    }

    public static KatalogDestinacijEntity toEntity(KatalogDestinacij dto) {

        KatalogDestinacijEntity entity = new KatalogDestinacijEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setLocation(dto.getLocation());
        entity.setAccessibility(dto.getAccessibility());
        entity.setInfrastructure(dto.getInfrastructure());
        entity.setPrice(dto.getPrice());
        //entity.setActivities(dto.getActivities());

        return entity;

    }

}
