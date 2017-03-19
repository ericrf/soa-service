package br.ufpr.sept.soa.components.aluno;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.sept.soa.domain.Aluno;
import br.ufpr.sept.soa.domain.Endereco;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

	@Autowired
	AlunoRepository repository;
	
    @RequestMapping(value={"/", ""},method={PUT, POST})
    public Aluno save(@RequestBody Aluno aluno) {
    	List<Endereco> enderecos = aluno.getEnderecos();
    	for (Endereco endereco : enderecos) {
			endereco.setAluno(aluno);
		}
    	return repository.save(aluno);
    }
    
    @RequestMapping(value={"/{id}"},method=RequestMethod.DELETE)
    public void delete(@PathVariable(value="id") final Long id) {
    	repository.delete(id);
    }
    
    @RequestMapping(value={"/", ""},method=GET)
    public List<Aluno> findAll() {
        return repository.findAll();
    }

    @RequestMapping(value={"/{id}"},method=GET)
    public Aluno findOneById(@PathVariable(value="id") final Long id) {
        return repository.findOne(id);
    }
}
