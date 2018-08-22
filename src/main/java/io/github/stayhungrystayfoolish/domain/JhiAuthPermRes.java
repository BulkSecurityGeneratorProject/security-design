package io.github.stayhungrystayfoolish.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A JhiAuthPermRes.
 */
@Entity
@Table(name = "jhi_auth_perm_res")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JhiAuthPermRes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authority_name")
    private String authorityName;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "resource_name")
    private String resourceName;

    @ManyToOne
    @JsonIgnoreProperties("permissions")
    private JhiPermission jhiPermission;

    @ManyToOne
    @JsonIgnoreProperties("resources")
    private JhiResource jhiResource;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public JhiAuthPermRes authorityName(String authorityName) {
        this.authorityName = authorityName;
        return this;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public JhiAuthPermRes permissionName(String permissionName) {
        this.permissionName = permissionName;
        return this;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public JhiAuthPermRes resourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public JhiPermission getJhiPermission() {
        return jhiPermission;
    }

    public JhiAuthPermRes jhiPermission(JhiPermission jhiPermission) {
        this.jhiPermission = jhiPermission;
        return this;
    }

    public void setJhiPermission(JhiPermission jhiPermission) {
        this.jhiPermission = jhiPermission;
    }

    public JhiResource getJhiResource() {
        return jhiResource;
    }

    public JhiAuthPermRes jhiResource(JhiResource jhiResource) {
        this.jhiResource = jhiResource;
        return this;
    }

    public void setJhiResource(JhiResource jhiResource) {
        this.jhiResource = jhiResource;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JhiAuthPermRes jhiAuthPermRes = (JhiAuthPermRes) o;
        if (jhiAuthPermRes.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jhiAuthPermRes.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JhiAuthPermRes{" +
            "id=" + getId() +
            ", authorityName='" + getAuthorityName() + "'" +
            ", permissionName='" + getPermissionName() + "'" +
            ", resourceName='" + getResourceName() + "'" +
            "}";
    }
}
