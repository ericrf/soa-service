package br.ufpr.sept.soa.tests;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
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
import org.springframework.web.context.WebApplicationContext;

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
    	assertAlunoJsonPath(actions, hasSize(2));
    	assertEnderecoHomeJsonPath(actions);
    	assertEnderecoObjectiveJsonPath(actions);
    	
    }

    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts="classpath:config/schema.sql")
    public void testInsert() throws IOException, Exception{
    	Endereco endereco = new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, CEP, BAIRRO, CIDADE, ESTADO);
		Aluno aluno = new Aluno(0, "06460775974","Eric Rodrigo de Freitas", 29, Arrays.asList(endereco));
    	ResultActions actions = mvc.perform(post("/alunos")
                .content(json(aluno))
                .contentType(contentType))
                .andExpect(status().isOk());
    	assertAlunoJsonPath(actions, hasSize(1));
    	assertEnderecoHomeJsonPath(actions);
    }
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts={"classpath:config/schema.sql", "classpath:config/data.sql"} )
    public void testUpdate() throws IOException, Exception{
    	Endereco endereco = new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, CEP, BAIRRO, CIDADE, ESTADO);
		Aluno aluno = new Aluno(1, "06460775974","Eric Rodrigo de Freitas", 29, Arrays.asList(endereco));
    	ResultActions actions = mvc.perform(put("/alunos")
                .content(json(aluno))
                .contentType(contentType))
                .andExpect(status().isOk());
    	assertAlunoJsonPath(actions, hasSize(1));
    }
    
    
    @Test
    @Sql(executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts={"classpath:config/schema.sql", "classpath:config/data.sql"})
    public void testRemove() throws IOException, Exception{
    	mvc.perform(delete("/alunos/1")
                .contentType(contentType))
                .andExpect(status().isOk());
    	
    }
    
    private void assertAlunoJsonPath(ResultActions actions, Matcher<Collection<? extends Object>> enderecosHasSize) throws Exception {
		actions.andExpect(jsonPath("$.matricula", is(1)))
            .andExpect(jsonPath("$.nome", is("Eric Rodrigo de Freitas")))
            .andExpect(jsonPath("$.cpf", is("06460775974")))
            .andExpect(jsonPath("$.idade", is(29)))
            .andExpect(jsonPath("$.enderecos", enderecosHasSize));
            
	}

	private void assertEnderecoHomeJsonPath(ResultActions actions) throws Exception {
		actions.andExpect(jsonPath("$.enderecos[0].id", is(1)))
			.andExpect(jsonPath("$.enderecos[0].logradouro", is(LOGRADOURO)))
            .andExpect(jsonPath("$.enderecos[0].numero", is(NUMERO)))
            .andExpect(jsonPath("$.enderecos[0].complemento", is(COMPLEMENTO)))
            .andExpect(jsonPath("$.enderecos[0].bairro", is(BAIRRO)))
            .andExpect(jsonPath("$.enderecos[0].cidade", is(CIDADE)))
            .andExpect(jsonPath("$.enderecos[0].estado", is(ESTADO)))
            .andExpect(jsonPath("$.enderecos[0].cep", is(CEP)));
	}
	
	private void assertEnderecoObjectiveJsonPath(ResultActions actions) throws Exception {
		actions.andExpect(jsonPath("$.enderecos[1].id", is(2)))
	        .andExpect(jsonPath("$.enderecos[1].logradouro", is("Av. João Gualberto")))
	        .andExpect(jsonPath("$.enderecos[1].numero", is("1740")))
	        .andExpect(jsonPath("$.enderecos[1].complemento", is("9° Andar")))
	        .andExpect(jsonPath("$.enderecos[1].bairro", is("Juvevê")))
	        .andExpect(jsonPath("$.enderecos[1].cidade", is("Curitiba")))
	        .andExpect(jsonPath("$.enderecos[1].estado", is("Paraná")))
	        .andExpect(jsonPath("$.enderecos[1].cep", is(80030001)));
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

}
