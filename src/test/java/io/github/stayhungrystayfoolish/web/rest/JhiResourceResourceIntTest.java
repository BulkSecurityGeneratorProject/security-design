package io.github.stayhungrystayfoolish.web.rest;

import io.github.stayhungrystayfoolish.SecurityDesignApp;

import io.github.stayhungrystayfoolish.domain.JhiResource;
import io.github.stayhungrystayfoolish.repository.JhiResourceRepository;
import io.github.stayhungrystayfoolish.service.JhiResourceService;
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
 * Test class for the JhiResourceResource REST controller.
 *
 * @see JhiResourceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityDesignApp.class)
public class JhiResourceResourceIntTest {

    private static final String DEFAULT_RESOURCE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_NAME = "BBBBBBBBBB";

    @Autowired
    private JhiResourceRepository jhiResourceRepository;

    

    @Autowired
    private JhiResourceService jhiResourceService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJhiResourceMockMvc;

    private JhiResource jhiResource;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JhiResourceResource jhiResourceResource = new JhiResourceResource(jhiResourceService);
        this.restJhiResourceMockMvc = MockMvcBuilders.standaloneSetup(jhiResourceResource)
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
    public static JhiResource createEntity(EntityManager em) {
        JhiResource jhiResource = new JhiResource()
            .resourceName(DEFAULT_RESOURCE_NAME);
        return jhiResource;
    }

    @Before
    public void initTest() {
        jhiResource = createEntity(em);
    }

    @Test
    @Transactional
    public void createJhiResource() throws Exception {
        int databaseSizeBeforeCreate = jhiResourceRepository.findAll().size();

        // Create the JhiResource
        restJhiResourceMockMvc.perform(post("/api/jhi-resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiResource)))
            .andExpect(status().isCreated());

        // Validate the JhiResource in the database
        List<JhiResource> jhiResourceList = jhiResourceRepository.findAll();
        assertThat(jhiResourceList).hasSize(databaseSizeBeforeCreate + 1);
        JhiResource testJhiResource = jhiResourceList.get(jhiResourceList.size() - 1);
        assertThat(testJhiResource.getResourceName()).isEqualTo(DEFAULT_RESOURCE_NAME);
    }

    @Test
    @Transactional
    public void createJhiResourceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jhiResourceRepository.findAll().size();

        // Create the JhiResource with an existing ID
        jhiResource.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJhiResourceMockMvc.perform(post("/api/jhi-resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiResource)))
            .andExpect(status().isBadRequest());

        // Validate the JhiResource in the database
        List<JhiResource> jhiResourceList = jhiResourceRepository.findAll();
        assertThat(jhiResourceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJhiResources() throws Exception {
        // Initialize the database
        jhiResourceRepository.saveAndFlush(jhiResource);

        // Get all the jhiResourceList
        restJhiResourceMockMvc.perform(get("/api/jhi-resources?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jhiResource.getId().intValue())))
            .andExpect(jsonPath("$.[*].resourceName").value(hasItem(DEFAULT_RESOURCE_NAME.toString())));
    }
    

    @Test
    @Transactional
    public void getJhiResource() throws Exception {
        // Initialize the database
        jhiResourceRepository.saveAndFlush(jhiResource);

        // Get the jhiResource
        restJhiResourceMockMvc.perform(get("/api/jhi-resources/{id}", jhiResource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jhiResource.getId().intValue()))
            .andExpect(jsonPath("$.resourceName").value(DEFAULT_RESOURCE_NAME.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingJhiResource() throws Exception {
        // Get the jhiResource
        restJhiResourceMockMvc.perform(get("/api/jhi-resources/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJhiResource() throws Exception {
        // Initialize the database
        jhiResourceService.save(jhiResource);

        int databaseSizeBeforeUpdate = jhiResourceRepository.findAll().size();

        // Update the jhiResource
        JhiResource updatedJhiResource = jhiResourceRepository.findById(jhiResource.getId()).get();
        // Disconnect from session so that the updates on updatedJhiResource are not directly saved in db
        em.detach(updatedJhiResource);
        updatedJhiResource
            .resourceName(UPDATED_RESOURCE_NAME);

        restJhiResourceMockMvc.perform(put("/api/jhi-resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJhiResource)))
            .andExpect(status().isOk());

        // Validate the JhiResource in the database
        List<JhiResource> jhiResourceList = jhiResourceRepository.findAll();
        assertThat(jhiResourceList).hasSize(databaseSizeBeforeUpdate);
        JhiResource testJhiResource = jhiResourceList.get(jhiResourceList.size() - 1);
        assertThat(testJhiResource.getResourceName()).isEqualTo(UPDATED_RESOURCE_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingJhiResource() throws Exception {
        int databaseSizeBeforeUpdate = jhiResourceRepository.findAll().size();

        // Create the JhiResource

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJhiResourceMockMvc.perform(put("/api/jhi-resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jhiResource)))
            .andExpect(status().isBadRequest());

        // Validate the JhiResource in the database
        List<JhiResource> jhiResourceList = jhiResourceRepository.findAll();
        assertThat(jhiResourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJhiResource() throws Exception {
        // Initialize the database
        jhiResourceService.save(jhiResource);

        int databaseSizeBeforeDelete = jhiResourceRepository.findAll().size();

        // Get the jhiResource
        restJhiResourceMockMvc.perform(delete("/api/jhi-resources/{id}", jhiResource.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<JhiResource> jhiResourceList = jhiResourceRepository.findAll();
        assertThat(jhiResourceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JhiResource.class);
        JhiResource jhiResource1 = new JhiResource();
        jhiResource1.setId(1L);
        JhiResource jhiResource2 = new JhiResource();
        jhiResource2.setId(jhiResource1.getId());
        assertThat(jhiResource1).isEqualTo(jhiResource2);
        jhiResource2.setId(2L);
        assertThat(jhiResource1).isNotEqualTo(jhiResource2);
        jhiResource1.setId(null);
        assertThat(jhiResource1).isNotEqualTo(jhiResource2);
    }
}
