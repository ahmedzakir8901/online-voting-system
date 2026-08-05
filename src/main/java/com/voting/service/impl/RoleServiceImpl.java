package com.voting.service.impl;

import com.voting.entity.Role;
import com.voting.repository.RoleRepository;
import com.voting.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;


    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }


    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElse(null);
    }


    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}