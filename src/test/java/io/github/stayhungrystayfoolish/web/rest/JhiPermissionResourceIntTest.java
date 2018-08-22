package io.github.stayhungrystayfoolish.web.rest;

import io.github.stayhungrystayfoolish.SecurityDesignApp;

import io.github.stayhungrystayfoolish.domain.JhiPermission;
import io.github.stayhungrystayfoolish.repository.JhiPermissionRepository;
import io.github.stayhungrystayfoolish.service.JhiPermissionService;
import io.github.stayhungrystayfoolish.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static io.github.stayhungrystayfoolish.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JhiPermissionResource REST controller.
 *
 * @see JhiPermissionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityDesignApp.class)
public class JhiPermissionResourceIntTest {

    private static final String DEFAULT_PERMISSION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PERMISSION_NAME = "BBBBBBBBBB";

    @Autowired
    private JhiPermissionRepository jhiPermissionRepository;

    

    @Autowired
    private JhiPermissionService jhiPermissionService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJhiPermissionMockMvc;

    private JhiPermission jhiPermission;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JhiPermissionResource jhiPermissionResource = new JhiPermissionResource(jhiPermissionService);
        this.restJhiPermissionMockMvc = MockMvcBuilders.standaloneSetup(jhiPermissionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JhiPermission createEntity(EntityManager em) {
        JhiPermission jhiPermission = new JhiPermission()
            .permissionName(DEFAULT_PERMISSION_NAME);
        return jhiPermission;
    }

    @Before
    public void initTest() {
        jhiPermission = createEntity(em);
    }

    @Test
    @Transactional
    public void createJhiPermission() throws Exception {
        int databaseSizeBeforeCreate = jhiPermissionRepository.findAll().size();

        // Create the JhiPermission
        restJhiPermissionMockMvc.perform(post("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiPermission)))
            .andExpect(status().isCreated());

        // Validate the JhiPermission in the database
        List<JhiPermission> jhiPermissionList = jhiPermissionRepository.findAll();
        assertThat(jhiPermissionList).hasSize(databaseSizeBeforeCreate + 1);
        JhiPermission testJhiPermission = jhiPermissionList.get(jhiPermissionList.size() - 1);
        assertThat(testJhiPermission.getPermissionName()).isEqualTo(DEFAULT_PERMISSION_NAME);
    }

    @Test
    @Transactional
    public void createJhiPermissionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jhiPermissionRepository.findAll().size();

        // Create the JhiPermission with an existing ID
        jhiPermission.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJhiPermissionMockMvc.perform(post("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiPermission)))
            .andExpect(status().isBadRequest());

        // Validate the JhiPermission in the database
        List<JhiPermission> jhiPermissionList = jhiPermissionRepository.findAll();
        assertThat(jhiPermissionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJhiPermissions() throws Exception {
        // Initialize the database
        jhiPermissionRepository.saveAndFlush(jhiPermission);

        // Get all the jhiPermissionList
        restJhiPermissionMockMvc.perform(get("/api/jhi-permissions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jhiPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].permissionName").value(hasItem(DEFAULT_PERMISSION_NAME.toString())));
    }
    

    @Test
    @Transactional
    public void getJhiPermission() throws Exception {
        // Initialize the database
        jhiPermissionRepository.saveAndFlush(jhiPermission);

        // Get the jhiPermission
        restJhiPermissionMockMvc.perform(get("/api/jhi-permissions/{id}", jhiPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jhiPermission.getId().intValue()))
            .andExpect(jsonPath("$.permissionName").value(DEFAULT_PERMISSION_NAME.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingJhiPermission() throws Exception {
        // Get the jhiPermission
        restJhiPermissionMockMvc.perform(get("/api/jhi-permissions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJhiPermission() throws Exception {
        // Initialize the database
        jhiPermissionService.save(jhiPermission);

        int databaseSizeBeforeUpdate = jhiPermissionRepository.findAll().size();

        // Update the jhiPermission
        JhiPermission updatedJhiPermission = jhiPermissionRepository.findById(jhiPermission.getId()).get();
        // Disconnect from session so that the updates on updatedJhiPermission are not directly saved in db
        em.detach(updatedJhiPermission);
        updatedJhiPermission
            .permissionName(UPDATED_PERMISSION_NAME);

        restJhiPermissionMockMvc.perform(put("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJhiPermission)))
            .andExpect(status().isOk());

        // Validate the JhiPermission in the database
        List<JhiPermission> jhiPermissionList = jhiPermissionRepository.findAll();
        assertThat(jhiPermissionList).hasSize(databaseSizeBeforeUpdate);
        JhiPermission testJhiPermission = jhiPermissionList.get(jhiPermissionList.size() - 1);
        assertThat(testJhiPermission.getPermissionName()).isEqualTo(UPDATED_PERMISSION_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingJhiPermission() throws Exception {
        int databaseSizeBeforeUpdate = jhiPermissionRepository.findAll().size();

        // Create the JhiPermission

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJhiPermissionMockMvc.perform(put("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiPermission)))
            .andExpect(status().isBadRequest());

        // Validate the JhiPermission in the database
        List<JhiPermission> jhiPermissionList = jhiPermissionRepository.findAll();
        assertThat(jhiPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJhiPermission() throws Exception {
        // Initialize the database
        jhiPermissionService.save(jhiPermission);

        int databaseSizeBeforeDelete = jhiPermissionRepository.findAll().size();

        // Get the jhiPermission
        restJhiPermissionMockMvc.perform(delete("/api/jhi-permissions/{id}", jhiPermission.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<JhiPermission> jhiPermissionList = jhiPermissionRepository.findAll();
        assertThat(jhiPermissionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JhiPermission.class);
        JhiPermission jhiPermission1 = new JhiPermission();
        jhiPermission1.setId(1L);
        JhiPermission jhiPermission2 = new JhiPermission();
        jhiPermission2.setId(jhiPermission1.getId());
        assertThat(jhiPermission1).isEqualTo(jhiPermission2);
        jhiPermission2.setId(2L);
        assertThat(jhiPermission1).isNotEqualTo(jhiPermission2);
        jhiPermission1.setId(null);
        assertThat(jhiPermission1).isNotEqualTo(jhiPermission2);
    }
}
