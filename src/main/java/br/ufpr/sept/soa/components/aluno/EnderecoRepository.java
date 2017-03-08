package br.ufpr.sept.soa.components.aluno;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.ufpr.sept.soa.domain.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
	
	@Query("SELECT e FROM Endereco e WHERE alunoMatricula = (:matricula)")
	List<Endereco> findByAlunoMatricula(@Param("matricula") Long matricula);
}
