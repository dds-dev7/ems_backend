package net.dds.ems.dtoMapper;

import net.dds.ems.dto.CaissierDto;
import net.dds.ems.entity.Caissier;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CaissierDtoMapper implements Function<Caissier, CaissierDto>{

    @Override
    public CaissierDto apply(Caissier caissier) {
        return new CaissierDto(caissier.getId(), caissier.getNom(), caissier.getNumero(),caissier.getNumeroIdentifiant(),caissier.getStatut(),caissier.getRole().getNom(), caissier.getQuartier(), caissier.getVille(), caissier.getDateCreation());
    }
}
