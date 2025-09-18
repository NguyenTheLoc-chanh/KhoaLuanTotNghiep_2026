package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tblPermission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sPermissionId")
    private Long permissionId;

    @Column(name = "sPermissionName", nullable = false, unique = true)
    private String permissionName;

    @Column(name = "sDescription")
    private String description;

    // Permission ↔ RolePermission (1-n)
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    private List<RolePermission> rolePermissions;
}
