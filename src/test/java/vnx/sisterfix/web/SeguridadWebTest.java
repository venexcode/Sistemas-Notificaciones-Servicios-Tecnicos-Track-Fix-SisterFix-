package vnx.sisterfix.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vnx.sisterfix.security.RateLimiter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "sisterfix.seguridad.login-max-intentos=3",
        "sisterfix.seguridad.consulta-max-intentos=3"
})
class SeguridadWebTest {

    @Autowired
    private WebApplicationContext contexto;

    @Autowired
    private RateLimiter limitador;

    @Value("${sisterfix.admin.usuario}")
    private String adminUsuario;

    @Value("${sisterfix.admin.password}")
    private String adminPassword;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
        limitador.reiniciar();
    }

    @Test
    void inicioConsultaYLoginSonPublicos() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/sisterfix")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void elPanelSinSesionRedirigeAlLogin() throws Exception {
        mvc.perform(get("/admin/ordenes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void unTecnicoVeElPanelPeroNoLosUsuarios() throws Exception {
        mvc.perform(get("/admin/ordenes").with(user("tec").roles("TECNICO")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/usuarios").with(user("tec").roles("TECNICO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unAdministradorVeElPanelYLosUsuarios() throws Exception {
        mvc.perform(get("/admin/ordenes").with(user("jefa").roles("ADMIN")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/usuarios").with(user("jefa").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void elAdministradorInicialPuedeEntrar() throws Exception {
        mvc.perform(post("/login").with(csrf())
                        .param("username", adminUsuario)
                        .param("password", adminPassword))
                .andExpect(redirectedUrl("/admin/ordenes"));
    }

    @Test
    void conUnaContrasenaIncorrectaVuelveAlLoginConError() throws Exception {
        mvc.perform(post("/login").with(csrf())
                        .param("username", adminUsuario)
                        .param("password", "incorrecta-123"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void despuesDeVariosIntentosFallidosElLoginSeBloquea() throws Exception {
        for (int i = 0; i < 3; i++) {
            mvc.perform(post("/login").with(csrf()).param("username", "x").param("password", "mala"))
                    .andExpect(redirectedUrl("/login?error"));
        }

        mvc.perform(post("/login").with(csrf()).param("username", "x").param("password", "mala"))
                .andExpect(redirectedUrl("/login?bloqueado"));
    }
}