package br.ufpr.sept.soa.components.aluno;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ufpr.sept.soa.domain.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long>{
}
