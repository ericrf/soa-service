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
	AlunoRepository alunoRepository;
	
	@Autowired
	EnderecoRepository enderecoRepository;
	
    @RequestMapping(value={"/", ""},method={PUT, POST})
    public Aluno save(@RequestBody Aluno aluno) {
    	List<Endereco> enderecos = aluno.getEnderecos();
    	aluno = alunoRepository.saveAndFlush(aluno);
		salvaEnderecosDoAluno(enderecos, aluno);
    	return aluno;
    }

	private void salvaEnderecosDoAluno(List<Endereco> enderecos, Aluno aluno) {
		for (Endereco endereco : enderecos) {
			endereco.setAlunoMatricula(aluno.getMatricula());
			endereco = enderecoRepository.saveAndFlush(endereco);
		}
		aluno.setEnderecos(enderecos);
	}
    
    @RequestMapping(value={"/{id}"},method=RequestMethod.DELETE)
    public void delete(@PathVariable(value="id") final Long id) {
    	Aluno aluno = alunoRepository.findOne(id);
    	enderecoRepository.findByAlunoMatricula(aluno.getMatricula())
    		.forEach(endereco -> {
    			enderecoRepository.delete(endereco.getId());
    		});
    	alunoRepository.delete(id);
    }
    
    @RequestMapping(value={"/", ""},method=GET)
    public List<Aluno> findAll() {
    	List<Aluno> alunos = alunoRepository.findAll();
    	setaEnderecosDoAluno(alunos);
        return alunos;
    }

    @RequestMapping(value={"/{id}"},method=GET)
    public Aluno findOneById(@PathVariable(value="id") final Long id) {
    	Aluno aluno = alunoRepository.findOne(id);
    	setaEnderecosDoAluno(aluno);
        return aluno;
    }
    
    
    private void setaEnderecosDoAluno(List<Aluno> alunos) {
    	alunos.forEach(aluno ->{
    		setaEnderecosDoAluno(aluno);
    	});
	}

	private void setaEnderecosDoAluno(Aluno aluno) {
		aluno.setEnderecos(enderecoRepository.findByAlunoMatricula(aluno.getMatricula()));
	}
}
