package br.ufpr.sept.soa.tests;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.sept.soa.domain.Aluno;
import br.ufpr.sept.soa.domain.Endereco;
import br.ufpr.sept.soa.main.Application;
import ch.qos.logback.core.db.dialect.SQLDialectCode;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource("classpath:config/application.properties")
@WebAppConfiguration
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class IntegrationTests {
	
	private static final int CEP = 82320390;

	private static final String ESTADO = "Paraná";

	private static final String CIDADE = "Curitiba";

	private static final String BAIRRO = "Butiatuvinha";

	private static final String COMPLEMENTO = "casa";

	private static final String NUMERO = "1003";

	private static final String LOGRADOURO = "Rua Acelino Grande";

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

    @Before
    public void setup() throws Exception {
        mvc = webAppContextSetup(context).build();
    }
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts="classpath:config/schema.sql")
    public void testFindAllReturnsEmptyListOfAluno() throws IOException, Exception{
    	mvc.perform(get("/alunos/")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts={"classpath:config/schema.sql", "classpath:config/data.sql"})
    public void testFindAllReturnsListOfAluno() throws IOException, Exception{
    	ResultActions actions = mvc.perform(get("/alunos/")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts={"classpath:config/schema.sql", "classpath:config/data.sql"} )
    public void testFindOne() throws IOException, Exception{
    	ResultActions actions = mvc.perform(get("/alunos/1")
            .contentType(contentType))
            .andExpect(status().isOk());
    	assertAlunoJsonPath(actions);
    }

    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts="classpath:config/schema.sql")
    public void testInsert() throws IOException, Exception{
    	Endereco endereco = new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, CEP, BAIRRO, CIDADE, ESTADO);
		Aluno aluno = new Aluno(0, "06460775974","Eric Rodrigo de Freitas", 29, asList(endereco));
    	ResultActions actions = mvc.perform(post("/alunos")
                .content(json(aluno))
                .contentType(contentType))
                .andExpect(status().isOk());
    	assertAlunoJsonPath(actions);
    	
    	actions = mvc.perform(get("/alunos/1")
    			.contentType(contentType))
    			.andExpect(status().isOk());
    	assertAlunoJsonPath(actions);
    }
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts={"classpath:config/schema.sql", "classpath:config/data.sql"} )
    public void testUpdate() throws IOException, Exception{
    	String nome = "Ana Paula Bail dos Santos";
    	
    	String json = mvc.perform(get("/alunos/1")).andReturn().getResponse().getContentAsString();
    	ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    	Aluno aluno = mapper.readValue(json, Aluno.class);
		aluno.setNome(nome);
    	
		ResultActions actions = mvc.perform(put("/alunos")
                .content(json(aluno))
                .contentType(contentType))
                .andExpect(status().isOk());
    	assertAluno(actions, 1, 1, nome, "06460775974", 29, LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO,
				CIDADE, ESTADO, CEP);
    }
    
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts={"classpath:config/schema.sql", "classpath:config/data.sql"})
    public void testRemove() throws IOException, Exception{
    	mvc.perform(delete("/alunos/1")
                .contentType(contentType))
                .andExpect(status().isOk());
    	
    }
    
    private void assertAlunoJsonPath(ResultActions actions) throws Exception {
		assertAluno(actions, 1, 1, "Eric Rodrigo de Freitas", "06460775974", 29, LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO,
				CIDADE, ESTADO, CEP);
            
	}

	private void assertAluno(ResultActions actions, int quantidadeEnderecos, int matricula, String nome, String cpf,
			int idade, String logradouro, String numero, String complemento, String bairro, String cidade,
			String estado, int cep) throws Exception {
		actions.andExpect(jsonPath("$.matricula", is(matricula)))
            .andExpect(jsonPath("$.nome", is(nome)))
            .andExpect(jsonPath("$.cpf", is(cpf)))
            .andExpect(jsonPath("$.idade", is(idade)))
            .andExpect(jsonPath("$.enderecos", hasSize(quantidadeEnderecos)))
			.andExpect(jsonPath("$.enderecos[0].id", is(quantidadeEnderecos)))
			.andExpect(jsonPath("$.enderecos[0].logradouro", is(logradouro)))
	        .andExpect(jsonPath("$.enderecos[0].numero", is(numero)))
	        .andExpect(jsonPath("$.enderecos[0].complemento", is(complemento)))
	        .andExpect(jsonPath("$.enderecos[0].bairro", is(bairro)))
	        .andExpect(jsonPath("$.enderecos[0].cidade", is(cidade)))
	        .andExpect(jsonPath("$.enderecos[0].estado", is(estado)))
	        .andExpect(jsonPath("$.enderecos[0].cep", is(cep)));
	}
    
    @Autowired
    private WebApplicationContext webApplicationContext;
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("UTF-8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }
	
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
    
    protected String aluno(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
    
    

}
