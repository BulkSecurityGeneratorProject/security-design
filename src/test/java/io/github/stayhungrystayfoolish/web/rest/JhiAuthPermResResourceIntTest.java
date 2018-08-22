package io.github.stayhungrystayfoolish.web.rest;

import io.github.stayhungrystayfoolish.SecurityDesignApp;

import io.github.stayhungrystayfoolish.domain.JhiAuthPermRes;
import io.github.stayhungrystayfoolish.repository.JhiAuthPermResRepository;
import io.github.stayhungrystayfoolish.service.JhiAuthPermResService;
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
 * Test class for the JhiAuthPermResResource REST controller.
 *
 * @see JhiAuthPermResResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityDesignApp.class)
public class JhiAuthPermResResourceIntTest {

    private static final String DEFAULT_AUTHORITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHORITY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PERMISSION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PERMISSION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RESOURCE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_NAME = "BBBBBBBBBB";

    @Autowired
    private JhiAuthPermResRepository jhiAuthPermResRepository;

    

    @Autowired
    private JhiAuthPermResService jhiAuthPermResService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJhiAuthPermResMockMvc;

    private JhiAuthPermRes jhiAuthPermRes;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JhiAuthPermResResource jhiAuthPermResResource = new JhiAuthPermResResource(jhiAuthPermResService);
        this.restJhiAuthPermResMockMvc = MockMvcBuilders.standaloneSetup(jhiAuthPermResResource)
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
    public static JhiAuthPermRes createEntity(EntityManager em) {
        JhiAuthPermRes jhiAuthPermRes = new JhiAuthPermRes()
            .authorityName(DEFAULT_AUTHORITY_NAME)
            .permissionName(DEFAULT_PERMISSION_NAME)
            .resourceName(DEFAULT_RESOURCE_NAME);
        return jhiAuthPermRes;
    }

    @Before
    public void initTest() {
        jhiAuthPermRes = createEntity(em);
    }

