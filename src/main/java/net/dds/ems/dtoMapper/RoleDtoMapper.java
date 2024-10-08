package net.dds.ems.dtoMapper;

import net.dds.ems.dto.RoleDto;
import net.dds.ems.entity.Role;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RoleDtoMapper implements Function<Role, RoleDto>{

    @Override
    public RoleDto apply(Role role) {
        return new RoleDto(role.getId(), role.getNom(), role.getDroit());
    }
}
