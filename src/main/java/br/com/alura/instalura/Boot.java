package br.com.alura.instalura;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import br.com.alura.instalura.models.Foto;
import br.com.alura.instalura.models.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Service
@EnableSwagger2
@SpringBootApplication
public class Boot implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Boot.class);

    @PersistenceContext
    private EntityManager em;

	public static void main(String[] args) {
		SpringApplication.run(Boot.class, args);
	}

	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		//jogando a ordem lá para baixo, tem que rodar do filtro do security.
		bean.setOrder(-100000);
		return bean;
	}

	@Bean
	public Docket swaggerSettings() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().pathMapping("/")
				.ignoredParameterTypes(Usuario.class)
				.globalOperationParameters(Arrays.asList(new ParameterBuilder().name("X-AUTH-TOKEN")
						.description("Description of header").modelRef(new ModelRef("string"))
						.parameterType("header").required(false).build()));
	}


    private List<Usuario> geraUsuariosEAmigos() {
        Usuario alberto = new Usuario("alots", "123456","https://instagram.fcgh9-1.fna.fbcdn.net/vp/960227fa1524bee9e36610f8da71889c/5B6F42E1/t51.2885-19/11199408_569104449895751_1837574990_a.jpg");
        Usuario rafael = new Usuario("rafael", "123456","https://instagram.fcgh9-1.fna.fbcdn.net/vp/faf1cd7c1d50bbf382cad0d43df15a49/5B5FF9ED/t51.2885-19/s150x150/12599387_1591433254512484_973178862_a.jpg");
        Usuario vitor = new Usuario("vitor", "123456","https://instagram.fcgh9-1.fna.fbcdn.net/vp/671f159e4aa9c3f6f3f4107305cf1462/5B5747E6/t51.2885-19/s150x150/23417279_144305519547753_7852761162822189056_n.jpg");


        alberto.adicionaAmigo(rafael);
        alberto.adicionaAmigo(vitor);
        rafael.adicionaAmigo(vitor);
        vitor.adicionaAmigo(alberto);

        em.persist(alberto);
        em.persist(rafael);
        em.persist(vitor);

        return Arrays.asList(alberto,rafael,vitor);
    }

    private void geraFotos(Integer usuarioId) {
        Usuario usuario = em.find(Usuario.class, usuarioId);
        Foto foto1 = new Foto(
                "https://instagram.fcgh10-1.fna.fbcdn.net/t51.2885-15/e35/14482111_1635089460122802_8984023070045896704_n.jpg?ig_cache_key=MTM1MzEzNjM4NzAxMjIwODUyMw%3D%3D.2",
                "comentario da foto",
                usuario);
        Foto foto2 = new Foto(
                "https://instagram.fcgh9-1.fna.fbcdn.net/t51.2885-15/e35/15276770_381074615568085_8052939980646907904_n.jpg?ig_cache_key=MTM5ODY4MDMyNjYyMDA1MDE4OQ%3D%3D.2",
                "comentario da foto",
                usuario);

        em.persist(foto1);
        em.persist(foto2);
    }

	@Override
    @Transactional
	public void run(String... args) throws Exception {
	    LOGGER.info("Criando usuarios e fotos...");

        List<Usuario> usuarios = geraUsuariosEAmigos();
        for (Usuario usuario : usuarios) {
            geraFotos(usuario.getId());
        }
    }
}