    @Test
    @Transactional
    public void createJhiAuthPermRes() throws Exception {
        int databaseSizeBeforeCreate = jhiAuthPermResRepository.findAll().size();

        // Create the JhiAuthPermRes
        restJhiAuthPermResMockMvc.perform(post("/api/jhi-auth-perm-res")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiAuthPermRes)))
            .andExpect(status().isCreated());

        // Validate the JhiAuthPermRes in the database
        List<JhiAuthPermRes> jhiAuthPermResList = jhiAuthPermResRepository.findAll();
        assertThat(jhiAuthPermResList).hasSize(databaseSizeBeforeCreate + 1);
        JhiAuthPermRes testJhiAuthPermRes = jhiAuthPermResList.get(jhiAuthPermResList.size() - 1);
        assertThat(testJhiAuthPermRes.getAuthorityName()).isEqualTo(DEFAULT_AUTHORITY_NAME);
        assertThat(testJhiAuthPermRes.getPermissionName()).isEqualTo(DEFAULT_PERMISSION_NAME);
        assertThat(testJhiAuthPermRes.getResourceName()).isEqualTo(DEFAULT_RESOURCE_NAME);
    }

    @Test
    @Transactional
    public void createJhiAuthPermResWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jhiAuthPermResRepository.findAll().size();

        // Create the JhiAuthPermRes with an existing ID
        jhiAuthPermRes.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJhiAuthPermResMockMvc.perform(post("/api/jhi-auth-perm-res")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiAuthPermRes)))
            .andExpect(status().isBadRequest());

        // Validate the JhiAuthPermRes in the database
        List<JhiAuthPermRes> jhiAuthPermResList = jhiAuthPermResRepository.findAll();
        assertThat(jhiAuthPermResList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJhiAuthPermRes() throws Exception {
        // Initialize the database
        jhiAuthPermResRepository.saveAndFlush(jhiAuthPermRes);

        // Get all the jhiAuthPermResList
        restJhiAuthPermResMockMvc.perform(get("/api/jhi-auth-perm-res?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jhiAuthPermRes.getId().intValue())))
            .andExpect(jsonPath("$.[*].authorityName").value(hasItem(DEFAULT_AUTHORITY_NAME.toString())))
            .andExpect(jsonPath("$.[*].permissionName").value(hasItem(DEFAULT_PERMISSION_NAME.toString())))
            .andExpect(jsonPath("$.[*].resourceName").value(hasItem(DEFAULT_RESOURCE_NAME.toString())));
    }
    

    @Test
    @Transactional
    public void getJhiAuthPermRes() throws Exception {
        // Initialize the database
        jhiAuthPermResRepository.saveAndFlush(jhiAuthPermRes);

        // Get the jhiAuthPermRes
        restJhiAuthPermResMockMvc.perform(get("/api/jhi-auth-perm-res/{id}", jhiAuthPermRes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jhiAuthPermRes.getId().intValue()))
            .andExpect(jsonPath("$.authorityName").value(DEFAULT_AUTHORITY_NAME.toString()))
            .andExpect(jsonPath("$.permissionName").value(DEFAULT_PERMISSION_NAME.toString()))
            .andExpect(jsonPath("$.resourceName").value(DEFAULT_RESOURCE_NAME.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingJhiAuthPermRes() throws Exception {
        // Get the jhiAuthPermRes
        restJhiAuthPermResMockMvc.perform(get("/api/jhi-auth-perm-res/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJhiAuthPermRes() throws Exception {
        // Initialize the database
        jhiAuthPermResService.save(jhiAuthPermRes);

        int databaseSizeBeforeUpdate = jhiAuthPermResRepository.findAll().size();

        // Update the jhiAuthPermRes
        JhiAuthPermRes updatedJhiAuthPermRes = jhiAuthPermResRepository.findById(jhiAuthPermRes.getId()).get();
        // Disconnect from session so that the updates on updatedJhiAuthPermRes are not directly saved in db
        em.detach(updatedJhiAuthPermRes);
        updatedJhiAuthPermRes
            .authorityName(UPDATED_AUTHORITY_NAME)
            .permissionName(UPDATED_PERMISSION_NAME)
            .resourceName(UPDATED_RESOURCE_NAME);

        restJhiAuthPermResMockMvc.perform(put("/api/jhi-auth-perm-res")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJhiAuthPermRes)))
            .andExpect(status().isOk());

        // Validate the JhiAuthPermRes in the database
        List<JhiAuthPermRes> jhiAuthPermResList = jhiAuthPermResRepository.findAll();
        assertThat(jhiAuthPermResList).hasSize(databaseSizeBeforeUpdate);
        JhiAuthPermRes testJhiAuthPermRes = jhiAuthPermResList.get(jhiAuthPermResList.size() - 1);
        assertThat(testJhiAuthPermRes.getAuthorityName()).isEqualTo(UPDATED_AUTHORITY_NAME);
        assertThat(testJhiAuthPermRes.getPermissionName()).isEqualTo(UPDATED_PERMISSION_NAME);
        assertThat(testJhiAuthPermRes.getResourceName()).isEqualTo(UPDATED_RESOURCE_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingJhiAuthPermRes() throws Exception {
        int databaseSizeBeforeUpdate = jhiAuthPermResRepository.findAll().size();

        // Create the JhiAuthPermRes

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJhiAuthPermResMockMvc.perform(put("/api/jhi-auth-perm-res")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiAuthPermRes)))
            .andExpect(status().isBadRequest());

        // Validate the JhiAuthPermRes in the database
        List<JhiAuthPermRes> jhiAuthPermResList = jhiAuthPermResRepository.findAll();
        assertThat(jhiAuthPermResList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJhiAuthPermRes() throws Exception {
        // Initialize the database
        jhiAuthPermResService.save(jhiAuthPermRes);

        int databaseSizeBeforeDelete = jhiAuthPermResRepository.findAll().size();

        // Get the jhiAuthPermRes
        restJhiAuthPermResMockMvc.perform(delete("/api/jhi-auth-perm-res/{id}", jhiAuthPermRes.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<JhiAuthPermRes> jhiAuthPermResList = jhiAuthPermResRepository.findAll();
        assertThat(jhiAuthPermResList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JhiAuthPermRes.class);
        JhiAuthPermRes jhiAuthPermRes1 = new JhiAuthPermRes();
        jhiAuthPermRes1.setId(1L);
        JhiAuthPermRes jhiAuthPermRes2 = new JhiAuthPermRes();
        jhiAuthPermRes2.setId(jhiAuthPermRes1.getId());
        assertThat(jhiAuthPermRes1).isEqualTo(jhiAuthPermRes2);
        jhiAuthPermRes2.setId(2L);
        assertThat(jhiAuthPermRes1).isNotEqualTo(jhiAuthPermRes2);
        jhiAuthPermRes1.setId(null);
        assertThat(jhiAuthPermRes1).isNotEqualTo(jhiAuthPermRes2);
    }
}
