package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table( name = "tblRole")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sRoleId")
    private Long roleId;

    @Column(name = "sRoleName", nullable = false, unique = true)
    private String roleName;

    @Column(name = "sDescription")
    private String description;

    // Role ↔ UserRole (1-n)
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserRole> userRoles;

    // Role ↔ RolePermission (1-n)
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions;
}
