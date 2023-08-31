package scs.planus.infra.universalLink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping
public class UniversalLinkController {
    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping(value = {"/.well-known/apple-app-site-association", "/apple-app-site-association"})
    public String getUniversalLink() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:apple-app-site-association.json");
        String aasa = Files.readString(Path.of(resource.getURI()));

        return aasa;
    }
}
