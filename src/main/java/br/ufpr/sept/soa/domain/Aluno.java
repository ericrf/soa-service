package br.ufpr.sept.soa.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Aluno {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private long matricula;
    private String cpf;
    private String nome;
    private int idade;
    
    @OneToMany(fetch=LAZY, mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<Endereco> enderecos;
}
