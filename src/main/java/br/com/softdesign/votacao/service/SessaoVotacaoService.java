    package br.com.softdesign.votacao.service;

    import br.com.softdesign.votacao.domain.Pauta;
    import br.com.softdesign.votacao.domain.SessaoVotacao;
    import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
    import br.com.softdesign.votacao.repository.PautaRepository;
    import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;

    @Service
    @RequiredArgsConstructor
    public class SessaoVotacaoService {

        private final SessaoVotacaoRepository  sessaoVotacaoRepository;

        private final PautaRepository pautaRepository;

        public SessaoVotacao criar(SessaoVotacao sessaoVotacao) {

            if (sessaoVotacao.getPauta() == null){
                throw new SessaoVotacaoInvaalidaException("A Pauta é obrigatória para abrir uma sessão");
            }

            Pauta pauta = pautaRepository.findById(sessaoVotacao.getPauta().getId())
                    .orElseThrow(() -> new SessaoVotacaoInvaalidaException(
                            "A sessão de votação não pode ser criada para uma pauta inexistente"));
            sessaoVotacao.setPauta(pauta);

            if (existeSessaoAtiva(pauta)) {
                throw new SessaoVotacaoInvaalidaException(
                        "Não é possível criar uma sessão de votação em pauta com sessão já ativa");
            }

            setDuracao(sessaoVotacao);
            return sessaoVotacaoRepository.save(sessaoVotacao);
        }

        public boolean existeSessaoAtiva(Pauta pauta){
            return sessaoVotacaoRepository
                    .findByPautaIdAndDataFimAfter(pauta.getId(), LocalDateTime.now())
                    .stream()
                    .findAny()
                    .isPresent();
        }

        public void setDuracao(SessaoVotacao sessaoVotacao) {
            sessaoVotacao.setDataInicio(LocalDateTime.now());
            int duracao = sessaoVotacao.getDuracao() > 0 ? sessaoVotacao.getDuracao() : 1;
            sessaoVotacao.setDuracao(duracao);
            sessaoVotacao.setDataFim(sessaoVotacao.getDataInicio().plusMinutes(duracao));
        }
    }
