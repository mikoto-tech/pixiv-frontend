package net.mikoto.pixiv.frontend.controller;

import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.api.model.ForwardServer;
import net.mikoto.pixiv.database.connector.DatabaseConnector;
import net.mikoto.pixiv.database.connector.exception.GetArtworkException;
import net.mikoto.pixiv.forward.connector.ForwardConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author mikoto
 * @date 2022/6/3 17:10
 */
@Controller
public class SearchController {
    @Qualifier("databaseConnector")
    private final DatabaseConnector databaseConnector;
    @Qualifier("forwardConnector")
    private final ForwardConnector forwardConnector;
    @Value("${mikoto.pixiv.frontend.database.address}")
    private String databaseAddress;
    @Value("${mikoto.pixiv.frontend.forwardServers}")
    private String forwardServers;

    @Autowired
    public SearchController(DatabaseConnector databaseConnector, ForwardConnector forwardConnector) {
        this.databaseConnector = databaseConnector;
        this.forwardConnector = forwardConnector;
    }

    @RequestMapping(
            "/"
    )
    public String indexPage(Model model) throws GetArtworkException, IOException {
        return search(model, null, null, null, null);
    }

    @RequestMapping(
            "/search"
    )
    public String search(Model model, String s, Sort.Direction order, String properties, Integer page) throws GetArtworkException, IOException {
        if (forwardConnector.isEmpty()) {
            for (String forwardServer :
                    forwardServers.split(";")) {
                String[] forwardServerConfig = forwardServer.split(",");
                forwardConnector.addForwardServer(new ForwardServer(forwardServerConfig[0], Integer.parseInt(forwardServerConfig[1]), forwardServerConfig[2]));
            }
        }
        if (s == null) {
            s = ";";
        }
        if (page == null) {
            page = 1;
        }
        if (order == null) {
            order = Sort.Direction.DESC;
        }
        if (properties == null) {
            properties = "bookmarkCount";
        }
        Artwork[] artworks = databaseConnector.getArtworks(databaseAddress, s, order, properties, page - 1);

        model.addAttribute("forwardConnector", forwardConnector);
        model.addAttribute("artworks", artworks);
        model.addAttribute("notice", "");
        model.addAttribute("page", page);
        model.addAttribute("properties", properties);
        model.addAttribute("order", order);
        model.addAttribute("s", s);
        return "search";
    }
}
