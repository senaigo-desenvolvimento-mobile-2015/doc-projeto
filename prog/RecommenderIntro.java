package br.com.ambientinformatica.fatesg.corporatum.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.ambientinformatica.ambientjsf.util.UtilFaces;
import br.com.ambientinformatica.fatesg.api.entidade.Aluno;
import br.com.ambientinformatica.fatesg.api.entidade.EnumStatusAluno;
import br.com.ambientinformatica.fatesg.api.entidade.EnumTipoSexo;
import br.com.ambientinformatica.fatesg.corporatum.dao.AlunoDao;
import br.com.ambientinformatica.util.UtilCpf;

@Controller("AlunoControl")
@Scope("conversation")
public class AlunoControl implements Serializable {

private static final long serialVersionUID = 1L;
private Aluno aluno = new Aluno();
@Autowired
private AlunoDao alunoDao;

private List<Aluno> alunos = new ArrayList<Aluno>();

@PostConstruct
public void init() {
listar(null);
}

public void confirmar(ActionEvent evt) {
		try {
			alunoDao.verificarCampos(aluno);
			String cpf = aluno.getCpfCnpj();
			if (UtilCpf.validarCpf(cpf)) {
				alunoDao.alterar(aluno);
				listar(evt);
				aluno = new Aluno();
			} else {
				UtilFaces.addMensagemFaces("CPF Inválido");
			}
		} catch (Exception e) {
			UtilFaces.addMensagemFaces(e);
		}
	}

	public void excluir() {
		try {
			alunoDao.excluirPorId(aluno.getId());
			aluno = new Aluno();
			alunos = alunoDao.listar();
		} catch (Exception e) {
			UtilFaces.addMensagemFaces(e);
		}
	}

	public void listar(ActionEvent evt) {
		try {
			alunos = alunoDao.listar();
		} catch (Exception e) {
			UtilFaces.addMensagemFaces(e);
		}
	}

	public void limpar() {
		aluno = new Aluno();
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public List<Aluno> getAlunos() {
		return alunos;
	}

	public List<SelectItem> getTiposSexo() {
		return UtilFaces.getListEnum(EnumTipoSexo.values());
	}

	public List<SelectItem> getStatus() {
		return UtilFaces.getListEnum(EnumStatusAluno.values());
	}
}


package br.com.ambientinformatica.fatesg.corporatum.persistencia;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.ambientinformatica.fatesg.api.entidade.Aluno;
import br.com.ambientinformatica.fatesg.api.entidade.EnumStatusAluno;
import br.com.ambientinformatica.fatesg.corporatum.util.CorporatumException;
import br.com.ambientinformatica.jpa.persistencia.PersistenciaJpa;

@Repository("alunoDao")
public class AlunoDaoJpa extends PersistenciaJpa<Aluno> implements AlunoDao {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
   @Override
	public List<Aluno> listar(boolean todos, EnumStatusAluno status) throws CorporatumException {
		String jpaql = "select distinct a from Aluno a ";
		if(status != null){
			jpaql += " where a.status = :status";
		}
      Query query = em.createQuery(jpaql);
      if(status != null){
      	query.setParameter("status", status);
      }
      if(!todos){
      	query.setMaxResults(200);
      }
      return query.getResultList();

	}

@Override
public void validarCampos(Aluno aluno) throws CorporatumException {

if (aluno.getNome() == null || aluno.getNome().isEmpty()) {
			throw new CorporatumException("*Campo Obrigátorio: Nome");
}
		if (aluno.getRg() == null || aluno.getRg().isEmpty()) {
			throw new CorporatumException("*Campo Obrigátorio: RG");
		}
		if (aluno.getCpfCnpj() == null || aluno.getCpfCnpj().isEmpty()) {
			throw new CorporatumException("*Campo Obrigátorio: CPF");
		}
		if (aluno.getTituloEleitor() == null || aluno.getTituloEleitor().isEmpty()) {
			throw new CorporatumException(
			      "*Campo Obrigátorio: titulo de eleitor");
		}
		if (aluno.getTipoSexo() == null) {
			throw new CorporatumException(
			      "*Campo Obrigátorio: Sexo");
		}
		if (aluno.getReservista() == null || aluno.getReservista().isEmpty()) {
			throw new CorporatumException(
			      "*Campo Obrigátorio: numero da reservista");
		}
		if (aluno.getCertificado2Grau() == null || aluno.getCertificado2Grau().isEmpty()) {
			throw new CorporatumException(
			      "*Campo Obrigátorio: Certificado 2º Grau");
		}
		if ((aluno.getTelefone() == null || aluno.getTelefone().isEmpty()) && (aluno.getCelular() 
				== null || aluno.getCelular().isEmpty())) {
			throw new CorporatumException(
			      "É necessário um numero Celular ou Telefone");
		}
		if (aluno.getEmail() == null || aluno.getEmail().isEmpty()) {
			throw new CorporatumException("*Campo Obrigátorio: E-mail");
		}
		if (aluno.getCep() == null || aluno.getCep().isEmpty()) {
			throw new CorporatumException("*Campo Obrigátorio: CEP");
		}
		if (aluno.getEndereco() == null || aluno.getEndereco().isEmpty()) {
			throw new CorporatumException("*Campo Obrigátorio: Endereço");
		}
		if (aluno.getMunicipio() == null) {
			throw new CorporatumException("*Campo Obrigátorio: Municipio");
		}
		if (aluno.getUf() == null) {
			throw new CorporatumException("*Campo Obrigátorio: UF(Estado)");
		}
	}

	@Override
   public Aluno consultarPorCpfCnpj(String cpfCnpj) throws CorporatumException {
		try{
			String jpaql = "select distinct a from Aluno a ";
			if(cpfCnpj != null){
				jpaql += " where a.cpfCnpj = :cpfCnpj";
			}else{
				throw new CorporatumException("Informe um CPF válido");
			}
			Query query = em.createQuery(jpaql);
			if(cpfCnpj != null){
				query.setParameter("cpfCnpj", cpfCnpj);
			}
			return (Aluno) query.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
   }
}


package br.com.ambientinformatica.fatesg.corporatum.persistencia;

import java.util.List;

import br.com.ambientinformatica.fatesg.api.entidade.Aluno;
import br.com.ambientinformatica.fatesg.api.entidade.EnumStatusAluno;
import br.com.ambientinformatica.fatesg.corporatum.util.CorporatumException;
import br.com.ambientinformatica.jpa.persistencia.Persistencia;

public interface AlunoDao extends Persistencia<Aluno>{
	
	public void validarCampos(Aluno aluno) throws CorporatumException;
	
	public List<Aluno> listar(boolean todos, EnumStatusAluno status) 
			throws CorporatumException;
	
	public Aluno consultarPorCpfCnpj(String cpfCnpj) throws CorporatumException;
	
}
