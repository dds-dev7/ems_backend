package net.dds.ems.service;

import jakarta.persistence.EntityNotFoundException;
import net.dds.ems.dto.AdminDto;
import net.dds.ems.dtoMapper.AdminDtoMapper;
import net.dds.ems.entity.Admin;
import net.dds.ems.entity.Role;
import net.dds.ems.repository.UtilisateurRepository;
import net.dds.ems.repository.RoleRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AdminService {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private UtilisateurRepository utilisateurRepository; // Use the repository for Utilisateur

    @Autowired
    private AdminDtoMapper adminDtoMapper;

    @Autowired
    private RoleRepository roleRepository;

    // Create an Admin admin in the single table inheritance
    public Admin createAdmin(Admin admin) throws Exception {
        Role role = admin.getRole();
        if (role != null && role.getId() != 0) {
            Optional<Role> optionalRole = roleRepository.findById(role.getId());
            if (!optionalRole.isPresent() || !optionalRole.get().getNom().equals("ADMIN")) {
                throw new BadRequestException("Un admin ne peut pas être créé avec ce rôle");
            }
            admin.setRole(optionalRole.get());
        } else {
            throw new BadRequestException("Erreur lors de la récupération du rôle");
        }

        // Set the MotDePasse
        admin.setMotDePasse(encoder.encode(admin.getMotDePasse()));
        try {
            this.utilisateurRepository.save(admin);
        } catch (Exception ex) {
            System.out.println(ex);
            throw new BadRequestException(ex.getMessage());
        }

        return this.utilisateurRepository.save(admin);
    }

    // Search for an Admin by ID
    public Admin search(int id) {
        Optional<Admin> optionalAdmin = utilisateurRepository.findById(id)
                .filter(utilisateur -> utilisateur instanceof Admin)
                .map(utilisateur -> (Admin) utilisateur);

        return optionalAdmin.orElseThrow(
                () -> new EntityNotFoundException("Aucun admin n'existe avec cet id")
        );
    }

    // Show all Admins
    public Stream<AdminDto> showAdmin() {
        return utilisateurRepository.findAll().stream()
                .filter(utilisateur -> utilisateur instanceof Admin)
                .map(utilisateur -> (Admin) utilisateur)
                .map(adminDtoMapper);
    }

    // Show a specific Admin by ID
    public Stream<AdminDto> showAdminById(int id) {
        return utilisateurRepository.findById(id)
                .filter(utilisateur -> utilisateur instanceof Admin)
                .map(utilisateur -> (Admin) utilisateur)
                .stream()
                .map(adminDtoMapper);
    }

    // Update an existing Admin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Admin updateAdmin(int id, Admin admin) throws Exception {
        Admin existingAdmin = this.search(id);

        if (admin.getNom() != null) existingAdmin.setNom(admin.getNom());
        if (admin.getNumero() != null) existingAdmin.setNumero(admin.getNumero());
        if (admin.getMotDePasse() != null) existingAdmin.setMotDePasse(admin.getMotDePasse());
        if (admin.getRole() != null) existingAdmin.setRole(admin.getRole());

        try {
            this.utilisateurRepository.save(existingAdmin);
        } catch (Exception ex) {
            throw new BadRequestException("Erreur lors de la mise à jour de l'admin, vérifiez votre syntaxe !");
        }

        return this.utilisateurRepository.save(existingAdmin);
    }

    // Delete an Admin by ID
    public void deleteAdmin(int id) {
        Admin existingAdmin = this.search(id);
        utilisateurRepository.delete(existingAdmin);
    }
}
