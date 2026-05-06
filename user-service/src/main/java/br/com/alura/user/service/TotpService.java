package br.com.alura.user.service;

import br.com.alura.user.dto.TotpDTO;
import br.com.alura.user.dto.TotpSetupDTO;
import br.com.alura.user.exception.RegraDeNegocioException;
import br.com.alura.user.model.Usuario;
import br.com.alura.user.repository.UsuarioRepository;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TotpService {

    private final UsuarioRepository usuarioRepository;
    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final DefaultCodeVerifier codeVerifier;

    public TotpService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator();
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());
    }

    @Transactional
    public TotpSetupDTO gerarSegredo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));

        String secret = secretGenerator.generate();
        usuario.setTotpSecret(secret);
        usuario.setTotpAtivo(false);
        usuarioRepository.save(usuario);

        String qrCodeUrl = String.format(
            "otpauth://totp/ReservaSalas:%s?secret=%s&issuer=ReservaSalas",
            usuario.getEmail(), secret
        );

        return new TotpSetupDTO(secret, qrCodeUrl);
    }

    @Transactional
    public void ativar(Long usuarioId, TotpDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));

        if (usuario.getTotpSecret() == null) {
            throw new RegraDeNegocioException("Segredo 2FA não configurado. Chame /setup antes.");
        }

        boolean valido = codeVerifier.isValidCode(usuario.getTotpSecret(), dto.codigoOtp());
        if (!valido) {
            throw new RegraDeNegocioException("Código OTP inválido ou expirado. Tente novamente.");
        }

        usuario.setTotpAtivo(true);
        usuarioRepository.save(usuario);
    }

    public boolean verificar(Long usuarioId, String codigo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));

        if (!usuario.isTotpAtivo() || usuario.getTotpSecret() == null) {
            return true;
        }

        return codeVerifier.isValidCode(usuario.getTotpSecret(), codigo);
    }
}
