package br.ufpr.sept.soa.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Aluno {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private long matricula;
    private String cpf;
    private String nome;
    private int idade;
    
    @Transient
    private List<Endereco> enderecos;
}
