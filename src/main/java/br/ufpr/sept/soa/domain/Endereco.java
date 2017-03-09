package br.ufpr.sept.soa.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(value={"aluno"})
public class Endereco {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private int cep;
    private String cidade;
    private String estado;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    
    private Aluno aluno;
    
    @JsonIgnore
    public Aluno getAluno(){
    	return aluno;
    }
}
