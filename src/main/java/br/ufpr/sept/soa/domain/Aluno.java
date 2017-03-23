package br.ufpr.sept.soa.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

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
	
	@NotEmpty
    private String cpf;
	
	@NotEmpty
    private String nome;
	
    private int idade;
    
    @OneToMany(fetch=LAZY, mappedBy = "aluno", cascade = CascadeType.ALL)
    @Valid
    private List<Endereco> enderecos;
}
