package br.ufpr.sept.soa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
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
    
    private long alunoMatricula;
}
