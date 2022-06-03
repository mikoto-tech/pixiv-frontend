package net.mikoto.pixiv.frontend.controller;

import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.database.connector.DatabaseConnector;
import net.mikoto.pixiv.database.connector.exception.GetArtworkException;
import net.mikoto.pixiv.database.connector.exception.WrongSignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author mikoto
 * @date 2022/6/3 17:10
 */
@Controller
public class IndexController {
    @Qualifier("databaseConnector")
    DatabaseConnector databaseConnector;
    @Value("${mikoto.frontend.database.address}")
    private String databaseAddress;
    @Value("${mikoto.frontend.forward.address}")
    private String forwardAddress;
    @Value("${mikoto.frontend.forward.key}")
    private String forwardKey;

    @Autowired
    public IndexController(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @RequestMapping(
            "/"
    )
    public String index(Model model) throws GetArtworkException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, WrongSignException {
        search(model, ";", Sort.Direction.DESC, "bookmarkCount", 1);
        return "search";
    }

    @RequestMapping(
            "/search"
    )
    public String search(Model model, String s, Sort.Direction order, String properties, Integer page) throws GetArtworkException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, WrongSignException {
        if (page == null) {
            page = 1;
        }
        Artwork[] artworks = databaseConnector.getArtworks(databaseAddress, s, order, properties, page - 1);
        StringBuilder stringBuilder = new StringBuilder();

        for (Artwork artwork :
                artworks) {
            stringBuilder
                    .append("<div class=\"card\" style=\"width: 300px\"><img class=\"card-img-top\" src=\"")
                    .append(forwardAddress)
                    .append("/artwork/getImage?key=")
                    .append(forwardKey)
                    .append("&url=")
                    .append(artwork.getIllustUrlRegular())
                    .append("\" alt=\"Card image\" style=\"width: 100%\" /><div class=\"card-body\"><h4 class=\"card-title\">")
                    .append(artwork.getArtworkTitle())
                    .append("</h4><p class=\"card-text\">")
                    .append(artwork.getAuthorName())
                    .append("</p></div></div><br>");
        }

        model.addAttribute("artworks", stringBuilder.toString());

        StringBuilder pageHtml = new StringBuilder();

        if (page != 1) {
            pageHtml.append("<a href=\"/search?s=")
                    .append(s)
                    .append("&order=")
                    .append(order)
                    .append("&properties=")
                    .append(properties)
                    .append("&page=")
                    .append(page - 1)
                    .append("\"><button type=\"button\" class=\"btn btn-primary\">上一页</button></a>");
        }

        pageHtml.append("<a href=\"/search?s=")
                .append(s)
                .append("&order=")
                .append(order)
                .append("&properties=")
                .append(properties)
                .append("&page=")
                .append(page + 1)
                .append("\"><button type=\"button\" class=\"btn btn-primary\">下一页</button></a>");

        model.addAttribute("page", pageHtml);

        StringBuilder script = new StringBuilder();

        script.append("document.getElementById(\"properties\").value = \"")
                .append(properties)
                .append("\";");

        script.append("document.getElementById(\"order\").value = \"")
                .append(order)
                .append("\";");

        script.append("document.getElementById(\"s\").value = \"")
                .append(s)
                .append("\";");

        script.append("document.getElementById(\"page\").value = \"")
                .append(page)
                .append("\";");

        model.addAttribute("script", script);
        return "search";
    }
}
